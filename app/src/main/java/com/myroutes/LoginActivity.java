package com.myroutes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import java.util.HashMap;
import java.util.Map;

import im.delight.android.ddp.Meteor;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
  private static final String TAG = "LoginActivity";
  private static final int GOOGLE_SIGN_IN = 0;
  private static final int REQUEST_FORGOT = 1;

  private static final int TIMEOUT_REQUEST = 0;

  EditText emailText, passwordText;
  AppCompatButton loginButton, googleSignInButton;
  TextView signupLink, forgotLink;
  ProgressDialog dialog;
  Meteor meteorSingleton;

  GoogleApiClient mGoogleApiClient;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);

    meteorSingleton = MeteorSingleton.getInstance();
    meteorSingleton.reconnect();

    emailText = (EditText) findViewById(R.id.input_email);
    passwordText = (EditText) findViewById(R.id.input_password);
    loginButton = (AppCompatButton) findViewById(R.id.btn_login);
    googleSignInButton = (AppCompatButton) findViewById(R.id.google_sign_in);
    signupLink = (TextView) findViewById(R.id.link_signup);
    forgotLink = (TextView) findViewById(R.id.link_forgot);

    loginButton.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          loginButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button_pressed));
          loginButton.setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.loginPrimary));
          loginButton.setTextColor(Color.WHITE);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          loginButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.login_button));
          loginButton.setTextColor(ContextCompat.getColor(LoginActivity.this, R.color.loginPrimary));
        }
        return false;
      }
    });

    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (email.isEmpty()) {
          emailText.setError(getString(R.string.email_required));
        } else if (password.isEmpty()) {
          passwordText.setError(getString(R.string.password_required));
        } else {
          dialog = new ProgressDialog(LoginActivity.this);
          dialog.setMessage("Logging in");
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


          //Login user
          meteorSingleton.loginWithEmail(email, password, new ResultListener() {
            @Override
            public void onSuccess(String result) {
              dialog.dismiss();

              //send to MainActivity, clear back stack
              Intent intent = new Intent(LoginActivity.this, MainActivity.class);
              intent.putExtra("loggedin", true);
              startActivity(intent);
            }

            @Override
            public void onError(String error, String reason, String details) {
              //Something went wrong
              dialog.dismiss();
              Log.d(TAG, "onError: " + details);
              //Waarom geeft die hier constant No response from server?
              new MaterialDialog.Builder(LoginActivity.this)
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

    googleSignInButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        //Check if connected
        if (meteorSingleton.isConnected()) {
          Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
          startActivityForResult(signInIntent, GOOGLE_SIGN_IN);
        } else {
          meteorSingleton.connect();

          new MaterialDialog.Builder(LoginActivity.this)
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
      }
    });

    signupLink.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          signupLink.setText(Html.fromHtml("<u>" + getString(R.string.create_account) + "</u>"));
        } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
          signupLink.setText(Html.fromHtml(getString(R.string.create_account)));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
          startActivity(intent);
        }
        return true;
      }
    });

    forgotLink.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
          forgotLink.setText(Html.fromHtml("<u>" + getString(R.string.forgot_password_question) + "</u>"));
        } else if (event.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
          forgotLink.setText(Html.fromHtml(getString(R.string.forgot_password_question)));
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
          forgotLink.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
          Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
          //Prefill password
          String email = emailText.getText().toString();
          intent.putExtra("email", email);
          startActivityForResult(intent, REQUEST_FORGOT);
        }
        return true;
      }
    });

    Button skipBtn = (Button) findViewById(R.id.btn_skip);
    skipBtn.setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        //Go to mainactivity
        meteorSingleton.disconnect();
        finish();
      }
    });
  }

  @Override
  protected void onStart() {
    super.onStart();

    //Reset
    signupLink.setText(Html.fromHtml(getString(R.string.create_account)));
    forgotLink.setText(Html.fromHtml(getString(R.string.forgot_password_question)));

    //Google Account
    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestEmail()
        .requestProfile()
        .requestIdToken(getString(R.string.google_sign_in_Serverkey))
        .requestServerAuthCode(getString(R.string.google_sign_in_Serverkey))
        .build();

    mGoogleApiClient = new GoogleApiClient.Builder(this)
        .enableAutoManage(this, this)
        .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
        .addConnectionCallbacks(this)
        .build();

    mGoogleApiClient.connect();
  }

  @Override
  protected void onStop() {
    super.onStop();

    mGoogleApiClient.stopAutoManage(this);
    mGoogleApiClient.disconnect();
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_FORGOT) {
      //User came back from ForgotPasswordActivity
      if (resultCode == RESULT_OK) {
        String email = data.getStringExtra("email");
        //Show alertdialog
        new MaterialDialog.Builder(LoginActivity.this)
            .title("Reset Password Link Sent")
            .content("Email has been send to " + email)
            .positiveText(android.R.string.ok)
            .onPositive(new MaterialDialog.SingleButtonCallback() {
              @Override
              public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
              }
            })
            .show();
      }
    } else if (requestCode == GOOGLE_SIGN_IN) {
      GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

      if (result.isSuccess()) {
        GoogleSignInAccount acct = result.getSignInAccount();
        assert acct != null;
        String email = acct.getEmail();
        String userId = acct.getId();
        String idToken = acct.getIdToken();
        String oAuthToken = acct.getServerAuthCode();
        registerWithGoogle(email, userId, idToken, oAuthToken, new ResultListener() {
          @Override
          public void onSuccess(String result) {
            //send to mainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("loggedin", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
          }

          @Override
          public void onError(String error, String reason, String details) {
             new MaterialDialog.Builder(LoginActivity.this)
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
  }

  /**
   * Registers a new user with the Google oAuth API
   * <p>
   * This method will automatically login as the new user on success
   * <p>
   * Please note that this requires `accounts-base` package
   *
   * @param email      the email to register with. Must be fetched from the Google oAuth Android API
   * @param userId     the unique google plus userId. Must be fetched from the Google oAuth Android API
   * @param idToken    the idToken from Google oAuth Android API
   * @param oAuthToken the oAuthToken from Google oAuth Android API for server side validation
   * @param listener   the listener to call on success/error
   */
  public void registerWithGoogle(final String email, final String userId, final String idToken, final String oAuthToken, final ResultListener listener) {
    Map<String, Object> accountData = new HashMap<>();
    accountData.put("email", email);
    accountData.put("userId", userId);
    accountData.put("idToken", idToken);
    accountData.put("oAuthToken", oAuthToken);
    accountData.put("googleLoginPlugin", true);

    meteorSingleton.call("login", new Object[]{accountData}, listener);
  }

  @Override
  public void onConnected(Bundle bundle) {
    if (mGoogleApiClient.isConnected()) {

      //Make sure you can log in with otheraccount
      //Runs every time when user is in LoginActivity
      Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
          new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
            }
          });
    }
  }

  @Override
  public void onConnectionSuspended(int i) {

  }

  public Handler handler = new Handler(Looper.getMainLooper()) {
    @Override
    public void handleMessage(Message msg) {
      switch (msg.what) {
        // The decoding is done
        case TIMEOUT_REQUEST:
          if (dialog.isShowing()) {
            dialog.dismiss();
            new MaterialDialog.Builder(LoginActivity.this)
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
    finish();
  }
}

