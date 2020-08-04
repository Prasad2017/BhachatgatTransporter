package com.graminvikreta_transporter.Fragment;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Adapter.MyOrdersAdapter;
import com.graminvikreta_transporter.Adapter.TruckLoadOrderAdapter;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Model.AllList;
import com.graminvikreta_transporter.Model.BidData;
import com.graminvikreta_transporter.Model.Order;
import com.graminvikreta_transporter.R;
import com.graminvikreta_transporter.Retrofit.Api;
import com.graminvikreta_transporter.Retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit.RetrofitError;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class OrderHistory extends Fragment {

    View view;
    @BindView(R.id.bidorderList)
    RecyclerView orderHistoryList;
    @BindView(R.id.simpleSwipeRefreshLayout)
    SwipeRefreshLayout order_history_wipe;
    MyOrdersAdapter myOrdersAdapter;
    public static BidData mybiddingResponse;
    @BindView(R.id.defaultMessage)
    TextView textView;
    List<Order> truckList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_order_history, container, false);
        ButterKnife.bind(this, view);
        MainPage.title.setText("Order History");


        order_history_wipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(DetectConnection.checkInternetConnection(getActivity())) {
                    StockList();
                    order_history_wipe.setRefreshing(false);
                }else {
                    order_history_wipe.setRefreshing(false);
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }

    public void onStart() {
        super.onStart();
        Log.e("onStart", "called");
        MainPage.title.setVisibility(View.VISIBLE);
        ((MainPage) getActivity()).lockUnlockDrawer(1);
        MainPage.drawerLayout.closeDrawers();
        if (DetectConnection.checkInternetConnection(getActivity())){
            StockList();
        }else {
            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public  void StockList() {
        try {

            ApiInterface apiInterface = Api.getClient().create(ApiInterface.class);
            Call<BidData> call = apiInterface.getBidding(MainPage.userId);
            call.enqueue(new Callback<BidData>() {
                @Override
                public void onResponse(Call<BidData> call, Response<BidData> response) {

                    if (response.body().getSuccess().equalsIgnoreCase("true")) {
                        try {

                            mybiddingResponse = response.body();
                            if (mybiddingResponse != null) {
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                                linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                                orderHistoryList.setLayoutManager(linearLayoutManager);
                                myOrdersAdapter = new MyOrdersAdapter(getActivity(), mybiddingResponse.getOrderdata());
                                orderHistoryList.setAdapter(myOrdersAdapter);
                                myOrdersAdapter.notifyDataSetChanged();
                                orderHistoryList.setHasFixedSize(true);
                                textView.setVisibility(View.GONE);
                            } else {
                                textView.setVisibility(View.VISIBLE);
                                textView.setText("No Order Found");
                            }

                        } catch (Exception e) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("No Order Found");
                        }
                    } else {
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("No Order Found");
                    }

                }

                @Override
                public void onFailure(Call<BidData> call, Throwable t) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("No Order Found");
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}