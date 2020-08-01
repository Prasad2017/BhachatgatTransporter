package com.bhachtgattransporter.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bhachtgattransporter.R;

import butterknife.BindView;
import butterknife.ButterKnife;

class MyOrderholder extends RecyclerView.ViewHolder {

    @BindView(R.id.orderedProductsRecyclerView)
    RecyclerView orderedProductsRecyclerView;
    @BindView(R.id.bid_details)
    TextView bid_details;
    @BindView(R.id.orderdate)
    TextView date;


    public MyOrderholder(final Context context, View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }
}

