package com.graminvikreta_transporter.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.graminvikreta_transporter.Activity.Login;
import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Adapter.TruckDetailsAdapter;
import com.graminvikreta_transporter.Extra.Config;
import com.graminvikreta_transporter.Extra.DetectConnection;
import com.graminvikreta_transporter.Extra.Utility;
import com.graminvikreta_transporter.Model.TruckDetails;
import com.graminvikreta_transporter.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;
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

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class Profile extends Fragment {

    View view;
    @BindViews({R.id.fname, R.id.lname, R.id.email, R.id.mobile, R.id.pancard, R.id.gstnumber, R.id.address})
    List<EditText> editTexts;
    @BindViews({R.id.detailsLinear, R.id.truckLinear})
    List<LinearLayout> linearLayouts;
    @BindViews({R.id.basic, R.id.truck})
    List<TextView> textViews;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    List<TruckDetails> truckDetailsList = new ArrayList<>();
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    TruckDetailsAdapter adapter;
    public static String VendorTruckDetailsURL="http://graminvikreta.com/androidApp/Transporter/VendorTruckDetails.php";
    public static String VendorTruckListURL="http://graminvikreta.com/androidApp/Transporter/VendorTruckList.php";
    public String getProfile = "http://graminvikreta.com/androidApp/Transporter/getProfile.php";
    public String UpdateProfileURl = "http://graminvikreta.com/androidApp/Transporter/UpdateProfile.php";
    public static final String msgSend="http://graminvikreta.com/androidApp/Supplier/sendSMS.php";

    Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    Pattern trucknumberpattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}");
    Pattern gstpattern = Pattern.compile("[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}");
    String convert, code, OTP, TruckTypeValue, TruckCapacityValue;
    Matcher matcher;
    ImageView truckimageView;
    protected ArrayList<String> selectedMaterial = new ArrayList<String>();
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    public Bitmap bitmap;
    private String userChoosenTask, imageString, trucknumber;
    RequestQueue requestQueue;
    JsonObjectRequest objectRequest;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile_vendor, container, false);
        ButterKnife.bind(this, view);
        MainPage.title.setText("Profile");

        editTexts.get(0).setSelection(editTexts.get(0).getText().toString().length());
        editTexts.get(1).setSelection(editTexts.get(1).getText().toString().length());
        editTexts.get(2).setSelection(editTexts.get(2).getText().toString().length());
        editTexts.get(3).setSelection(editTexts.get(3).getText().toString().length());
        editTexts.get(4).setSelection(editTexts.get(4).getText().toString().length());
        editTexts.get(5).setSelection(editTexts.get(5).getText().toString().length());
        editTexts.get(6).setSelection(editTexts.get(6).getText().toString().length());

        editTexts.get(0).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editTexts.get(1).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        editTexts.get(4).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        editTexts.get(5).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        editTexts.get(6).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

        editTexts.get(0).setFilters(new InputFilter[]{getEditTextFilter()});
        editTexts.get(1).setFilters(new InputFilter[]{getEditTextFilter()});

        return view;
    }

    @OnClick({R.id.basic, R.id.truck, R.id.addtruck, R.id.update})
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.basic:

                textViews.get(0).setBackground(getActivity().getResources().getDrawable(R.drawable.carborder));
                textViews.get(1).setBackground(getActivity().getResources().getDrawable(R.drawable.bikeborder));
                textViews.get(0).setTextColor(getActivity().getResources().getColor(R.color.white));
                textViews.get(1).setTextColor(getActivity().getResources().getColor(R.color.black));
                recyclerView.setVisibility(View.GONE);
                linearLayouts.get(1).setVisibility(View.GONE);
                linearLayouts.get(0).setVisibility(View.VISIBLE);

                break;

            case R.id.truck:

                textViews.get(0).setBackground(getActivity().getResources().getDrawable(R.drawable.reversecarborder));
                textViews.get(1).setBackground(getActivity().getResources().getDrawable(R.drawable.reversebikeborder));
                textViews.get(0).setTextColor(getActivity().getResources().getColor(R.color.black));
                textViews.get(1).setTextColor(getActivity().getResources().getColor(R.color.white));
                getTruckDetails();
                recyclerView.setVisibility(View.VISIBLE);
                linearLayouts.get(0).setVisibility(View.GONE);
                linearLayouts.get(1).setVisibility(View.VISIBLE);

                break;

            case R.id.addtruck:

                final AlertDialog dialogBuilder = new AlertDialog.Builder(getActivity()).create();
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                View viewf = layoutInflater.inflate(R.layout.truck_details_vendor, null);
                dialogBuilder.setCancelable(false);
                final EditText edttrucknumber = (EditText) viewf.findViewById(R.id.trucknumber);
                final EditText edttruckweight = (EditText) viewf.findViewById(R.id.truckweight);
                final EditText edttruckheight = (EditText) viewf.findViewById(R.id.truckheight);
                final EditText edttrucklength = (EditText) viewf.findViewById(R.id.trucklength);
                //  final EditText edttruckcapacity = (EditText) viewf.findViewById(R.id.truckcapacity);
                final EditText edttrucktyres = (EditText) viewf.findViewById(R.id.trucktyres);
                final ImageView btn_close = (ImageView) viewf.findViewById(R.id.btn_close);
                Spinner trucktypespin = (Spinner) viewf.findViewById(R.id.trucktypespin);
                Spinner truckcapacityspin = (Spinner) viewf.findViewById(R.id.truckcapacityspin);
                truckimageView = (ImageView) viewf.findViewById(R.id.imageView);
                final TextView save = (TextView) viewf.findViewById(R.id.save);

                btn_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                });

                truckimageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPictureDialog();
                    }
                });

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (truckimageView.getDrawable() == null) {
                            Toasty.normal(getActivity(), "Please Select RC Book Image", Toasty.LENGTH_SHORT).show();
                        } else {
                                if (validate(edttrucknumber) && validate(edttrucktyres)) {
                                    matcher = trucknumberpattern.matcher(edttrucknumber.getText().toString().trim());
                                    if (matcher.matches()) {

                                        imageString = getStringImage(bitmap);

                                        ProgressDialog progressDialog = new ProgressDialog(getActivity());
                                        progressDialog.setMessage("Loading...");
                                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        progressDialog.show();
                                        progressDialog.setCancelable(false);

                                        RequestParams requestParams = new RequestParams();
                                        requestParams.put("trucknumber", edttrucknumber.getText().toString());
                                        requestParams.put("truckcapacity", TruckCapacityValue);
                                        requestParams.put("trucktyres", edttrucktyres.getText().toString());
                                        requestParams.put("truckimage", imageString);
                                        requestParams.put("trucktype", TruckTypeValue);
                                        requestParams.put("userId", MainPage.userId);

                                        asyncHttpClient.post(VendorTruckDetailsURL, requestParams, new AsyncHttpResponseHandler() {
                                            @Override
                                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                                String s = new String(responseBody);
                                                try {
                                                    JSONObject jsonObject = new JSONObject(s);
                                                    if (jsonObject.getString("success").equalsIgnoreCase("1")) {
                                                        progressDialog.dismiss();
                                                        dialogBuilder.dismiss();
                                                        getTruckDetails();
                                                    } else if (jsonObject.getString("success").equalsIgnoreCase("0")) {
                                                        progressDialog.dismiss();
                                                        dialogBuilder.dismiss();
                                                    }
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            @Override
                                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                             progressDialog.dismiss();
                                              dialogBuilder.dismiss();
                                            }
                                        });
                                    } else {
                                        edttrucknumber.requestFocus();
                                        edttrucknumber.setError("Invalid Truck Number");
                                    }
                                }

                        }
                    }
                });

                dialogBuilder.setView(viewf);
                dialogBuilder.show();

                break;

            case R.id.update:
                if (DetectConnection.checkInternetConnection(getActivity())){

                    if (editTexts.get(5).getText().toString().trim().equalsIgnoreCase("")){

                        if (validate(editTexts.get(0)) && Config.validateEmail(editTexts.get(2), getActivity()) && validate(editTexts.get(3)) && validate(editTexts.get(4))
                                && validate(editTexts.get(6))) {
                            matcher = pattern.matcher(editTexts.get(4).getText().toString());
                            if (matcher.matches()) {
                                Update();
                            } else {
                                editTexts.get(4).requestFocus();
                                editTexts.get(4).setError("Invalid PAN Number");
                            }
                        }

                    }else {

                        if (validate(editTexts.get(0)) && Config.validateEmail(editTexts.get(2), getActivity()) && validate(editTexts.get(3)) && validate(editTexts.get(4))
                                && validate(editTexts.get(5)) && validate(editTexts.get(6))) {
                            matcher = pattern.matcher(editTexts.get(4).getText().toString());
                            if (matcher.matches()) {
                                matcher = gstpattern.matcher(editTexts.get(5).getText().toString());
                                if (matcher.matches()) {
                                    Update();
                                } else {
                                    editTexts.get(5).requestFocus();
                                    editTexts.get(5).setError("Invalid GST Number");
                                }
                            } else {
                                editTexts.get(4).requestFocus();
                                editTexts.get(4).setError("Invalid PAN Number");
                            }
                        }
                    }

                }else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void Update() {

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        RequestParams requestParams = new RequestParams();
        requestParams.put("id", MainPage.userId);
        requestParams.put("first_name", editTexts.get(0).getText().toString());
        requestParams.put("last_name", editTexts.get(1).getText().toString());
        requestParams.put("email", editTexts.get(2).getText().toString());
        requestParams.put("contact", editTexts.get(3).getText().toString());
        requestParams.put("pancard_card_number", editTexts.get(4).getText().toString());
        requestParams.put("gst_number", editTexts.get(5).getText().toString());
        requestParams.put("address", editTexts.get(6).getText().toString());

        asyncHttpClient.post(UpdateProfileURl, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    if (jsonObject.getString("success").equalsIgnoreCase("1")){
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Update Done", Toast.LENGTH_SHORT).show();
                        getProfile();
                    }else if (jsonObject.getString("success").equalsIgnoreCase("0")){
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), "Update Fail", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(getActivity(),"Server Error", Toast.LENGTH_SHORT).show();
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


    public static InputFilter getEditTextFilter() {
        return new InputFilter() {

            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                boolean keepOriginal = true;
                StringBuilder sb = new StringBuilder(end - start);
                for (int i = start; i < end; i++) {
                    char c = source.charAt(i);
                    if (isCharAllowed(c)) // put your condition here
                        sb.append(c);
                    else
                        keepOriginal = false;
                }
                if (keepOriginal)
                    return null;
                else {
                    if (source instanceof Spanned) {
                        SpannableString sp = new SpannableString(sb);
                        TextUtils.copySpansFrom((Spanned) source, start, sb.length(), null, sp, 0);
                        return sp;
                    } else {
                        return sb;
                    }
                }
            }

            private boolean isCharAllowed(char c) {
                Pattern ps = Pattern.compile("^[a-zA-Z ]+$");
                Matcher ms = ps.matcher(String.valueOf(c));
                return ms.matches();
            }
        };
    }



    private void getTruckDetails() {


        recyclerView.clearOnScrollListeners();
        truckDetailsList.clear();

        RequestParams requestParams = new RequestParams();
        requestParams.put("userId", MainPage.userId);

        asyncHttpClient.get(VendorTruckListURL, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String s = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("success");
                    if (jsonArray.length()==0){
                        recyclerView.setVisibility(View.GONE);

                    }else {
                        for (int i=0;i<jsonArray.length();i++){
                            jsonObject = jsonArray.getJSONObject(i);

                            TruckDetails truckDetails = new TruckDetails();
                            truckDetails.setTruckId(jsonObject.getString("truck_dt_id"));
                            truckDetails.setTrucknumber(jsonObject.getString("trucknumber"));
                            truckDetails.setTruckcapacity(jsonObject.getString("truckcapacity"));
                            truckDetails.setTruckheight(jsonObject.getString("truckheight"));
                            truckDetails.setTruckweight(jsonObject.getString("truckweight"));
                            truckDetails.setTrucktyres(jsonObject.getString("trucktyres"));
                            truckDetails.setTrucklength(jsonObject.getString("trucklength"));
                            truckDetails.setTruckImages(jsonObject.getString("truckimage"));
                            truckDetails.setTruckType(jsonObject.getString("trucktype"));

                            truckDetailsList.add(truckDetails);

                            adapter = new TruckDetailsAdapter(truckDetailsList, getActivity());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            recyclerView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            adapter.notifyItemInserted(truckDetailsList.size() - 1);
                            recyclerView.setHasFixedSize(true);

                            recyclerView.setVisibility(View.VISIBLE);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Server Error", Toast.LENGTH_LONG).show();
                recyclerView.setVisibility(View.GONE);
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

    private boolean validateTextView(TextView textView) {

        if (textView.getText().toString().trim().length() > 0) {
            return true;
        }
        textView.setError("Please Fill This");
        textView.requestFocus();
        Toasty.normal(getActivity(), "Please Select Material Type", Toasty.LENGTH_SHORT).show();
        return false;
    }



    public boolean isValidPassword(final String password) {

        Pattern pattern;
        Matcher matcher;

        String PASSWORD_PATTERN = "((?=.*\\d)(?=.*[a-z][A-Z])(?=.*[@#$%]).{6,20})";


        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    public void onStart() {
        super.onStart();
        Log.e("onStart", "called");
        MainPage.title.setVisibility(View.VISIBLE);
        ((MainPage) getActivity()).lockUnlockDrawer(1);
        MainPage.drawerLayout.closeDrawers();
        if (DetectConnection.checkInternetConnection(getActivity())){
           getProfile();
        }else {
            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
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
                        Toast.makeText(getActivity(),"No UserId Found", Toast.LENGTH_SHORT).show();
                    }else {
                        for (int i=0;i<jsonArray.length();i++){

                            jsonObject = jsonArray.getJSONObject(i);
                            editTexts.get(0).setText(jsonObject.getString("first_name"));
                            editTexts.get(1).setText(jsonObject.getString("last_name"));
                            editTexts.get(2).setText(jsonObject.getString("email"));
                            editTexts.get(3).setText(jsonObject.getString("contact"));
                            editTexts.get(4).setText(jsonObject.getString("pancard_number")!=null?"":jsonObject.getString("pancard_number"));
                            editTexts.get(5).setText(jsonObject.getString("gst_number")!=null?"":jsonObject.getString("gst_number"));
                            editTexts.get(6).setText(jsonObject.getString("address"));

                            editTexts.get(0).setSelection(editTexts.get(0).getText().toString().length());
                            editTexts.get(1).setSelection(editTexts.get(1).getText().toString().length());
                            editTexts.get(2).setSelection(editTexts.get(2).getText().toString().length());
                            editTexts.get(3).setSelection(editTexts.get(3).getText().toString().length());
                            editTexts.get(4).setSelection(editTexts.get(4).getText().toString().length());
                            editTexts.get(5).setSelection(editTexts.get(5).getText().toString().length());
                            editTexts.get(6).setSelection(editTexts.get(6).getText().toString().length());


                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(),"Server Error", Toast.LENGTH_SHORT).show();
            }
        });

    }


}
