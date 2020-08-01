package com.graminvikreta_transporter.Modules;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by Prasad on 07-06/2019.
 */
public class Route {
    public Distance distance;
    public Duration duration;
    public String endAddress;
    public LatLng endLocation;
    public String startAddress;
    public LatLng startLocation;

    public List<LatLng> points;
}
