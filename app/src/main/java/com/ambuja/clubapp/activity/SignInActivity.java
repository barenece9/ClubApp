package com.ambuja.clubapp.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.utils.Constants;
import com.ambuja.clubapp.utils.PrefStore;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;


public class SignInActivity extends BaseActivity {
    String fontReg = "fonts/SEGOEUI_0.TTF";
    private EditText emailET;
    private EditText passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initUI();

        initGCM();
    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.member_login);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        Button signInBT = (Button) findViewById(R.id.signInBT);
        Button signupBT = (Button) findViewById(R.id.signupBT);
        Typeface typeface = Typeface.createFromAsset(getAssets(), fontReg);
        emailET.setTypeface(typeface);
        passwordET.setTypeface(typeface);
        TextView forgotPasswordTV = (TextView) findViewById(R.id.forgotPasswordTV);

        signInBT.setOnClickListener(loginListener);
        signupBT.setOnClickListener(signupListener);
        forgotPasswordTV.setOnClickListener(forgotPasswordListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private View.OnClickListener signupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
            startActivity(intent);
            finish();
        }
    };

    private View.OnClickListener forgotPasswordListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        }
    };

    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString();

            if (email.isEmpty()) {

                emailET.setError("Please enter email/phone/member Id");

            } else if (password.isEmpty()) {

                passwordET.setError("Please enter Password!");

            } else {
                SignIn(email, password);
            }
        }
    };

    private void SignIn(String email, String password) {
        String deviceType = "1";   /* for Android "1" and for i-Phone "2" */
        String deviceTokenId = store.getString(PrefStore.REG_TOKEN);

        if (deviceTokenId.equals("")) {
            deviceTokenId = Settings.Secure.getString(getApplicationContext().
                    getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        log("Login screen Device Token:  " + deviceTokenId);
        String action = "login";
        RequestParams params = new RequestParams();
        params.put("email", email);
        params.put("password", password);
        params.put("device_type", deviceType);
        params.put("device_token", deviceTokenId);
        syncManager.sendToServer(action, params, this);
    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);

        if (action.equalsIgnoreCase("login") && controller.equalsIgnoreCase("user")) {
            try {
                if (status) {
                    if (object.has("auth_code")) {
                        try {
                            String auth_code = object.getString("auth_code");
                            log(" loginDone => auth_code :" + auth_code);
                            syncManager.setLoginStatus(auth_code);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }
                    }

                    myColoredToast("Login Success");
                    store.setString("appLogin", "true");
                    JSONObject jsonObject = object.getJSONObject("viewprofile");
                    Constants.userId = jsonObject.getString("id");
                    store.setString("userId", Constants.userId);
                    Intent intent = new Intent(this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    myColoredToast(object.getString("error"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
