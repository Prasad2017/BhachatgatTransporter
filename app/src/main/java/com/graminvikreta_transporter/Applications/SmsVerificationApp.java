package com.graminvikreta_transporter.Applications;

import android.app.Application;

import com.graminvikreta_transporter.helper.AppSignatureHelper;


public class SmsVerificationApp extends Application {


  @Override
  public void onCreate() {
    super.onCreate();
    AppSignatureHelper appSignatureHelper = new AppSignatureHelper(this);
    appSignatureHelper.getAppSignatures();
  }

}
