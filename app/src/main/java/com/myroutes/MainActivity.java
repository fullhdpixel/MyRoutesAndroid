package com.myroutes;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flurry.android.FlurryAgent;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.gson.Gson;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.myroutes.helper.CustomFrameLayout;
import com.myroutes.helper.DataParser;
import com.myroutes.helper.DistanceMarker;
import com.myroutes.helper.GpxCreator;
import com.myroutes.helper.Haversine;
import com.myroutes.helper.Notices;
import com.myroutes.helper.PathMethods;
import com.myroutes.helper.ResourceSizes;
import com.myroutes.helper.RouteSpec;
import com.myroutes.helper.SharedPreferenceItems;
import com.myroutes.helper.SiteHelpers;
import com.myroutes.helper.UriString;
import com.myroutes.iconbox.Icon;
import com.myroutes.iconbox.IconboxAdapter;
import com.myroutes.iconbox.MapWrapperLayout;
import com.myroutes.iconbox.OnInfoWindowElemTouchListener;
import com.myroutes.tiles.CustomUrlTileProvider;
import com.myroutes.tiles.ExpandedMBTilesTileProvider;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NONE;
import static com.myroutes.MainActivity.ParserTask.DIVISION_POINTS_NUMBER;
import static com.myroutes.UploadActivity.baseUrl;
import static com.myroutes.iconbox.Icon.checkLatLngEqual;
import static java.lang.Integer.parseInt;

/**
 * The type Main activity.
 */
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, NavigationView.OnNavigationItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
  private static final String TAG = "MainActivity";

  private static final int REQUEST_CHECK_SETTINGS = 99;
  //Maps
  private GoogleMap gMap;
  private LatLng coordinates;
  private static final int paddingBuilder = 150;
  private static final int animateDelay = 800;
  //Testing coordinates
  private LatLng defaultCoordinates = new LatLng(52.3702, 4.8952);
  public static boolean testing = false;

  //Positioning
  private MarkerOptions markerOpt = new MarkerOptions();
  private ArrayList<Icon> icons = new ArrayList<>();
  private ArrayList<LatLng> path = new ArrayList<>();

  private ImageView drawerImageView;
  private TextView viewmode, attribution;
  private String travelSetting = "bicycling";
  private String routename = "";

  CustomFrameLayout frame;
  NavigationView navigationView;

  private boolean startFromMarker = true;
  private boolean snapToRoad = true;
  private boolean useMetric = true;
  private boolean importFile = false;
  private boolean privacy = false;
  private boolean viewMode = false;

  Toolbar toolbar;

  FloatingActionMenu routeoption;
  FloatingActionButton driving, running, bicycling, backbutton;

  //To use vector graphics in older versions of android
  static {
    AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
  }

  MeteorSingleton meteorSingleton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    meteorSingleton = MeteorSingleton.getInstance();
    MapsInitializer.initialize(this);

    //Set Default Preferences if not set
    SharedPreferenceItems prefs = new SharedPreferenceItems();
