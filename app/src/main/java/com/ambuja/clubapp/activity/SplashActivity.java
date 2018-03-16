package com.ambuja.clubapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import com.ambuja.clubapp.utils.PrefStore;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;


public class SplashActivity extends BaseActivity {
    private String clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // clubId = store.getString("clubId");
        initUI();
    }

    private void initUI() {
        startTimer();
    }

    private void startTimer() {

        int SPLASH_TIME_OUT = 1000;
        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {

                checkUser();

                // startApp();
            }
        }, SPLASH_TIME_OUT);
    }

    private void startApp() {
        if (clubId != null && !clubId.equalsIgnoreCase("")) {
            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(SplashActivity.this, ClubListActivity.class);
            startActivity(intent);
            finish();
        }
    }

    private void checkUser() {
        String deviceType = "1";   /* for Android "1" and for i-Phone "2" */
        String deviceTokenId = store.getString(PrefStore.REG_TOKEN);
        if (deviceTokenId.equals("")) {
            deviceTokenId = Settings.Secure.getString(getApplicationContext().
                    getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        log("Login screen Device Token:  " + deviceTokenId);
        String action = "check";
        RequestParams params = new RequestParams();
        params.put("device_type", deviceType);
        params.put("device_token", deviceTokenId);
        syncManager.sendToServer(action, params, this);
    }

    @Override
    public void onSyncStart() {

    }

    @Override
    public void onSyncFinish() {

    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);
        if (action.equalsIgnoreCase("check") && controller.equalsIgnoreCase("user")) {
            try {
                if (status) {
                    log("User Check Api: " + object.getString("message"));

                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();


                } else {


                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();

                    /*Intent intent = new Intent(SplashActivity.this, AskLoginActvity.class);
                    startActivity(intent);
                    finish();*/

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
