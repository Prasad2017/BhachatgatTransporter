package com.graminvikreta_transporter.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Extra.RecyclerTouchListener;
import com.graminvikreta_transporter.Fragment.TruckLoadDetails;
import com.graminvikreta_transporter.Model.Order;
import com.graminvikreta_transporter.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class AwardordersAdapter extends RecyclerView.Adapter<AwardordersAdapter.MyViewHolder> {


    private final RecyclerTouchListener.ClickListener listener;
    public static List<Order> itemsList;
    Context context;
    String id = null;
    String add = null;
    String time = null;
    public static String OrdrId;
    public static String total_amt,order_title,order_status, id1, user_name, contactNo, add1,add2, paymentMode, type,date, movers_id;
    public static ArrayList<Integer> selectedList;

    public AwardordersAdapter(List<Order> itemsList, RecyclerTouchListener.ClickListener listener) {
        this.listener = listener;
        this.itemsList = itemsList;
        this.context= context;
        selectedList = new ArrayList<>();
        for (int i = 0; i < itemsList.size(); i++) {
            selectedList.add(0);
        }

    }

    @Override public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return  new MyViewHolder((LayoutInflater.from(parent.getContext()).inflate(R.layout.award_order_row_vendor,null)),listener);
    }

    @Override public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.txtorder_id.setText(itemsList.get(position).getOrderNumber());
        holder.txtamount.setText("Rs "+itemsList.get(position).getProductBidAmount() );
        holder.txtdate.setText(itemsList.get(position).getDate());
        id =itemsList.get(position).getMasterBidId();

        holder.track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {



                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });
    }

    @Override public int getItemCount() {
        return itemsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView cancelBtn;
        public TextView txtorder_id , OrderType;
        public  TextView txttime;
        public  TextView txtdate;
        public TextView txtamount, track;
        LinearLayout layout;
        WeakReference<RecyclerTouchListener.ClickListener> listenerRef;
        Context context;


        public MyViewHolder( View itemView, RecyclerTouchListener.ClickListener listener) {

            super(itemView);
            context = itemView.getContext();
            txtorder_id =(TextView)itemView.findViewById(R.id.OrderNo2) ;
            txtdate =(TextView)itemView.findViewById(R.id.date2) ;
            txtamount = (TextView) itemView.findViewById(R.id.rate2);
            track = (TextView) itemView.findViewById(R.id.track);
            layout = (LinearLayout) itemView.findViewById(R.id.LinearLayout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {


            final String order_id = itemsList.get(getAdapterPosition()).getMasterBidId();
            ((MainPage)context).loadFragment(new TruckLoadDetails(), true);
            user_name = itemsList.get(getAdapterPosition()).getFullName();
            id1 = itemsList.get(getAdapterPosition()).getMasterBidId();
            contactNo = itemsList.get(getAdapterPosition()).getMobileno();
            order_status = itemsList.get(getAdapterPosition()).getBidStatus();
            order_title = itemsList.get(getAdapterPosition()).getOrderNumber();
            date = itemsList.get(getAdapterPosition()).getDate();
            total_amt =itemsList.get(getAdapterPosition()).getProductBidAmount();


        }

    }

}

