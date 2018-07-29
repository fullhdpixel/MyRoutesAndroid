package com.myroutes.helper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by tsmud on 27-12-2016.
 */

public class Haversine {
  static final double CONVERSEKM = 0.621371192;
  public static double calc(LatLng left, LatLng right) {
    double lat1 = left.latitude;
    double lng1 = left.longitude;
    double lat2 = right.latitude;
    double lng2 = right.longitude;
    int r = 6371; // average radius of the earth in km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lng2 - lng1);
    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
        Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    return r * c;
  }
  public static double toMiles(double distance) {
    return distance * CONVERSEKM;
  }
}