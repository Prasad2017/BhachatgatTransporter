package com.graminvikreta_transporter.Activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.graminvikreta_transporter.Adapter.TruckLoadOrderAdapter;
import com.graminvikreta_transporter.Extra.Common;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Map.GeocodingLocation;
import com.graminvikreta_transporter.Model.StatusResponse;
import com.graminvikreta_transporter.Modules.DirectionFinder;
import com.graminvikreta_transporter.Modules.DirectionFinderListener;
import com.graminvikreta_transporter.Modules.Route;
import com.graminvikreta_transporter.R;
import com.graminvikreta_transporter.Retrofit.Api;
import com.graminvikreta_transporter.Retrofit.ApiInterface;
import com.graminvikreta_transporter.track.LocationTrack;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.SquareCap;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RaisedTruckLoadDetailVendor extends FragmentActivity implements DirectionFinderListener {

    @BindViews({R.id.order_title, R.id.date, R.id.Name, R.id.Number, R.id.alerAdd, R.id.deliveryAdd, R.id.timer, R.id.type,
            R.id.weight})
    List<TextView> textView;
    @BindView(R.id.back)
    ImageView imageView;
    @BindViews({R.id.cancelTxt, R.id.acceptTxt})
    List<TextView> buttons;
    public static String OrdrId, order_title, date, order_status, total_amount, user_name, source_contact, total_km, c_pickup_location, c_delivery_location,
            movers_image, specification, movers_date, time2, endtime, time1;

    public static String type, weight, material, remark;

    double lat, longi;
    public String start_lng, start_lat, end_latitute, end_lng;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Polyline blackPolyLine, greyPolyLine;
    private List<LatLng> listLatLng = new ArrayList<>();
    LocationTrack gps;
    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    Double latitude, longitude;
    String currentAddress, userId;
    LatLng citylocation, citylocation1;

    AlertDialog alertDialog1;
    AlertDialog.Builder dialogBuilder1;
    LayoutInflater inflater1;
    View dialogView1;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public String TruckOrderCountURL = "http://graminvikreta.com/androidApp/Transporter/CheckedCount.php";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raised_truck_load_detail_vendor);
        ButterKnife.bind(this);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {

            userId  = Common.getSavedUserData(RaisedTruckLoadDetailVendor.this,"userId");

            OrdrId = TruckLoadOrderAdapter.orderId;
            order_title = TruckLoadOrderAdapter.orderNumber;
            date = TruckLoadOrderAdapter.date;
            total_amount = TruckLoadOrderAdapter.totalAmount;
            user_name = TruckLoadOrderAdapter.fullName;
            source_contact = TruckLoadOrderAdapter.mobilNumber;
            c_pickup_location = TruckLoadOrderAdapter.source_address;
            c_delivery_location = TruckLoadOrderAdapter.billingAddress;
            type = TruckLoadOrderAdapter.productName;

            textView.get(0).setText(order_title);
            textView.get(1).setText(date);
            textView.get(2).setText("Name : " + user_name);
            textView.get(3).setText(source_contact);
            textView.get(4).setText(c_pickup_location);
            textView.get(5).setText(c_delivery_location);

            textView.get(7).setText(type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        GeocodingLocation locationAddress = new GeocodingLocation();
        locationAddress.getAddressFromLocation(c_delivery_location,
                RaisedTruckLoadDetailVendor.this, new GeocoderHandler());

        try {

            lat = GeocodingLocation.lattitude;
            longi = GeocodingLocation.longitude;
            gps = new LocationTrack(RaisedTruckLoadDetailVendor.this);
            if (gps.canGetLocation()) {
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();

                String lat = String.valueOf(latitude);
                String longi = String.valueOf(longitude);
                currentAddress = gps.getAdd();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        try {
            integrateMap();
        } catch (Exception e) {

        }

        try {

            /************************************/

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String start = simpleDateFormat.format(new Date());
            Date startDate = simpleDateFormat.parse(time1);
            Date endDate = simpleDateFormat.parse(start);
            Log.i("time1", "" + time1);
            Log.i("time2", "" + start);
            Log.i("time2", "" + endDate);

            long difference = startDate.getTime() - endDate.getTime();
            if (difference < 0) {
                Date dateMax = simpleDateFormat.parse("24:00");
                Date dateMin = simpleDateFormat.parse("00:00");
                difference = (dateMax.getTime() - startDate.getTime()) + (endDate.getTime() - dateMin.getTime());
            }
            int days = (int) (difference / (1000 * 60 * 60 * 24));
            int hours = (int) ((difference - (1000 * 60 * 60 * 24 * days)) / (1000 * 60 * 60));
            int min = (int) (difference - (1000 * 60 * 60 * 24 * days) - (1000 * 60 * 60 * hours)) / (1000 * 60);
            Log.e("log_tag", "Hours: " + hours + ", Mins: " + min);


            final int min1 = ((hours * 60) + min) * 60 * 1000;
            final int min2 = ((hours * 60) + min);
            Log.e("log_tag", "sec: " + min1);
            Log.e("log_tag", "sec: " + min2);
            textView.get(6).setVisibility(View.GONE);


            CountDownTimer countDownTimer = new CountDownTimer(min1, 1000) {
                public void onTick(long millisUntilFinished) {
                    textView.get(6).setVisibility(View.VISIBLE);
                    long millis = millisUntilFinished;
                    //Convert milliseconds into hour,minute and seconds
                    /*String hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis), TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));*/
                    String hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
                    textView.get(6).setText(hms);//set text

                    if (hms.equalsIgnoreCase("0")) {
                        textView.get(6).setText("TIME'S UP!!");
                        buttons.get(0).setVisibility(View.GONE);
                        buttons.get(1).setVisibility(View.GONE);
                    }
                }

                public void onFinish() {
                    textView.get(6).setText("TIME'S UP!!"); //On finish change timer text
                    buttons.get(0).setVisibility(View.GONE);
                    buttons.get(1).setVisibility(View.GONE);
                }
            }.start();

        } catch (Exception e) {
            e.printStackTrace();
        }


        buttons.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Order Rejected", Toast.LENGTH_SHORT).show();
            }
        });


        buttons.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogBuilder1 = new AlertDialog.Builder(v.getContext());
                inflater1 = LayoutInflater.from(v.getContext());
                dialogView1 = inflater1.inflate(R.layout.alert_bid_vendor, null);
                dialogBuilder1.setView(dialogView1);
                alertDialog1 = dialogBuilder1.create();
                alertDialog1.show();

                final EditText bid_edt = (EditText) dialogView1.findViewById(R.id.bid_edit);
                final Button submit = (Button) dialogView1.findViewById(R.id.submit);
                final Button cancel = (Button) dialogView1.findViewById(R.id.cancel);

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (validate(bid_edt)) {

                            String bid = bid_edt.getText().toString();
                            getorderBidded(RaisedTruckLoadDetailVendor.this, OrdrId, bid);
                            alertDialog1.dismiss();
                        }

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        alertDialog1.dismiss();

                    }
                });
            }
        });

    }

    private boolean validate(EditText editText) {
        if (editText.getText().toString().trim().length() >0) {
            return true;
        }
        editText.setError("Please Fill This");
        editText.requestFocus();
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DetectConnection.checkInternetConnection(getApplicationContext())){
           // getCheckCount();
        }
    }

    private void getCheckCount() {

        RequestParams requestParams = new RequestParams();
        requestParams.put("o_id", OrdrId);
        requestParams.put("c_id", userId);

        asyncHttpClient.post(TruckOrderCountURL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("success").equalsIgnoreCase("1")){

                    }else if (jsonObject.getString("success").equalsIgnoreCase("0")){

                    }else if (jsonObject.getString("success").equalsIgnoreCase("2")){

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(RaisedTruckLoadDetailVendor.this, "Server Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getorderBidded(final Context context, final String id, String bid) {

        String vendor_id = Common.getSavedUserData(RaisedTruckLoadDetailVendor.this,"userId");

        ApiInterface apiService = Api.getClient().create(ApiInterface.class);
        Call<StatusResponse> call = apiService.biddedOrder(vendor_id, id, bid);
        call.enqueue(new Callback<StatusResponse>() {
            @Override
            public void onResponse(Call<StatusResponse> call, Response<StatusResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RaisedTruckLoadDetailVendor.this, MainPage.class);
                        startActivity(intent);
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Toast.makeText(context, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onFailure(Call<StatusResponse> call, Throwable t) {


            }
        });


    }

    private void integrateMap() {

        mapFragment = new SupportMapFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.vendorMap, mapFragment).commit();
        Bundle args = new Bundle();
        args.putString("longitude", String.valueOf(latitude));
        args.putString("latitude", String.valueOf(longitude));
        mapFragment.setArguments(args);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                citylocation = new LatLng(latitude, longitude);

            }
        });

    }

    public class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();

                    locationAddress = bundle.getString("address");

                    Log.e("gps", "" + locationAddress + " ");
                    lat = GeocodingLocation.lattitude;
                    longi = GeocodingLocation.longitude;
                    citylocation1 = new LatLng(lat, longi);

                    try {
                        new DirectionFinder(RaisedTruckLoadDetailVendor.this, c_pickup_location, c_delivery_location).execute();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    break;
                default:
                    locationAddress = null;
            }
        }

    }

    @Override
    public void onDirectionFinderStart() {

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 7));
            BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.sourcelocation);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(icon)
                    .title("Source")
                    .snippet(route.startAddress)
                    .position(route.startLocation)));

            String s = new String(String.valueOf(route.startLocation));

            StringTokenizer tokens = new StringTokenizer(s, ",");
            String first = tokens.nextToken();
            start_lng = tokens.nextToken();

            String b = first;
            b = b.replaceFirst("lat/lng: ", "");

            start_lat = b.substring(1);
            start_lng = start_lng.substring(0, start_lng.length() - 1);
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.destlocation))
                    .title("Destination")
                    .snippet(route.endAddress)
                    .position(route.endLocation)));

            ArrayList<LatLng> points = new ArrayList<LatLng>();
            for (int i = 0; i < route.points.size(); i++) {
                points.add(route.points.get(i));
            }
            listLatLng.addAll(points);

           /* PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLACK).
                    width(10);
            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));*/
            PolylineOptions lineOptions = new PolylineOptions();
            lineOptions.width(10);
            lineOptions.color(Color.BLACK);
            lineOptions.startCap(new SquareCap());
            lineOptions.endCap(new SquareCap());
            lineOptions.jointType(JointType.ROUND);
            blackPolyLine = mMap.addPolyline(lineOptions);

            PolylineOptions greyOptions = new PolylineOptions();
            greyOptions.width(10);
            greyOptions.color(Color.GRAY);
            greyOptions.startCap(new SquareCap());
            greyOptions.endCap(new SquareCap());
            greyOptions.jointType(JointType.ROUND);
            greyPolyLine = mMap.addPolyline(greyOptions);

            animatePolyLine();

        }
    }

    public void addOverlay(LatLng place) {

        GroundOverlay groundOverlay = mMap.addGroundOverlay(new
                GroundOverlayOptions()
                .position(place, 100)
                .transparency(0.5f)
                .zIndex(3));
        // .image(BitmapDescriptorFactory.fromBitmap(drawableToBitmap(getDrawable(R.drawable.map_overlay)))));

        startOverlayAnimation(groundOverlay);
    }


    private void startOverlayAnimation(final GroundOverlay groundOverlay) {

        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator vAnimator = ValueAnimator.ofInt(0, 100);
        vAnimator.setRepeatCount(ValueAnimator.INFINITE);
        vAnimator.setRepeatMode(ValueAnimator.RESTART);
        vAnimator.setInterpolator(new LinearInterpolator());
        vAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final Integer val = (Integer) valueAnimator.getAnimatedValue();
                groundOverlay.setDimensions(val);

            }
        });

        ValueAnimator tAnimator = ValueAnimator.ofFloat(0, 1);
        tAnimator.setRepeatCount(ValueAnimator.INFINITE);
        tAnimator.setRepeatMode(ValueAnimator.RESTART);
        tAnimator.setInterpolator(new LinearInterpolator());
        tAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float val = (Float) valueAnimator.getAnimatedValue();
                groundOverlay.setTransparency(val);
            }
        });

        animatorSet.setDuration(3000);
        animatorSet.playTogether(vAnimator, tAnimator);
        animatorSet.start();
    }

    private void animatePolyLine() {

        ValueAnimator animator = ValueAnimator.ofInt(0, 100);
        animator.setDuration(1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {

                List<LatLng> latLngList = blackPolyLine.getPoints();
                int initialPointSize = latLngList.size();
                int animatedValue = (int) animator.getAnimatedValue();
                int newPoints = (animatedValue * listLatLng.size()) / 100;

                if (initialPointSize < newPoints) {
                    latLngList.addAll(listLatLng.subList(initialPointSize, newPoints));
                    blackPolyLine.setPoints(latLngList);
                }

            }
        });

        animator.addListener(polyLineAnimationListener);
        animator.start();

    }

    Animator.AnimatorListener polyLineAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animator) {

            addMarker(listLatLng.get(listLatLng.size() - 1));
        }

        @Override
        public void onAnimationEnd(Animator animator) {

            List<LatLng> blackLatLng = blackPolyLine.getPoints();
            List<LatLng> greyLatLng = greyPolyLine.getPoints();

            greyLatLng.clear();
            greyLatLng.addAll(blackLatLng);
            blackLatLng.clear();

            blackPolyLine.setPoints(blackLatLng);
            greyPolyLine.setPoints(greyLatLng);

            blackPolyLine.setZIndex(2);

            animator.start();
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {


        }
    };

    private void addMarker(LatLng destination) {

        MarkerOptions options = new MarkerOptions();
        options.position(destination);
        //options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        //mMap.addMarker(options);
    }

}