package com.graminvikreta_transporter.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeliverStock {

    @SerializedName("dates")
    @Expose
    private List<Qdata> qdataList = null;

    @SerializedName("order_title")
    @Expose
    private String order_title;

    @SerializedName("id")
    @Expose
    private String id;


    public List<Qdata> getQdataList() {
        return qdataList;
    }

    public void setQdataList(List<Qdata> qdataList) {
        this.qdataList = qdataList;
    }

    public String getOrder_title() {
        return order_title;
    }

    public void setOrder_title(String order_title) {
        this.order_title = order_title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
