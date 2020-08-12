package com.graminvikreta_transporter.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Extra.Utility;
import com.graminvikreta_transporter.Model.Order;
import com.graminvikreta_transporter.Model.Truck;
import com.graminvikreta_transporter.R;
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;


public class FullScreenTruckDetails extends BottomSheetDialogFragment {

    BottomSheetBehavior mBehavior;
    AppBarLayout app_bar_layout;
    private String UpdateTruckURL  = "http://graminvikreta.com/androidApp/Transporter/UpdateVendorTruckDetails.php";
    public static String TruckURL="http://graminvikreta.com/androidApp/Transporter/getTruck.php";
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    String TruckTypeValue, truckId, truckImages, trucknumber, truckType, trucktyres;
    RequestQueue requestQueue;
    JsonObjectRequest objectRequest;
    List<Truck> truckList = new ArrayList<>();
    public String userId;
    ImageView truckimageView;
    EditText edttrucknumber, edttrucktyres;
    Spinner trucktypespin;
    TextView save;
    Pattern trucknumberpattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}");
    Matcher matcher;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public Bitmap bitmap;
    private String userChoosenTask, imageString;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        final View view = View.inflate(getContext(), R.layout.edit_truck_details_vendor, null);

        dialog.setContentView(view);
        mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        dialog.setCancelable(false);

        app_bar_layout = (AppBarLayout) view.findViewById(R.id.app_bar_layout);
        edttrucknumber = (EditText) view.findViewById(R.id.trucknumber);
        edttrucktyres = (EditText) view.findViewById(R.id.trucktyres);
        trucktypespin = (Spinner) view.findViewById(R.id.trucktypespin);
        truckimageView = (ImageView) view.findViewById(R.id.imageView);
        save = (TextView) view.findViewById(R.id.save);

        mBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    showView(app_bar_layout, getActionBarSize());
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == newState) {
                    showView(app_bar_layout, getActionBarSize());
                }

                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        ((ImageButton) view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (DetectConnection.checkInternetConnection(getActivity())){

                    try {
                        if (truckimageView.getDrawable() == null) {
                            imageString = "";
                        } else {
                            imageString = getStringImage(bitmap);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (validate(edttrucknumber) && validate(edttrucktyres)) {
                        matcher = trucknumberpattern.matcher(edttrucknumber.getText().toString());
                        if (matcher.matches()) {

                            ProgressDialog progressDialog = new ProgressDialog(getActivity());
                            progressDialog.setMessage("Loading...");
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            progressDialog.show();
                            progressDialog.setCancelable(false);

                            RequestParams requestParams = new RequestParams();
                            requestParams.put("trucktyres", edttrucktyres.getText().toString().trim());
                            requestParams.put("truckId", truckId);
                            requestParams.put("trucktype", TruckTypeValue);
                            requestParams.put("imageString", imageString);
                            requestParams.put("truckImages", truckImages);
                            requestParams.put("userId", MainPage.userId);

                            asyncHttpClient.post(UpdateTruckURL, requestParams, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                    String s = new String(responseBody);
                                    try {
                                        JSONObject jsonObject = new JSONObject(s);
                                        if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), "Update Done", Toast.LENGTH_SHORT).show();
                                            ((MainPage) getActivity()).removeCurrentFragmentAndMoveBack();
                                            ((MainPage) getActivity()).loadFragment(new Profile(), true);

                                        } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                                            progressDialog.dismiss();
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                    progressDialog.dismiss();
                                    dialog.dismiss();
                                    Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_SHORT).show();
                                }
                            });

                        } else {
                            edttrucknumber.requestFocus();
                            edttrucknumber.setError("Invalid Truck Number");
                        }
                    }
                }else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }
        });
        requestQueue= Volley.newRequestQueue(getActivity());
        objectRequest=new JsonObjectRequest(Request.Method.GET, TruckURL, null, new com.android.volley.Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray jsonArray=response.getJSONArray("success");

                    if (jsonArray.length()==0){

                    }else {
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject = jsonArray.getJSONObject(i);

                            Truck material = new Truck();
                            material.setType_id(jsonObject.getString("truck_id"));
                            material.setType_name(jsonObject.getString("truck_type"));
                            truckList.add(material);

                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(objectRequest);


        final ArrayAdapter ctype = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, truckList);
        ctype.setDropDownViewResource(android.R.layout.simple_list_item_1);
        trucktypespin.setAdapter(ctype);
        trucktypespin.setSelection(getIndex(trucktypespin, TruckTypeValue));


        trucktypespin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                TruckTypeValue = String.valueOf(adapterView.getItemAtPosition(position));
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

        try{

            edttrucknumber.setSelection(edttrucknumber.getText().toString().length());
            edttrucktyres.setSelection(edttrucktyres.getText().toString().length());
            edttrucknumber.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            edttrucknumber.setText(trucknumber);
            edttrucktyres.setText(trucktyres);

        }catch (Exception e){
            e.printStackTrace();
        }

        truckimageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showPictureDialog();

            }
        });


        return dialog;
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

        truckimageView.setImageBitmap(bitmap);
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
        truckimageView.setImageBitmap(bitmap);
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        return encodedImage;
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

    private boolean validate(EditText editText) {
        if (editText.getText().toString().trim().length() > 0) {
            return true;
        }
        editText.setError("Please Fill This");
        editText.requestFocus();
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void hideView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = 0;
        view.setLayoutParams(params);
    }

    private void showView(View view, int size) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = size;
        view.setLayoutParams(params);
    }

    private int getActionBarSize() {
        final TypedArray styledAttributes = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int size = (int) styledAttributes.getDimension(0, 0);
        return size;
    }

    public void setId(String userId, String truckImages, String trucknumber, String truckType, String trucktyres, String truckId) {

        this.userId = userId;
        this.truckImages = truckImages;
        this.trucknumber = trucknumber;
        this.TruckTypeValue = truckType;
        this.trucktyres = trucktyres;
        this.truckId = truckId;

    }
}
