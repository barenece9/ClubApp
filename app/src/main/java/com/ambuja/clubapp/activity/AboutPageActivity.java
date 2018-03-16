package com.ambuja.clubapp.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.squareup.picasso.Picasso;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;


public class AboutPageActivity extends BaseActivity {

    private TextView mToolbarTitle;
    private ImageView mPageImage;
    //private TextView mPageText;
    private String pageId;
    private String pageTitle;
    private String pageImage;
    private String pageContent;
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_page);

        pageId = store.getString("aboutPageId");

        initUI();


    }

    @Override
    protected void onResume() {
        super.onResume();

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

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        webview = (WebView) findViewById(R.id.activitiesWV);

        mToolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        mPageImage = (ImageView) findViewById(R.id.pageImageIV);
       // mPageText = (TextView) findViewById(R.id.pageDataTV);


        webview.setWebViewClient(new MyWebViewClient());
        webview.setWebViewClient(new MyWebViewClientChrome());
        webview.getSettings().setLoadWithOverviewMode(true);


    }

    private void getPageData() {

        RequestParams params = new RequestParams();
        params.put("page_id", pageId);

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

                    loadDataOnViews();


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadDataOnViews() {

        mToolbarTitle.setText(pageTitle);
       // mPageText.setText(pageContent);

        webview.loadData("<html><head><style>img {\n" +
                "    max-width: 100%;\n" +
                "    height: auto;width:auto;\n" +
                "}</style></head><body style='text-align:center;'>" + pageContent +
                "</body></html>", "text/html; charset=UTF-8", null);


        //mPageText.setText(Html.fromHtml(pageContent), TextView.BufferType.SPANNABLE);
        Picasso.with(this)
                .load(pageImage).placeholder(R.drawable.placeholder).
                error(R.drawable.placeholder).into(mPageImage);

        /*String colorText = "<font color=\"#59852e\"><bold>" + "Continue Reading" + "</bold></font>";
        makeTextViewResizable(mPageText, 3, "Continue Reading", true);*/

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



}
