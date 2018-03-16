package com.ambuja.clubapp.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.fragments.AboutFragment;
import com.ambuja.clubapp.fragments.CalendarEventsFragment;
import com.ambuja.clubapp.fragments.ClubActivitiesFragment;
import com.ambuja.clubapp.fragments.ClubListsFragment;
import com.ambuja.clubapp.fragments.EventNotificationFragment;
import com.ambuja.clubapp.fragments.UpcomingEventsFragment;
import com.ambuja.clubapp.utils.BadgeView;
import com.ambuja.clubapp.utils.Constants;
import com.ambuja.clubapp.utils.InputFilterMinMax;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.CENTER;

public class HomeActivity extends BaseActivity {
    String fontReg = "fonts/SEGOEUI_0.TTF";
    private TabLayout tabLayout;
    private boolean doubleBackToExitPressedOnce = false;
    private String appLogin;
    private TextView tabOne, tabTwo, tabThree, tabFour, tabFive, tabSix;
    private BadgeView badgeview;
    private String notifnumber;
    private EditText fullNameET, emailET, mobileET, commentsET;
    private String firstTime;
    private ViewPager viewPager;
    private int currentPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initGCM();


        appLogin = store.getString("appLogin");

        if (Build.VERSION.SDK_INT >= 23) {

            checkReadWritePermission();

        }

