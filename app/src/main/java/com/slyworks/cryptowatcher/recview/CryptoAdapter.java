package com.slyworks.cryptowatcher.recview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.slyworks.cryptowatcher.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joshua Sylvanus, 6:06 PM, 8/30/2020.
 */

public class CryptoAdapter extends RecyclerView.Adapter<CryptoAdapter.CoinViewHolder> {
    List<CoinModel> mItems = new ArrayList<>();
    public final String STR_TEMPLATE_NAME = "%s\t\t\t\t\t\t%s";
    public final String STR_TEMPLATE_PRICE = "%s$\t\t24H Volume: %s$";


    @Override
    public void onBindViewHolder(CoinViewHolder holder, int position) {
        final CoinModel model = mItems.get(position);
        holder.tvNameAndSymbol.setText(String.format(STR_TEMPLATE_NAME, model.name, model.symbol));
        holder.tvPriceAndVolume.setText(String.format(STR_TEMPLATE_PRICE, model.priceUsd, model.volume24H));
        Glide.with(holder.itemView).load(model.imageUrl).into(holder.ivIcon);
    }

    @NotNull
    @Override
    public CoinViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new CoinViewHolder(LayoutInflater.from(parent.getContext())
                                          .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void setItems(List<CoinModel> items) {
        this.mItems.clear();
        this.mItems.addAll(items);
        notifyDataSetChanged();
    }


    class CoinViewHolder extends RecyclerView.ViewHolder {

        TextView tvNameAndSymbol;
        TextView tvPriceAndVolume;
        ImageView ivIcon;

        public CoinViewHolder(View itemView) {
            super(itemView);
            tvNameAndSymbol = itemView.findViewById(R.id.tvNameAndSymbol);
            tvPriceAndVolume = itemView.findViewById(R.id.tvPriceAndVolume);
            ivIcon = itemView.findViewById(R.id.ivIcon);
        }

    }

}

