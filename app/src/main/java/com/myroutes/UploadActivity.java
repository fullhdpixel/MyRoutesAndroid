package com.myroutes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;
import com.myroutes.helper.RouteSpec;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;

public class UploadActivity extends AppCompatActivity {
  public static final String TAG = "UploadActivity";

  private static final int DEFAULT_ROUTE_SETTING = 0;
  public static String baseUrl = "https://myroutes.io";
//    public static String baseUrl = "http://192.168.2.2:3000";
  public static String websocket = "ws://myroutes.io/websocket";
//  public static String websocket = "ws://192.168.2.2:3000/websocket";

  CoordinatorLayout coordinatorLayout;

  RouteSpec routeSpec;
  EditText routename, description;
  Spinner routetypes, routesettings;
  CheckBox privacy;
  Button uploadBtn;

  String routeNameResult;
  boolean isUpdate = false;
  String oldRouteName = "";

  Meteor meteorSingleton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_upload);

    meteorSingleton = MeteorSingleton.getInstance();
    meteorSingleton.reconnect();

    // Instantiate the cache
    Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

    // Set up the network to use HttpURLConnection as the HTTP client.
    Network network = new BasicNetwork(new HurlStack());
    // Instantiate the RequestQueue with the cache and network.
    mRequestQueue = new RequestQueue(cache, network);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
    routename = (EditText) findViewById(R.id.routename);
    routetypes = (Spinner) findViewById(R.id.routetypes);
    routesettings = (Spinner) findViewById(R.id.routesettings);
    description = (EditText) findViewById(R.id.description);
    privacy = (CheckBox) findViewById(R.id.privacy);
    uploadBtn = (Button) findViewById(R.id.uploadBtn);

    routename.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        routeNameResult = s.toString();
        int length = s.length();
        //Route Name has to be between 2 and 50 parameters
        if (length < 6) {
          routename.setError(getString(R.string.upload_name_too_short));
          uploadBtn.setAlpha(.5f);
        } else if (length > 50) {
          routename.setError(getString(R.string.upload_name_too_long));
          uploadBtn.setAlpha(.5f);
        } else {
          uploadBtn.setAlpha(1f);
        }
      }

      @Override
      public void afterTextChanged(Editable s) {

      }
    });

    uploadBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //Catch network available
        if (!isNetworkAvailable()) {
          String localized_network_unavailable = getString(R.string.network_unavailable);
          Toast.makeText(getApplicationContext(), localized_network_unavailable, Toast.LENGTH_SHORT).show();
          return;
        }

        //Check if button is disabled
        if (uploadBtn.getAlpha() != 1f) {
          int lengthroutename = routename.getText().length();
          if (lengthroutename < 6) {
            Toast.makeText(getApplicationContext(), getString(R.string.upload_name_too_short) + " (min: 6)", Toast.LENGTH_SHORT).show();
          } else if (lengthroutename > 50) {
            Toast.makeText(getApplicationContext(), getString(R.string.upload_name_too_long) + " (max: 50)", Toast.LENGTH_SHORT).show();
          }
        } else {
          String name = routename.getText().toString();
          String type = getResources().getStringArray(R.array.routetypesvalues)[routetypes.getSelectedItemPosition()].toLowerCase();
          String setting = getResources().getStringArray(R.array.routesettingvalues)[routesettings.getSelectedItemPosition()].toLowerCase();
          String descriptionResult = description.getText().toString();
          boolean privacyResult = privacy.isChecked();

          if (meteorSingleton.isLoggedIn()) {
            String userId = meteorSingleton.getUserId();

            routeSpec.setUserId(userId);
          } else {
            Toast.makeText(getApplicationContext(), "Could not detect userId, please log in again", Toast.LENGTH_SHORT).show();
            finish();
          }
          routeSpec.setName(name);
          routeSpec.setType(type);
          routeSpec.setSetting(setting);
          routeSpec.setDescription(descriptionResult);
          routeSpec.setPrivacy(privacyResult);

          uploadRoute(routeSpec);
        }
      }
    });

    uploadBtn.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          uploadBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.upload_button_pressed));
          uploadBtn.setBackgroundColor(ContextCompat.getColor(UploadActivity.this, R.color.colorPrimaryDark));
          uploadBtn.setTextColor(Color.WHITE);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          uploadBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.upload_button));
          uploadBtn.setTextColor(ContextCompat.getColor(UploadActivity.this, R.color.colorPrimaryDark));
        }
        return false;
      }
    });

    String json = getIntent().getStringExtra("routespec");
    isUpdate = getIntent().getBooleanExtra("isUpdate", false);
    if (json != null) {
      //Output json routespec to fill in blanks
      Gson gson = new Gson();
      routeSpec = gson.fromJson(json, RouteSpec.class);

      //Save oldRouteName, so we can delete it later
      oldRouteName = routeSpec.getName();
      routename.setText(oldRouteName);

      //update route type
      if (isUpdate) {
        getSupportActionBar().setTitle(getString(R.string.route_update));

        //update description
        description.setText(routeSpec.getDescription());
        //update route setting
        String routeSetting = routeSpec.getSetting();
        int setting = DEFAULT_ROUTE_SETTING;
        if (routeSetting != null) {
          switch (routeSetting) {
            case "none":
              setting = 0;
              break;
            case "training":
              setting = 1;
              break;
            case "race":
              setting = 2;
              break;
          }
        }
        routesettings.setSelection(setting);
        //check/uncheck private route
        privacy.setChecked(routeSpec.isPrivacy());
      } else {
        routesettings.setSelection(DEFAULT_ROUTE_SETTING);
      }
      String routetype = routeSpec.getType();
      int type = 1;
      if (routetype != null) {
        switch (routetype) {
          case "walking":
            type = 0;
            break;
          case "running":
            type = 1;
            break;
          case "bicycling":
            type = 2;
            break;
          case "driving":
            type = 3;
            break;
        }
      }
      routetypes.setSelection(type);
    } else {
      //Quit if there is no routespec
      this.finish();
    }
  }

  private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  ProgressDialog dialog;
  private static final int TIMEOUT_REQUEST = 0;

  public void uploadRoute(RouteSpec routespec) {
    dialog = new ProgressDialog(UploadActivity.this);
    dialog.setMessage(getString(R.string.uploading));
    dialog.setCancelable(true);
    dialog.setInverseBackgroundForced(true);
    dialog.show();

    Runnable r = new Runnable() {
      @Override
      public void run() {
        try {
          Thread.sleep(5000);
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
        }
        Message msg = Message.obtain();
        msg.what = TIMEOUT_REQUEST;
        msg.setTarget(handler);
        msg.sendToTarget();
      }
    };
    Thread thread = new Thread(r);
    thread.start();

    final RouteSpec routespecUploaded = routespec;
    Map<String, String> jsonData = null;
    try {
      jsonData = routespec.toJsonObject();
    } catch (JSONException e) {
      e.printStackTrace();
    }

    final Map<String, String> jsonFinalResult = jsonData;

    //Send http request with
    String uri = baseUrl + "/api/routes";

    RequestQueue requestQueue = Volley.newRequestQueue(this);
    StringRequest jsObjRequest = new StringRequest(Request.Method.POST, uri,
        new Response.Listener<String>() {
          @Override
          public void onResponse(String response) {
            dialog.dismiss();
            //Result validation
            if (response == null) {
              Toast.makeText(getApplicationContext(), getString(R.string.upload_no_response), Toast.LENGTH_SHORT).show();
            } else {
              JSONObject jsonObj = null;
              try {
                jsonObj = new JSONObject(response);
              } catch (JSONException e) {
                e.printStackTrace();
              }
              showServerResponse(routespecUploaded, jsonObj);
            }
          }
        }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        dialog.dismiss();
        showErrorResponse(error);
      }
    }) {
      @Override
      protected Map<String, String> getParams() {
        return jsonFinalResult;
      }

      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> params = new HashMap<>();
        params.put("Content-Type", "application/x-www-form-urlencoded");
        return params;
      }
    };

    jsObjRequest.setTag(TAG);
    // Add the request to the RequestQueue.
    jsObjRequest.setShouldCache(false);
    requestQueue.add(jsObjRequest);
  }

  public void showServerResponse(RouteSpec routespecUploaded, JSONObject response) {
    String status;
    try {
      status = response.getString("status");
      String message = response.getJSONObject("data").getString("message");
      if (status.equals("error")) {
        Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
      }
    } catch (JSONException e) {
      e.printStackTrace();
      return;
    }
    //No error occurred, save id in object
    if (status.equals("success")) {
      Map<String, String> routeParams = new HashMap<>();
      routeParams.put("Route Name", routespecUploaded.getName());
      routeParams.put("Distance", Double.toString(routespecUploaded.getDistance()));
      routeParams.put("Setting", routespecUploaded.getSetting());
      routeParams.put("Type", routespecUploaded.getType());
      routeParams.put("Privacy", Boolean.toString(routespecUploaded.isPrivacy()));
      if (!BuildConfig.DEBUG) {
        FlurryAgent.logEvent("Route_Uploaded", routeParams);
      }
      try {
        final String id = response.getJSONObject("data").get("id").toString();
        //Save id in sharedprefs
        routespecUploaded.setId(id);
        Gson gson = new Gson();

        if (isUpdate) {
          //Delete old route, only here so the new one is already saved in UploadActivity
          if (oldRouteName != null && oldRouteName.length() > 5) {
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            sharedPrefs.edit().remove(oldRouteName).apply();
          }
        }

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Update route again with result which was send to the server
        sharedPrefs.edit().putString(routespecUploaded.getName(), gson.toJson(routespecUploaded)).apply();

        //Send ID back
        String url = baseUrl + "/route/" + id;
        Intent returnIntent = new Intent();
        returnIntent.putExtra("id", id);
        returnIntent.putExtra("url", url);
        returnIntent.putExtra("oldRouteName", oldRouteName);
        returnIntent.putExtra("updatedRouteSpec", gson.toJson(routespecUploaded));
        setResult(Activity.RESULT_OK, returnIntent);
        this.finish();
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }
  }

  public void showErrorResponse(VolleyError error) {
    String errorResponse = "";
    if (error instanceof TimeoutError || error instanceof NoConnectionError) {
      errorResponse = "Upload Error: network timeout";
    } else if (error instanceof AuthFailureError) {
      errorResponse = "Upload Error: auth error";
    } else if (error instanceof ServerError) {
      errorResponse = "Upload Error: server error";
    } else if (error instanceof NetworkError) {
      errorResponse = "Upload Error: network error";
    } else if (error instanceof ParseError) {
      errorResponse = "Upload Error: parse error";
    }
    Snackbar snackbar = Snackbar.make(coordinatorLayout, errorResponse, Snackbar.LENGTH_SHORT);
    snackbar.show();
  }

  RequestQueue mRequestQueue;

  @Override
  protected void onStop() {
    if (mRequestQueue != null) {
      mRequestQueue.cancelAll(TAG);
    }
    super.onStop();
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        this.finish();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  public Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        // The decoding is done
        case TIMEOUT_REQUEST:
          if (dialog.isShowing()) {
            dialog.dismiss();
            MaterialDialog materialDialog = new MaterialDialog.Builder(UploadActivity.this)
                .title(getString(R.string.something_went_wrong))
                .content(getString(R.string.upload_no_response))
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                  @Override
                  public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                  }
                })
                .show();
          }
          break;
        default:
          break;
      }
    }
  };
}
