package com.graminvikreta_transporter.interfaces;


public interface OtpReceivedInterface {
  void onOtpReceived(String otp);
  void onOtpTimeout();
}
