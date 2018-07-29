package com.myroutes;

import android.support.multidex.MultiDexApplication;

import com.flurry.android.FlurryAgent;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;

/**
 * Created by thijssmudde on 18/04/2017.
 */

public class BaseApplication extends MultiDexApplication {
  Meteor meteorSingleton;

  @Override
  public void onCreate() {
    super.onCreate();
    meteorSingleton = MeteorSingleton.createInstance(this, UploadActivity.websocket);
    meteorSingleton.connect();

    if (!BuildConfig.DEBUG) {
      new FlurryAgent.Builder()
          .withLogEnabled(false)
          .build(this, "XHYHBCX3FNGRGPBX2Y7G");
    }
  }
}
