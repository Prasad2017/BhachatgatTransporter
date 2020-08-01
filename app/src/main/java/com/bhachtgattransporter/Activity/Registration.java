package com.bhachtgattransporter.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aminography.choosephotohelper.ChoosePhotoHelper;
import com.aminography.choosephotohelper.callback.ChoosePhotoCallback;
import com.andreabaccega.widget.FormEditText;
import com.bhachtgattransporter.Model.LoginResponse;
import com.bhachtgattransporter.R;
import com.bhachtgattransporter.Retrofit.Api;
import com.bhachtgattransporter.Retrofit.ApiInterface;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.ByteArrayOutputStream;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity {

    @BindViews({R.id.firstName, R.id.lastName, R.id.mobileNumber, R.id.address, R.id.aadharCard, R.id.panCard, R.id.planterArea, R.id.dryArea, R.id.emailID, R.id.password, R.id.bankName,
    R.id.accountNumber, R.id.branchName, R.id.iFSC})
    List<FormEditText> formEditTexts;
    @BindView(R.id.imageView)
    ImageView imageView;
    @BindView(R.id.loginLinearLayout)
    LinearLayout loginLinearLayout;
    private ChoosePhotoHelper choosePhotoHelper;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        loginLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
            }
        });


        formEditTexts.get(0).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(1).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(3).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(5).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(6).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(7).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(8).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(9).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(10).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(11).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(12).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        formEditTexts.get(13).setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        choosePhotoHelper = ChoosePhotoHelper.with(this)
                .asFilePath()
                .build(new ChoosePhotoCallback<String>() {
                    @Override
                    public void onChoose(String photo) {
                        Glide.with(imageView)
                                .load(photo)
                                .apply(RequestOptions.placeholderOf(R.drawable.profileimg))
                                .into(imageView);
                    }
                });

    }

    @OnClick({R.id.profile_add, R.id.back,R.id.signIn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                moveTaskToBack(false);
                break;

            case R.id.profile_add:
                choosePhotoHelper.showChooser();
                break;

            case R.id.signIn:

                if (formEditTexts.get(0).testValidity() && formEditTexts.get(1).testValidity() && formEditTexts.get(2).testValidity() && formEditTexts.get(3).testValidity() && formEditTexts.get(4).testValidity()
                && formEditTexts.get(5).testValidity() && formEditTexts.get(6).testValidity() && formEditTexts.get(7).testValidity() && formEditTexts.get(8).testValidity() && formEditTexts.get(9).testValidity()
                && formEditTexts.get(11).testValidity() && formEditTexts.get(12).testValidity() && formEditTexts.get(13).testValidity()) {

                    String imageString = "";

                    if (imageView.getDrawable()!=null){
                        bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        imageString = getStringImage(bitmap);
                    } else {
                        imageString = "";
                    }

                    registration(imageString, formEditTexts.get(0).getText().toString(), formEditTexts.get(1).getText().toString(), formEditTexts.get(2).getText().toString(), formEditTexts.get(3).getText().toString(),
                            formEditTexts.get(4).getText().toString(), formEditTexts.get(5).getText().toString(), formEditTexts.get(6).getText().toString(), formEditTexts.get(7).getText().toString(),
                            formEditTexts.get(8).getText().toString(), formEditTexts.get(9).getText().toString(), formEditTexts.get(10).getText().toString(), formEditTexts.get(11).getText().toString(),
                            formEditTexts.get(12).getText().toString(), formEditTexts.get(13).getText().toString());

                }

                break;
        }
    }

    private void registration(String profilePhoto, String firstName, String lastName, String mobileNumber, String address, String aadharCard, String panCard, String planterArea, String dryArea, String emailId, String password, String bankName, String accountNumber, String branchNme, String iFSC) {

        ProgressDialog progressDialog = new ProgressDialog(Registration.this);
        progressDialog.setMessage("Loading...");
        progressDialog.setTitle("Account is creating");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
        progressDialog.setCancelable(false);

        ApiInterface apiInterface = Api.getClient().create(ApiInterface.class);
        Call<LoginResponse> call = apiInterface.Registration(profilePhoto, firstName, lastName, mobileNumber, address, aadharCard, panCard, planterArea, dryArea, emailId, password, bankName, accountNumber, branchNme, iFSC);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {

                if (response.body().getStatus().equals("true")){
                    progressDialog.dismiss();
                    Toast.makeText(Registration.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registration.this, Login.class);
                    startActivity(intent);
                } else if (response.body().getStatus().equals("false")){
                    progressDialog.dismiss();
                    Toast.makeText(Registration.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("Error", ""+t.getMessage());
            }
        });

    }

    private String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = android.util.Base64.encodeToString(imageBytes, android.util.Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        choosePhotoHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        choosePhotoHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void requestPermission() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
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
                        Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Registration.this);
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
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    protected void hideKeyboard(View view)
    {
        InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(false);
    }
}