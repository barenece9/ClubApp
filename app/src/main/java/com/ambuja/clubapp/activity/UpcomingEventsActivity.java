package com.ambuja.clubapp.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.adapter.EventsListAdapter;
import com.ambuja.clubapp.data.EventsData;
import com.ambuja.clubapp.data.SingleItemModel;
import com.wandrip.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpcomingEventsActivity extends BaseActivity {

    private String clubId = "";
    private RecyclerView mUpcomingEventsList;
    private SwipeRefreshLayout mSwipeEventsSRL;
    private TextView mEventsEmptyTV;
    private JSONObject obj2;
    ArrayList<EventsData> eventsDataList;
    private String clubName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_events);

        eventsDataList = new ArrayList<EventsData>();
        clubName = store.getString("clubName");
        initUI();

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
    public void onResume() {
        super.onResume();

        getUpcomingEvents();
    }


    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle(clubName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUpcomingEventsList = (RecyclerView) findViewById(R.id.upcomingEventsRV);
        mSwipeEventsSRL = (SwipeRefreshLayout) findViewById(R.id.swipeEventsSRL);
        mEventsEmptyTV = (TextView) findViewById(R.id.eventsEmptyTV);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mUpcomingEventsList.setLayoutManager(mLayoutManager);
        mSwipeEventsSRL.setColorSchemeColors(Color.parseColor("#0266C8"), Color.parseColor("#F90101"),
                Color.parseColor("#F2B50F"), Color.parseColor("#00933B"));
        mSwipeEventsSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                getUpcomingEvents();
            }
        });
    }

    private void getUpcomingEvents() {

        clubId = store.getString("clubActivitiesId");

        RequestParams params = new RequestParams();
        params.put("event_date", "");
        params.put("club_id", "");
        syncManager.getFromServer("upcoming_events", params, this);
    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);
        if (action.equalsIgnoreCase("upcoming_events") && controller.equalsIgnoreCase("user")) {
            if (status) {
                mEventsEmptyTV.setVisibility(View.GONE);
                mUpcomingEventsList.setVisibility(View.VISIBLE);
                mSwipeEventsSRL.setVisibility(View.VISIBLE);
                JSONObject obj;
                try {
                    eventsDataList.clear();
                    JSONArray array = object.getJSONArray("upcoming_events");
                    for (int i = 0; i < array.length(); i++) {
                        EventsData eventsData = new EventsData();
                        obj = array.getJSONObject(i);
                        eventsData.setId(obj.getString("id"));
                        eventsData.setEvent_name(obj.getString("event_name"));
                        eventsData.setEvent_startdate(obj.getString("event_startdate"));
                        eventsData.setEvent_starttime(obj.getString("event_starttime"));
                        eventsData.setEvent_enddate(obj.getString("event_enddate"));
                        eventsData.setEvent_endtime(obj.getString("event_endtime"));
                        eventsData.setFeatured_image(obj.getString("featured_image"));
                        eventsData.setEvent_description(obj.getString("event_description"));
                        eventsData.setClub_name(obj.getString("club_name"));
                        String createdDate = obj.getString("created_on");
                        eventsData.setCreated_on(formateDateFromstring("yyyy-MM-dd HH:mm:ss",
                                "dd/MM/yyyy hh:mm a", createdDate));
                        eventsData.setFacebook_link(obj.getString("facebook_link"));
                        eventsData.setBooking_link(obj.getString("booking_link"));
                        if (!eventsData.featured_image.equalsIgnoreCase("")) {
                            JSONArray array1 = obj.getJSONArray("event_image_slider");
                            ArrayList<SingleItemModel> singleItem = new ArrayList<SingleItemModel>();
                            for (int j = 0; j < array1.length(); j++) {
                                obj2 = array1.getJSONObject(j);
                                String id = obj2.getString("id");
                                String img = obj2.getString("img");
                                singleItem.add(new SingleItemModel(id, img));
                            }
                            eventsData.setAllItemsInSection(singleItem);
                        }
                        eventsDataList.add(eventsData);
                    }
                    EventsListAdapter mAdapter = new EventsListAdapter(this, eventsDataList);
                    mUpcomingEventsList.setAdapter(mAdapter);
                    mSwipeEventsSRL.setRefreshing(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mEventsEmptyTV.setVisibility(View.VISIBLE);
                mUpcomingEventsList.setVisibility(View.GONE);
                mSwipeEventsSRL.setVisibility(View.GONE);
            }
        }
    }
}