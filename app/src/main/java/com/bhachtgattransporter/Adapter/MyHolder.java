package com.bhachtgattransporter.Adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.bhachtgattransporter.Model.Qdata;
import com.bhachtgattransporter.R;

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
