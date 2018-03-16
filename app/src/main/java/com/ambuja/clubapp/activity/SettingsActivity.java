package com.ambuja.clubapp.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

import static android.view.Gravity.CENTER;


public class SettingsActivity extends BaseActivity {


    private String pageTitle;
    private String pageImage;
    private String pageContent;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initUI();
    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.action_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView aboutTV = (TextView) findViewById(R.id.aboutTV);
        //  TextView clubListTV = (TextView) findViewById(R.id.clubListTV);
        aboutTV.setOnClickListener(aboutListener);
        //clubListTV.setOnClickListener(clubListener);
        getPageData();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }


    private View.OnClickListener aboutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            showAboutDialog();
        }
    };

    private View.OnClickListener clubListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(SettingsActivity.this, ClubListSettingsActivity.class);
            startActivity(intent);
        }
    };


    private void showAboutDialog() {

        final Dialog dialog;
        dialog = new Dialog(SettingsActivity.this, android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);

        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        window.setGravity(CENTER);

        ImageView cancelIV;

        dialog.setContentView(R.layout.dialog_about);
        TextView versionTV, desTV, websiteTV;

        cancelIV = (ImageView) dialog.findViewById(R.id.cancelIV);
        desTV = (TextView) dialog.findViewById(R.id.desTV);
        desTV.setText(pageContent);
        cancelIV.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    private void getPageData() {
        RequestParams params = new RequestParams();
        params.put("page_id", "21");

        syncManager.getFromServer("get_page_details", params, this);

    }


    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);

        if (action.equalsIgnoreCase("get_page_details") && controller.equalsIgnoreCase("user")) {


            try {
                if (status) {

                    JSONObject jsonObject = object.getJSONObject("get_page_details");

                    pageTitle = jsonObject.getString("page_title");
                    pageImage = jsonObject.getString("page_image");
                    pageContent = jsonObject.getString("page_content");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
