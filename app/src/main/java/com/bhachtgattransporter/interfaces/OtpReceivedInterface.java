package com.bhachtgattransporter.interfaces;


public interface OtpReceivedInterface {
  void onOtpReceived(String otp);
  void onOtpTimeout();
}
