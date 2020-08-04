package com.graminvikreta_transporter.Fragment;


import android.app.Activity;
import android.app.AlertDialog;
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
import androidx.recyclerview.widget.RecyclerView;

import com.graminvikreta_transporter.Activity.MainPage;
import com.graminvikreta_transporter.Extra.Config;
import com.graminvikreta_transporter.Extra.DetectConnection;
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
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.pedant.SweetAlert.SweetAlertDialog;
import cz.msebera.android.httpclient.Header;
import es.dmoral.toasty.Toasty;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class Profile extends Fragment {

    View view;
    @BindViews({R.id.fname, R.id.lname, R.id.email, R.id.mobile, R.id.pancard, R.id.gstnumber, R.id.address})
    List<EditText> editTexts;
    AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
    public String getProfile = "http://graminvikreta.com/androidApp/Transporter/getProfile.php";
    public String UpdateProfileURl = "http://graminvikreta.com/androidApp/Transporter/UpdateProfile.php";

    Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");
    Pattern trucknumberpattern = Pattern.compile("[A-Z]{2}[0-9]{2}[A-Z]{2}[0-9]{4}");
    Pattern gstpattern = Pattern.compile("[0-9]{2}[a-zA-Z]{5}[0-9]{4}[a-zA-Z]{1}[1-9A-Za-z]{1}[Z]{1}[0-9a-zA-Z]{1}");
    Matcher matcher;



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

    @OnClick({R.id.update})
    public void onClick(View view) {
        switch (view.getId()) {


            case R.id.update:
                if (DetectConnection.checkInternetConnection(getActivity())) {

                    if (editTexts.get(5).getText().toString().trim().equalsIgnoreCase("")) {

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

                    } else {

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

                } else {
                    Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;
        }
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


    private void Update() {


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
                        Toast.makeText(getActivity(), "Update Done", Toast.LENGTH_SHORT).show();
                        getProfile();
                    }else if (jsonObject.getString("success").equalsIgnoreCase("0")){
                        Toast.makeText(getActivity(), "Update Fail", Toast.LENGTH_SHORT).show();
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
