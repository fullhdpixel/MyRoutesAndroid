package com.myroutes.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.Html;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.myroutes.Changelog.ChangeLogUpdates;
import com.myroutes.ChangelogActivity;
import com.myroutes.R;

public class Notices {
  public static void showRatingNotice(final Context context, SharedPreferences sharedPrefs) {
    //Show notice if not yet updated
    boolean isRated = sharedPrefs.getBoolean("-rated_notice-", false);

    if (!isRated) {
      //Show updates
      new MaterialDialog.Builder(context)
          .title(context.getString(R.string.thanks_for_using) + " " + context.getString(R.string.app_name))
          .content(context.getString(R.string.rate_play_store))
          .positiveText(context.getString(R.string.rate_app))
          .onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              Intent intent = new Intent(Intent.ACTION_VIEW);
              intent.setData(Uri.parse(context.getResources().getString(R.string.play_store_link)));
              context.startActivity(intent);
            }
          })
          .negativeText(context.getString(android.R.string.cancel))
          .show();
      //Change number in sharedprefs to last version
      sharedPrefs.edit().putBoolean("-rated_notice-", true).apply();
    }
  }

  public static void showChangelogNotice(final Context context, SharedPreferences sharedPrefs) {
    ChangeLogUpdates latestUpdate = ChangelogActivity.getLatest();

    PackageInfo pInfo;
    try {
      pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
      int versionCode = pInfo.versionCode;
      int noticeVersion = sharedPrefs.getInt("-notice-", 1);
      if (versionCode != noticeVersion) {
        //Show updates
        new MaterialDialog.Builder(context)
            .title("Changelog v" + latestUpdate.version)
            .content(Html.fromHtml(latestUpdate.text))
            .positiveText(context.getString(android.R.string.ok)).show();
        //Change number in sharedprefs to last version
        sharedPrefs.edit().putInt("-notice-", versionCode).apply();
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }

  }
}
