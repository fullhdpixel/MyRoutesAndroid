package com.myroutes.helper;

import android.net.Uri;

/**
 * Created by thijssmudde on 24/05/2017.
 */

public class UriString {
  public String filename;
  public Uri filePath;

  public UriString(Uri filePath, String filename) {
    this.filePath = filePath;
    this.filename = filename;
  }
}
