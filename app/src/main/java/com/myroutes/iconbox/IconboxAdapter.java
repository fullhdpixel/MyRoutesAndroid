package com.myroutes.iconbox;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;
import com.myroutes.R;

/**
 * Created by thijssmudde on 25/04/2017.
 */

public class IconboxAdapter extends BaseAdapter {
  private Context mContext;
  private int iconSize;

  public IconboxAdapter(Context c) {
    mContext = c;
    //check screen size

    //XLarge screens 250 - 60
    //Large screens 300 - 80
    //Normal 350 - 100
    iconSize = 100;
    if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
      iconSize = 60;
    } else if ((mContext.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {
      iconSize = 80;
    }
  }

  public int getCount() {
    return mThumbIds.length;
  }

  public Object getItem(int position) {
    return null;
  }

  public long getItemId(int position) {
    return 0;
  }

  // create a new ImageView for each item referenced by the Adapter
  public View getView(int position, View convertView, ViewGroup parent) {
    ImageView imageView;
    if (convertView == null) {
      // if it's not recycled, initialize some attributes
      imageView = new ImageView(mContext);

      imageView.setLayoutParams(new GridView.LayoutParams(iconSize, iconSize));
      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
      imageView.setMinimumWidth(30);
      imageView.setMinimumHeight(30);
      imageView.setPadding(8, 8, 8, 8);
    } else {
      imageView = (ImageView) convertView;
    }

    imageView.setImageResource(mThumbIds[position]);
    return imageView;
  }

  public static final int COLUMNS = 3;
  public static final int ROWS = 5;

  /**
   * Determine Icon type from x y position;
   *
   * @param location the location
   * @param width    the width
   * @param height   the height
   * @param x        the x
   * @param y        the y
   * @return the clicked icon
   */
  public static Icon getClickedIcon(LatLng location, float width, float height, float x, float y) {
    float widthOfGridItem = width / COLUMNS; // 3 icons in width
    float heightOfGridItem = height / ROWS; // 5 icons in height

    int position = 0;
    int xAddition = (int) Math.floor(x / widthOfGridItem);
    int yAddition = (int) Math.floor(y / heightOfGridItem);

    position += xAddition;
    position += yAddition * 3;

    if (position < mThumbIds.length) {
      int thumbs = mThumbIds[position];
      String type = typeOfDrawables[position];
      return new Icon(type, location, thumbs);
    } else {
      return null;
    }
  }


  public static int getResourceInt(String type) {
    for (int i = 0; i < typeOfDrawables.length; i++) {
      if (typeOfDrawables[i].equals(type)) {
        return mThumbIds[i];
      }
    }
    return -1;
  }

  // references to our images
  public static Integer[] mThumbIds = {
      R.drawable.water,
      R.drawable.coffee,
      R.drawable.restaurant,
      R.drawable.toilet,
      R.drawable.info,
      R.drawable.park,
      R.drawable.help,
      R.drawable.top,
      R.drawable.right,
      R.drawable.left,
      R.drawable.down,
      R.drawable.topturn,
      R.drawable.rightturn,
      R.drawable.botturn,
      R.drawable.leftturn
  };

  public static String[] typeOfDrawables = {
      "water",
      "coffee",
      "restaurant",
      "toilet",
      "info",
      "park",
      "help",
      "top",
      "right",
      "left",
      "down",
      "topturn",
      "rightturn",
      "botturn",
      "leftturn"
  };
}
