package com.graminvikreta_transporter.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.agik.AGIKSwipeButton.Controller.OnSwipeCompleteListener;
import com.agik.AGIKSwipeButton.View.Swipe_Button_View;
import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Adapter.AwardordersAdapter;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Extra.Utility;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class TruckLoadDetails extends Fragment {

    View view;
    @BindViews({R.id.name, R.id.contact, R.id.pickuplocation, R.id.deliverylocation, R.id.productName, R.id.quantity})
    List<TextView> textViews;
    @BindViews({R.id.swipeButtonView, R.id.swipeButtonView1})
    List<Swipe_Button_View> swipeButtonView;
    String order_id;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public String OrderDetailsUrl = "http://graminvikreta.com/androidApp/Transporter/TruckOrderDetails.php";
    public String ReasonDeatilsUrl = "http://graminvikreta.com/androidApp/Transporter/ReasonDeatils.php";
    public static String VendorTruckListURL="http://graminvikreta.com/androidApp/Transporter/VendorTruckList.php";
    public static String msgsend="http://graminvikreta.com/androidApp/Supplier/sendSMS.php";
    String order_status = null;
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

                final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
                final LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View f = inflater.inflate(R.layout.driver_dialog_vendor, null);
                dialogBuilder.setCancelable(false);
                TextView submit = (TextView) f.findViewById(R.id.submit);
                final ImageView close = (ImageView) f.findViewById(R.id.close);
                driverimageView = (ImageView) f.findViewById(R.id.imageView);
                final EditText editText1 = (EditText) f.findViewById(R.id.edit1);
                final EditText editText2 = (EditText) f.findViewById(R.id.edit2);
                final Spinner spinner = (Spinner) f.findViewById(R.id.truckspinner);

                try{

                    RequestParams requestParams = new RequestParams();
                    requestParams.put("userId", MainPage.userId);

                    asyncHttpClient.get(VendorTruckListURL, requestParams, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            String s = new String(responseBody);
                            try {
                                JSONObject jsonObject = new JSONObject(s);
                                JSONArray jsonArray = jsonObject.getJSONArray("success");
                                if (jsonArray.length() == 0) {
                                    Toast.makeText(getActivity(), "No Truck Found", Toast.LENGTH_SHORT).show();
                                } else {
                                    truck_id = new String[jsonArray.length()];
                                    truck_number = new String[jsonArray.length()];
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        jsonObject = jsonArray.getJSONObject(i);
                                        truck_id[i] = jsonObject.getString("truck_dt_id");
                                        truck_number[i] = jsonObject.getString("trucknumber");
                                    }

                                    ArrayAdapter<String> truckadapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, truck_number);
                                    truckadapter.setDropDownViewResource(android.R.layout.simple_list_item_checked);
                                    spinner.setAdapter(truckadapter);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                        }
                    });

                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                            trucknumber = truck_number[position];

                            try {
                                ((TextView) adapterView.getChildAt(0)).setTextColor(getActivity().getResources().getColor(R.color.black));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                }catch (Exception e){
                    e.printStackTrace();
                }

                driverimageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPictureDialog();
                    }
                });

                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (driverimageView.getDrawable() == null) {
                            Toasty.normal(getActivity(), "Please Select Driver Image", Toasty.LENGTH_SHORT).show();
                        } else {
                            if (validate(editText1) && validate(editText2)) {


                                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                progressDialog.setMessage("Loading...");
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progressDialog.show();
                                progressDialog.setCancelable(false);

                                imageString = getStringImage(bitmap);

                                RequestParams requestParams = new RequestParams();
                                requestParams.put("drivername", editText1.getText().toString());
                                requestParams.put("drivermobile", editText2.getText().toString());
                                requestParams.put("trucknumber", trucknumber);
                                requestParams.put("driverimage", imageString);
                                requestParams.put("order_id", order_id);
                                requestParams.put("order_status", order_status);
                                requestParams.put("vendorId", MainPage.userId);


                                asyncHttpClient.post("http://graminvikreta.com/androidApp/Transporter/AddDriverDetails.php", requestParams, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        String s = new String(responseBody);

                                        try {
                                            JSONObject jsonObject = new JSONObject(s);
                                            if (jsonObject.getString("success").equalsIgnoreCase("1")) {

                                                progressDialog.dismiss();

                                                String message = "Your order has been order delivered by "+MainPage.name+"( "+MainPage.contact+" )"+" successfully.\n Thank you.";
                                                String encoded_message= URLEncoder.encode(message);

                                                ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                                progressDialog.setMessage("Loading...");
                                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                                progressDialog.show();
                                                progressDialog.setCancelable(false);

                                                RequestParams requestParams=new RequestParams();
                                                requestParams.put("number", MainPage.contact);
                                                requestParams.put("message",encoded_message);

                                                asyncHttpClient.get(msgsend, requestParams, new AsyncHttpResponseHandler() {
                                                    @Override
                                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                        String s=new String(responseBody);
                                                        try {
                                                            JSONObject jsonObject=new JSONObject(s);

                                                            if (jsonObject.getString("success").equals("1"))
                                                            {
                                                                progressDialog.dismiss();
                                                                swipeButtonView.get(1).setVisibility(View.VISIBLE);
                                                                swipeButtonView.get(0).setVisibility(View.GONE);
                                                                getConfirmPickup(order_id, order_status);
                                                                dialogBuilder.dismiss();
                                                            }else {
                                                                progressDialog.dismiss();
                                                                swipeButtonView.get(1).setVisibility(View.VISIBLE);
                                                                swipeButtonView.get(0).setVisibility(View.GONE);

                                                                dialogBuilder.dismiss();
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                    @Override
                                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                                        progressDialog.dismiss();
                                                        swipeButtonView.get(1).setVisibility(View.VISIBLE);
                                                        swipeButtonView.get(0).setVisibility(View.GONE);

                                                        dialogBuilder.dismiss();
                                                    }
                                                });
                                            } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {

                                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                });

                dialogBuilder.setView(f);
                dialogBuilder.show();

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
                            textViews.get(3).setText(jsonObject.getString("billing_address").equals("null")?"-":jsonObject.getString("billing_address"));
                            textViews.get(4).setText(jsonObject.getString("product_name").equals("")?"0":jsonObject.getString("product_name"));
                            textViews.get(5).setText(jsonObject.getString("quantity").equals("")?"0":jsonObject.getString("quantity"));

                            if(jsonObject.getString("payment_status").equalsIgnoreCase("paid")){
                                swipeButtonView.get(0).setVisibility(View.GONE);
                                swipeButtonView.get(1).setVisibility(View.VISIBLE);
                            } else {
                                swipeButtonView.get(0).setVisibility(View.GONE);
                                swipeButtonView.get(1).setVisibility(View.GONE);
                            }

                            if(jsonObject.getString("order_status").equalsIgnoreCase("order_complete")){
                                swipeButtonView.get(0).setVisibility(View.GONE);
                                swipeButtonView.get(1).setVisibility(View.GONE);
                            } else {
                                swipeButtonView.get(0).setVisibility(View.GONE);
                                swipeButtonView.get(1).setVisibility(View.VISIBLE);
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

    private void showPictureDialog() {
        final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getActivity());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask ="Take Photo";
                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bitmap = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        driverimageView.setImageBitmap(bitmap);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        bitmap=null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        driverimageView.setImageBitmap(bitmap);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        return encodedImage;
    }


    private boolean validate(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            return true;
        }
        editText.setError("Please Fill This");
        editText.requestFocus();
        return false;
    }

}