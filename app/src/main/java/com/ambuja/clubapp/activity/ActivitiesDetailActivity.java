package com.ambuja.clubapp.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.squareup.picasso.Picasso;
import com.wandrip.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class ActivitiesDetailActivity extends BaseActivity {

    WebView webview;
    TextView headling;
    ProgressBar progressBar1;
    ImageView featured_image;

    // FrameLayout loaderlayout;
    public int post_id = 0;
    private Toolbar toolbar;
    private String attractionID;
    private String id;
    private String facility;
    private String image;
    private String description;
    private String parent_id;
    private String club_id;
    private String created_on;
    private String activityDetailsId;
    ArrayList<String> activityDataList;
    private String heading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities_detail);

        activityDetailsId = store.getString("activityDetailsId");
        heading = store.getString("activityHeading");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webview = (WebView) findViewById(R.id.activitiesWV);
        headling = (TextView) findViewById(R.id.headling);
        progressBar1 = (ProgressBar) findViewById(R.id.progressBar1);
        featured_image = (ImageView) findViewById(R.id.featured_image);
        getSupportActionBar().setTitle(heading);
        activityDataList = new ArrayList<String>();
        getSupportActionBar().setElevation(0);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebViewClient(new MyWebViewClientChrome());
        webview.getSettings().setLoadWithOverviewMode(true);

        getDetailActivity();

    }

    private void getDetailActivity() {

        String action = "clubfacility_by_id";

        RequestParams params = new RequestParams();
        params.put("club_facility_id", activityDetailsId);

        syncManager.getFromServer(action, params, this);

    }


    public class MyWebViewClient extends WebViewClient {

        @Override

        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            // TODO Auto-generated method stub

            view.loadUrl(url);

            return true;

        }

    }

    private class MyWebViewClientChrome extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {

            // loaderlayout.setVisibility(View.GONE);

            // progressBar1.setVisibility(View.VISIBLE);

            super.onPageFinished(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            //  loaderlayout.setVisibility(View.VISIBLE);
//
            super.onPageStarted(view, url, favicon);
        }

    }


    public boolean canGoBack() {
        return webview.canGoBack();
    }

    public void goBack() {
        webview.goBack();
    }


    @Override
    public void onBackPressed() {


        if (canGoBack()) {

            goBack();

        } else {

            super.onBackPressed();

        }

    }


    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);


        if (action.equalsIgnoreCase("clubfacility_by_id") && controller.equalsIgnoreCase("user")) {


            try {
                if (status) {
                    activityDataList.clear();
                    JSONArray array = object.getJSONArray("clubfacilities_list");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        id = obj.getString("menu_id");
                        facility = obj.getString("menu_title");
                        image = obj.getString("image");
                        description = obj.getString("menu_content");
                        parent_id = obj.getString("menu_type");
                        club_id = obj.getString("menu_area_name");
                        //created_on = obj.getString("created_on");

                    }


                    loadDatatoViews();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void loadDatatoViews() {


        progressBar1.setVisibility(View.GONE);

        headling.setText(Html.fromHtml(facility));

        if (!image.equalsIgnoreCase("") && image != null) {

            featured_image.setVisibility(View.VISIBLE);
            Picasso.with(this).load(image).into(featured_image);
        } else {

            featured_image.setVisibility(View.GONE);

        }


        String data = "<html><head><style>img {\n" +
                "    max-width: 100%;\n" +
                "    height: auto;width:auto;\n" +
                "}</style></head><body style='text-align:center;'>" + description +
                "</body></html>";
        webview.loadData(data, "text/html; charset=UTF-8", null);


    }
}
