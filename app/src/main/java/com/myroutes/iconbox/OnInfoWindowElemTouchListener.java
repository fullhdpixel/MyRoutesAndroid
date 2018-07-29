package com.myroutes.iconbox;

import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by thijssmudde on 26/04/2017.
 */

public abstract class OnInfoWindowElemTouchListener implements AdapterView.OnTouchListener {
  private final View view;
  private Marker marker;


  public OnInfoWindowElemTouchListener(View view) {
    this.view = view;
  }
  public void setMarker(Marker marker) {
    this.marker = marker;
  }

  @Override
  public boolean onTouch(View vv, MotionEvent event) {
    if (0 <= event.getX() && event.getX() <= view.getWidth() &&
        0 <= event.getY() && event.getY() <= view.getHeight()) {
      onClickConfirmed(view, marker, view.getWidth(), view.getHeight(), event.getX(), event.getY());
    }
    return false;
  }

  /**
   * This is called after a successful click
   */
  protected abstract void onClickConfirmed(View v, Marker marker, float width, float height, float x, float y);
}
