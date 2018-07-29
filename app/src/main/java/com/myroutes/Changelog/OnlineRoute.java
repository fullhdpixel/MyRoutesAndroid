package com.myroutes.Changelog;

/**
 * Created by thijssmudde on 22/05/2017.
 */

public class OnlineRoute {
  public String name;
  public String type;
  public int date;
  public double distance;
  public int views;
  public String location;

  public OnlineRoute(String name, String type, int date, double distance, int views, String location) {
    this.name = name;
    this.type = type;
    this.date = date;
    this.distance = distance;
    this.views = views;
    this.location = location;
  }
}
