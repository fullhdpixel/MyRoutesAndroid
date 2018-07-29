package com.myroutes.helper;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static com.myroutes.R.drawable.endmarker;
import static com.myroutes.R.drawable.startmarker;

/**
 * Created by thijssmudde on 24/05/2017.
 */

public class ResourceSizes {
  public static int getTrackWidth(Context context) {
    //XLarge screens 5
    //Large screens 7
    //Normal 10
    int trackWidth = 10;
    if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
      trackWidth = 5;
    } else if ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
      trackWidth = 7;
    }
    return trackWidth;
  }

  public static int getIconSize(Context context) {
    int markerSize = 80;
    int screenSize = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);

    if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
      markerSize = 45;
    } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
      markerSize = 60;
    }
    return markerSize;
  }

  public static int getWidthGridView(Context context) {
    int widthGridView = 350;

    int screenSize = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
    if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
      widthGridView = 200;
    } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
      widthGridView = 275;
    }
    return widthGridView;
  }

  public static Bitmap[] createStartEndBitmaps(Context context) {
    int widthMarker, heightMarker;
    int screenSize = (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK);
    if (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
      widthMarker = 30;
      heightMarker = 45;
    } else if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE) {
      widthMarker = 42;
      heightMarker = 63;
    } else {
      widthMarker = 60;
      heightMarker = 90;
    }

    Bitmap startBitmap = BitmapFactory.decodeResource(context.getResources(), startmarker);
    Bitmap resizedStart = Bitmap.createScaledBitmap(startBitmap, widthMarker, heightMarker, false);

    Bitmap endBitmap = BitmapFactory.decodeResource(context.getResources(), endmarker);
    Bitmap resizedEnd = Bitmap.createScaledBitmap(endBitmap, widthMarker, heightMarker, false);

    return new Bitmap[] {resizedStart, resizedEnd};
  }
}
