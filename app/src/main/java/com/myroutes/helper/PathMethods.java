package com.myroutes.helper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by thijssmudde on 17/04/2017.
 */

public class PathMethods {
  public static LatLng getStartLatLng(ArrayList<LatLng> path) {
    LatLng startPos;
    if (path.get(0).latitude == -1) {
      startPos = path.get(1);
    } else {
      startPos = path.get(0);
    }
    return startPos;
  }
  public static LatLng getEndLatLng(ArrayList<LatLng> path) {
    LatLng endPos;
    if (path.get(path.size() - 1).latitude == -1) {
      endPos = path.get(path.size() - 2);
    } else {
      endPos = path.get(path.size() - 1);
    }
    return endPos;
  }
}
