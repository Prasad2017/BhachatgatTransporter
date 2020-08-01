package com.graminvikreta_transporter.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.graminvikreta_transporter.Activity.Login;
import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Adapter.MyOrdersAdapter;
import com.graminvikreta_transporter.Adapter.TruckLoadOrderAdapter;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Model.AllList;
import com.graminvikreta_transporter.Model.BidData;
import com.graminvikreta_transporter.Model.Order;
import com.graminvikreta_transporter.Model.RaisedOrderData;
import com.graminvikreta_transporter.R;
import com.graminvikreta_transporter.Retrofit.Api;
import com.graminvikreta_transporter.Retrofit.ApiInterface;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Home extends Fragment {

    View view;
    @BindView(R.id.simpleListView)
    RecyclerView recyclerView;
    @BindView(R.id.acceptsimpleListView)
    RecyclerView acceptsimpleListView;
    @BindViews({R.id.txtraisedorder, R.id.txtacceptorder})
    List<TextView> textViews;
    @BindViews({R.id.raisedordercardview, R.id.acceptordercardview})
    List<CardView> cardViews;
    public static SwipeRefreshLayout swipeRefreshLayout;
    TruckLoadOrderAdapter truckLoadOrderAdapter;
    SupportMapFragment mapFragment;
    List<Order> truckList = new ArrayList<>();
    public String getProfile = "http://graminvikreta.com/androidApp/Transporter/GetProfile.php";
    public String AddLocationURl = "http://graminvikreta.com/androidApp/Transporter/AddLocation.php";
    // location last updated time
    private String mLastUpdateTime;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;
    private static final int REQUEST_CHECK_SETTINGS = 100;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    MyOrdersAdapter myOrdersAdapter;
    Double userLatitude, userLogitude;
    public List<RaisedOrderData> clientList;
    public static BidData mybiddingResponse;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.simpleSwipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (DetectConnection.checkInternetConnection(getActivity())) {
                      getOrderRaised();
                       getProfile();
                       getBidOrderList();
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    Toast.makeText(getActivity(), "Internet Not Available", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        init();
        requestPermission();

        return view;

    }

    private void getProfile() {

        RequestParams requestParams = new RequestParams();
        requestParams.put("userId", MainPage.userId);

        asyncHttpClient.get(getProfile, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("success");
                    if (jsonArray.length()==0){
                        //getLocation();
                    }else {
                        for (int i=0;i<jsonArray.length();i++){

                            jsonObject = jsonArray.getJSONObject(i);
                            MainPage.name = jsonObject.getString("first_name")+" "+ jsonObject.getString("last_name");
                            MainPage.contact = jsonObject.getString("contact");
                            MainPage.email = jsonObject.getString("email");
                            MainPage.address = jsonObject.getString("address");
                            MainPage.pancardNumber = jsonObject.getString("pancard_number");
                            MainPage.gstNumber = jsonObject.getString("gst_number");

                           // getLocation();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                //TastyToast.makeText(getActivity(),"Server Error", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
               // getLocation();
            }
        });

    }

    private void getLocation() {

        RequestParams requestParams = new RequestParams();
        requestParams.put("userId", MainPage.userId);
        requestParams.put("userLatitude", userLatitude+"");
        requestParams.put("userLongitude", userLogitude+"");

        asyncHttpClient.post(AddLocationURl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("success").equalsIgnoreCase("1")){
                        Log.e("Location","Successfully Location");
                        getBidOrderList();
                    }else if (jsonObject.getString("success").equalsIgnoreCase("0")){
                        Log.e("Location",""+jsonObject.getString("message"));
                        getBidOrderList();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("Location","Server Error");
                getBidOrderList();
            }
        });

    }

    public void onStart() {
        super.onStart();
        Log.d("onStart", "called");
        MainPage.title.setVisibility(View.GONE);
        ((MainPage) getActivity()).lockUnlockDrawer(DrawerLayout.LOCK_MODE_UNLOCKED);
        MainPage.drawerLayout.closeDrawers();
        if (DetectConnection.checkInternetConnection(getActivity())){
            if (MainPage.userId.equalsIgnoreCase("")) {
                startActivity(new Intent(getActivity(), Login.class));
            }else {
                getOrderRaised();
                getBidOrderList();
                getProfile();
            }
        }else {
            Toast.makeText(getActivity(), "Internet Not Available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public void onResume() {
        super.onResume();
        startLocationUpdates();
    }

    public void stopLocationUpdates() {
        // Removing location updates
        mFusedLocationClient
                .removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                    }
                });
    }


    private void getBidOrderList() {

       try{

           ApiInterface apiInterface = Api.getClient().create(ApiInterface.class);
           Call<BidData> call = apiInterface.getBidding(MainPage.userId);
           call.enqueue(new Callback<BidData>() {
               @Override
               public void onResponse(Call<BidData> call, Response<BidData> response) {

                   if (response.body().getSuccess().equalsIgnoreCase("true")) {
                       try {

                           mybiddingResponse = response.body();
                           LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                           linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                           acceptsimpleListView.setLayoutManager(linearLayoutManager);
                           myOrdersAdapter = new MyOrdersAdapter(getActivity(), mybiddingResponse.getOrderdata());
                           acceptsimpleListView.setAdapter(myOrdersAdapter);
                           myOrdersAdapter.notifyDataSetChanged();
                           acceptsimpleListView.setHasFixedSize(true);
                           cardViews.get(1).setVisibility(View.VISIBLE);
                       } catch (Exception e) {
                           cardViews.get(1).setVisibility(View.GONE);
                       }
                   }else {
                       cardViews.get(1).setVisibility(View.GONE);
                   }

               }

               @Override
               public void onFailure(Call<BidData> call, Throwable t) {
                   cardViews.get(1).setVisibility(View.GONE);
               }
           });

       }catch (Exception e){
           e.printStackTrace();
       }

    }

    private void getOrderRaised() {

        recyclerView.clearOnScrollListeners();
        truckList.clear();

        ApiInterface apiInterface = Api.getClient().create(ApiInterface.class);
        Call<AllList> call = apiInterface.raisedOrderList(MainPage.userId);
        call.enqueue(new Callback<AllList>() {
            @Override
            public void onResponse(Call<AllList> call, Response<AllList> response) {

                AllList allList = response.body();
                truckList = allList.getGetdatas();
                if (truckList.size()<1){
                    textViews.get(0).setVisibility(View.GONE);
                    cardViews.get(0).setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Currently there is no order", Toast.LENGTH_SHORT).show();
                } else {
                    //Set Adapter...
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    truckLoadOrderAdapter = new TruckLoadOrderAdapter(truckList, getActivity());
                    recyclerView.setAdapter(truckLoadOrderAdapter);
                    truckLoadOrderAdapter.notifyDataSetChanged();
                    recyclerView.setHasFixedSize(true);

                    textViews.get(0).setVisibility(View.VISIBLE);
                    cardViews.get(0).setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onFailure(Call<AllList> call, Throwable t) {
                textViews.get(0).setVisibility(View.GONE);
                cardViews.get(0).setVisibility(View.GONE);
            }
        });

    }

    private void init() {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
        mSettingsClient = LocationServices.getSettingsClient(getActivity());

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // location is received
                mCurrentLocation = locationResult.getLastLocation();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                updateLocationUI();
            }
        };

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
    }

    private void updateLocationUI() {
        if (mCurrentLocation != null) {

            userLatitude = mCurrentLocation.getLatitude();
            userLogitude = mCurrentLocation.getLongitude();

        }
    }

    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i("", "All location settings are satisfied.");

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        updateLocationUI();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.e("", "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.e("", "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e("", errorMessage);
                        }

                        updateLocationUI();
                    }
                });
    }

    private void requestPermission() {

        Dexter.withActivity(getActivity())
                .withPermissions(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {

                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }

        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}