        initUI();

    }

    private void getMyProfile() {
        syncManager.getFromServer("view_profile", null, this);
    }


    @Override
    protected void onResume() {
        super.onResume();

        Constants.userId = store.getString("userId");

        if (appLogin.equalsIgnoreCase("true")) {


            getMyProfile();

        }

    }

    private void initUI() {

        notifnumber = store.getString("notifNumber");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setupViewPagerUser(viewPager);
        setupTabIconsUser();
        badgeview = new BadgeView(this, tabFour);

        if (notifnumber != null && !notifnumber.equalsIgnoreCase("") && !notifnumber.equalsIgnoreCase("0")) {
            badgeview.setText(notifnumber);
            badgeview.setTextColor(Color.WHITE);
            badgeview.setBadgeBackgroundColor(Color.parseColor("#b21e1e"));
            badgeview.setTextSize(10);
            tabFour.setText("");
            badgeview.show();
        }

        /*Intent intent = new Intent(HomeActivity.this, ClubActivitiesSettingsActivity.class);
        startActivity(intent);*/
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPage = position;
                if (position != 2) {
                    store.setString("clubId", "");
                }
            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

                if (position != 2) {
                    store.setString("clubId", "");
                }
                if (position == 1) {
                    if (store.getString("clubActivitiesId").equalsIgnoreCase("")) {
                        viewPager.setCurrentItem(0);
                        selectClub();
                    }
                }

                if (position == 3) {
                    //store.setString("notifNumber", "0");
                    badgeview.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void setupTabIconsUser() {

        tabSix = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabSix.setText("");
        tabSix.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.club_selector, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabSix);

        tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.activities_selector, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabThree);

        tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.newsfeed_selector, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabOne);

        tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.schedule_selector, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabTwo);

        tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("");
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.notification_selector, 0, 0);
        tabLayout.getTabAt(4).setCustomView(tabFour);

        tabFive = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFive.setText("");
        tabFive.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.info_selector, 0, 0);
        tabLayout.getTabAt(5).setCustomView(tabFive);

    }


    private void setupViewPagerUser(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ClubListsFragment(), "SIX");
        adapter.addFrag(new ClubActivitiesFragment(), "THREE");
        adapter.addFrag(new UpcomingEventsFragment(), "ONE");
        adapter.addFrag(new CalendarEventsFragment(), "TWO");
        adapter.addFrag(new EventNotificationFragment(), "FOUR");
        adapter.addFrag(new AboutFragment(), "FIVE");
        viewPager.setAdapter(adapter);
    }

    public void sitchPager() {
        viewPager.setCurrentItem(1);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem logout = menu.findItem(R.id.action_logout);
        MenuItem payment = menu.findItem(R.id.payments);
        MenuItem changePassword = menu.findItem(R.id.changePassword);
        if (appLogin.equalsIgnoreCase("true")) {
            logout.setVisible(true);
            payment.setVisible(true);
            changePassword.setVisible(true);
        } else {
            logout.setVisible(false);
            payment.setVisible(false);
            changePassword.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            //backButtonPressed();
            return true;
        }

        Intent intent;

        switch (item.getItemId()) {
            case R.id.shareApp:

                shareApp();

                return true;

            case R.id.appFeedback:

                openFeedbackDialog();

                return true;

            case R.id.contactUs:

                openContactUsDialog();

                return true;

            case R.id.clubLists:
                intent = new Intent(this, ClubListSettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.changePassword:

                intent = new Intent(this, ChangePasswordActivity.class);
                startActivity(intent);
                return true;

            case R.id.payments:

                intent = new Intent(this, PaymentActivity.class);
                startActivity(intent);

                return true;
            case R.id.tutorials:
                intent = new Intent(this, TutorialsActivity.class);
                startActivity(intent);
                return true;

            case R.id.settings:

                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_login:

                if (appLogin.equalsIgnoreCase("true")) {
                    intent = new Intent(this, MyAccountActivity.class);
                    startActivity(intent);

                } else {

                    intent = new Intent(this, SignUpActivity.class);
                    startActivity(intent);
                }
                return true;

            case R.id.action_logout:
                showLogoutDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void openFeedbackDialog() {

        final Dialog dialog;
        dialog = new Dialog(HomeActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(CENTER);

        Button SubmitBT;
        ImageView cancelIV;

        dialog.setContentView(R.layout.dialog_feedback);

        fullNameET = (EditText) dialog.findViewById(R.id.fullNameET);
        emailET = (EditText) dialog.findViewById(R.id.emailET);
        mobileET = (EditText) dialog.findViewById(R.id.mobileET);
        mobileET.setFilters(new InputFilter[]{new InputFilterMinMax("1", "9999999999")});
        commentsET = (EditText) dialog.findViewById(R.id.commentsET);
        Typeface typeface = Typeface.createFromAsset(getAssets(), fontReg);
        fullNameET.setTypeface(typeface);
        emailET.setTypeface(typeface);
        mobileET.setTypeface(typeface);
        commentsET.setTypeface(typeface);
        cancelIV = (ImageView) dialog.findViewById(R.id.cancelIV);
        SubmitBT = (Button) dialog.findViewById(R.id.SubmitBT);

        SubmitBT.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String feedbackName = fullNameET.getText().toString();
                String feedbackemail = emailET.getText().toString().trim();
                String feedbackmobile = mobileET.getText().toString().trim();
                String feedbackcomments = commentsET.getText().toString();

                if (feedbackName.isEmpty()) {

                    fullNameET.setError(getString(R.string.err_msg_first_name));

                } else if (feedbackemail.isEmpty()) {

                    emailET.setError(getString(R.string.err_msg_email));

                } else if (feedbackcomments.isEmpty()) {

                    commentsET.setError("Please add your comments!");

                } else {
                    sendFeedback(feedbackName, feedbackemail, feedbackmobile, feedbackcomments, dialog);
                }
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

    private void sendFeedback(String feedbackName, String feedbackemail,
                              String feedbackmobile, String feedbackcomments, Dialog dialog) {
        String action = "add_feedback";
        RequestParams params = new RequestParams();
        params.put("name", feedbackName);
        params.put("email", feedbackemail);
        params.put("phonenumber", feedbackmobile);
        params.put("feedback", feedbackcomments);

        syncManager.sendToServer(action, params, this);
        dialog.dismiss();

    }


    @Override
    public void onBackPressed() {

        if (currentPage == 1 || currentPage == 2 || currentPage == 3 ||
                currentPage == 4 || currentPage == 5) {

            viewPager.setCurrentItem(0);

        } else {

            if (doubleBackToExitPressedOnce) {
                exitFromApp();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            showSnackbar(tabLayout, "Please click back again to exit");


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }
    }


    private void backButtonPressed() {


        if (tabThree.isSelected()) {

            Intent intent = new Intent(HomeActivity.this, ClubActivitiesSettingsActivity.class);
            startActivity(intent);

        } else {

            if (doubleBackToExitPressedOnce) {
                exitFromApp();
                return;
            }

            this.doubleBackToExitPressedOnce = true;

            showSnackbar(tabLayout, "Please click back again to exit");


            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        }

    }

    @Override
    protected void onStop() {
        super.onStop();

        if (toast != null)
            toast.cancel();

    }

    protected void showLogoutDialog() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(R.string.logout);
        builder.setMessage(R.string.do_you_want_logout);
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String action = "logout";
                syncManager.getFromServer(action, null, HomeActivity.this);
                dialog.dismiss();


            }
        });


        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });
        builder.show();


    }


    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);


        if (action.equalsIgnoreCase("view_profile") && controller.equalsIgnoreCase("user")) {

            try {
                if (status) {

                    JSONObject jsonObject = object.getJSONObject("viewprofile");
                    String userId = jsonObject.getString("id");
                    String membership_id = jsonObject.getString("membership_id");
                    String email = jsonObject.getString("email");
                    String name = jsonObject.getString("name");
                    String phonenumber = jsonObject.getString("phonenumber");
                    String gender = jsonObject.getString("gender");
                    String dob = jsonObject.getString("dob");
                } else {
                    myColoredToast(object.getString("error"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (action.equalsIgnoreCase("feedback") && controller.equalsIgnoreCase("user")) {

            try {
                if (status) {
                    myColoredToast(object.getString("message"));
                } else {
                    myColoredToast(object.getString("error"));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (action.equalsIgnoreCase("logout") && controller.equalsIgnoreCase("user")) {
            if (status) {
                store.setString("userId", "");
                Constants.userId = "";
            }
        }
    }
}