//    prefs.clearAll(this);
    prefs.setDefaults(this);

    setContentView(R.layout.activity_main);
    toolbar = (Toolbar) findViewById(R.id.toolbar);
    String distanceText;
    if (useMetric) {
      distanceText = String.format(Locale.US, "%.2f km", distanceResult);
    } else {
      distanceText = String.format(Locale.US, "%.2f m", Haversine.toMiles(distanceResult));
    }
    setSupportActionBar(toolbar);
    getSupportActionBar();
    setTitle(distanceText);

    // Obtain the SupportMapFragment and get notified when the map is ready to be used.
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
    mapFragment.getMapAsync(this);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    frame = (CustomFrameLayout) findViewById(R.id.frame);
    drawerImageView = (ImageView) findViewById(R.id.drawer_bg);
    viewmode = (TextView) findViewById(R.id.viewmode);
    attribution = (TextView) findViewById(R.id.attribution);
    routeoption = (FloatingActionMenu) findViewById(R.id.routeoption);
    driving = (FloatingActionButton) findViewById(R.id.driving);
    running = (FloatingActionButton) findViewById(R.id.running);
    bicycling = (FloatingActionButton) findViewById(R.id.bicycling);
    backbutton = (FloatingActionButton) findViewById(R.id.backbutton);
    routeoption.setIconAnimated(false);

    viewmode.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (viewMode) {
          changeViewMode(false);
          if (path.size() > 0) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), paddingBuilder)); //fit bounds
          }
          if (importFile) {
            updateMarkerVisibility();
          }
        }
      }
    });

    backbutton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        deleteLast();
      }
    });

    //Fab menu - Change mode
    driving.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        setTravelSetting("driving");
      }
    });

    running.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        setTravelSetting("walking");
      }
    });

    bicycling.setOnClickListener(new View.OnClickListener() {
      public void onClick(View v) {
        setTravelSetting("bicycling");
      }
    });

    backbutton.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        if (snapToRoad) {
          backbutton.setProgress(0, true);
          Toast.makeText(getApplicationContext(), getString(R.string.snap_to_road_off), Toast.LENGTH_SHORT).show();
        } else {
          backbutton.setProgress(100, true);
          Toast.makeText(getApplicationContext(), getString(R.string.snap_to_road_on), Toast.LENGTH_SHORT).show();
        }
        snapToRoad = !snapToRoad;
        return true;
      }
    });

    //Opened from link
    if (getIntent().getData() != null) {
      Uri data = getIntent().getData();
      if (data != null) {
        SiteHelpers.DownloadRoute(this, handler, data);
      }
    }

    //Get -default_coordinates- to overwrite amsterdam
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    Gson gson = new Gson();
    String coordinatesFromPrefs = gson.toJson(defaultCoordinates);
    coordinatesFromPrefs = sharedPrefs.getString("-default_coordinates-", coordinatesFromPrefs);
    defaultCoordinates = gson.fromJson(coordinatesFromPrefs, LatLng.class);
    coordinates = defaultCoordinates;


    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      Notices.showChangelogNotice(this, sharedPrefs);
    }
  }

  /**
   * Sets travel setting.
   * And updates Fab Menu accordingly
   *
   * @param travelSetting the travel setting
   */

  public void setTravelSetting(String travelSetting) {
    this.travelSetting = travelSetting;
    switch (travelSetting) {
      case "driving":
        routeoption.getMenuIconView().setImageResource(R.drawable.ic_directions_car_black_24dp);
        routeoption.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.driving));
        routeoption.setMenuButtonColorPressed(ContextCompat.getColor(this, R.color.pressedDriving));
        routeoption.close(true);
        drawerImageView.setImageDrawable(getResources().getDrawable((R.drawable.sidebar)));
        break;
      case "walking":
        routeoption.getMenuIconView().setImageResource(R.drawable.ic_directions_run_black_24dp);
        routeoption.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.walking));
        routeoption.setMenuButtonColorPressed(ContextCompat.getColor(this, R.color.pressedWalking));
        routeoption.close(true);
        drawerImageView.setImageDrawable(getResources().getDrawable((R.drawable.sidebarwalking)));
        break;
      case "bicycling":
        routeoption.getMenuIconView().setImageResource(R.drawable.ic_directions_bike_black_24dp);
        routeoption.setMenuButtonColorNormal(ContextCompat.getColor(this, R.color.toolbar));
        routeoption.setMenuButtonColorPressed(ContextCompat.getColor(this, R.color.pressedBicycling));
        routeoption.close(true);
        drawerImageView.setImageDrawable(getResources().getDrawable((R.drawable.sidebarbicycling)));
        break;
    }
  }

  String routeType;
  String routeSetting;
  String routeDescription;

  public void setRouteSpec(RouteSpec routeSpec) {
    this.icons.clear();
    this.icons = routeSpec.getIcons();

    this.path.clear();
    this.path = routeSpec.getPath();
    this.routename = routeSpec.getName();
    this.routeType = routeSpec.getType();
    this.routeSetting = routeSpec.getSetting();
    this.routeDescription = routeSpec.getDescription();
    setTravelSetting(routeType);

    this.distanceResult = routeSpec.getDistance();
    this.importFile = routeSpec.isImported();
    this.viewMode = routeSpec.isImported();

    Handler handler = new Handler();
    handler.postDelayed(importfromsite, 1000);
  }

  private Runnable importfromsite = new Runnable() {
    public void run() {
      saveRoute(false, true); //Save route directly when opened from site

      changeViewMode(false);
      drawLines(true);
      drawIcons();
    }
  };

  @Override
  public void onStart() {
    meteorSingleton.reconnect();
    initGps(); // Restart tracking every time onStart is called

    //Transparente toolbar
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean toolbarTransparency = sharedPrefs.getBoolean("-toolbar_transparency-", false);
    if (getSupportActionBar() != null) {
      if (toolbarTransparency) {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.toolbarSemi)));
      } else {
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.toolbar)));
      }
    }
    String unit = sharedPrefs.getString("-unit-", "0");
    useMetric = parseInt(unit) == 0;
    addSavedRoutes();
    updateDistanceToolbar();
    //Set location on startup correctly
    if (mGoogleApiClient != null) {
      mGoogleApiClient.connect();
    }

    startLocationUpdates();

    int mapSetting = parseInt(sharedPrefs.getString("-maptype-", "0"));
    int mapboxSetting = parseInt(sharedPrefs.getString("-mapboxtype-", "0"));
    int mapBoxQuality = parseInt(sharedPrefs.getString("-mapboxquality-", "0"));
    changeMapType(mapSetting + 1, mapboxSetting, mapBoxQuality);
    if (!path.isEmpty()) {
      drawLines(false); //In order to recalculate Distance markers, since its dependent on accumulated
    }

    if (getIntent() != null) {
      boolean signedup = getIntent().getBooleanExtra("signedup", false);
      boolean loggedin = getIntent().getBooleanExtra("loggedin", false);
      if (signedup) {
        Toast.makeText(getApplicationContext(), getString(R.string.thanks_for_joining) + " " + getString(R.string.app_name), Toast.LENGTH_SHORT).show();
        changeUserState();
        //Clear intent
        setIntent(null);
        super.onStart();
        return;
      }
      if (loggedin) {
        forceLoggedin = true;
        changeUserState();
        Toast.makeText(getApplicationContext(), getString(R.string.login_success), Toast.LENGTH_SHORT).show();
        //Clear intent
        setIntent(null);
        super.onStart();
        return;
      }

      String action = getIntent().getAction();
      if (action != null) {
        if (action.compareTo(Intent.ACTION_VIEW) == 0 || action.compareTo(ContentResolver.SCHEME_FILE) == 0) {
          String schemeStart = getIntent().getScheme();
          if (schemeStart.compareTo(ContentResolver.SCHEME_CONTENT) == 0) {
            importFilePath = getIntent().getData().getLastPathSegment().replaceAll("primary:", "/storage/emulated/0/");

            Handler handler = new Handler();
            handler.postDelayed(importGPX, 1000);
            setIntent(null);
          } else if (schemeStart.compareTo(ContentResolver.SCHEME_FILE) == 0) {
            importFilePath = getIntent().getData().getPath();

            Handler handler = new Handler();
            handler.postDelayed(importGPX, 1000);
            setIntent(null);
          }
        }
      }
    }
    changeUserState();

    super.onStart();
  }

  private String importFilePath;

  private boolean forceLoggedin = false;

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    drawLines(false);
    super.onWindowFocusChanged(hasFocus);
  }

  private Runnable importGPX = new Runnable() {
    public void run() {
      if (routename.length() > 0) {
        viewmode.setText(routename);
      }
      changeViewMode(false);
      importFile(importFilePath);
    }
  };

  private Runnable importchange = new Runnable() {
    public void run() {
      if (routename.length() > 0) {
        viewmode.setText(routename);
      }
      changeViewMode(false);
    }
  };

  //Save values
  @Override
  protected void onSaveInstanceState(Bundle outState) {
    if (path != null && path.size() > 1) {
      int unix = (int) (System.currentTimeMillis() / 1000L);
      String id = "";
      if (routeObj != null) {
        id = routeObj.getId();
      }
      importFile = importFile || viewMode; //CHANGE THIS. VIEWMODE != IMPORTFILE
      routeObj = new RouteSpec(id, routename, travelSetting, distanceResult, icons, path, importFile, privacy, unix);
      Gson gson = new Gson();
      outState.putString("routespec", gson.toJson(routeObj));
      outState.putBoolean("startFromMarker", startFromMarker);
      outState.putBoolean("viewMode", viewMode);
    }
    super.onSaveInstanceState(outState);
  }

  //Back from other activity, set values back to before exiting
  @Override
  protected void onRestoreInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null) {
      // Restore value of members from saved state
      String json = savedInstanceState.getString("routespec");
      if (json != null) {
        Gson gson = new Gson();
        setBackObject(gson.fromJson(json, RouteSpec.class));
      }
      this.startFromMarker = savedInstanceState.getBoolean("startFromMarker", true);
      this.viewMode = savedInstanceState.getBoolean("viewMode", false);
      changeViewMode(viewMode);
      useZoomPrefs();

    }
    super.onRestoreInstanceState(savedInstanceState);
  }

  private void setBackObject(RouteSpec routeSpec) {
    this.path = routeSpec.getPath();
    this.routename = routeSpec.getName();
    this.distanceResult = routeSpec.getDistance();
    this.importFile = routeSpec.isImported();
    this.privacy = routeSpec.isPrivacy();
  }

  @Override
  public void onPause() {
    if (mGoogleApiClient != null) {
      stopLocationUpdates();
    }

    //Allow screen to go off
    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    super.onPause();
  }

  @Override
  public void onDestroy() {
    if (meteorSingleton.isConnected()) {
      meteorSingleton.disconnect();
    }

    super.onDestroy();
  }

  /**
   * Import file.
   *
   * @param filepath the filepath
   */
  public void importFile(String filepath) {
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
      requestPermission(FILE_PERMISSION);
      return;
    }
    if (filepath != null) {
      File gpxFile = new File(filepath);
      clearAll(true);
      path = GpxCreator.decodeGPX(this, gpxFile);
      removeLines(true); //in this order
      routename = gpxFile.getName();
      importFile = true;
      viewmode.setText(routename);
      Handler handler = new Handler();
      handler.postDelayed(importchange, 2000);
    }
  }

  /**
   * Change view mode.
   *
   * @param viewMode the mode it is going to be set to
   */
  public void changeViewMode(boolean viewMode) {
    this.viewMode = viewMode;
    if (viewMode) {
      toolbar.setVisibility(View.GONE);

      if (gMap != null) {
        gMap.getUiSettings().setMyLocationButtonEnabled(false);
        gMap.setPadding(0, 0, 0, 0);
      }
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

      if (toggleViewModeAction != null) {
        toggleViewModeAction.setTitle("Stop View Mode");
      }
      viewmode.setVisibility(View.VISIBLE);
      backbutton.setVisibility(View.INVISIBLE);
      routeoption.setVisibility(View.INVISIBLE);
      returnToStartAction.setEnabled(false);
      snapToRoadAction.setEnabled(false);
      clearRouteAction.setEnabled(false);
      startMarkerAction.setEnabled(false);

      if (routename != null && routename.length() > 1) {
        viewmode.setText(routename);
      } else {
        viewmode.setText(R.string.viewmode);
      }
    } else {
      if (importFile) {
        if (routename != null && routename.length() > 1) {
          viewmode.setText(routename);
        } else {
          viewmode.setText(R.string.viewmode);
        }
        viewmode.setVisibility(View.VISIBLE);
        backbutton.setVisibility(View.INVISIBLE);
        routeoption.setVisibility(View.INVISIBLE);
      } else {
        viewmode.setVisibility(View.INVISIBLE);
        backbutton.setVisibility(View.VISIBLE);
        routeoption.setVisibility(View.VISIBLE);
      }

      toolbar.setVisibility(View.VISIBLE);

      if (gMap != null) {
        gMap.getUiSettings().setMyLocationButtonEnabled(true);
        gMap.setPadding(0, googleMapPadding, 0, 0);
      }

      if (toggleViewModeAction != null) {
        toggleViewModeAction.setTitle("Start View Mode");
      }
      if (snapToRoadAction != null) {
        snapToRoadAction.setEnabled(true);
        clearRouteAction.setEnabled(true);
        startMarkerAction.setEnabled(true);
      }
      getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
  }

  GoogleApiClient mGoogleApiClient; //GPS
  LocationRequest locationRequest;

  public void initGps() {
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      mGoogleApiClient = new GoogleApiClient.Builder(this)
          .addConnectionCallbacks(this)
          .addOnConnectionFailedListener(this)
          .addApi(LocationServices.API)
          .build();
      locationRequest = new LocationRequest();
      locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
      locationRequest.setInterval(1000);
      locationRequest.setFastestInterval(500);
      LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
      PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

      result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
        @Override
        public void onResult(@NonNull LocationSettingsResult result) {
          final Status status = result.getStatus();
          switch (status.getStatusCode()) {
            case LocationSettingsStatusCodes.SUCCESS:
              startLocationUpdates();
              break;
            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
              try {
                status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
              } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
              }
              break;
            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
              break;
          }
        }
      });
    } else {
      requestPermission(GPS_PERMISSION);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      // Check for the integer request code originally supplied to startResolutionForResult().
      case REQUEST_CHECK_SETTINGS:
        switch (resultCode) {
          case RESULT_OK:
            startLocationUpdates();
            break;
          case Activity.RESULT_CANCELED:
            //Request another time
            break;
        }
      case FILE_SELECT_CODE:
        if (resultCode == RESULT_OK) {
          String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
          if (filePath != null) {
            if (filePath.substring(filePath.length() - 5).contains(".gpx")) {
              importFile(filePath);
            } else {
              Toast.makeText(getApplicationContext(), getString(R.string.no_gpx_file), Toast.LENGTH_SHORT).show();
            }
          }
        }
        break;
      case REQUEST_ROUTE_ID:
        if (resultCode == RESULT_OK) {
          //Routespec is succesfully uploaded
          this.savedId = data.getStringExtra("id");
          final String url = data.getStringExtra("url");
          Gson gson = new Gson();
          RouteSpec updatedRouteSpec = gson.fromJson(data.getStringExtra("updatedRouteSpec"), RouteSpec.class);

          setBackObject(updatedRouteSpec); //Set to active route

          if (url != null) {
            showRouteSaved(url, false);
          }
          addSavedRoutes();
        }
        break;
      case REQUEST_UPDATE_SUCCESS:
        if (resultCode == RESULT_OK) {
          //Routespec is succesfully updated
          this.savedId = data.getStringExtra("id");
          Gson gson = new Gson();
          RouteSpec updatedRouteSpec = gson.fromJson(data.getStringExtra("updatedRouteSpec"), RouteSpec.class);
          setBackObject(updatedRouteSpec); //Set to active route

          Toast.makeText(getApplicationContext(), "Route successfully updated", Toast.LENGTH_SHORT).show();
        }
        break;
      case PLACE_AUTOCOMPLETE_REQUEST_CODE:
        if (resultCode == RESULT_OK) {
          Place place = PlaceAutocomplete.getPlace(this, data);
          centerPlace(place.getLatLng());
        } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
          Status status = PlaceAutocomplete.getStatus(this, data);
          Toast.makeText(getApplicationContext(), status.getStatusMessage(), Toast.LENGTH_SHORT).show();
        }
        break;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  public void showRouteSaved(String url, boolean showOverwriteButton) {
    final String endurl = url;
    MaterialDialog dialog = new MaterialDialog.Builder(this)
        .title(getString(R.string.route_saved))
        .customView(R.layout.route_saved, false)
        .positiveText(android.R.string.copyUrl)
        .negativeText(android.R.string.ok)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("myroutes url", endurl);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getApplicationContext(), getString(R.string.link_copied), Toast.LENGTH_SHORT).show();
          }
        })
        .show();

    View view = dialog.getCustomView();

    if (view != null) {
      TextView route_saved = (TextView) view.findViewById(R.id.route_saved);
      route_saved.setText(getString(R.string.route_available_web_address) + ":\n\n" + url + "\n");

      Button overwriteRoute = (Button) view.findViewById(R.id.overwriteRoute);
      if (showOverwriteButton) {
        overwriteRoute.setVisibility(View.VISIBLE);
      }
    }
    uploadRouteAction.setIcon(R.drawable.ic_cloud_done_black_24dp);

  }

  //From route_saved.xml => start uploadActivity with isUpdate = true
  public void overWriteRoute(View view) {
    //Start Uploadactivity with routespec
    RouteSpec routespec = saveRoute(true, false);
    Gson gson = new Gson();
    Intent intent = new Intent(this, UploadActivity.class);
    intent.putExtra("isUpdate", true);
    intent.putExtra("routespec", gson.toJson(routespec));
    startActivityForResult(intent, REQUEST_UPDATE_SUCCESS);
  }

  /**
   * Start location updates.
   */
  protected void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      requestPermission(GPS_PERMISSION);
      return;
    }
    if (mGoogleApiClient != null) {
      if (mGoogleApiClient.isConnected()) {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
      }
    }
  }

  /**
   * Stop location updates.
   */
  protected void stopLocationUpdates() {
    if (mGoogleApiClient != null) {
      if (mGoogleApiClient.isConnected()) {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
      }
    }
  }

  @Override
  public void onConnected(Bundle bundle) {
    //lastknown location
    updateLastLocation();
  }

  /**
   * Update last known location.
   */
  public void updateLastLocation() {
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
      requestPermission(GPS_PERMISSION);
      return;
    }
    Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    if (mGoogleApiClient != null) {
      if (!testing && location != null) {
        LatLng subresult = new LatLng(location.getLatitude(), location.getLongitude());
        if (userMarker != null) {
          userMarker.setVisible(true);
          userMarker.setPosition(subresult);
        }
        if (!(path.size() > 0)) {
          if (defaultCoordinates == coordinates && !importFile) {
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(subresult, 13), 200, null);
          } else {
            //if import file
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(subresult, 13));
          }
        }
        this.coordinates = subresult;
        FlurryAgent.setLocation((float) coordinates.latitude, (float) coordinates.longitude);
      }
    }
  }

  @Override
  public void onConnectionSuspended(int i) {
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  }

  /**
   * Update drawer menu with new items
   */
  public void addSavedRoutes() {
    final Menu menu = navigationView.getMenu();
    final Menu submenu = menu.getItem(0).getSubMenu();

    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    Map<String, ?> keys = sharedPrefs.getAll();

    SharedPreferenceItems prefs = new SharedPreferenceItems();
    //Remove all shared preferences
    if (keys == null || keys.size() == 0) {
      submenu.clear();
      submenu.add(getString(R.string.no_routes_saved));
    } else {
      submenu.clear();
    }

    int index = 0;
    assert keys != null;
    for (Map.Entry<String, ?> entry : keys.entrySet()) {
      String val = entry.getKey();
      if (!prefs.containsItem(val)) {
        //Get object from sharedprefs to calculate distance in metric/imperial
        Gson gson = new Gson();
        String localrouteName = entry.getKey();
        String json = sharedPrefs.getString(localrouteName, null);
        if (json != null) {
          routeObj = gson.fromJson(json, RouteSpec.class);

          if (routeObj != null) {
            double distance = routeObj.getDistance();

            String localdistance;
            //So we can split at " - " later when loading the route
            if (useMetric) {
              localdistance = " - " + String.format(Locale.US, "%.2f km", distance);
            } else {
              //Convert to miles
              localdistance = " - " + String.format(Locale.US, "%.2f m", Haversine.toMiles(distance));
            }
            String id = routeObj.getId();
            //Show that item is uploaded
            if (id != null && id.length() > 2) {
              submenu.add(localrouteName + localdistance);
              submenu.getItem(index).setIcon(R.drawable.ic_menu_uploaded);
            } else {
              submenu.add(localrouteName + localdistance);
              submenu.getItem(index).setIcon(R.drawable.ic_menu_not_uploaded);
            }
            index++;
          }
        }
      }
    }
  }

  boolean forceLoggedout = false;

  private void changeUserState() {
    navigationView = (NavigationView) findViewById(R.id.nav_view);
    Menu nav_Menu = navigationView.getMenu();
    MenuItem nav_login = nav_Menu.findItem(R.id.nav_login);
    MenuItem nav_logout = nav_Menu.findItem(R.id.nav_logout);

    //get userID from sharedprefs
    boolean isLoggedIn = meteorSingleton.isLoggedIn();
    if (forceLoggedin) {
      isLoggedIn = true;
    }
    if (forceLoggedout) {
      isLoggedIn = false;
    }

    if (isLoggedIn) { //Not logged in
      nav_login.setVisible(false);
      nav_logout.setVisible(true);
    } else {
      nav_login.setVisible(true);
      nav_logout.setVisible(false);
    }
  }

  /**
   * Clear all.
   *
   * @param removeMarker removes start and end marker
   */
  public void clearAll(boolean removeMarker) {
    if (removeMarker && startMarker != null && endMarker != null) {
      //Remove all markers,
      startMarker.setVisible(false);
      endMarker.setVisible(false);
      updateLastLocation();
    }
    uploadRouteAction.setIcon(R.drawable.ic_cloud_upload_black_24dp);
    deleteRouteAction.setEnabled(false);
    icons.clear();
    removeIcons();
    path.clear();
    removeLines(false);
    importFile = false;
    changeViewMode(false);
  }

  /**
   * Delete last.
   */
  public void deleteLast() {
    if (busyRemoving) {
      return;
    }
    busyRemoving = true;
    deleteRouteAction.setEnabled(false);

    if (path != null && path.size() >= 1) {
      Runnable r = new Runnable() {
        @Override
        public void run() {
          LatLng last = path.get(path.size() - 1);
          if (last.latitude == -1) {
            int numberOfPoints;
            numberOfPoints = (int) (last.longitude * DIVISION_POINTS_NUMBER) + 1;

            for (int i = 0; i < numberOfPoints; i++) {
              path.remove(path.size() - 1);
            }
          }
          path.remove(path.size() - 1);

          busyRemoving = false;
          Message msg = Message.obtain();
          msg.what = DELETE_LAST;
          msg.setTarget(handler);
          msg.sendToTarget();
        }
      };
      Thread thread = new Thread(r);
      thread.start();
    } else {
      if (path != null) {
        path.clear();
      }
      if (startMarker != null) {
        startMarker.setVisible(false);
      }
      busyRemoving = false;
    }
  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (viewMode) {
      changeViewMode(false);
      if (path.size() > 1) {
        gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), paddingBuilder)); //fit bounds
      }
      if (importFile) {
        updateMarkerVisibility();
      }
    } else if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      new MaterialDialog.Builder(this)
          .title(getString(R.string.quit_application))
          .content(getString(R.string.confirm_quit_application))
          .positiveText(android.R.string.yes)
          .negativeText(android.R.string.no)
          .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              MainActivity.this.finish();
            }
          })
          .onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            }
          })
          .show();
    }
  }

  //--------------- MENUS ---------------//
  /**
   * The Delete route action.
   */
  MenuItem uploadRouteAction, deleteRouteAction, saveRouteAction, clearRouteAction, lastMarkerAction, snapToRoadAction, startMarkerAction, toggleViewModeAction, returnToStartAction;

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu, menu);
    uploadRouteAction = menu.findItem(R.id.action_upload).setEnabled(true);
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.mainmenu, menu);
    deleteRouteAction = menu.findItem(R.id.action_delete).setEnabled(false);
    saveRouteAction = menu.findItem(R.id.action_save).setEnabled(false);
    clearRouteAction = menu.findItem(R.id.action_clear).setEnabled(false);
    lastMarkerAction = menu.findItem(R.id.action_last_marker).setEnabled(true);
    snapToRoadAction = menu.findItem(R.id.action_snap_road).setEnabled(true);
    startMarkerAction = menu.findItem(R.id.action_start_marker).setEnabled(true);
    returnToStartAction = menu.findItem(R.id.action_return_to_start).setEnabled(false);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    menu.findItem(R.id.action_start_marker).setChecked(startFromMarker);
    menu.findItem(R.id.action_snap_road).setChecked(snapToRoad);
    return true;
  }

  String savedId = "";
  private static final int REQUEST_ROUTE_ID = 1;
  private static final int REQUEST_UPDATE_SUCCESS = 2;

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_search) {
      try {
        Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN).build(this);
        startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
      } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
      }
    } else if (id == R.id.action_upload) {
      final Context context = this;

      //Upload route data to server
      if (path != null && path.size() > 0) {
        String toUploadFileName = routename;

        //Get routespec object
        if (routename != null) {
          SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
          String json = sharedPrefs.getString(routename, null);
          //Route has been uploaded before
          if (json != null && savedId.length() > 5) {
            String finalUrl = baseUrl + "/route/" + savedId;
            showRouteSaved(finalUrl, true);

          } else {
            new MaterialDialog.Builder(this)
                .iconRes(R.drawable.icon)
                .limitIconToDefaultSize()
                .title(getString(R.string.upload_to_server))
                .content(R.string.upload_generate_link)
                .inputRangeRes(6, 50, R.color.materialRed)
                .input("Route name", toUploadFileName, new MaterialDialog.InputCallback() {
                  @Override
                  public void onInput(@NonNull MaterialDialog dialog, CharSequence route) {
                    routename = route.toString().trim();
                    //Length too short (double check for spaces only)
                    if (routename.length() == 0) {
                      //Save old name
                      Toast.makeText(getApplicationContext(), getString(R.string.routename_too_short), Toast.LENGTH_SHORT).show();
                      return;
                    }
                    RouteSpec routespec = saveRoute(false, true);
                    routespec.setName(routename);
                    routespec.setSetting(travelSetting);

                    if (!meteorSingleton.isLoggedIn()) {
                      //login required
                      new MaterialDialog.Builder(MainActivity.this)
                          .title(getString(R.string.signin_required))
                          .content(getString(R.string.signin_required_explanation))
                          .positiveText(android.R.string.ok)
                          .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                              Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                              startActivity(intent);
                            }
                          })
                          .negativeText(android.R.string.cancel)
                          .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                            }
                          })
                          .show();
                      return;
                    }

                    //Start Uploadactivity with routespec
                    Gson gson = new Gson();
                    Intent intent = new Intent(context, UploadActivity.class);
                    //if has id, putExtra
                    intent.putExtra("isUpdate", false);
                    intent.putExtra("routespec", gson.toJson(routespec));
                    startActivityForResult(intent, REQUEST_ROUTE_ID);
                  }
                })
                .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
                .positiveText(android.R.string.yes)
                .negativeText(android.R.string.no)
                .show();
          }
        }
      } else {
        Toast.makeText(getApplicationContext(), getString(R.string.no_points_to_save), Toast.LENGTH_SHORT).show();
      }
    } else if (id == R.id.action_delete) {
      new MaterialDialog.Builder(this)
          .title(getString(R.string.delete_route))
          .content(getString(R.string.confirm_delete_route))
          .positiveText(android.R.string.yes)
          .negativeText(android.R.string.no)
          .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              deleteRoute();
              changeViewMode(false);
              centerLastMarker();
            }
          })
          .onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            }
          })
          .show();
      return true;
    } else if (id == R.id.action_save) {
      if (path != null && path.size() > 0) {
        showSaveDialog(getString(R.string.save_route), getString(R.string.name_your_route) + ":");
      } else {
        Toast.makeText(getApplicationContext(), getString(R.string.no_points_to_save), Toast.LENGTH_SHORT).show();
      }
      return true;
    } else if (id == R.id.action_toggle_view_mode) {
      changeViewMode(true);
    } else if (id == R.id.action_return_to_start) {
      //make copy of path
      ArrayList<LatLng> copyOfPath = new ArrayList<>();
      copyOfPath.addAll(path);
      //Remove last point from path in copy if it is not -1
      LatLng last = copyOfPath.get(path.size() - 1);
      if (last.latitude != -1) {
        copyOfPath.remove(path.size() - 1);
      }
      //reverse and add to end
      Collections.reverse(copyOfPath);
      path.addAll(copyOfPath);
      drawLines(true);
      //show message path is back at start
      Toast.makeText(getApplicationContext(), getString(R.string.path_user_point), Toast.LENGTH_SHORT).show();
    } else if (id == R.id.action_clear) {
      showClearAllDialog();
      return true;
    } else if (id == R.id.action_last_marker) {
      centerLastMarker();
      return true;
    } else if (id == R.id.action_start_marker) {
      startFromMarker = !startFromMarker;
      item.setChecked(startFromMarker);
      removeLines(false);
      return true;
    } else if (id == R.id.action_snap_road) {
      if (snapToRoad) {
        backbutton.setProgress(0, true);
      } else {
        backbutton.setProgress(100, true);
      }
      snapToRoad = !snapToRoad;
      item.setChecked(snapToRoad);
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void showClearAllDialog() {
    new MaterialDialog.Builder(this)
        .title(getString(R.string.clear_map))
        .content(getString(R.string.confirm_remove_path))
        .positiveText(android.R.string.yes)
        .negativeText(android.R.string.no)
        .onPositive(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
            clearAll(true);
            centerLastMarker();
          }
        })
        .onNegative(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
          }
        })
        .show();
  }

  /**
   * Show save dialog.
   *
   * @param title   the title
   * @param message the message
   */
  public void showSaveDialog(String title, String message) {
    final Context context = this;
    new MaterialDialog.Builder(this)
        .title(title)
        .content(message)
        //Validation
        .inputRangeRes(6, 50, R.color.materialRed)
        .positiveText(android.R.string.yes)
        .negativeText(android.R.string.no)
        .inputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS)
        .input("", routename, new MaterialDialog.InputCallback() {
          @Override
          public void onInput(@NonNull MaterialDialog dialog, CharSequence route) {
            // Do something
            String saveFileName = route.toString().trim();
            if (saveFileName.length() == 0) {
              Toast.makeText(getApplicationContext(), getString(R.string.routename_too_short), Toast.LENGTH_SHORT).show();
            } else {
              routename = saveFileName;
              saveRoute(false, true);

              SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
              //if 5 routes saved
              if (SharedPreferenceItems.numberOfSavedRoutes(sharedPrefs) >= 3) {
                Notices.showRatingNotice(context, sharedPrefs);
              }
            }
          }
        })
        .onNegative(new MaterialDialog.SingleButtonCallback() {
          @Override
          public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
          }
        })
        .show();
  }

  //--------------- END MENUS ---------------//

  /**
   * Delete route.
   */
  public void deleteRoute() {
    uploadRouteAction.setIcon(R.drawable.ic_cloud_upload_black_24dp);
    //Set disabled
    if (routename != null) {
      SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
      sharedPrefs.edit().remove(routename).apply();
      icons.clear();
      removeIcons();
      path.clear();
      removeLines(false);
      addSavedRoutes();
      importFile = false;
      changeViewMode(false);
      deleteRouteAction.setEnabled(false);
      if (startMarker != null && endMarker != null) {
        startMarker.remove();
        endMarker.remove();
      }
      Toast.makeText(getApplicationContext(), routename + " " + getString(R.string.is_deleted), Toast.LENGTH_SHORT).show();
    }
  }

  /**
   * Save route in sharedprefs
   *
   * @param isOverwrite  true if overwrite route
   * @param showToast true if should show text
   */
  public RouteSpec saveRoute(boolean isOverwrite, boolean showToast) {
    uploadRouteAction.setIcon(R.drawable.ic_cloud_upload_black_24dp);
    if (path != null && path.size() > 0) {

      //If path contains 1 element, add 1 point to the path
      if (path.size() == 1) {
        path.add(0, coordinates);
      }

      Gson gson = new Gson();
      RouteSpec routespec;
      SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

      if (isOverwrite) { //Use existing
        //Get from sharedpreferences
        String json = sharedPrefs.getString(routename, null);
        routespec = gson.fromJson(json, RouteSpec.class);
        //change path and distance
        routespec.setIcons(icons);
        routespec.setPath(path);
        routespec.setDistance(distanceResult);
        routespec.setStartMarker(PathMethods.getStartLatLng(path));
        routespec.setEndMarker(PathMethods.getEndLatLng(path));
        //Save to sharedpreferences
        sharedPrefs.edit().putString(routename, gson.toJson(routespec)).apply();
      } else { //Create new
        String routeNameResult = routename.trim();
        int unix = (int) (System.currentTimeMillis() / 1000L);
        String id = "";
        if (this.savedId != null) {
          id = this.savedId;
        }
        routespec = new RouteSpec(id, routeNameResult, travelSetting, distanceResult, icons, path, importFile, privacy, unix);
        sharedPrefs.edit().putString(routeNameResult, gson.toJson(routespec)).apply();
      }
      if (showToast) {
        addSavedRoutes();
        Toast.makeText(getApplicationContext(), routename + " " + getString(R.string.is_saved), Toast.LENGTH_LONG).show();
      }

      return routespec;
    } else {
      Toast.makeText(getApplicationContext(), R.string.action_no_points_upload, Toast.LENGTH_SHORT).show();
      return null;
    }
  }

  RouteSpec routeObj;

  /**
   * Load route.
   *
   * @param routeName the route name
   */
  public void getRoute(String routeName) {
    removeLines(false);
    clearAll(false);
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    Gson gson = new Gson();
    String json = sharedPrefs.getString(routeName, null);
    if (json != null) {
      routeObj = gson.fromJson(json, RouteSpec.class);
      //Not yet uploaded
      if (routeObj.getId().isEmpty()) {
        uploadRouteAction.setIcon(R.drawable.ic_cloud_upload_black_24dp);
      } else {
        this.savedId = routeObj.getId();
        uploadRouteAction.setIcon(R.drawable.ic_cloud_done_black_24dp);
      }
      this.icons.clear();
      this.icons = routeObj.getIcons();
      this.path.clear();
      this.path = routeObj.getPath();
      this.routename = routeObj.getName();
      this.distanceResult = routeObj.getDistance();
      this.importFile = routeObj.isImported();
      this.viewMode = false;

      //Double check
      if (snapToRoadAction != null) {
        changeViewMode(false);
      } else {
        Handler handler = new Handler();
        handler.postDelayed(importchange, 2000);
      }

      if (importFile) {
        viewmode.setText(routename);
      } else {
        changeViewMode(false);
        Toast.makeText(getApplicationContext(), routename + " " + getString(R.string.is_imported), Toast.LENGTH_SHORT).show();
      }
      deleteRouteAction.setEnabled(true);
      centerPlace = false;
      drawIcons();
      drawLines(true);
    } else {
      Toast.makeText(getApplicationContext(), routename + " " + getString(R.string.does_not_exist), Toast.LENGTH_SHORT).show();
      addSavedRoutes();
    }
  }

  public boolean centerPlace = false;

  public void centerPlace(LatLng place) {
    //Do not start from user marker
    startFromMarker = false;
    MapsInitializer.initialize(this);

    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place, 13), animateDelay, null);
    centerPlace = true;
    //Save this place in sharedprefs to override the defaultcoordinates

    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    Gson gson = new Gson();
    sharedPrefs.edit().putString("-default_coordinates-", gson.toJson(place)).apply();
  }

  public void centerLastMarker() {
    MapsInitializer.initialize(this);

    if (gMap != null) {
      if (path != null && path.size() > 1) {
        LatLng last = path.get(path.size() - 1);
        LatLng result;
        //If last is -1, center second to last
        if (last.latitude == -1) {
          result = path.get(path.size() - 2);
        } else {
          result = last;
        }
        if (!centerPlace) {
          gMap.animateCamera(CameraUpdateFactory.newLatLng(result), animateDelay, null);
        }
      } else {
        //Add additional check, prevent crashes
        try {
          if (!centerPlace && startFromMarker) { //center user location
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 13), animateDelay, null);
          } else if (!startFromMarker && startMarker != null) { //center start marker
            LatLng startPos = startMarker.getPosition();
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPos, 13), animateDelay, null);
          }
        } catch (Exception e) {
          MapsInitializer.initialize(this);
        }
      }
    }
  }

  ProgressDialog encodeDialog;
  private static final int FILE_SELECT_CODE = 0;

  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();
    String title = item.getTitle().toString();

    if (id == R.id.nav_login) {
      Intent intent = new Intent(this, LoginActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav_logout) {
      //clear sharedprefs userId
      meteorSingleton.reconnect();

      forceLoggedout = true;
      changeUserState();

      meteorSingleton.logout(new ResultListener() {
        @Override
        public void onSuccess(String result) {
          Toast.makeText(getApplicationContext(), getString(R.string.action_signed_out), Toast.LENGTH_SHORT).show();
          changeUserState();
        }

        @Override
        public void onError(String error, String reason, String details) {
          Toast.makeText(getApplicationContext(), reason, Toast.LENGTH_SHORT).show();
        }
      });
    } else if (id == R.id.nav_import) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        requestPermission(FILE_PERMISSION);
      } else {
        new MaterialFilePicker()
            .withActivity(this)
            .withRequestCode(FILE_SELECT_CODE)
            .withFilterDirectories(true)
            .withHiddenFiles(true)
            .start();
      }
    } else if (id == R.id.nav_export) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        requestPermission(FILE_PERMISSION);
      } else if (path.size() != 0) {
        DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date = new Date();
        String distance;
        if (useMetric) {
          distance = String.format(Locale.US, "%.2f km", distanceResult);
        } else {
          distance = String.format(Locale.US, "%.2f m", Haversine.toMiles(distanceResult));
        }

        final String localFilename;
        if (routename != null && routename.length() > 0 && !importFile && !routename.contains(".gpx")) {
          localFilename = routename + ".gpx";
        } else {
          localFilename = "MyRoutes " + dateFormat.format(date) + " (" + distance + ").gpx";
        }
        final String localFilenameResult = localFilename.replaceAll("/", "");

        final File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyRoutes/" + localFilename);
        new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MyRoutes").mkdirs();

        final ArrayList<LatLng> toEncodeCoordinates = new ArrayList<>(); //So it doesnt interfere with path concurrency
        toEncodeCoordinates.addAll(path);

        encodeDialog = new ProgressDialog(this);
        encodeDialog.setMessage(getString(R.string.creating_gpx));
        encodeDialog.setCancelable(false);
        encodeDialog.setInverseBackgroundForced(true);
        encodeDialog.show();
        Runnable r = new Runnable() {
          @Override
          public void run() {
            GpxCreator.encodeGPX(getApplicationContext(), file, localFilenameResult, toEncodeCoordinates);

            //calculate Bounds of path
            Message msg = Message.obtain();
            msg.what = ENCODE_GPX;
            msg.obj = new UriString(Uri.fromFile(file), localFilename);
            msg.setTarget(handler);
            msg.sendToTarget();

            Runnable r = new Runnable() {
              @Override
              public void run() {
                try {
                  Thread.sleep(10000);
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
          }
        };
        Thread thread = new Thread(r);
        thread.start();
      } else {
        Toast.makeText(getApplicationContext(), getString(R.string.no_path_to_share), Toast.LENGTH_SHORT).show();
      }
    } else if (id == R.id.nav_offline) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        requestPermission(FILE_PERMISSION);
      } else {
        Intent intent = new Intent(this, OfflineActivity.class);
        startActivity(intent);
      }
    } else if (id == R.id.nav_settings) {
      Intent intent = new Intent(this, SettingsActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav_donate) {
      Intent intent = new Intent(this, DonateActivity.class);
      startActivity(intent);
    } else if (id == R.id.nav_website) {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(UploadActivity.baseUrl));
      startActivity(intent);
    } else if (!title.contains(getString(R.string.routes_saved))) {
      //Route from menu
      int i = title.lastIndexOf(" - ");
      String routename = title.substring(0, i).trim();
      getRoute(routename);
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  @Override
  public void onClick(View view) {
  }

  @Override
  public void onStop() {
    if (mGoogleApiClient != null) {
      mGoogleApiClient.disconnect();
    }
    super.onStop();
  }

  private static final int GPS_PERMISSION = 1;
  private static final int FILE_PERMISSION = 2;
  private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 3;

  /**
   * Request permission.
   *
   * @param requestCode the request code
   */
  @SuppressLint("InlinedApi")
  public void requestPermission(int requestCode) {
    switch (requestCode) {
      case GPS_PERMISSION:
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GPS_PERMISSION);
        break;
      case FILE_PERMISSION:
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, FILE_PERMISSION);
        break;
    }
  }

  //GPS (1) and File permission (2)
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
    //Avoid restarting activity on import
    if (requestCode == GPS_PERMISSION) {
      if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        recreate();
      }
    } else if (requestCode == FILE_PERMISSION) {
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
        new MaterialFilePicker()
            .withActivity(this)
            .withRequestCode(FILE_SELECT_CODE)
            .withFilterDirectories(true)
            .withHiddenFiles(true)
            .start();
      }
    }
  }

  public void onLocationChanged(Location location) {
    double latitude = location.getLatitude();
    double longitude = location.getLongitude();

    LatLng subresult = new LatLng(latitude, longitude);
    if (!testing) {
      if (defaultCoordinates == coordinates) {
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(subresult, 13), 200, null);
      } else if (viewMode) {
        if (location.hasBearing()) {
          float speed;
          if (location.hasSpeed()) {
            speed = location.getSpeed();
            //Bearing only update if speed is minimal 4km/uur
            if (speed < 2.5f) {
              location.removeBearing();
            }
          }
          //Limit zoom out level to 13
          float zoom = gMap.getCameraPosition().zoom;
          if (zoom < 13) {
            zoom = 13;
          }
          CameraPosition cameraPosition = new CameraPosition.Builder().target(subresult).zoom(zoom).bearing(location.getBearing()).build();
          gMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), null);
        } else {
          gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(subresult, 13), null);
        }
      }
      this.coordinates = subresult;
      if (userMarker != null) {
        userMarker.setVisible(true);
        userMarker.setPosition(coordinates);
      }
    }
  }

  float currentZoomLvl;
  Marker userMarker;
  MarkerOptions markerStart, markerEnd;
  boolean contextmenu = false; //To avoid closing the contextmenu and drawing a line at the same time

  //Iconbox
  ViewGroup infoWindow;
  private OnInfoWindowElemTouchListener infoButtonListener;

  int googleMapPadding;

  @Override
  public void onMapReady(GoogleMap googleMap) {
    gMap = googleMap;
    // Calculate ActionBar height
    TypedValue tv = new TypedValue();
    if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
      googleMapPadding = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
    }

    gMap.setPadding(0, googleMapPadding, 0, 0);
    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
      gMap.setMyLocationEnabled(true);
    }
    gMap.setMapType(1);
    if (coordinates == defaultCoordinates) {
      markerOpt.visible(false);
    }
    markerOpt.position(coordinates);
    if (userMarker == null) {
      userMarker = gMap.addMarker(markerOpt);
    }

    //Initialize startMarker
    markerStart = new MarkerOptions().flat(false).title("Start");
    markerEnd = new MarkerOptions().flat(false).title("End");

    Bitmap[] bitmaps = ResourceSizes.createStartEndBitmaps(this);
    markerStart.icon(BitmapDescriptorFactory.fromBitmap(bitmaps[0]));
    markerEnd.icon(BitmapDescriptorFactory.fromBitmap(bitmaps[1]));

    markerStart.position(defaultCoordinates);
    startMarker = gMap.addMarker(markerStart);
    startMarker.setVisible(false);

    markerEnd.position(defaultCoordinates);
    endMarker = gMap.addMarker(markerEnd);
    endMarker.setVisible(false);

    this.infoWindow = (ViewGroup) getLayoutInflater().inflate(R.layout.infowindow, null);

