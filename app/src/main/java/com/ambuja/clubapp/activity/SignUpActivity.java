package com.ambuja.clubapp.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.utils.InputFilterMinMax;
import com.ambuja.clubapp.utils.PrefStore;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

import java.util.Calendar;


public class SignUpActivity extends BaseActivity {

    private EditText dobET;
    private EditText genderET;
    private String bikeDetailYear;
    private String bikeDetailMonth;
    private String bikeDetailDay;
    private String gender;
    private EditText emailET;
    private EditText mobileET;
    private EditText passwordET;
    private EditText fullNameET;
    private String clubSignUp_Id;
    String fontReg = "fonts/SEGOEUI_0.TTF";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

      /*  try {
            clubSignUp_Id = store.getString("clubId");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!clubSignUp_Id.equalsIgnoreCase("") && clubSignUp_Id != null) {
        } else {
            Intent intent = new Intent(this, ClubListSettingsActivity.class);
            startActivity(intent);
        }*/

        initUI();
        initGCM();
    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(R.string.member_register);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fullNameET = (EditText) findViewById(R.id.fullNameET);
        emailET = (EditText) findViewById(R.id.emailET);
        passwordET = (EditText) findViewById(R.id.passwordET);

        mobileET = (EditText) findViewById(R.id.mobileET);
        mobileET.setFilters(new InputFilter[]{new InputFilterMinMax("1", "9999999999")});
        genderET = (EditText) findViewById(R.id.genderET);
        genderET.setKeyListener(null);

        dobET = (EditText) findViewById(R.id.dobET);
        dobET.setKeyListener(null);

        Button signInBT = (Button) findViewById(R.id.signInBT);
        Button signupBT = (Button) findViewById(R.id.signupBT);

        Typeface typeface = Typeface.createFromAsset(getAssets(), fontReg);
        fullNameET.setTypeface(typeface);
        emailET.setTypeface(typeface);
        passwordET.setTypeface(typeface);
        mobileET.setTypeface(typeface);
        genderET.setTypeface(typeface);
        dobET.setTypeface(typeface);

        genderET.setOnClickListener(genderListener);
        dobET.setOnClickListener(dateListener);

        signInBT.setOnClickListener(loginListener);
        signupBT.setOnClickListener(signupListener);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


