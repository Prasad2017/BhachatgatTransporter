package com.graminvikreta_transporter.Model;

public class TruckDetails {

    String truckId, trucknumber, truckcapacity, truckheight, truckweight, trucktyres, trucklength, truckImages, truckType;

    // State of the item
    private boolean expanded;


    public String getTruckId() {
        return truckId;
    }

    public void setTruckId(String truckId) {
        this.truckId = truckId;
    }

    public String getTrucknumber() {
        return trucknumber;
    }

    public void setTrucknumber(String trucknumber) {
        this.trucknumber = trucknumber;
    }

    public String getTruckcapacity() {
        return truckcapacity;
    }

    public void setTruckcapacity(String truckcapacity) {
        this.truckcapacity = truckcapacity;
    }

    public String getTruckheight() {
        return truckheight;
    }

    public void setTruckheight(String truckheight) {
        this.truckheight = truckheight;
    }

    public String getTruckweight() {
        return truckweight;
    }

    public void setTruckweight(String truckweight) {
        this.truckweight = truckweight;
    }

    public String getTrucktyres() {
        return trucktyres;
    }

    public void setTrucktyres(String trucktyres) {
        this.trucktyres = trucktyres;
    }

    public String getTrucklength() {
        return trucklength;
    }

    public void setTrucklength(String trucklength) {
        this.trucklength = trucklength;
    }

    public String getTruckImages() {
        return truckImages;
    }

    public void setTruckImages(String truckImages) {
        this.truckImages = truckImages;
    }

    public String getTruckType() {
        return truckType;
    }

    public void setTruckType(String truckType) {
        this.truckType = truckType;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }
}
