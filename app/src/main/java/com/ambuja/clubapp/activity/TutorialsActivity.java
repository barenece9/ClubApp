package com.ambuja.clubapp.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.squareup.picasso.Picasso;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

/**
 * Created by partikshas on 03-Jul-17.
 */

public class TutorialsActivity extends BaseActivity {

    private ImageView ivTutorialImage;
    private TextView tvTitle;
    private TextView tvDesc;
    private String pageTitle;
    private String pageImage;
    private String pageContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.tutorials));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        ivTutorialImage = (ImageView) findViewById(R.id.ivTutorialImage);
        tvDesc = (TextView) findViewById(R.id.tvDesc);
        getTutorialDetails();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    private void getTutorialDetails() {
        RequestParams params = new RequestParams();
        params.put("page_id", "23");
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
                    setValues();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setValues() {
        Picasso.with(this).load(pageImage).placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder).into(ivTutorialImage);
        tvDesc.setText(pageContent);
        tvTitle.setText(pageTitle);
    }
}
