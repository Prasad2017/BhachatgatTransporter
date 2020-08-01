package com.bhachtgattransporter.Fragment;

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

import com.bhachtgattransporter.Activity.MainPage;
import com.bhachtgattransporter.Adapter.MyOrdersAdapter;
import com.bhachtgattransporter.Adapter.TruckLoadOrderAdapter;
import com.bhachtgattransporter.Extra.DetectConnection;
import com.bhachtgattransporter.Model.Order;
import com.bhachtgattransporter.Model.RaisedOrderData;
import com.bhachtgattransporter.Model.StockData;
import com.bhachtgattransporter.R;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public String TruckOrder = "http://softmate.in/androidApp/Qsar/DeliveryBoy/RaisedTruckLoad.php";
    public String getProfile = "http://softmate.in/androidApp/Qsar/DeliveryBoy/GetProfile.php";
    public String AddLocationURl = "http://softmate.in/androidApp/Qsar/DeliveryBoy/AddLocation.php";
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
    public List<RaisedOrderData> movieList = new ArrayList();
    public static StockData mybiddingResponse;


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
                    //    getProfile();
                    //  StockList();
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

    private void getOrderRaised() {

        recyclerView.clearOnScrollListeners();
        truckList.clear();

        RequestParams requestParams = new RequestParams();
        requestParams.put("vendorId", MainPage.userId);

        asyncHttpClient.get(TruckOrder, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("getdatas");
                    if (jsonArray.length()==0){

                        textViews.get(0).setVisibility(View.GONE);
                        cardViews.get(0).setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "Currently there is no order", Toast.LENGTH_SHORT).show();

                    }else {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);


                            /*Truck truck = new Truck();
                            truck.setId(jsonObject.getString("id"));
                            truck.setUser_name(jsonObject.getString("user_name"));
                            truck.setOrder_title(jsonObject.getString("order_title"));
                            truck.setDate(jsonObject.getString("date"));
                            truck.setTime(jsonObject.getString("time"));
                            truck.setEnd_time(jsonObject.getString("end_time"));
                            truck.setSource_contact(jsonObject.getString("source_contact"));
                            truck.setC_pickup_location(jsonObject.getString("c_pickup_location"));
                            truck.setC_delivery_location(jsonObject.getString("c_delivery_location"));
                            truck.setMovers_date(jsonObject.getString("movers_date"));
                            truck.setTruck_type(jsonObject.getString("truck_type"));
                            truck.setMaterial(jsonObject.getString("material_type"));
                            truck.setTruck_weight(jsonObject.getString("tot_weight"));
                            truck.setRemark(jsonObject.getString("add_note"));

                            truckList.add(truck);*/

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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                textViews.get(0).setVisibility(View.GONE);
                cardViews.get(0).setVisibility(View.GONE);
                // TastyToast.makeText(getActivity(), "Order Server Error", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
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