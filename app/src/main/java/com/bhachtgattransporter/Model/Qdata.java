package com.bhachtgattransporter.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Qdata {


    @SerializedName("bid_id")
    @Expose
    private String bidId;
    @SerializedName("vendor_id")
    @Expose
    private String vendorId;
    @SerializedName("bid_amount")
    @Expose
    private String bidAmount;
    @SerializedName("full_name")
    @Expose
    private String fullName;


    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getBidAmount() {
        return bidAmount;
    }

    public void setBidAmount(String bidAmount) {
        this.bidAmount = bidAmount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
