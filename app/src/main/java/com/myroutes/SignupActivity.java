package com.myroutes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.util.HashMap;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

public class SignupActivity extends AppCompatActivity {
  private static final String TAG = "SignupActivity";

  EditText emailText, passwordText;
  Button signupButton;
  TextView loginLink, checkbox_agree;
  RelativeLayout relativeLayout;
  CheckBox checkbox_email_updates;

  Meteor meteorSingleton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_signup);

    meteorSingleton = MeteorSingleton.getInstance();
    meteorSingleton.connect();

    emailText = (EditText) findViewById(R.id.input_email);
    passwordText = (EditText) findViewById(R.id.input_password);
    signupButton = (Button) findViewById(R.id.btn_signup);
    loginLink = (TextView) findViewById(R.id.link_login);
    checkbox_agree = (TextView) findViewById(R.id.checkbox_agree);
    loginLink.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

    emailText.setHintTextColor(getResources().getColor(R.color.white));
    passwordText.setHintTextColor(getResources().getColor(R.color.white));

    relativeLayout = (RelativeLayout) findViewById(R.id.relativeLay1);

    checkbox_email_updates = (CheckBox) findViewById(R.id.checkbox_email_updates);
    checkbox_email_updates.setMovementMethod(LinkMovementMethod.getInstance());

    signupButton.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          signupButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button_pressed));
          signupButton.setBackgroundColor(ContextCompat.getColor(SignupActivity.this, R.color.loginPrimary));
          signupButton.setTextColor(Color.WHITE);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          signupButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button));
          signupButton.setTextColor(ContextCompat.getColor(SignupActivity.this, R.color.loginPrimary));
        }
        return false;
      }
    });

    signupButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        signup();
      }
    });

    loginLink.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          loginLink.setPaintFlags(loginLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
          loginLink.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          loginLink.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
          finish();
        }
        return true;
      }
    });

    String html = "By creating a new account you agree to the <a href='https://myroutes.io/privacypolicy'>privacy policy</a> and <a href='https://myroutes.io/termsofservice'>terms of service</a>";
    Spanned result;
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
      result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
    } else {
      result = Html.fromHtml(html);
    }
    checkbox_agree.setText(result);
    checkbox_agree.setMovementMethod(LinkMovementMethod.getInstance());
  }

  public void signup() {
    String email = emailText.getText().toString();
    String password = passwordText.getText().toString();

    emailText.setError(null);

    if (email.isEmpty()) {
      emailText.setError("Email is required");
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
      emailText.setError("Email is invalid");
    } else if (password.isEmpty()) {
      passwordText.setError("Password is required");
    } else if (password.length() <= 6) {
      passwordText.setError("Password is too easy, at least 6 characters");
    } else {
      dialog = new ProgressDialog(SignupActivity.this);
      dialog.setMessage("Creating account");
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

      HashMap profile = new HashMap<>();
      boolean emailUpdates = checkbox_email_updates.isChecked();
      profile.put("emailUpdates", emailUpdates);
      profile.put("app", "android");
      meteorSingleton.registerAndLogin(null, email, password, profile, new ResultListener() {
        @Override
        public void onSuccess(String result) {
          dialog.dismiss();

          if (meteorSingleton.isLoggedIn()) {
            //send to MainActivity, clear back stack
            Intent intent = new Intent(SignupActivity.this, MainActivity.class);
            intent.putExtra("signedup", true);
            startActivity(intent);
          }
        }

        @Override
        public void onError(String error, String reason, String details) {
          //Something went wrong
          dialog.dismiss();
          new MaterialDialog.Builder(SignupActivity.this)
              .title(getString(R.string.something_went_wrong))
              .content(reason)
              .positiveText(android.R.string.ok)
              .onPositive(new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                }
              })
              .show();
        }
      });
    }
  }

  @Override
  public void onBackPressed() {
    this.finish();
  }

  ProgressDialog dialog;
  private static final int TIMEOUT_REQUEST = 0;

  public Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        // The decoding is done
        case TIMEOUT_REQUEST:
          if (dialog.isShowing()) {
            dialog.dismiss();
            new MaterialDialog.Builder(SignupActivity.this)
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
