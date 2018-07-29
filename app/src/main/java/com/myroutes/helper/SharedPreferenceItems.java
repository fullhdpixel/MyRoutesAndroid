package com.myroutes.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.Arrays;

/**
 * Created by Thijs on 5-2-2017.
 */
public class SharedPreferenceItems {

  /**
   * The Prefs. Required so we can set up the menu without showing the prefs
   * Everything in sharedprefs is a route, except what is defined here.
   */
  public static String[] prefs = new String[]{"-toolbar_transparency-", "-unit-", "-maptype-", "-mapboxtype-", "-mapboxquality-", "-emptied-", "mbtiles", "offline", "-minzoom-", "-maxzoom-", "-color_line-", "-travel_mode-", "-default_coordinates-", "-notice-", "-rated_notice-", "-showdistancemarkers-", "-distance_marker_offset-", "-userId-"};
//  "-default_coordinates-"

  /**
   * Sets default for each sharedpreferences
   * First checks if it is already set
   * If not set, set to values
   *
   * @param context the context
   */
  public void setDefaults(Context context) {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPrefs.edit();

    for (String pref : prefs) {
      if (pref.equals("-toolbar_transparency-")) { // Boolean
        boolean isSet = sharedPrefs.getBoolean("-toolbar_transparency-", false);
        if (!isSet) {
          editor.putBoolean("-toolbar_transparency-", false);
        }
      } else if (pref.equals("-unit-")) { // String
        String isSet = sharedPrefs.getString("-unit-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-unit-", "0");
        }
      } else if (pref.equals("-maptype-")) { // String
        String isSet = sharedPrefs.getString("-maptype-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-maptype-", "0");
        }
      } else if (pref.equals("-mapboxtype-")) { // String
        String isSet = sharedPrefs.getString("-mapboxtype-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-mapboxtype-", "0");
        }
      } else if (pref.equals("-mapboxquality-")) { // String
        String isSet = sharedPrefs.getString("-mapboxquality-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-mapboxquality-", "0");
        }
      } else if (pref == "-emptied-") { // String
        String isSet = sharedPrefs.getString("-emptied-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-emptied-", "0");
        }
      } else if (pref.equals("-mbtiles-")) { // String
        String isSet = sharedPrefs.getString("-mbtiles-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-mbtiles-", "0");
        }
      } else if (pref.equals("-offline-")) { // Boolean
        boolean isSet = sharedPrefs.getBoolean("-offline-", false);
        if (!isSet) {
          editor.putBoolean("-offline-", false);
        }
      } else if (pref.equals("-minzoom-")) { // String
        String isSet = sharedPrefs.getString("-minzoom-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-minzoom-", "0");
        }
      } else if (pref.equals("-maxzoom-")) { // String
        String isSet = sharedPrefs.getString("-maxzoom-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-maxzoom-", "21");
        }
      } else if (pref.equals("-color_line-")) { // String
        int isSet = sharedPrefs.getInt("-color_line-", 0);
        if (isSet == 0) {
          editor.putInt("-color_line-", 0xFF246DBF);
        }
      } else if (pref.equals("-default_coordinates-")) { // String
        LatLng coordinates = new LatLng(52.3702, 4.8952);
        Gson gson = new Gson();
        String defaultCoordinates = gson.toJson(coordinates);
        String isSet = sharedPrefs.getString("-default_coordinates-", defaultCoordinates);
        if (isSet == defaultCoordinates) {
          editor.putString("-default_coordinates-", defaultCoordinates);
        }
      } else if (pref.equals("-notice-")) { //String
        //Keep version
        int isSet = sharedPrefs.getInt("-notice-", 0);
        if (isSet == 0) {
          editor.putInt("-notice-", 1);
        }
      } else if (pref.equals("-rated_notice-")) { //Boolean
        boolean isSet = sharedPrefs.getBoolean("-rated_notice-", false);
        if (!isSet) {
          editor.putBoolean("-rated_notice-", false);
        }
      } else if (pref.equals("-showdistancemarkers-")) { //Boolean
        boolean isSet = sharedPrefs.getBoolean("-showdistancemarkers-", false);
        if (!isSet) {
          editor.putBoolean("-showdistancemarkers-", true);
        }
      } else if (pref.equals("-userId-")) { //Boolean
        String isSet = sharedPrefs.getString("-userId-", "-1");
        if (isSet.equals("-1")) {
          editor.putString("-userId-", null);
        }
      }
    }
    editor.apply();
  }

  /**
   * Gets total.
   *
   * @return the total
   */
  public static int getTotal() {
    return prefs.length;
  }

  /**
   * Number of saved routes int.
   *
   * @param sharedPrefs the shared prefs
   * @return the int
   */
  public static int numberOfSavedRoutes(SharedPreferences sharedPrefs) {
    return sharedPrefs.getAll().size() - getTotal();
  }

  /**
   * Contains item boolean.
   *
   * @param val the val
   * @return the boolean
   */
  public boolean containsItem(String val) {
    return Arrays.asList(prefs).contains(val);
  }

  /**
   * For testing purposes, clear all shared preferences
   *
   * @param context the context
   */
  public void clearAll(Context context) {
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    SharedPreferences.Editor editor = sharedPrefs.edit();
    editor.clear().apply();
  }
}
