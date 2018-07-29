package com.myroutes.iconbox;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by thijssmudde on 21/04/2017.
 */

public class Icon {
  public String type; //["water", "coffee", "restaurant", "toilet", "info", "park", "help", "top", "right", "left", "down", "topturn", "rightturn", "botturn", "leftturn"]
  public LatLng location;
  public int resourceInt;

  public Icon(String type, LatLng location, int resourceInt) {
    this.type = type;
    this.location = location;
    this.resourceInt = resourceInt;
  }


  public static boolean locationInMarkers(ArrayList<Marker> markers, LatLng location) {
    for (Marker marker : markers) {
      if (checkLatLngEqual(location, marker.getPosition())) {
        return true;
      }
    }
    return false;
  }

  public static int getTagPositionInMarkers(ArrayList<Marker> icons, LatLng location) {
    for (int i = 0; i < icons.size(); i++) {
      if (checkLatLngEqual(location, icons.get(i).getPosition())) {
        return i;
      }
    }
    return -1;
  }

  public static boolean locationInIcons(ArrayList<Icon> icons, LatLng location) {
    for (Icon icon : icons) {
      if (checkLatLngEqual(location, icon.location)) {
        return true;
      }
    }
    return false;
  }

  public static int getTagPositionInIcons(ArrayList<Icon> icons, LatLng location) {
    for (int i = 0; i < icons.size(); i++) {
      if (checkLatLngEqual(location, icons.get(i).location)) {
        return i;
      }
    }
    return -1;
  }

  public static boolean checkLatLngEqual(LatLng c1, LatLng c2) {
    return c1.longitude == c2.longitude && c1.latitude == c2.latitude;
  }

  @Override
  public String toString() {
    Gson gson = new Gson();
    return gson.toJson(this);
  }
}
