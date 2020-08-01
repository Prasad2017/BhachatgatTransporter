package com.bhachtgattransporter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.bhachtgattransporter.Model.Qdata;
import com.bhachtgattransporter.R;

import java.util.List;

public class OrderProductListAdapter extends RecyclerView.Adapter<MyHolder> {

    Context context;
    List<Qdata> newsListResponse;
    public static String OrdrId1, Vendor_id;

    public OrderProductListAdapter(Context context, List<Qdata> newsListResponse) {
        this.context = context;
        this.newsListResponse = newsListResponse;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.odere_bid_rate_row_vendor, null);
        MyHolder OrderedProductsListViewHolder = new MyHolder(context, view, newsListResponse);
        return OrderedProductsListViewHolder;
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        Vendor_id=newsListResponse.get(position).getVendorId();
        holder.textViews.get(0).setText("Anonymous");
        holder.textViews.get(1).setText(newsListResponse.get(position).getBidAmount());
    }

    @Override
    public int getItemCount() {
        return newsListResponse.size();
    }
}


