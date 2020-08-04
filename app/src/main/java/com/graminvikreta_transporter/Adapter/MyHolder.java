package com.graminvikreta_transporter.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.graminvikreta_transporter.Model.Qdata;
import com.graminvikreta_transporter.R;

import java.util.List;

import butterknife.BindViews;
import butterknife.ButterKnife;


class MyHolder extends RecyclerView.ViewHolder {

    @BindViews({R.id.name, R.id.amount})
    List<TextView> textViews;

    public MyHolder(final Context context, View itemView, List<Qdata> newsListResponse) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

}
