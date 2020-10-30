package com.slyworks.cryptowatcher;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.slyworks.cryptowatcher.fragments.UILessFragment;
import com.slyworks.cryptowatcher.recview.CoinModel;
import com.slyworks.cryptowatcher.recview.CryptoAdapter;
import com.slyworks.cryptowatcher.recview.Divider;
import com.slyworks.cryptowatcher.screens.MainScreen;
import com.slyworks.cryptowatcher.viewmodel.CryptoViewModel;

import java.util.List;

public class MainActivity extends LocationActivity implements MainScreen{
    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView recView;
    private CryptoAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CryptoViewModel mViewModel;
    private long mLastFetchedDataTimeStamp;

    private final Observer<List<CoinModel>> dataObserver = coinModels -> updateData(coinModels);

    private final Observer<String> errorObserver = errorMsg -> setError(errorMsg);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();
        mViewModel= ViewModelProviders.of(this).get(CryptoViewModel.class);
        mViewModel.setAppContext(getApplicationContext());

        mViewModel.getCoinsMarketData().observe(this, dataObserver);

        mViewModel.getErrorUpdates().observe(this, errorObserver);


        getSupportFragmentManager().beginTransaction()
                .add(new UILessFragment(),"UILessFragment").commit();
    }





    @Override
    protected void onDestroy() {
        Log.e(TAG, "BEFORE super.onDestroy() called");
        super.onDestroy();
        Log.e(TAG, "AFTER super.onDestroy() called");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private final static int DATA_FETCHING_INTERVAL=10*1000; //10 seconds
    private void bindViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        recView = findViewById(R.id.recView);
        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (System.currentTimeMillis() - mLastFetchedDataTimeStamp < DATA_FETCHING_INTERVAL) {
                Log.e(TAG, "\tNot fetching from network because interval didn't reach");
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
            mViewModel.fetchData();
        });
        mAdapter = new CryptoAdapter();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        recView.setLayoutManager(lm);
        recView.setAdapter(mAdapter);
        recView.addItemDecoration(new Divider(this));
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> recView.smoothScrollToPosition(0));
        fab=findViewById(R.id.fabExit);
        fab.setOnClickListener(view -> finish());
    }

    private void showErrorToast(String error) {
        Toast.makeText(this, "Error:" + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void updateData(List<CoinModel> data) {
        mLastFetchedDataTimeStamp= System.currentTimeMillis();
        mAdapter.setItems(data);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setError(String msg) {
        showErrorToast(msg);
    }
}
