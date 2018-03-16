package com.ambuja.clubapp.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.adapter.EventsListAdapter;
import com.ambuja.clubapp.data.EventsData;
import com.ambuja.clubapp.data.SingleItemModel;
import com.wandrip.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class UpcomingEventsFragment extends BaseFragment {
    private View rootView;
    private String clubId = "";
    private RecyclerView mUpcomingEventsList;
    private SwipeRefreshLayout mSwipeEventsSRL;
    private TextView mEventsEmptyTV;
    private JSONObject obj2;
    ArrayList<EventsData> eventsDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_upcoming_events, container, false);

        initUI();


        eventsDataList = new ArrayList<EventsData>();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        getUpcomingEvents();
    }



    private void initUI() {
        mUpcomingEventsList = (RecyclerView) rootView.findViewById(R.id.upcomingEventsRV);
        mSwipeEventsSRL = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeEventsSRL);
        mEventsEmptyTV = (TextView) rootView.findViewById(R.id.eventsEmptyTV);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
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

        clubId = store.getString("clubId");

        RequestParams params = new RequestParams();
        params.put("event_date", "");
        params.put("club_id", clubId);
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
                    EventsListAdapter mAdapter = new EventsListAdapter((BaseActivity) getActivity(),
                            eventsDataList);
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