//  Prepare infoWindow
    GridView gridview = (GridView) infoWindow.findViewById(R.id.gridview);
    gridview.setBackgroundResource(R.color.iconbox_bg);
    gridview.setAdapter(new IconboxAdapter(this));

    int widthGridView = ResourceSizes.getWidthGridView(this);

    gridview.setLayoutParams(new RelativeLayout.LayoutParams(widthGridView, ViewGroup.LayoutParams.WRAP_CONTENT));

    final MapWrapperLayout mapWrapperLayout = (MapWrapperLayout) findViewById(R.id.map_relative_layout);
    mapWrapperLayout.init(gMap, 0);

    // Setting custom OnTouchListener which deals with the pressed state so it shows up
    this.infoButtonListener = new OnInfoWindowElemTouchListener(gridview) {
      @Override
      protected void onClickConfirmed(View view, Marker marker, float width, float height, float x, float y) {
        invisibleMarker.hideInfoWindow();
        LatLng position = marker.getPosition();

        Icon icon = IconboxAdapter.getClickedIcon(position, width, height, x, y);
        if (icon != null) {
          icons.add(icon);
          drawIcons();
        }
      }
    };
    gridview.setOnTouchListener(infoButtonListener);

    gMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
      @Override
      public View getInfoWindow(Marker marker) {
        // Setting up the infoWindow with current's marker info
        // We must call this to set the current marker and infoWindow references
        // to the MapWrapperLayout
        infoButtonListener.setMarker(marker);
        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
        return infoWindow;
      }

      @Override
      public View getInfoContents(Marker marker) {
        return null;

      }
    });

    //Context menu
    gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
      @Override
      public void onMapLongClick(LatLng location) {
        //Create invisible marker and show custom Info Window
        MarkerOptions markerInvisible = new MarkerOptions().alpha(0.00f).infoWindowAnchor(0.5f, 1);
        markerInvisible.position(location);
        Marker invisibleMarker = gMap.addMarker(markerInvisible);
        invisibleMarker.setTitle("invisible");
        invisibleMarker.showInfoWindow();
        setInvisibleMarker(invisibleMarker);

        contextmenu = true;
      }
    });

    gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
      @Override
      public void onMapClick(LatLng destination) {
        if (contextmenu) {
          contextmenu = false;
        } else if (!importFile && !viewMode) { //only if not in view mode
          if (snapToRoad) {
            if (isNetworkAvailable()) { //Check if internet is available
              //need startpoint
              LatLng origin;
              if (path.size() == 0) {
                if (startFromMarker && coordinates != null) {
                  //Startpoint is clearly defined as user coordinates
                  origin = coordinates;
                } else {
                  //Startpoint is yet to be set
                  //Just add first point touched on the map
                  path.add(destination);
                  drawLines(false);
                  return;
                }
              } else {
                //Last point in path
                LatLng last = path.get(path.size() - 1);
                if (last.latitude == -1) {
                  //If last point is outOfBounds, get the second to last point as origin
                  origin = path.get(path.size() - 2);
                } else {
                  origin = last;
                }
              }
              gMap.animateCamera(CameraUpdateFactory.newLatLng(destination), animateDelay, null);

              calcSnapRoute(origin, destination);
            } else {
              Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
            }
          } else {
            if (path.size() == 0) {
              if (startFromMarker && coordinates != null) {
                path.add(coordinates);
              }
            }
            path.add(destination);
            drawLines(false);
          }
          deleteRouteAction.setEnabled(false);
        }
      }
    });

    gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
      @Override
      public boolean onMarkerClick(Marker marker) {
        //Check if marker is from iconbox
        if (marker != null) {
          LatLng markerPosition = marker.getPosition();
          if (Icon.locationInMarkers(iconMarkers, markerPosition)) {
            if (timeOutMarker > 0 && System.currentTimeMillis() - timeOutMarker > 1000) {
              //Remove 1 marker
              LatLng position = marker.getPosition();
              int posIcons = Icon.getTagPositionInIcons(icons, position);
              int posMarkers = Icon.getTagPositionInMarkers(iconMarkers, position);

              if (posIcons != -1) {
                icons.remove(posIcons);
              }

              if (posMarkers != -1) {
                iconMarkers.get(posMarkers).remove();
                iconMarkers.remove(posMarkers);
              }
            }
          } else if (!importFile && !viewMode) { //Only if not in view mode
            if (contextmenu) {
              contextmenu = false;
            } else {
              if (snapToRoad) {
                if (isNetworkAvailable()) { //Check if internet is available
                  //need start point
                  if (path.size() > 0) {
                    LatLng origin;
                    LatLng last = path.get(path.size() - 1);
                    if (last.latitude == -1) {
                      //If last point is outOfBounds, get the second to last point as origin
                      origin = path.get(path.size() - 2);
                    } else {
                      origin = last;
                    }
                    calcSnapRoute(origin, markerPosition);
                  }
                } else {
                  Toast.makeText(getApplicationContext(), getString(R.string.network_unavailable), Toast.LENGTH_SHORT).show();
                }
              } else {
                path.add(markerPosition);
                drawLines(true);
              }

              //Back at start point
              if (startMarker != null && path.size() > 1) {
                if (checkLatLngEqual(markerPosition, startMarker.getPosition())) {
                  Toast.makeText(getApplicationContext(), getString(R.string.path_start_point), Toast.LENGTH_SHORT).show();
                  //Back at user point
                }
              }
              if (userMarker != null) {
                if (checkLatLngEqual(markerPosition, userMarker.getPosition())) {
                  Toast.makeText(getApplicationContext(), getString(R.string.path_user_point), Toast.LENGTH_SHORT).show();
                }
              }
            }
          }
        }
        return true;
      }
    });

    gMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
      @Override
      public void onCameraMove() {
        //Check if zoomlvl changed
        CameraPosition cameraPosition = gMap.getCameraPosition();

        if (Math.abs(cameraPosition.zoom - currentZoomLvl) > 0.5) {
          if (path.size() > 0) {
            updateMarkerVisibility();
          }
        }
      }
    });

    //Use maptype, from setting
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    int mapSetting = parseInt(sharedPrefs.getString("-maptype-", "0"));
    int mapboxSetting = parseInt(sharedPrefs.getString("-mapboxtype-", "0"));
    int mapBoxQuality = parseInt(sharedPrefs.getString("-mapboxquality-", "0"));
    changeMapType(mapSetting + 1, mapboxSetting, mapBoxQuality);
  }

  double distanceMarkerOffset = 1; //Lowest distance between markers

  Marker invisibleMarker;

  public void setInvisibleMarker(Marker marker) {
    invisibleMarker = marker;
  }

  private boolean isNetworkAvailable() {
    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
    return activeNetworkInfo != null && activeNetworkInfo.isConnected();
  }

  private boolean busyDrawing = false; //Prevents double clicking and multiple snap routes at the same time
  private boolean busyRemoving = false; //Prevents double method execution at the same time

  public void calcSnapRoute(LatLng origin, LatLng destination) {
    if (busyDrawing) {
      return;
    }
    busyDrawing = true;
    String url = getUrl(origin, destination, travelSetting);
    FetchUrl FetchUrl = new FetchUrl();
    FetchUrl.execute(url);
  }

  List<Polyline> polylines = new ArrayList<>();

  /**
   * Remove lines from the map, reset routename.
   */
  public void removeLines(boolean centerBounds) {
    bounds = new LatLngBounds.Builder(); //Reset bounds
    routename = ""; //Reset route
    savedId = "";
    for (Polyline line : polylines) {
      line.remove();
    }
    polylines.clear();
    drawLines(centerBounds);
  }

  double distanceResult;
  Marker startMarker, endMarker;

  LatLngBounds.Builder bounds;

  ArrayList<Double> accumulated = new ArrayList<>(); //Get accumulated line lengths
  ArrayList<LatLng> pathNoOutOfBounds = new ArrayList<>(); //Without outOfBounds coordinates
  PolylineOptions polyOpt;

  /**
   * Draw lines on map
   */
  public void drawLines(final boolean centerBounds) {
    distanceResult = 0;
    removeDistanceMarkers();
    if (!(path.size() > 0)) { // Path is empty
      saveRouteAction.setEnabled(false);
      clearRouteAction.setEnabled(false);
      lastMarkerAction.setEnabled(false);
      returnToStartAction.setEnabled(false);
      updateDistanceToolbar();
    } else {
      saveRouteAction.setEnabled(true);
      returnToStartAction.setEnabled(true);
      if (!importFile) {
        clearRouteAction.setEnabled(true);
      }
      lastMarkerAction.setEnabled(true);
      polyOpt = new PolylineOptions();
      polyOpt.zIndex(99);

      //Get track color from shared prefs
      SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
      int trackColor = sharedPrefs.getInt("-color_line-", 0xFF246DBF);
      int trackWidth = ResourceSizes.getTrackWidth(this);
      polyOpt.width(trackWidth).color(trackColor).geodesic(false);

      LatLng startPos = PathMethods.getStartLatLng(path);
      // hightlight start point
      if (startMarker == null) {
        startMarker = gMap.addMarker(markerStart);
      } else {
        startMarker.setPosition(startPos);
        startMarker.setVisible(true);
      }

      if (endMarker == null) {
        endMarker = gMap.addMarker(markerEnd);
      } else {
        LatLng endPos = PathMethods.getEndLatLng(path);
        if (viewMode || importFile) {
          endMarker.setPosition(endPos);
          endMarker.setVisible(true);
        } else {
          endMarker.setVisible(false);
        }
      }

      if (importFile) {
        startMarker.setTitle("startmarker");

        Bitmap[] bitmaps = ResourceSizes.createStartEndBitmaps(this);
        startMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmaps[0]));
        endMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmaps[1]));
      }

      final ArrayList<LatLng> drawableCoordinates = new ArrayList<>(); //So it doesnt interfere with path concurrency
      drawableCoordinates.addAll(path);

      if (path.size() > 1) {
        Runnable r = new Runnable() {
          @Override
          public void run() {
            bounds = new LatLngBounds.Builder();

            //Remove out of bounds elements
            pathNoOutOfBounds.clear();
            for (int i = 0; i < path.size(); i++) {
//            for (LatLng coordinate : drawableCoordinates) {
              if (drawableCoordinates.get(i).latitude != -1) {
                pathNoOutOfBounds.add(drawableCoordinates.get(i));
                bounds.include(drawableCoordinates.get(i));
              }
            }

            accumulated.clear(); //Get accumulated line lengths
            accumulated.add(0.00);
            //Draw lines
            for (int j = 0; j < pathNoOutOfBounds.size() - 1; j++) {
              LatLng from = pathNoOutOfBounds.get(j);
              LatLng to = pathNoOutOfBounds.get(j + 1);
              polyOpt.add(from, to);

              distanceResult += Haversine.calc(from, to);

              if (useMetric) {
                accumulated.add(distanceResult * 1000);
              } else {
                accumulated.add(Haversine.toMiles(distanceResult * 1000));
              }

            }

            //calculate Bounds of path
            Message msg = Message.obtain();
            msg.what = DRAW_LINES;
            msg.obj = centerBounds;
            msg.setTarget(handler);
            msg.sendToTarget();
          }
        };
        Thread thread = new Thread(r);
        thread.start();
      }
    }
  }

  IconGenerator iconFactory;

  //----- DISTANCE MARKERS -----/
  private void drawDistanceMarkers() {
    //Check settings
    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean showDistanceMarkers = sharedPrefs.getBoolean("-showdistancemarkers-", true);
    if (showDistanceMarkers) {
      iconFactory = new IconGenerator(this);
      iconFactory.setContentPadding(10, 0, 10, 0);
      Runnable r = new Runnable() {
        @Override
        public void run() {
          double offset = 1000; //Draw all possible markes, use lowest value

          // Number of distance markers to be added
          double count;
          if (useMetric) {

            count = Math.floor(distanceResult * 1000 / offset);
            accumulated.add(distanceResult * 1000);
          } else {
            count = Math.floor(Haversine.toMiles(distanceResult * 1000) / offset);
          }

          //Remove all unnecessary distance markers...
          //zodra aantal markers groter is dan de totale afstand
          //verwijder de laatste afstandmarker
//          while (distanceMarkers.size() - 1 > distanceResult) {
//            Message msg = Message.obtain();
//            msg.what = REMOVE_LAST_DISTANCE_MARKER;
//            msg.setTarget(handler);
//            msg.sendToTarget();
//          }

          int l = 0; // Position in accumulated line length array
          //start m after ...
          //te tekenen wat er nog niet is
//          for (int m = distanceMarkers.size() - 1; m <= count; m++) {
          for (int m = 1; m <= count; m++) {
            double distance = offset * m;

            // Find the first accumulated distance that is greater
            // than the distance of this marker
            while (l < accumulated.size() - 1 && accumulated.get(l) < distance) {
              l++;
            }
            // Now grab the two nearest points either side of
            // distance marker position and interpolate on)
            if (l - 1 >= 0 && l != pathNoOutOfBounds.size()) { //Cannot be last element...
              LatLng p1 = pathNoOutOfBounds.get(l - 1);
              LatLng p2 = pathNoOutOfBounds.get(l);

              double ratio = (distance - accumulated.get(l - 1)) / (accumulated.get(l) - accumulated.get(l - 1));
              LatLng interpolatePos = SphericalUtil.interpolate(p1, p2, ratio);
              String iconText = String.valueOf((int) distance / 1000);

              Message msg = Message.obtain();
              msg.what = ADD_DISTANCE_MARKER;
              msg.obj = new DistanceMarker(iconText, interpolatePos);

              msg.setTarget(handler);
              msg.sendToTarget();
            }
          }
          Message msg = Message.obtain();
          msg.what = UPDATE_MARKER_VISIBILITY;
          msg.setTarget(handler);
          msg.sendToTarget();
        }
      };
      Thread thread = new Thread(r);
      thread.start();
    } else {
      removeDistanceMarkers();
    }
  }

  private ArrayList<Marker> iconMarkers = new ArrayList<>();
  private ArrayList<Marker> distanceMarkers = new ArrayList<>();

  private static final int ADD_DISTANCE_MARKER = 0;
  private static final int REMOVE_LAST_DISTANCE_MARKER = 1;
  private static final int UPDATE_MARKER_VISIBILITY = 2;
  public static final int DOWNLOAD_ROUTE = 3;
  public static final int DOWNLOAD_ROUTE_ERROR = 4;
  public static final int DELETE_LAST = 5;
  public static final int DRAW_LINES = 6;
  public static final int ENCODE_GPX = 7;
