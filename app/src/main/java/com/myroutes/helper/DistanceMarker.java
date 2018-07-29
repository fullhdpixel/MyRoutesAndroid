package com.myroutes.helper;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by thijssmudde on 14/05/2017.
 */

public class DistanceMarker {
  public String iconText;
  public LatLng interpolatePos;

  public DistanceMarker(String iconText, LatLng interpolatePos) {
    this.iconText = iconText;
    this.interpolatePos = interpolatePos;
  }
}
