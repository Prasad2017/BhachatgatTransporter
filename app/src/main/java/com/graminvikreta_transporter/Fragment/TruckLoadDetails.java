package com.graminvikreta_transporter.Fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener;
import com.agik.AGIKSwipeButton.View.Swipe_Button_View;
import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Adapter.AwardordersAdapter;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Model.StatusResponse;
import com.graminvikreta_transporter.R;
import com.graminvikreta_transporter.Retrofit.Api;
import com.graminvikreta_transporter.Retrofit.ApiInterface;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TruckLoadDetails extends Fragment {

    View view;
    @BindViews({R.id.name, R.id.contact, R.id.pickuplocation, R.id.deliverylocation, R.id.productName, R.id.quantity})
    List<TextView> textViews;
    @BindViews({R.id.swipeButtonView, R.id.swipeButtonView1})
    List<Swipe_Button_View> swipeButtonView;
    String order_id;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public String OrderDetailsUrl = "http://graminvikreta.com/androidApp/Transporter/TruckOrderDetails.php";
    public static String msgsend="http://graminvikreta.com/androidApp/Supplier/sendSMS.php";
    String order_status = null, miscellaneous_amount1, movers_id;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public Bitmap bitmap;
    private String userChoosenTask, imageString, trucknumber;
    ImageView driverimageView;
    public String truck_id[];
    public String truck_number[];

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_truck_load_details, container, false);
        ButterKnife.bind(this, view);
        MainPage.title.setText("Orders Details");

        try {
            order_id = AwardordersAdapter.id1;
            order_status = AwardordersAdapter.order_status;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (DetectConnection.checkInternetConnection(getActivity()))
        {
            getOrderDetails();
            if(order_status.equalsIgnoreCase("order_pickup")) {
                swipeButtonView.get(1).setText("Confirm Delivery");
                swipeButtonView.get(0).setEnabled(false);
                swipeButtonView.get(0).setVisibility(View.GONE);
                swipeButtonView.get(1).setVisibility(View.VISIBLE);
            }else if(order_status.equalsIgnoreCase("order_complete")){
                swipeButtonView.get(1).setText("Delivery completed");
                swipeButtonView.get(1).setEnabled(false);
                swipeButtonView.get(0).setVisibility(View.GONE);
                swipeButtonView.get(1).setVisibility(View.VISIBLE);
            }

        }else
        {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }

        swipeButtonView.get(1).setOnSwipeCompleteListener_forward_reverse(new OnSwipeCompleteListener() {
            @Override
            public void onSwipe_Forward(Swipe_Button_View swipe_button_view) {
                swipe_button_view.setText("Delivery Completed");
                swipe_button_view.setThumbBackgroundColor(ContextCompat.getColor(getActivity(), R.color.main_green_color));
                swipe_button_view.setSwipeBackgroundColor(ContextCompat.getColor(getActivity(), R.color.main_green_color));
                order_status = "order_complete";
                getConfirmPickup(order_id, order_status);
            }

            @Override
            public void onSwipe_Reverse(Swipe_Button_View swipe_button_view) {
                swipeButtonView.get(1).setEnabled(false);

            }
        });

        return view;
    }

    private void getOrderDetails() {

        RequestParams requestParams = new RequestParams();
        requestParams.put("order_id", order_id);
        requestParams.put("vendor_id", MainPage.userId);

        asyncHttpClient.post(OrderDetailsUrl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);

                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("success");
                    if (jsonArray.length()==0){

                    }else {
                        for (int i=0; i<jsonArray.length();i++){
                            jsonObject = jsonArray.getJSONObject(i);

                            textViews.get(0).setText(jsonObject.getString("full_name").equals("null")?"-":jsonObject.getString("full_name"));
                            textViews.get(1).setText(jsonObject.getString("mobileno").equals("null")?"-":jsonObject.getString("mobileno"));
                            textViews.get(2).setText(jsonObject.getString("source_address").equals("null")?"-":jsonObject.getString("source_address"));
                            textViews.get(3).setText(jsonObject.getString("billing_address").equals("null")?"-":jsonObject.getString("c_delivery_location"));
                            textViews.get(4).setText(jsonObject.getString("product_name").equals("")?"0":jsonObject.getString("product_name"));
                            textViews.get(5).setText(jsonObject.getString("quantity").equals("")?"0":jsonObject.getString("quantity"));

                            if(jsonObject.getString("payment_status").equalsIgnoreCase("paid")){
                                swipeButtonView.get(0).setVisibility(View.GONE);
                                swipeButtonView.get(1).setVisibility(View.GONE);
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });


    }

    private void getConfirmPickup(String order_id, String order_status) {

        ApiInterface apiService = Api.getClient().create(ApiInterface.class);
        Call<StatusResponse> call = apiService.getConfirmPickUp(MainPage.userId, order_id, order_status);
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful()) {
                    StatusResponse data = response.body();
                   if (order_status.equalsIgnoreCase("order_deliver")) {

                       String message = "Your order has been order delivered by " + MainPage.name + "( " + MainPage.contact + " )" + " successfully.\n Thank you.";
                       String encoded_message = URLEncoder.encode(message);

                       RequestParams requestParams = new RequestParams();
                       requestParams.put("number", MainPage.contact);
                       requestParams.put("message", encoded_message);

                       asyncHttpClient.get(msgsend, requestParams, new AsyncHttpResponseHandler() {
                           @Override
                           public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                               String s = new String(responseBody);
                               try {
                                   JSONObject jsonObject = new JSONObject(s);

                                   if (jsonObject.getString("success").equals("1")) {
                                   } else {
                                   }
                               } catch (JSONException e) {
                                   e.printStackTrace();
                               }

                           }

                           @Override
                           public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                           }
                       });
                   }
                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();

            }
        });

    }
}