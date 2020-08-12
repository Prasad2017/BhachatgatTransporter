package com.graminvikreta_transporter.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Extra.Blur;
import com.graminvikreta_transporter.Fragment.FullScreenTruckDetails;
import com.graminvikreta_transporter.Model.Order;
import com.graminvikreta_transporter.Model.TruckDetails;
import com.graminvikreta_transporter.R;
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TruckDetailsAdapter extends RecyclerView.Adapter<TruckDetailsAdapter.MyViewHolder> {

    Context context;
    List<TruckDetails> truckDetailsList;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    ImageView truckimageView;
    private String UpdateTruckURL  = "http://softmate.in/androidApp/Qsar/DeliveryBoy/UpdateVendorTruckDetails.php";
    public static String TruckURL="http://softmate.in/androidApp/Qsar/getTruck.php";
    public static String TruckCapacityURL="http://softmate.in/androidApp/Qsar/DeliveryBoy/getCapacity.php";

    Pattern trucknumberpattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}");
    Matcher matcher;
    String TruckTypeValue, TruckCapacityValue;
    RequestQueue requestQueue;
    JsonObjectRequest objectRequest;
    List<Order> truckList = new ArrayList<>();
    List<Order> trucktonList = new ArrayList<>();



    public TruckDetailsAdapter(List<TruckDetails> truckDetailsList, Context context) {

        this.context = context;
        this.truckDetailsList = truckDetailsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {

        View view = LayoutInflater.from(context).inflate(R.layout.truck_details_layout_vendor, null);
        MyViewHolder holder = new MyViewHolder(context, view, truckDetailsList);
        return holder;

    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        final TruckDetails truckDetails = truckDetailsList.get(position);

        holder.bind(truckDetails);
        holder.expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean expanded = truckDetails.isExpanded();
                truckDetails.setExpanded(!expanded);
                notifyItemChanged(position);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // display password sheet
                FullScreenTruckDetails fragment = new FullScreenTruckDetails();
                fragment.setId(MainPage.userId, truckDetails.getTruckImages(), truckDetails.getTrucknumber(),
                        truckDetails.getTruckType(), truckDetails.getTrucktyres(), truckDetails.getTruckId());
                fragment.show(((AppCompatActivity) context).getSupportFragmentManager(), fragment.getTag());

            }
        });

    }

    private boolean validate(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            return true;
        }
        editText.setError("Please Fill This");
        editText.requestFocus();
        return false;
    }

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    @Override
    public int getItemCount() {
        return truckDetailsList == null ? 0 : truckDetailsList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView trucknumber, truckweight, truckcapacity, truckheight, trucklength, trucktyres, truckname;
        public ImageView edit, expand, imageView;
        private View subItem;


        public MyViewHolder(Context context, View itemView, List<TruckDetails> truckDetailsList) {
            super(itemView);

            trucknumber = (TextView) itemView.findViewById(R.id.trucknumber);
            truckname = (TextView) itemView.findViewById(R.id.truckname);
            truckweight = (TextView) itemView.findViewById(R.id.truckweight);
            truckcapacity = (TextView) itemView.findViewById(R.id.truckcapacity);
            truckheight = (TextView) itemView.findViewById(R.id.truckheight);
            trucklength = (TextView) itemView.findViewById(R.id.trucklength);
            trucktyres = (TextView) itemView.findViewById(R.id.trucktyres);
            edit = (ImageView) itemView.findViewById(R.id.edit);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            expand = (ImageView) itemView.findViewById(R.id.expand);
            subItem = (View) itemView.findViewById(R.id.sub_item);

        }

        private void bind(final TruckDetails truckDetails) {
            boolean expanded = truckDetails.isExpanded();

            subItem.setVisibility(expanded ? View.VISIBLE : View.GONE);

            trucknumber.setText("Truck Number : "+truckDetails.getTrucknumber());
            truckname.setText("Truck Name : "+truckDetails.getTruckType());
            //  truckweight.setText("Truck Width : "+truckDetails.getTruckweight()+" Feet");
            truckcapacity.setText("Truck Capacity : "+truckDetails.getTruckcapacity());
            //  truckheight.setText("Truck Height : "+truckDetails.getTruckheight()+" Feet");
            // trucklength.setText("Truck length : "+truckDetails.getTrucklength()+" Feet");
            trucktyres.setText("Truck Tyres : "+truckDetails.getTrucktyres());

            Transformation blurTransformation = new Transformation() {
                @Override
                public Bitmap transform(Bitmap source) {
                    Bitmap blurred = Blur.fastblur(context, source, 10);
                    source.recycle();
                    return blurred;
                }

                @Override
                public String key() {
                    return "blur()";
                }
            };

            Picasso.with(context)
                    .load("http://graminvikreta.com/androidApp/Transporter/" + truckDetails.getTruckImages())
                    .placeholder(R.drawable.profileimg)
                    .transform(blurTransformation)
                    .skipMemoryCache()
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Picasso.with(context)
                                    .load("http://graminvikreta.com/androidApp/Transporter/" + truckDetails.getTruckImages())
                                    .placeholder(imageView.getDrawable())
                                    .into(imageView);
                        }

                        @Override
                        public void onError() {
                        }
                    });

        }
    }
}
