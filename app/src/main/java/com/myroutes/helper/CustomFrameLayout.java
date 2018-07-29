package com.myroutes.helper;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by Thijs on 12-1-2017.
 */

public class CustomFrameLayout extends FrameLayout {
  private GestureDetector gestureDetector;
  private IDragCallback dragListener;

  public CustomFrameLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    gestureDetector = new GestureDetector(context, new GestureListener());
  }

  public interface IDragCallback {
    void onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY);
  }

  public void setOnDragListener(IDragCallback listener) {
    this.dragListener = listener;
  }

  @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    gestureDetector.onTouchEvent(ev);
    return false;
  }

  private class GestureListener extends GestureDetector.SimpleOnGestureListener {

    @Override
    public boolean onDown(MotionEvent e) {
      return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
      return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
      return false;
    }

    //User starts dragging
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
      if (dragListener != null) {
        dragListener.onScroll(e1, e2, distanceX, distanceY);
      }
      return false;
    }
  }
}
