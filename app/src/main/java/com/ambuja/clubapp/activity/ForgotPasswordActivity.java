package com.ambuja.clubapp.activity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ambuja.clubapp.R;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

public class ForgotPasswordActivity extends BaseActivity {

    private EditText forgotEmailET;
    String fontReg = "fonts/SEGOEUI_0.TTF";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initUI();
    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.forgot_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Typeface typeface = Typeface.createFromAsset(getAssets(), fontReg);
        forgotEmailET = (EditText) findViewById(R.id.forgotEmailET);
        forgotEmailET.setTypeface(typeface);
        Button forgotPasswordBT = (Button) findViewById(R.id.forgotPasswordBT);

        forgotPasswordBT.setOnClickListener(forgotPasswordListener);
    }

    private View.OnClickListener forgotPasswordListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {

            String email = forgotEmailET.getText().toString();

            if (email.isEmpty()) {

                forgotEmailET.setError(getString(R.string.enter_email_error));
            } else {
                forgotPassword(email);
            }

        }
    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void forgotPassword(String email) {

        String action = "forgot_password";

        RequestParams params = new RequestParams();
        params.put("email", email);

        syncManager.sendToServer(action, params, this);

    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);

        try {
            if (action.equalsIgnoreCase("forgot_password") && controller.equalsIgnoreCase("user")) {

                if (status) {
                    String message = object.getString("message");
                    myColoredToast(message);
                    finish();
                } else {
                    String error = object.getString("error");
                    myColoredToast(error);
                }

            }
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}

