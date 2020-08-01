package com.graminvikreta_transporter.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BidData {

    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("orderdata")
    @Expose
    private List<DeliverStock> orderdata = null;



    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public List<DeliverStock> getOrderdata() {
        return orderdata;
    }

    public void setOrderdata(List<DeliverStock> orderdata) {
        this.orderdata = orderdata;
    }
}
