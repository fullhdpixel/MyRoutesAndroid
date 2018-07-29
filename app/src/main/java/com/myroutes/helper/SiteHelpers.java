package com.myroutes.helper;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.myroutes.MainActivity;
import com.myroutes.iconbox.Icon;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.myroutes.UploadActivity.baseUrl;

/**
 * Created by thijssmudde on 15/05/2017.
 */

public class SiteHelpers {
  private static final String TAG = "SiteHelpers";

  public static void DownloadRoute(final Context context, final Handler handler, final Uri data) {
    Runnable r = new Runnable() {
      @Override
      public void run() {
        String scheme = data.getScheme();
        String fullPath = data.getEncodedSchemeSpecificPart();

        if (scheme.equals("http") || scheme.equals("https")) {
          String id = fullPath.split("/")[4]; //Get ID of route
          if (id != null && id.length() > 0) {
            if (fullPath.contains("myroutes") || fullPath.contains("192.168")) { //Check if url is from my site
              //If so, then request route object from /api/routes with an id.
              RequestQueue queue = Volley.newRequestQueue(context);
              String url = baseUrl + "/api/routes?id=" + id + "&s=Q3xGdfL0KwcpLtsrOWU7";
              StringRequest sr = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                  JSONObject jsonObj;
                  try {
                    jsonObj = new JSONObject(response);
                    JSONObject data = jsonObj.getJSONObject("data");
                    if (data != null) {
                      JSONObject route = data.getJSONObject("route");
                      if (route != null) {
                        String name = route.getString("name");
                        String description = route.getString("description");
                        String type = route.getString("type");
                        String setting = route.getString("setting");
                        double distance = Double.parseDouble(route.getString("distance"));
                        String[] pathArray = route.getString("path").split(", ");

                        JSONArray iconArray = route.getJSONArray("icons");
                        boolean isImported = route.getBoolean("isImported");
                        boolean privacy = route.getBoolean("privacy");
                        int dateCreated = route.getInt("dateCreated");

                        ArrayList<LatLng> path = new ArrayList<>();
                        for (String splittedString : pathArray) {
                          String lat = splittedString.split(",")[0].split("\\(")[1];
                          String lng = splittedString.split(",")[1].split("\\)")[0];
                          path.add(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));
                        }

                        ArrayList<Icon> icons = new ArrayList<>();
                        if (iconArray != null) {
                          for (int i = 0; i < iconArray.length(); i++) {
                            Gson gson = new Gson();
                            icons.add(gson.fromJson(iconArray.getString(i), Icon.class));
                          }
                        }

                        RouteSpec routeSpec = new RouteSpec(name, description, type, setting, distance, path, icons, isImported, privacy, dateCreated);
                        Message msg = Message.obtain();
                        msg.what = MainActivity.DOWNLOAD_ROUTE;
                        msg.obj = routeSpec;

                        msg.setTarget(handler);
                        msg.sendToTarget();
                      }
                    }
                  } catch (Exception e) {
                    e.printStackTrace();
                  }
                }
              }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                  String errorResponse = "";
                  if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                    errorResponse = "Download Error: network timeout";
                  } else if (error instanceof AuthFailureError) {
                    errorResponse = "Download Error: auth error";
                  } else if (error instanceof ServerError) {
                    errorResponse = "Download Error: myroutes.io unavailable, try again later";
                  } else if (error instanceof NetworkError) {
                    errorResponse = "Download Error: network error";
                  } else if (error instanceof ParseError) {
                    errorResponse = "Download Error: parse error";
                  }
                  Message msg = Message.obtain();
                  msg.what = MainActivity.DOWNLOAD_ROUTE_ERROR;
                  msg.obj = errorResponse;

                  msg.setTarget(handler);
                  msg.sendToTarget();
                }
              });
              queue.add(sr);
            }
          }
        }
      }
    };
    Thread thread = new Thread(r);
    thread.start();
  }
}