//  public static final int DECODE_GPX = 8;
  public static final int TIMEOUT_REQUEST = 9;

  public Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        // The decoding is done
        case ADD_DISTANCE_MARKER:
          DistanceMarker distanceValue = (DistanceMarker) msg.obj;
          String iconText = distanceValue.iconText;
          LatLng interpolatePos = distanceValue.interpolatePos;
          addDistanceMarker(iconText, interpolatePos);
          break;
        case REMOVE_LAST_DISTANCE_MARKER:
          distanceMarkers.get(distanceMarkers.size() - 1).remove(); //Remove from list
          distanceMarkers.remove(distanceMarkers.size() - 1); //Remove from map
          break;
        case UPDATE_MARKER_VISIBILITY:
          updateMarkerVisibility();
          break;
        case DOWNLOAD_ROUTE:
          RouteSpec routeSpec = (RouteSpec) msg.obj;
          setRouteSpec(routeSpec);
          break;
        case DOWNLOAD_ROUTE_ERROR:
          String errorResponse = (String) msg.obj;
          Toast.makeText(getApplicationContext(), errorResponse, Toast.LENGTH_SHORT).show();
          break;
        case DELETE_LAST:
          centerLastMarker();
          removeLines(false);
          break;
        case DRAW_LINES:
          polylines.add(gMap.addPolyline(polyOpt));
          boolean centerBounds = (boolean) msg.obj;

          if (centerBounds && bounds != null) {
            gMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), paddingBuilder)); //fit bounds
          }

          //this can be optimized
          //only draw distance markers that are necessary
          drawDistanceMarkers();
          updateDistanceToolbar();
          break;
        case ENCODE_GPX:
          encodeDialog.dismiss();
          UriString uriString = (UriString) msg.obj;
          Uri fileUri = uriString.filePath;
          String localFilename = uriString.filename;
          try {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.setType("text/xml");
            startActivity(Intent.createChooser(shareIntent, localFilename));
          } catch (Exception e) {
            Toast.makeText(getApplicationContext(), getString(R.string.selected_file_unsharable), Toast.LENGTH_SHORT).show();
          }
          break;
        case TIMEOUT_REQUEST:
          if (encodeDialog.isShowing()) {
            encodeDialog.dismiss();
            new MaterialDialog.Builder(getApplicationContext())
                .title(getString(R.string.something_went_wrong))
                .content(getString(R.string.timeout))
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

  public void updateDistanceToolbar() {
    //Set Distance in title bar
    String result;
    if (useMetric) {
      result = String.format(Locale.US, "%.2f km", distanceResult);
    } else {
      result = String.format(Locale.US, "%.2f m", Haversine.toMiles(distanceResult));
    }
    getSupportActionBar();
    setTitle(result);
  }

  private void addDistanceMarker(CharSequence text, LatLng position) {
    MarkerOptions markerOptions = new MarkerOptions().
        icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(text))).
        position(position).
        anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV()).visible(false);
    Marker distanceMarker = gMap.addMarker(markerOptions);
    distanceMarker.setTitle(text.toString());
    distanceMarkers.add(distanceMarker);
  }

  private void removeDistanceMarkers() {
    for (Marker marker : distanceMarkers) {
      marker.remove();
    }
    distanceMarkers.clear();
  }

  private void updateMarkerVisibility() {
    //Calculate offset based on zoom
    float zoomLvl = gMap.getCameraPosition().zoom;
    if (!useMetric) { //When in miles show more distanceMarkers
      zoomLvl += 1;
    }
    if (zoomLvl > 13) {
      distanceMarkerOffset = 1;
    } else if (zoomLvl > 12.5) {
      distanceMarkerOffset = 2;
    } else if (zoomLvl > 12) {
      distanceMarkerOffset = 3;
    } else if (zoomLvl > 11.5) {
      distanceMarkerOffset = 4;
    } else if (zoomLvl > 11) {
      distanceMarkerOffset = 5;
    } else if (zoomLvl > 10.5) {
      distanceMarkerOffset = 8;
    } else if (zoomLvl > 10) {
      distanceMarkerOffset = 10;
    } else if (zoomLvl > 9) {
      distanceMarkerOffset = 25;
    } else if (zoomLvl > 8) {
      distanceMarkerOffset = 50;
    } else if (zoomLvl > 7) {
      distanceMarkerOffset = 100;
    } else if (zoomLvl > 5) {
      distanceMarkerOffset = 500;
    }

    for (Marker marker : distanceMarkers) {
      int markerVal = Integer.parseInt(marker.getTitle());
      if (markerVal % (int) distanceMarkerOffset == 0) {
        marker.setVisible(true);
      } else {
        marker.setVisible(false);
      }
    }
  }

  //---- ICON BOX -----//
  private void removeIcons() {
    for (Marker iconMarker : iconMarkers) {
      iconMarker.remove();
    }
  }

  private void drawIcons() {
    //Scale icon
    int iconSize = ResourceSizes.getIconSize(this);

    for (Icon iconItem : icons) {
      int resourceInt = IconboxAdapter.getResourceInt(iconItem.type);
      if (resourceInt != -1) {

        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), resourceInt);
        Bitmap scaledIconBitmap = Bitmap.createScaledBitmap(iconBitmap, iconSize, iconSize, false);

        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setContentPadding(10, 0, 10, 0);

        BitmapDrawable ob = new BitmapDrawable(getResources(), scaledIconBitmap);
        iconFactory.setBackground(ob);

        addIconMarker(iconFactory, iconItem.location);
      }
    }
  }

  long timeOutMarker = 0;

  private void addIconMarker(IconGenerator iconFactory, LatLng position) {
    MarkerOptions markerOptions = new MarkerOptions().
        icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon())).
        position(position).
        anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

    Marker iconMarker = gMap.addMarker(markerOptions);
    iconMarker.setTitle("visible");
    iconMarkers.add(iconMarker);
    timeOutMarker = System.currentTimeMillis(); //current timestamp
  }

  private TileOverlay mSelectedTileOverlay;
  private static final int MAP_TILE_GOOGLE_NORMAL = 1;
  private static final int MAP_TILE_GOOGLE_SATELLITE = 2;
  private static final int MAP_TILE_GOOGLE_HYBRID = 3;
  private static final int MAP_TILE_OPEN_STREET = 4;
  private static final int MAP_TILE_OPEN_TOPO = 5;
  private static final int MAP_TILE_OPEN_CYCLE = 6;
  private static final int MAP_TILE_LYRK = 7;
  private static final int MAP_TILE_MAPBOX = 8;

  /**
   * Change map type.
   *
   * @param maptype the maptype
   */
  public void changeMapType(int maptype, int mapboxSetting, int mapBoxQuality) {
    String overlayString = "";
    String attributionText = null;
    if (gMap == null) {
      return;
    }
    gMap.setMapType(maptype);
    if (mSelectedTileOverlay != null) {
      mSelectedTileOverlay.remove();
    }

    SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    boolean offline = sharedPrefs.getBoolean("offline", false);
    String filepath = sharedPrefs.getString("mbtiles", null);
    TileProvider provider;

    useZoomPrefs();

    if (offline && filepath != null && !filepath.equals("0")) {
      //Make sure permission is set
      if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
        requestPermission(FILE_PERMISSION);
        return;
      }

      //Offline Overlay
      File myMBTiles = new File(filepath);

      //Check if file exists
      if (myMBTiles.exists() && !myMBTiles.isDirectory()) {
        TileOverlayOptions opts = new TileOverlayOptions();
        provider = new ExpandedMBTilesTileProvider(myMBTiles, 256, 256);
        opts.tileProvider(provider);

        mSelectedTileOverlay = gMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider));
        gMap.setMapType(MAP_TYPE_NONE);
      } else {
        Toast.makeText(getApplicationContext(), "Offline file (.mbtiles) does not exist", Toast.LENGTH_SHORT).show();
      }
    } else {
      if (maptype == MAP_TILE_GOOGLE_NORMAL || maptype == MAP_TILE_GOOGLE_SATELLITE || maptype == MAP_TILE_GOOGLE_HYBRID) {
        attributionText = null;
        gMap.setMapType(maptype);
      } else {
        gMap.setMapType(MAP_TYPE_NONE);
        if (maptype == MAP_TILE_OPEN_STREET) {
          attributionText = "© Open Street";
          overlayString = "https://a.tile.openstreetmap.org/{z}/{x}/{y}.png";
        } else if (maptype == MAP_TILE_OPEN_TOPO) {
          attributionText = "© Open Topomap";
          overlayString = "https://a.tile.opentopomap.org/{z}/{x}/{y}.png";
        } else if (maptype == MAP_TILE_OPEN_CYCLE) {
          attributionText = "© Open Cycle";
          overlayString = "http://a.tile.opencyclemap.org/cycle/{z}/{x}/{y}.png";
        } else if (maptype == MAP_TILE_LYRK) {
          attributionText = null;
          overlayString = "https://tiles.lyrk.org/lr/{z}/{x}/{y}?apikey=ea4f307439474ce99e29662232f80885";
        } else if (maptype == MAP_TILE_MAPBOX) {
          attributionText = "© Mapbox";
          //check which mapboxtype to pick
          String type;

          if (mapboxSetting == 0) {
            type = "light";
          } else if (mapboxSetting == 1) {
            type = "dark";
          } else if (mapboxSetting == 2) {
            type = "streets-basic";
          } else if (mapboxSetting == 3) {
            type = "streets-satellite";
          } else if (mapboxSetting == 4) {
            type = "comic";
          } else if (mapboxSetting == 5) {
            type = "outdoors";
          } else if (mapboxSetting == 6) {
            type = "run-bike-hike";
          } else if (mapboxSetting == 7) {
            type = "pencil";
          } else if (mapboxSetting == 8) {
            type = "pirates";
          } else if (mapboxSetting == 9) {
            type = "emerald";
          } else if (mapboxSetting == 10) {
            type = "high-contrast";
          } else {
            //Default to light
            type = "light";
          }

          String quality;
          if (mapBoxQuality == 1) {
            //Medium: 90% jpeg compressed
            quality = ".jpg90";
          } else if (mapBoxQuality == 2) {
            //Low: 70% jpg compressed
            quality = ".jpg70";
          } else {
            //Default to true color png
            quality = "@2x.png";
          }

          overlayString = "https://api.mapbox.com/v4/mapbox." + type + "/{z}/{x}/{y}" + quality + "?access_token=pk.eyJ1IjoiZnVsbGhkcGl4ZWwiLCJhIjoiY2l4amhqYmg4MDAwZDMzbnl4YWtycXhjdyJ9.R8ZCTRKimifeQ2fXU6J6Qw";
        }
        //DEFAULT OVERLAY
        provider = new CustomUrlTileProvider(256, 256, overlayString);
        mSelectedTileOverlay = gMap.addTileOverlay(new TileOverlayOptions().tileProvider(provider).zIndex(-1));
      }
    }

    if (attributionText != null) {
      attribution.setVisibility(View.VISIBLE);
      attribution.setText(attributionText);
    } else {
      attribution.setVisibility(View.GONE);
    }
  }

  //From settings
  public void useZoomPrefs() {
    if (gMap != null) {
      SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

      String minZoom = sharedPrefs.getString("-minzoom-", "0");
      String maxZoom = sharedPrefs.getString("-maxzoom-", "21");

      int min = Integer.parseInt(minZoom);
      int max = Integer.parseInt(maxZoom);

      gMap.setMinZoomPreference(min);
      gMap.setMaxZoomPreference(max);
    }
  }

  /**
   * Gets url.
   *
   * @param origin     the origin
   * @param dest       the dest
   * @param travelmode the travelmode
   * @return the url
   */
  public String getUrl(LatLng origin, LatLng dest, String travelmode) {
    String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
    String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
    String sensor = "sensor=false";
    String mode = "mode=" + travelmode;
    String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;
    String output = "json";
    return "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
  }

  private String downloadUrl(String strUrl) throws IOException {
    String data = "";
    InputStream iStream = null;
    HttpURLConnection urlConnection = null;
    try {
      URL url = new URL(strUrl);
      urlConnection = (HttpURLConnection) url.openConnection();
      urlConnection.connect();
      iStream = urlConnection.getInputStream();

      BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
      StringBuilder sb = new StringBuilder();
      String line;
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
      data = sb.toString();
      br.close();
    } catch (Exception e) {
      Log.e(TAG, "downloadUrl: ");
    } finally {
      assert iStream != null;
      iStream.close();
      urlConnection.disconnect();
    }
    return data;
  }

  private class FetchUrl extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... url) {
      String data = "";

      try {
        data = downloadUrl(url[0]);
      } catch (Exception e) {
        Log.e(TAG, "downloadUrl: ");
      }
      return data;
    }

    @Override
    protected void onPostExecute(String result) {
      super.onPostExecute(result);
      ParserTask parserTask = new ParserTask();
      if (result != null) {
        parserTask.execute(result);
      }
    }
  }

  class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

      JSONObject jObject;
      List<List<HashMap<String, String>>> routes = null;

      try {
        jObject = new JSONObject(jsonData[0]);
        DataParser parser = new DataParser();
        routes = parser.parse(jObject);
      } catch (Exception e) {
        //show error
//        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//        centerLastMarker();
      }
      return routes;
    }

    static final double DIVISION_POINTS_NUMBER = 1000000.0;

    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
      busyDrawing = false;
      if (result != null) {
        for (int i = 0; i < result.size(); i++) {
          List<HashMap<String, String>> points = result.get(i);
          List<LatLng> tempResult = new ArrayList<>();
          for (int j = 0; j < points.size(); j++) {
            HashMap<String, String> point = points.get(j);
            double lat = Double.parseDouble(point.get("lat"));
            double lng = Double.parseDouble(point.get("lng"));
            LatLng position = new LatLng(lat, lng);
            tempResult.add(position);
          }
          // -1, 0.000532 ==> 532 points
          double sizeOfNewPath = tempResult.size() / DIVISION_POINTS_NUMBER;

          LatLng outOfBounds = new LatLng(-1, sizeOfNewPath);
          path.add(outOfBounds);
          for (int k = 0; k < tempResult.size(); k++) {
            path.add(tempResult.get(k));
          }
          path.add(outOfBounds);
          drawLines(false);
        }
      }
    }
  }
}
