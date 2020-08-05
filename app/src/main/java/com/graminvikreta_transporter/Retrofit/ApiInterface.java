package com.graminvikreta_transporter.Retrofit;

import com.graminvikreta_transporter.Model.AllList;
import com.graminvikreta_transporter.Model.BidData;
import com.graminvikreta_transporter.Model.LoginResponse;
import com.graminvikreta_transporter.Model.StatusResponse;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiInterface {

    @FormUrlEncoded
    @POST("/androidApp/Transporter/Login.php")
    Call<LoginResponse> Login(@Field("mobile") String mobile);


    @FormUrlEncoded
    @POST("/androidApp/Transporter/Registration.php")
    Call<LoginResponse> Registration(@Field("profilePhoto") String profilePhoto,
                                     @Field("firstName") String firstName,
                                     @Field("lastName") String lastName,
                                     @Field("middleName") String middleName,
                                     @Field("mobileNumber")  String mobileNumber,
                                     @Field("address") String address,
                                     @Field("aadharCard")  String aadharCard,
                                     @Field("panCard")  String panCard,
                                     @Field("planterArea")  String planterArea,
                                     @Field("dryArea") String dryArea,
                                     @Field("emailId") String emailId,
                                     @Field("password") String password,
                                     @Field("bankName") String bankName,
                                     @Field("accountNumber") String accountNumber,
                                     @Field("branchNme") String branchNme,
                                     @Field("iFSC") String iFSC);


    @GET("/androidApp/Transporter/sendSMS.php")
    Call<JSONObject> sendSMS(@Query("number") String mobileNumber,
                             @Query("message") String message);

    @FormUrlEncoded
    @POST("/androidApp/Transporter/biddedOrder.php")
    Call<StatusResponse> biddedOrder(@Field("vendor_id") String deliveryBoyId, @Field("id") String orderId, @Field("bid_amount") String bid_amount);

    @GET("/androidApp/Transporter/RaisedOrder.php")
    Call<AllList> raisedOrderList(@Query("userId") String userId);

    @FormUrlEncoded
    @POST("/androidApp/Transporter/OrderBidding.php")
    Call<BidData> getBidding(@Field("vendorId") String userId);


    @FormUrlEncoded
    @POST("/androidApp/Transporter/UpdateVendorBid.php")
    Call<StatusResponse> UpdateVendorBid(@Field("order_id") String order_id, @Field("vendor_id") String vendor_id, @Field("bid_amount") String bid_amount);


    @GET("/androidApp/Transporter/GetAwardOrders.php")
    Call<AllList> GetAwardOrders(@Query("delivery_boy_id") String userId);


    @FormUrlEncoded
    @POST("/androidApp/Transporter/Confirm_PickUp.php")
    Call<StatusResponse> getConfirmPickUp(@Field("delivery_boy_id") String delivery_boy_id,
                                  @Field("id") String order_id,
                                  @Field("order_status") String order_status);


}
