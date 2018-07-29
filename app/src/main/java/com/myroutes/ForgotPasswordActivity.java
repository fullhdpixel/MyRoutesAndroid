package com.myroutes;

import android.app.Activity;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

public class ForgotPasswordActivity extends AppCompatActivity {
  private static final String TAG = "ForgotPasswordActivity";

  EditText emailText;
  Button resetButton;
  TextView signupLink;
  Meteor meteorSingleton;
  ProgressDialog dialog;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_forgot_password);

    meteorSingleton = MeteorSingleton.getInstance();
    meteorSingleton.reconnect();

    emailText = (EditText) findViewById(R.id.input_email);
    resetButton = (Button) findViewById(R.id.btn_reset);

    String email = getIntent().getStringExtra("email");
    if (email != null) {
      emailText.setText(email);
    }

    resetButton.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          resetButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button_pressed));
          resetButton.setBackgroundColor(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.loginPrimary));
          resetButton.setTextColor(Color.WHITE);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          resetButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button));
          resetButton.setTextColor(ContextCompat.getColor(ForgotPasswordActivity.this, R.color.loginPrimary));
        }
        return false;
      }
    });

    resetButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String email = emailText.getText().toString(); //Get Email

        if (email.isEmpty()) {
          emailText.setError(getString(R.string.email_required));
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
          emailText.setError(getString(R.string.error_invalid_email));
        } else {
          //Show progress
          dialog = new ProgressDialog(ForgotPasswordActivity.this);
          dialog.setMessage(getString(R.string.sending_reset_email));
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

          meteorSingleton.call("sendResetPasswordEmail", new Object[]{email}, new ResultListener() {
            @Override
            public void onSuccess(String result) {
              dialog.dismiss();

              Intent returnIntent = new Intent();
              returnIntent.putExtra("email", email);
              setResult(Activity.RESULT_OK, returnIntent);
              finish();
            }

            @Override
            public void onError(String error, String reason, String details) {
              dialog.dismiss();
              new MaterialDialog.Builder(ForgotPasswordActivity.this)
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
    });

    signupLink = (TextView) findViewById(R.id.link_signup);
    signupLink.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          signupLink.setPaintFlags(signupLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
          signupLink.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          finish();
        }
        return true;
      }
    });
  }

  private static final int TIMEOUT_REQUEST = 0;

  public Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        // The decoding is done
        case TIMEOUT_REQUEST:
          if (dialog.isShowing()) {
            dialog.dismiss();
            new MaterialDialog.Builder(ForgotPasswordActivity.this)
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

  @Override
  public void onBackPressed() {
    this.finish();
  }
}
