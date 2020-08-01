package com.bhachtgattransporter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bhachtgattransporter.Activity.MainPage;
import com.bhachtgattransporter.Fragment.Home;
import com.bhachtgattransporter.Model.Order;
import com.bhachtgattransporter.R;
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
    public static String OrdrId,order_title,date, order_status, total_amount, user_name,source_contact,total_km, c_pickup_location, c_delivery_location,
            movers_image,specification, movers_date , time2, endtime;
    public static String truck_type, material_type, tot_weight, remark;

    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public static String DeleteOrderURL="http://softmate.in/androidApp/Qsar/DeliveryBoy/Delete_Order.php";


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

      /*  holder.txtorder_id.setText(itemsList.get(position).getOrder_title());
        holder.txtamount.setText("Rs. 0");
        holder.pickupadd1.setText(itemsList.get(position).getC_pickup_location() );
        holder.deliveryadd1.setText(itemsList.get(position).getC_delivery_location() );
        holder.date2.setText(itemsList.get(position).getDate() );

        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                OrdrId = itemsList.get(position).getId();
                order_title = itemsList.get(position).getOrder_title();
                date = itemsList.get(position).getDate();
                total_amount = itemsList.get(position).getTotal_amount();
                user_name = itemsList.get(position).getUser_name();
                source_contact = itemsList.get(position).getSource_contact();
                total_km = itemsList.get(position).getTotal_km();
                c_pickup_location = itemsList.get(position).getC_pickup_location();
                c_delivery_location = itemsList.get(position).getC_delivery_location();
                movers_image = itemsList.get(position).getMovers_image();
                movers_date = itemsList.get(position).getMovers_date();
                time2 = itemsList.get(position).getTime();
                endtime = itemsList.get(position).getEnd_time();

                truck_type = itemsList.get(position).getTruck_type();
                tot_weight = itemsList.get(position).getTruck_weight();
                material_type = itemsList.get(position).getMaterial();
                remark = itemsList.get(position).getRemark();

                Intent intent = new Intent(context, RaisedTruckLoadDetailVendor.class);
                context.startActivity(intent);

            }
        });*/


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
