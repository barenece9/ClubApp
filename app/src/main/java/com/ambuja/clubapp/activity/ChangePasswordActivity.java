package com.ambuja.clubapp.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.utils.Constants;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

/**
 * Created by pulkitm on 6/6/2017.
 */

public class ChangePasswordActivity extends BaseActivity {
    private EditText oldPasswordET;
    private EditText newPasswordET;
    private EditText confirmPasswordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Constants.userId = store.getString("userId");

        initUI();
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.change_password);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        oldPasswordET = (EditText) findViewById(R.id.oldPasswordET);
        newPasswordET = (EditText) findViewById(R.id.newPasswordET);
        confirmPasswordET = (EditText) findViewById(R.id.confirmPasswordET);
        Button submitBT = (Button) findViewById(R.id.submitBT);
        submitBT.setOnClickListener(submitListener);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private View.OnClickListener submitListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String oldPassword = oldPasswordET.getText().toString().trim();
            String newPassword = newPasswordET.getText().toString().trim();
            String confirmPassword = confirmPasswordET.getText().toString().trim();


            submitForm(oldPassword, newPassword, confirmPassword);

        }
    };

    private void submitForm(String oldPassword, String newPassword, String confirmPassword) {


        if (!validatePassword(oldPassword, newPassword, confirmPassword)) {
            return;

        }


        changePassword(oldPassword, newPassword, confirmPassword);


    }


    private boolean validatePassword(String oldPassword, String newPassword, String confirmPassword) {

        int minLength = 5;
        if (oldPassword.isEmpty()) {
            oldPasswordET.setError(getString(R.string.err_msg_password_old));
            requestFocus(oldPasswordET);
            return false;
        } else if (newPassword.isEmpty()) {
            newPasswordET.setError(getString(R.string.err_msg_password_new));
            requestFocus(newPasswordET);
            return false;
        } else if (confirmPassword.isEmpty()) {
            confirmPasswordET.setError(getString(R.string.err_msg_password_confirm));
            requestFocus(confirmPasswordET);
            return false;
        } else if (newPassword.length() <= minLength) {
            newPasswordET.setError(getString(R.string.err_msg_password_two));
            requestFocus(newPasswordET);
            return false;
        } else if (!confirmPassword.matches(newPassword)) {
            confirmPasswordET.setError(getString(R.string.err_msg_password_confirm_match));
            requestFocus(confirmPasswordET);
            return false;
        }

        return true;
    }


    private void changePassword(String oldPassword, String newPassword, String confirmPassword) {

        String action = "change_password";

        RequestParams params = new RequestParams();

        params.put("user_id", Constants.userId);
        params.put("oldPassword", oldPassword);
        params.put("newPassword", newPassword);

        syncManager.sendToServer(action, params, this);


    }


    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);

        if (action.equalsIgnoreCase("change_password") && controller.equalsIgnoreCase("user")) {

            try {
                if (status) {

                    myColoredToast(object.getString("message"));
                    finish();

                } else {
                    myColoredToast(object.getString("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
