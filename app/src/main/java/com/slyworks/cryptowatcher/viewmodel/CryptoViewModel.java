package com.slyworks.cryptowatcher.viewmodel;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.slyworks.cryptowatcher.entities.CryptoCoinEntity;
import com.slyworks.cryptowatcher.recview.CoinModel;
import com.slyworks.cryptowatcher.screens.MainScreen;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Joshua Sylvanus, 2:38 PM, 8/31/2020.
 */
//remember to add ViewModel dependencies
public class CryptoViewModel extends ViewModel {
    //region Vars
    private static final String TAG = CryptoViewModel.class.getSimpleName();
    public final String CRYPTO_URL_PATH = "https://s2.coinmarketcap.com/static/img/coins/128x128/%s.png";
    public final String ENDPOINT_FETCH_CRYPTO_DATA = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest?limit=100";
    public final String API_KEY = "c5edc459-1455-4291-88ff-89fadab8c08a";
    private RequestQueue mQueue;
    private final ObjectMapper mObjMapper = new ObjectMapper();
    private MainScreen mView;
    private Context mAppContext;

    //for liveData
    private MutableLiveData<List<CoinModel>> mDataApi = new MutableLiveData<>();
    private MutableLiveData<String> mError = new MutableLiveData<>();
    private ExecutorService mExecutor = Executors.newFixedThreadPool(5);

    private JsonArrayRequest mJsonObjReq;
    //endregion

    public LiveData<List<CoinModel>> getCoinsMarketData() {
        return mDataApi;
    }

    public LiveData<String> getErrorUpdates() {
        return mError;
    }

    public LiveData<Double> getTotalMarketCap() {

        return Transformations.map(mDataApi, input -> {
            double totalMarketCap = 0;
            for (int i = 0; i < input.size(); i++) {
                totalMarketCap += input.get(i).marketCap;
            }
            return totalMarketCap;
        });
    }

    public CryptoViewModel() {
        super();
        Log.e(TAG, "NEW VIEWMODEL IS CREATED");
    }

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
        if (mQueue == null)
            mQueue = Volley.newRequestQueue(mAppContext);
        fetchData();
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     * <p>
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel.
     */
    @Override
    protected void onCleared() {
        Log.e(TAG, "onCleared() called");
        super.onCleared();
    }

    public void unbind()
    {
        mView=null;
    }
    private  class EntityToModelMapperTask extends AsyncTask<List<CryptoCoinEntity>, Void, List<CoinModel>> {
        @Override
        protected List<CoinModel> doInBackground(List<CryptoCoinEntity>... data) {
            final ArrayList<CoinModel> listData = new ArrayList<>();
            CryptoCoinEntity entity;
            for (int i = 0; i < data[0].size(); i++) {
                entity = data[0].get(i);
                listData.add(new CoinModel(entity.getName(), entity.getSymbol(),
                        String.format(CRYPTO_URL_PATH, entity.getId()), entity.getPriceUsd(),
                        entity.get24hVolumeUsd(), Double.parseDouble(entity.getMarketCapUsd())));
            }

            return listData;
        }

        @Override
        protected void onPostExecute(List<CoinModel> data) {
            if (mView!=null)
                mView.updateData(data);

        }


    }
    private  Response.Listener</*JSONArray*/JSONObject> mResponseListener = response -> {
        writeDataToInternalStorage(response);
        ArrayList<CryptoCoinEntity> data = parseJSON(response.toString());
        Log.d(TAG, "data fetched:" + data);
        new EntityToModelMapperTask().execute(data);
    };

    private  Response.ErrorListener mErrorListener= error -> {
        if (mView!=null)
            mView.setError(error.toString());
        try {
            JSONArray data = readDataFromStorage();
            ArrayList<CryptoCoinEntity> entities = parseJSON(data.toString());
            new EntityToModelMapperTask().execute(entities);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    };

    @NonNull
    private List<CoinModel> mapEntityToModel(List<CryptoCoinEntity> datum) {
        final ArrayList<CoinModel> listData = new ArrayList<>();
        CryptoCoinEntity entity;
        for (int i = 0; i < datum.size(); i++) {
            entity = datum.get(i);
            listData.add(new CoinModel(entity.getName(), entity.getSymbol(), String.format(CRYPTO_URL_PATH, entity.getId()),entity.getPriceUsd(),
                    entity.get24hVolumeUsd(), Double.parseDouble(entity.getMarketCapUsd())));
        }

        return listData;
    }

    public void fetchData() {
        if (mQueue == null)
            mQueue = Volley.newRequestQueue(mAppContext);

        final /*JsonArrayRequest*/ JsonObjectRequest jsonObjReq =
                new /*JsonArrayRequest*/ JsonObjectRequest(ENDPOINT_FETCH_CRYPTO_DATA,
                        response -> {
                            Log.e(TAG, "Thread->" +
                                               Thread.currentThread().getName()+"\tGot some network response");
                            writeDataToInternalStorage(response);
                            final ArrayList<CryptoCoinEntity> data;
                            try {
                                data = parseJSON(/*response.toString()*/response.getString("data"));
                                List<CoinModel> mappedData = mapEntityToModel(data);
                                mDataApi.setValue(mappedData);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        },
                        error -> {
                            Log.e(TAG, "Thread->" +
                                               Thread.currentThread().getName()+"\tGot network error");
                            mError.setValue(error.toString());

                            Log.e(TAG, "fetchData: error:->"+error.toString());

                            mExecutor.execute(() -> {
                                try {
                                    Log.e(TAG, "Thread->"+ Thread.currentThread().getName()+
                                                       "\tNot fetching from network because of network error - fetching from disk");
                                    JSONArray data = readDataFromStorage();
                                    ArrayList<CryptoCoinEntity> entities = parseJSON(data.toString());
                                    List<CoinModel> mappedData = mapEntityToModel(entities);
                                    mDataApi.postValue(mappedData);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch(Exception e){
                                    e.printStackTrace();
                                }                            });}){
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("Accept", "application/json");
                            headers.put("X-CMC_PRO_API_KEY", API_KEY);
                            return headers;
                    }
                };

        // Add the request to the RequestQueue.
        mQueue.add(jsonObjReq);
    }


    public ArrayList<CryptoCoinEntity> parseJSON(String jsonStr) {
        ArrayList<CryptoCoinEntity> data = null;

        try {
            data = mObjMapper.readValue(jsonStr, new TypeReference<ArrayList<CryptoCoinEntity>>() {
            });
        } catch (Exception e) {
            if (mView!=null)
                mView.setError(e.getMessage());
            e.printStackTrace();

            Log.e(TAG, "parseJSON: "+ e);
        }
        return data;
    }
    //////////////////////////////////////////////////////////////////////////////////////STORAGE CODE///////////////////////////////////////////////////////////////////////////////////////////
    String DATA_FILE_NAME = "crypto.data";

    private void writeDataToInternalStorage(/*JSONArray*/ JSONObject data) {
        FileOutputStream fos = null;
        try {
            fos = mAppContext.openFileOutput(DATA_FILE_NAME, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            fos.write(data.toString().getBytes());
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private JSONArray readDataFromStorage() throws JSONException {
        //TODO:implement checking if there has been any writing to DataStorage done already
        FileInputStream fis = null;
        try {
            fis = mAppContext.openFileInput(DATA_FILE_NAME);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JSONArray(sb.toString());
    }
}
