package com.graminvikreta_transporter.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllList {


    @SerializedName("getdatas")
    @Expose
    private List<Order> getdatas = null;

    public List<Order> getGetdatas() {
        return getdatas;
    }

    public void setGetdatas(List<Order> getdatas) {
        this.getdatas = getdatas;
    }


}
