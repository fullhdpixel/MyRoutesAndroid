package com.myroutes.helper;

import com.google.android.gms.maps.model.LatLng;
import com.myroutes.iconbox.Icon;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Thijs on 9-1-2017.
 */

public class RouteSpec {
  private static final String TAG = "RouteSpec";
  String id;
  String userId;
  String name;
  String type = "unknown"; //Either driving, walking, running, cycling
  String setting = "none"; //Either race, training, none
  String description;
  double distance; //Always in km
  ArrayList<Icon> icons;
  ArrayList<LatLng> path;
  boolean isImported;
  LatLng startMarker;
  LatLng endMarker;
  int dateCreated;
  boolean privacy = false;

  public RouteSpec(String id, String name, String type, double distance, ArrayList<Icon> icons, ArrayList<LatLng> path, boolean isImported, boolean privacy, int dateCreated) {
    this.id = id;
    this.name = name;
    if (type.length() > 0) {
      this.type = type;
    } else {
      this.type = "unknown";
    }
    this.distance = distance;
    this.icons = icons;
    this.path = path;
    this.isImported = isImported;
    this.privacy = privacy;
    this.startMarker = PathMethods.getStartLatLng(path);
    this.endMarker = PathMethods.getEndLatLng(path);
    this.dateCreated = dateCreated;
  }

  //Download from site
  public RouteSpec(String name, String description, String type, String setting, double distance, ArrayList<LatLng> path, ArrayList<Icon> icons, boolean isImported, boolean privacy, int dateCreated) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.setting = setting;
    this.distance = distance;
    this.path = path;
    this.icons = icons;
    this.isImported = isImported;
    this.privacy = privacy;
    this.dateCreated = dateCreated;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Map<String, String> toJsonObject() throws JSONException {
    Map<String, String> params = new HashMap<>();
    if (id != null) {
      params.put("id", this.id.toString());
    }
    if (userId != null) {
      params.put("userId", this.userId.toString());
    }
    params.put("name", this.name.toString());
    params.put("type", this.type.toString());
    params.put("setting", this.setting.toString());
    params.put("description", this.description.toString());
    params.put("distance", Double.toString(this.distance));
    params.put("icons", this.icons.toString());
    params.put("path", this.path.toString());
    params.put("isImported", this.isImported == true ? "true" : "false");
    params.put("startMarker", this.startMarker.toString());
    params.put("endMarker", this.endMarker.toString());
    params.put("dateCreated", Long.toString(this.dateCreated));
    params.put("privacy", this.privacy == true ? "true" : "false");
    params.put("s", "Q3xGdfL0KwcpLtsrOWU7");
    return params;
  }

  public ArrayList<Icon> getIcons() {
    return icons;
  }

  public void setIcons(ArrayList<Icon> icons) {
    this.icons = icons;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getSetting() {
    return setting;
  }

  public void setSetting(String setting) {
    this.setting = setting;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance(double distance) {
    this.distance = distance;
  }

  public ArrayList<LatLng> getPath() {
    return path;
  }

  public void setPath(ArrayList<LatLng> path) {
    this.path = path;
  }

  public boolean isImported() {
    return isImported;
  }

  public void setImported(boolean imported) {
    isImported = imported;
  }

  public LatLng getStartMarker() {
    return startMarker;
  }

  public void setStartMarker(LatLng startMarker) {
    this.startMarker = startMarker;
  }

  public LatLng getEndMarker() {
    return endMarker;
  }

  public void setEndMarker(LatLng endMarker) {
    this.endMarker = endMarker;
  }

  public long getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(int dateCreated) {
    this.dateCreated = dateCreated;
  }

  public boolean isPrivacy() {
    return privacy;
  }

  public void setPrivacy(boolean privacy) {
    this.privacy = privacy;
  }
}
