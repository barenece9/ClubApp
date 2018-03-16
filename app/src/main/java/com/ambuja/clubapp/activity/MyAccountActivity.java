package com.ambuja.clubapp.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
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
import com.ambuja.clubapp.utils.Constants;
import com.ambuja.clubapp.utils.InputFilterMinMax;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by pulkitm on 7/26/2016.
 */
public class MyAccountActivity extends BaseActivity {

    private EditText dobET;
    private EditText genderET;
    private String bikeDetailYear;
    private String bikeDetailMonth;
    private String bikeDetailDay;
    private String gender;
    private EditText emailET;
    private EditText mobileET;
    private EditText fullNameET;
    private String userId;
    private String membership_id;
    private String email;
    private String name;
    private String phonenumber;
    private String dob;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        Constants.userId = store.getString("userId");

    }

    @Override
    protected void onResume() {
        super.onResume();

        initUI();
        getMyProfile();

    }

    private void getMyProfile() {


        syncManager.getFromServer("view_profile", null, this);

    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fullNameET = (EditText) findViewById(R.id.fullNameET);
        emailET = (EditText) findViewById(R.id.emailET);


        mobileET = (EditText) findViewById(R.id.mobileET);
        mobileET.setFilters(new InputFilter[]{new InputFilterMinMax("1", "9999999999")});
        genderET = (EditText) findViewById(R.id.genderET);
        genderET.setKeyListener(null);

        dobET = (EditText) findViewById(R.id.dobET);
        dobET.setKeyListener(null);


        Button updateBT = (Button) findViewById(R.id.updateBT);


        genderET.setOnClickListener(genderListener);
        dobET.setOnClickListener(dateListener);

        updateBT.setOnClickListener(updateProfileListener);

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


    private View.OnClickListener updateProfileListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            fullNameET = (EditText) findViewById(R.id.fullNameET);
            emailET = (EditText) findViewById(R.id.emailET);


            mobileET = (EditText) findViewById(R.id.mobileET);
            genderET = (EditText) findViewById(R.id.genderET);
            genderET.setKeyListener(null);

            dobET = (EditText) findViewById(R.id.dobET);
            dobET.setKeyListener(null);

            String name = fullNameET.getText().toString();
            String email = emailET.getText().toString().trim();
            String mobile = mobileET.getText().toString().trim();
            String profileGender = genderET.getText().toString().trim();
            String dob = dobET.getText().toString();

            submitForm(name, email, mobile, dob, profileGender);


        }
    };

    private void submitForm(String name, String email,
                            String mobile, String dob, String profileGender) {

        if (!validateName(name)) {
            return;
        }


        if (!validateEmail(email)) {
            return;
        }


        if (!validateMobile(mobile)) {
            return;

        }


        if (!validateDOB(dob)) {
            return;

        }

        if (!validateGender(profileGender)) {
            return;

        }

        updateProfile(name, email, mobile, dob, profileGender);

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


    private boolean validateDOB(String updateDate) {

        if (updateDate.isEmpty()) {
            myColoredToast("Please fill your DOB!");
            return false;
        }

        return true;
    }


    private boolean validateGender(String profileGender) {

        if (profileGender.isEmpty()) {
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
        dialog = new Dialog(MyAccountActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);


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

    private void updateProfile(String name, String email,
                               String mobile, String dob, String profileGender) {


        String action = "update_profile";

        RequestParams params = new RequestParams();

        params.put("name", name);
        params.put("email", email);
        params.put("phonenumber", mobile);
        params.put("dob", dob);
        params.put("gender", profileGender);


        syncManager.sendToServer(action, params, this);


    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);


        if (action.equalsIgnoreCase("update_profile") && controller.equalsIgnoreCase("user")) {


            try {
                if (status) {

                    myColoredToast(object.getString("message"));

                    getMyProfile();


                } else {

                    myColoredToast(object.getString("error"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equalsIgnoreCase("view_profile") && controller.equalsIgnoreCase("user")) {

            try {
                if (status) {

                    JSONObject jsonObject = object.getJSONObject("viewprofile");
                    userId = jsonObject.getString("id");
                    membership_id = jsonObject.getString("membership_id");
                    email = jsonObject.getString("email");
                    name = jsonObject.getString("name");
                    phonenumber = jsonObject.getString("phonenumber");
                    gender = jsonObject.getString("gender");
                    dob = jsonObject.getString("dob");

                    loadDatatoViews();


                } else {

                    myColoredToast(object.getString("error"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDatatoViews() {

        fullNameET.setText(name);
        emailET.setText(email);
        mobileET.setText(phonenumber);
        genderET.setText(gender);
        dobET.setText(dob);

    }
}
