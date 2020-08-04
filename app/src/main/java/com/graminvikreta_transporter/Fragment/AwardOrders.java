package com.graminvikreta_transporter.Fragment;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Adapter.AwardordersAdapter;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Extra.RecyclerTouchListener;
import com.graminvikreta_transporter.Model.AllList;
import com.graminvikreta_transporter.Model.Order;
import com.graminvikreta_transporter.Model.RaisedOrderData;
import com.graminvikreta_transporter.R;
import com.graminvikreta_transporter.Retrofit.Api;
import com.graminvikreta_transporter.Retrofit.ApiInterface;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AwardOrders extends Fragment {

    @BindView(R.id.simpleListView)
    RecyclerView recyclerView;
    View view;
    public static Activity activity;
    AwardordersAdapter orderAdapter;
    @BindView(R.id.simpleSwipeRefreshLayout)
    SwipeRefreshLayout award_swipe;
    @BindView(R.id.searchEditText)
    EditText searchEditText;
    @BindView(R.id.defaultMessage)
    TextView defaultMessage;
    List<Order> clientList;
    public List<Order> raisedOrderData = new ArrayList();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_award_orders, container, false);
        ButterKnife.bind(this, view);
        activity = (Activity) view.getContext();

        return view;
    }

    public void onStart() {
        super.onStart();
        Log.d("onStart", "called");
        MainPage.title.setVisibility(View.VISIBLE);
        ((MainPage) getActivity()).lockUnlockDrawer(1);
        MainPage.drawerLayout.closeDrawers();
        if (DetectConnection.checkInternetConnection(getActivity())) {
            GetAwardOrders(getActivity());
        }else {
            Toast.makeText(getActivity(),"No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    private void GetAwardOrders(FragmentActivity activity) {

        ApiInterface apiService = Api.getClient().create(ApiInterface.class);
        Call<AllList> call = apiService.GetAwardOrders(MainPage.userId);

        call.enqueue(new Callback<AllList>() {
            @Override
            public void onResponse(Call<AllList> call, Response<AllList> response) {


                if (response.isSuccessful()) {

                    AllList data = response.body();
                    raisedOrderData =response.body().getGetdatas();

                    if (raisedOrderData.size()==0){
                        defaultMessage.setVisibility(View.VISIBLE);
                        defaultMessage.setText("No Order Found");
                    }else {

                        orderAdapter = new AwardordersAdapter(raisedOrderData, new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {

                            }

                            @Override
                            public void onLongClick(View view, int position) {

                            }
                        });

                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                        recyclerView.setAdapter(orderAdapter);
                        orderAdapter.notifyDataSetChanged();
                        defaultMessage.setVisibility(View.GONE);

                    }

                }
            }

            @Override
            public void onFailure(Call<AllList> call, Throwable t) {
                // Log error here since request failed
                defaultMessage.setVisibility(View.VISIBLE);
                defaultMessage.setText("No Order Found");
                Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
            }
        });

    }
}