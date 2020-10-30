package com.slyworks.cryptowatcher.screens;

import com.slyworks.cryptowatcher.recview.CoinModel;

import java.util.List;

/**
 * Created by Joshua Sylvanus, 2:37 PM, 8/31/2020.
 */
public interface MainScreen {
    void updateData(List<CoinModel> data);
    void setError(String msg);
}
