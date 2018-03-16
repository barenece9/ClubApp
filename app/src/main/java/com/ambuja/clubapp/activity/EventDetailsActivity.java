package com.ambuja.clubapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.squareup.picasso.Picasso;
import com.wandrip.http.RequestParams;

import org.json.JSONObject;

/**
 * Created by pulkitm on 10/25/2016.
 */

public class EventDetailsActivity extends BaseActivity {
    private TextView lessTV;
    private TextView eventHeaderTV;
    private TextView eventDateTV;
    private TextView eventTimeTV;
    private TextView eventsDescriptionTV;
    private TextView continueTV;
    private ImageView eventFullImageIV;
    private ImageView eventShareIV;
    private LinearLayout eventsLL;
    private Button eventBookBT;
    private Button eventFBBT;
    private String eventID;
    private String event_Id;
    private String event_name;
    private String event_startdate;
    private String event_starttime;
    private String event_enddate;
    private String event_endtime;
    private String event_description;
    private String club_name;
    private String created_on;
    private String facebook_link;
    private String booking_link;
    private String featured_image;
    private String pageSubstring;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        eventID = store.getString("eventDetails");
        initUI();
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Event Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        eventsLL = (LinearLayout) findViewById(R.id.eventsLL);
        eventHeaderTV = (TextView) findViewById(R.id.eventHeaderTV);
        eventDateTV = (TextView) findViewById(R.id.eventDateTV);
        eventTimeTV = (TextView) findViewById(R.id.eventTimeTV);
        eventsDescriptionTV = (TextView) findViewById(R.id.eventsDescriptionTV);
        continueTV = (TextView) findViewById(R.id.continueTV);
        lessTV = (TextView) findViewById(R.id.lessTV);
        eventFullImageIV = (ImageView) findViewById(R.id.eventFullImageIV);
        eventShareIV = (ImageView) findViewById(R.id.eventShareIV);
        eventBookBT = (Button) findViewById(R.id.eventBookBT);
        eventFBBT = (Button) findViewById(R.id.eventFBBT);
        continueTV.setVisibility(View.VISIBLE);
        lessTV.setVisibility(View.GONE);
        getData(eventID);
    }

    private void getData(String eventID) {
        String action = "get_event_by_id";
        RequestParams params = new RequestParams();
        params.put("event_id", eventID);
        syncManager.getFromServer(action, params, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);
        if (action.equalsIgnoreCase("get_event_by_id") && controller.equalsIgnoreCase("user")) {
            if (status) {
                try {
                    JSONObject jsonObject = object.getJSONObject("get_event_by_id");
                    event_Id = jsonObject.getString("id");
                    event_name = jsonObject.getString("event_name");
                    event_startdate = jsonObject.getString("event_startdate");
                    event_starttime = jsonObject.getString("event_starttime");
                    event_enddate = jsonObject.getString("event_enddate");
                    event_endtime = jsonObject.getString("event_endtime");
                    event_description = jsonObject.getString("event_description");
                    club_name = jsonObject.getString("club_name");
                    created_on = jsonObject.getString("created_on");
                    facebook_link = jsonObject.getString("facebook_link");
                    booking_link = jsonObject.getString("booking_link");
                    featured_image = jsonObject.getString("featured_image");
                    loadDatatoViews();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadDatatoViews() {
        if (booking_link.equalsIgnoreCase("")) {
            eventBookBT.setVisibility(View.GONE);
        } else {
            eventBookBT.setVisibility(View.VISIBLE);
        }
        if (facebook_link.equalsIgnoreCase("")) {
            eventFBBT.setVisibility(View.GONE);
        } else {
            eventFBBT.setVisibility(View.GONE);
        }
        eventHeaderTV.setText(event_name);
        if (!event_startdate.equalsIgnoreCase("")) {
            eventDateTV.setText(event_startdate);
            eventTimeTV.setText(event_starttime);
            eventTimeTV.setVisibility(View.VISIBLE);
        } else {
            eventDateTV.setText(formateDateFromstring("yyyy-MM-dd HH:mm:ss",
                    "dd/MM/yyyy hh:mm a", created_on));
            eventTimeTV.setVisibility(View.GONE);
        }
        if (event_description.length() > 200) {
            pageSubstring = event_description.substring(0, 130);
            eventsDescriptionTV.setText(Html.fromHtml(pageSubstring));
            continueTV.setVisibility(View.VISIBLE);
            lessTV.setVisibility(View.GONE);
        } else {
            continueTV.setVisibility(View.GONE);
            lessTV.setVisibility(View.GONE);
            eventsDescriptionTV.setText(Html.fromHtml(event_description));
        }
        if (featured_image != null && !featured_image.isEmpty()) {
            eventFullImageIV.setVisibility(View.VISIBLE);
            Picasso.with(this)
                    .load(featured_image).into(eventFullImageIV);
        } else {
            eventFullImageIV.setVisibility(View.GONE);
        }

        continueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueTV.setVisibility(View.GONE);
                lessTV.setVisibility(View.VISIBLE);
                eventsDescriptionTV.setText(Html.fromHtml(event_description));
            }
        });

        lessTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueTV.setVisibility(View.VISIBLE);
                lessTV.setVisibility(View.GONE);
                eventsDescriptionTV.setText(Html.fromHtml(pageSubstring));
            }
        });

        eventShareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (featured_image != null
                        && !featured_image.isEmpty()) {
                    shareWallData(featured_image, event_description, event_name);
                } else {
                    shareTextApp(event_name, event_description);
                }
            }
        });

        eventBookBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(booking_link));
                startActivity(intent);
            }
        });


        eventFBBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(facebook_link));
                startActivity(intent);
            }
        });

    }
}
