package com.graminvikreta_transporter.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.andreabaccega.widget.FormEditText;
import com.graminvikreta_transporter.Model.LoginResponse;
import com.graminvikreta_transporter.R;
import com.graminvikreta_transporter.Retrofit.Api;
import com.graminvikreta_transporter.Retrofit.ApiInterface;
import com.graminvikreta_transporter.helper.AppSignatureHelper;
import com.graminvikreta_transporter.interfaces.OtpReceivedInterface;
import com.graminvikreta_transporter.receiver.SmsBroadcastReceiver;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        OtpReceivedInterface, GoogleApiClient.OnConnectionFailedListener {

    @BindView(R.id.mobileNumber)
    FormEditText formEditText;
    @BindView(R.id.otpView)
    OtpView otpView;
    @BindViews({R.id.sendotpLayout, R.id.verifyotpLayout})
    List<LinearLayout> linearLayouts;
    @BindView(R.id.forgotLayout)
    RelativeLayout forgotLayout;
    GoogleApiClient mGoogleApiClient;
    SmsBroadcastReceiver mSmsBroadcastReceiver;
    CountDownTimer cTimer = null;
    String otpCode, OTP;
    private String HASH_KEY;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        // init broadcast receiver
        mSmsBroadcastReceiver = new SmsBroadcastReceiver();

        //set google api client for hint request
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .enableAutoManage(this, this)
                .addApi(Auth.CREDENTIALS_API)
                .build();

        mSmsBroadcastReceiver.setOnOtpListeners(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
        getApplicationContext().registerReceiver(mSmsBroadcastReceiver, intentFilter);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        forgotLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });

        otpView.setOtpCompletionListener(new OnOtpCompletionListener() {
            @Override
            public void onOtpCompleted(String otp) {
                try {
                    otpCode = otp;
                    int length = otpCode.trim().length();
                    if (length == 4) {

                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        View view = getCurrentFocus();
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @OnClick({R.id.submit, R.id.verify, R.id.signIn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit:
                if (formEditText.testValidity()) {
                    sendOTP(formEditText.getText().toString().trim());
                }
                break;

            case R.id.signIn:
                Intent registrationIntent = new Intent(Login.this, Registration.class);
                startActivity(registrationIntent);
                break;

            case R.id.verify:

                if (otpCode != null) {

                    if (otpCode.equalsIgnoreCase(OTP)) {
                       login();
                    } else {
                        Toasty.error(Login.this, "OTP Not Match", Toasty.LENGTH_LONG, true).show();
                    }

                } else {
                    Toasty.warning(Login.this, "Please Enter OTP", Toasty.LENGTH_LONG, true).show();
                }

                break;
        }
    }

    private void login() {

        ApiInterface apiInterface = Api.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.Login(formEditText.getText().toString().trim());
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if(response.body().getStatus().equals("true")){
                    Toast.makeText(Login.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                } else  if(response.body().getStatus().equals("false")){
                    Toast.makeText(Login.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e("Error", ""+t.getMessage());
            }
        });

    }

    private void sendOTP(String mobileNumber) {

        HASH_KEY = (String) new AppSignatureHelper(this).getAppSignatures().get(0);
        HASH_KEY = HASH_KEY.replace("+", "%252B");
        OTP= new DecimalFormat("0000").format(new Random().nextInt(9999));
        String message = "<#> Your Bachatgat verification OTP code is "+ OTP +". Code valid for 10 minutes only, one time use. Please DO NOT share this OTP with anyone.\n" + HASH_KEY;

        ProgressDialog progressDialog = new ProgressDialog(Login.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("OTP is sending");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiInterface apiInterface = Api.getClient().create(ApiInterface.class);
        Call<JSONObject> call = apiInterface.sendSMS(mobileNumber, message);
        call.enqueue(new Callback<JSONObject>() {
            @Override
            public void onResponse(Call<JSONObject> call, Response<JSONObject> response) {

                try {
                    if (response.body().getString("success").equals("1"))
                    {
                        progressDialog.dismiss();

                        linearLayouts.get(0).setVisibility(View.GONE);
                        linearLayouts.get(1).setVisibility(View.VISIBLE);
                        Toast.makeText(Login.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                        startSMSListener();

                    } else  {
                        progressDialog.dismiss();

                        linearLayouts.get(1).setVisibility(View.GONE);
                        linearLayouts.get(0).setVisibility(View.VISIBLE);
                        Toast.makeText(Login.this, "Please use a valid phone number", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JSONObject> call, Throwable t) {
                progressDialog.dismiss();

                linearLayouts.get(1).setVisibility(View.GONE);
                linearLayouts.get(0).setVisibility(View.VISIBLE);
                Log.e("Error", ""+t.getMessage());
            }
        });

    }

    public void startSMSListener() {
        SmsRetrieverClient mClient = SmsRetriever.getClient(this);
        Task<Void> mTask = mClient.startSmsRetriever();
        mTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {
                linearLayouts.get(0).setVisibility(View.GONE);
                linearLayouts.get(1).setVisibility(View.VISIBLE);
                //    Toast.makeText(ForgotPassword.this, "SMS Retriever starts", Toast.LENGTH_LONG).show();
            }
        });
        mTask.addOnFailureListener(new OnFailureListener() {
            @Override public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Error", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onOtpReceived(String otp) {
        Toast.makeText(this, "Otp Received " + otp, Toast.LENGTH_LONG).show();
        otpView.setText(otp);
    }

    @Override
    public void onOtpTimeout() {

    }

    protected void hideKeyboard(View view) {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

}