    private View.OnClickListener genderListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            openGenderDialog();

        }
    };

    private View.OnClickListener dateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            displayDatePicker();
        }
    };


    private View.OnClickListener loginListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(intent);
            finish();

        }
    };


    private View.OnClickListener signupListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            fullNameET = (EditText) findViewById(R.id.fullNameET);
            emailET = (EditText) findViewById(R.id.emailET);
            passwordET = (EditText) findViewById(R.id.passwordET);

            mobileET = (EditText) findViewById(R.id.mobileET);
            genderET = (EditText) findViewById(R.id.genderET);
            genderET.setKeyListener(null);

            dobET = (EditText) findViewById(R.id.dobET);
            dobET.setKeyListener(null);

            String name = fullNameET.getText().toString();
            String email = emailET.getText().toString().trim();
            String password = passwordET.getText().toString().trim();
            String mobile = mobileET.getText().toString().trim();
            String signUpGender = genderET.getText().toString().trim();
            String dob = dobET.getText().toString();

            submitForm(name, email, password, mobile, dob, signUpGender);


        }
    };

    private void submitForm(String name, String email, String password,
                            String mobile, String dob, String signUpGender) {

        if (!validateName(name)) {
            return;
        }


        if (!validateEmail(email)) {
            return;
        }

        if (!validatePassword(password)) {
            return;

        }

        if (!validateMobile(mobile)) {
            return;

        }


        if (!validateDOB(dob)) {
            return;

        }

        if (!validateGender(signUpGender)) {
            return;

        }

        signUp(name, email, password, mobile, dob, signUpGender);

    }


    private boolean validateName(String name) {
        if (name.isEmpty()) {
            fullNameET.setError(getString(R.string.err_msg_first_name));
            requestFocus(fullNameET);
            return false;
        }

        return true;
    }


    private boolean validateEmail(String email) {

        if (email.isEmpty()) {
            emailET.setError(getString(R.string.err_msg_email));
            requestFocus(emailET);
            return false;
        } else if (!isValidEmail(email)) {
            emailET.setError(" Enter valid email address!");
            requestFocus(emailET);
            return false;

        }

        return true;
    }

    private boolean validatePassword(String password) {
        int minLength = 5;
        if (password.isEmpty()) {
            passwordET.setError(getString(R.string.err_msg_password));
            requestFocus(passwordET);
            return false;
        } else if (password.length() <= minLength) {
            passwordET.setError(getString(R.string.err_msg_password_two));
            requestFocus(passwordET);
            return false;
        }

        return true;
    }


    private boolean validateDOB(String updateDate) {

        if (updateDate.isEmpty()) {
            myColoredToast("Please fill your DOB!");
            return false;
        }

        return true;
    }


    private boolean validateGender(String signUpGender) {

        if (signUpGender.isEmpty()) {
            myColoredToast("Please select Gender!");
            return false;
        }

        return true;
    }

    private boolean validateMobile(String phone) {


        if (phone.isEmpty()) {
            mobileET.setError(getString(R.string.err_msg_mobile));
            requestFocus(mobileET);
            return false;
        } else if (phone.length() < 10 || phone.length() > 13) {

            mobileET.setError(getString(R.string.err_msg_mobile_format));
            requestFocus(mobileET);
            return false;

        }

        return true;

    }


    private void displayDatePicker() {

        Calendar date = Calendar.getInstance();

        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH);
        int day = date.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month = String.valueOf(monthOfYear + 1);
                if ((monthOfYear + 1) < 10) {

                    month = "0" + month;
                }
                String day = String.valueOf(dayOfMonth);
                if (dayOfMonth < 10) {

                    day = "0" + day;
                }


                // signupDobTV.setText(year + "-" + month + "-" + day);

                bikeDetailYear = year + "";
                bikeDetailMonth = month + "";
                bikeDetailDay = day + "";


                loadDateData();


            }

        }, year, month, day);

        dialog.getDatePicker().setMaxDate(System.currentTimeMillis());
        dialog.show();

    }

    private void loadDateData() {


        dobET.setText(bikeDetailDay + "-" + bikeDetailMonth + "-" + bikeDetailYear);


    }

    private void openGenderDialog() {

        final Dialog dialog;
        dialog = new Dialog(SignUpActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);

       /* Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(CENTER);*/


        ImageView cancelIV;
        TextView femaleTV, maleTV;

        dialog.setContentView(R.layout.dialog_gender);

        maleTV = (TextView) dialog.findViewById(R.id.maleTV);
        femaleTV = (TextView) dialog.findViewById(R.id.femaleTV);
        cancelIV = (ImageView) dialog.findViewById(R.id.cancelIV);

        maleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gender = "Male";
                genderET.setText(gender);
                dialog.dismiss();

            }


        });

        femaleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                gender = "Female";
                genderET.setText(gender);
                dialog.dismiss();

            }
        });


        cancelIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                dialog.cancel();

            }
        });


        dialog.show();


    }

    private void signUp(String name, String email, String password,
                        String mobile, String dob, String signUpGender) {

        String deviceType = "1";   /* for Android "1" and for i-Phone "2" */

        String deviceTokenId = store.getString(PrefStore.REG_TOKEN);

        if (deviceTokenId.equals("")) {

            deviceTokenId = Settings.Secure.getString(getApplicationContext().
                    getContentResolver(), Settings.Secure.ANDROID_ID);
        }

        log("Login screen Device Token:  " + deviceTokenId);


        String action = "signup";

        RequestParams params = new RequestParams();

        params.put("name", name);
        params.put("email", email);
        params.put("phonenumber", mobile);
        params.put("dob", dob);
        params.put("password", password);
        params.put("gender", signUpGender);
        try {
            params.put("club_id", clubSignUp_Id);
        } catch (Exception e) {
            e.printStackTrace();
        }


        syncManager.sendToServer(action, params, this);


    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);


        if (action.equalsIgnoreCase("signup") && controller.equalsIgnoreCase("user")) {


            try {
                if (status) {


                    myColoredToast(object.getString("message"));

                    Intent intent = new Intent(this, SignInActivity.class);
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
