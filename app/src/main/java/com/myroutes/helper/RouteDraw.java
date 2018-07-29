package com.myroutes.helper;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

/**
 * Created by Thijs on 2-3-2017.
 */

public class RouteDraw {
  public ArrayList<LatLng> path;
  public LatLngBounds bounds;
  public String distance;
  public String routeUrl;
  public boolean importFile;
  public boolean startFromMarker;
  public LatLng coordinates;

  public RouteDraw(ArrayList<LatLng> path, LatLngBounds bounds, String distance, String routeUrl, boolean startFromMarker, boolean importFile, LatLng coordinates) {
    this.path = path;
    this.bounds = bounds;
    this.distance = distance;
    this.routeUrl = routeUrl;
    this.startFromMarker = startFromMarker;
    this.importFile = importFile;
    this.coordinates = coordinates;
  }
}
