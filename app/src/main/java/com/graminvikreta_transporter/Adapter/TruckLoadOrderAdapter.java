package com.graminvikreta_transporter.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Activity.RaisedTruckLoadDetailVendor;
import com.graminvikreta_transporter.Fragment.Home;
import com.graminvikreta_transporter.Model.Order;
import com.graminvikreta_transporter.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

public class TruckLoadOrderAdapter extends RecyclerView.Adapter<TruckLoadOrderAdapter.MyViewHolder> {

    public static List<Order> itemsList;
    Context context;
    public static String orderId, orderNumber, date, fullName, mobilNumber, totalAmount,billingAddress, productName, source_address, quantity;

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public static String DeleteOrderURL="http://graminvikreta.com/androidApp/Transporter/Delete_Order.php";


    public TruckLoadOrderAdapter(List<Order> itemsList, Context context) {
        this.itemsList = itemsList;
        this.context= context;

    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.order_raised_row_layout_vendor, null);
        MyViewHolder holder = new MyViewHolder(context, view, itemsList);
        return holder;
    }

    @Override public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.txtorder_id.setText(itemsList.get(position).getOrderNumber());
        holder.txtamount.setText(MainPage.currency+" 0");
        holder.pickupadd1.setText(itemsList.get(position).getBillingAddress() );
        holder.deliveryadd1.setText(itemsList.get(position).getSource_address() );
        holder.date2.setText(itemsList.get(position).getDate() );

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                orderId = itemsList.get(position).getMasterBidId();
                orderNumber = itemsList.get(position).getOrderNumber();
                date = itemsList.get(position).getDate();
                totalAmount = itemsList.get(position).getProductBidAmount();
                fullName = itemsList.get(position).getFullName();
                mobilNumber = itemsList.get(position).getMobileno();
                billingAddress = itemsList.get(position).getBillingAddress();
                source_address = itemsList.get(position).getSource_address();
                quantity = itemsList.get(position).getQuantity();
                productName = itemsList.get(position).getProduct_name();


                Intent intent = new Intent(context, RaisedTruckLoadDetailVendor.class);
                context.startActivity(intent);

            }
        });


        holder.txtdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText("Are you sure to delete?");
                alertDialog.setCancelText("Cancel");
                alertDialog.setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        alertDialog.dismissWithAnimation();
                    }
                });
                alertDialog.show();
                Button btn = alertDialog.findViewById(R.id.confirm_button);
                btn.setBackground(context.getResources().getDrawable(R.drawable.custom_dialog_button));
                btn.setText("Delete");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        RequestParams requestParams = new RequestParams();
                        requestParams.put("vendor_id", MainPage.userId);
                    //    requestParams.put("order_id", itemsList.get(position).getId());

                        asyncHttpClient.post(DeleteOrderURL, requestParams, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                String s = new String(responseBody);
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    if (jsonObject.getString("success").equalsIgnoreCase("1")){
                                        alertDialog.dismissWithAnimation();
                                        Toasty.normal(context, "Order Has Been Deleted", Toasty.LENGTH_LONG).show();
                                        ((MainPage) context).removeCurrentFragmentAndMoveBack();
                                        ((MainPage) context).loadFragment(new Home(), false);
                                    }else if (jsonObject.getString("success").equalsIgnoreCase("0")){
                                        Toasty.error(context, ""+jsonObject.getString("message"), Toasty.LENGTH_LONG).show();
                                        alertDialog.dismissWithAnimation();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                alertDialog.dismissWithAnimation();

                            }
                        });



                    }
                });

            }
        });




    }

    @Override public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtorder_id, date2, pickupadd1, deliveryadd1, txtdelete;
        public TextView txtamount;
        Context context;
        CardView cardview;


        public MyViewHolder(Context context, View itemView, List<Order> itemsList) {

            super(itemView);
            this.context = itemView.getContext();
            txtorder_id = (TextView) itemView.findViewById(R.id.OrderNo2);
            date2 = (TextView) itemView.findViewById(R.id.date2);
            txtamount = (TextView) itemView.findViewById(R.id.rate2);
            pickupadd1 = (TextView) itemView.findViewById(R.id.pickupadd1);
            deliveryadd1 = (TextView) itemView.findViewById(R.id.deliveryadd1);
            txtdelete = (TextView) itemView.findViewById(R.id.txtdelete);
            cardview = (CardView) itemView.findViewById(R.id.cardview);
        }
    }


}
