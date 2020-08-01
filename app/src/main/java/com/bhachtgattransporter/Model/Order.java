package com.bhachtgattransporter.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Order {

    @SerializedName("master_bid_id")
    @Expose
    private String masterBidId;
    @SerializedName("order_number")
    @Expose
    private String orderNumber;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("product_name")
    @Expose
    private String product_name;
    @SerializedName("product_bid_amount")
    @Expose
    private String productBidAmount;
    @SerializedName("bid_status")
    @Expose
    private String bidStatus;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("mobileno")
    @Expose
    private String mobileno;
    @SerializedName("billing_address")
    @Expose
    private String billingAddress;


    public String getMasterBidId() {
        return masterBidId;
    }

    public void setMasterBidId(String masterBidId) {
        this.masterBidId = masterBidId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getProductBidAmount() {
        return productBidAmount;
    }

    public void setProductBidAmount(String productBidAmount) {
        this.productBidAmount = productBidAmount;
    }

    public String getBidStatus() {
        return bidStatus;
    }

    public void setBidStatus(String bidStatus) {
        this.bidStatus = bidStatus;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(String billingAddress) {
        this.billingAddress = billingAddress;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
