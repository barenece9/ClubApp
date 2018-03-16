package com.ambuja.clubapp.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.adapter.EventsListAdapter;
import com.ambuja.clubapp.data.EventsData;
import com.ambuja.clubapp.data.SingleItemModel;
import com.ambuja.clubapp.utils.KBCalendar;
import com.wandrip.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;


public class CalendarEventsFragment extends BaseFragment implements KBCalendar.IKBAgendaEvent {


    private View rootView;
    private TextView monthTV;
   // private String clubId;
    private String currentDate;
    private KBCalendar mKBCalendar;
    private RecyclerView mUpcomingEventsList;
    private TextView mEventsEmptyTV;
    private JSONObject obj2;
    ArrayList<EventsData> eventsDataList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_calendar_events, container, false);
       // clubId = store.getString("clubId");
        mKBCalendar = new KBCalendar(getActivity(), this, rootView);
        mKBCalendar.loadKBCalendar();
        eventsDataList = new ArrayList<EventsData>();
        return rootView;
    }

    private void initUI() {
        mKBCalendar.getCurrentDate();
        monthTV = (TextView) rootView.findViewById(R.id.monthTV);
        monthTV.setText(mKBCalendar.getCurrentMonth());
        currentDate = DateFormat.format("yyyy-MM-dd", mKBCalendar.getCurrentDate()).toString();
        mUpcomingEventsList = (RecyclerView) rootView.findViewById(R.id.upcomingEventsRV);
        mEventsEmptyTV = (TextView) rootView.findViewById(R.id.eventsEmptyTV);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mUpcomingEventsList.setLayoutManager(mLayoutManager);
        getEventsDates();
    }

    private void getEventsDates() {

        String action = "get_event_dates";

        RequestParams params = new RequestParams();
        params.put("club_id", "");

        syncManager.getFromServer(action, params, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
        getDateEvents(currentDate);
    }

    @Override
    public void onDateSelect(Date date) {
        log("KBCalendar" + "Date : " + DateFormat.format("yyyy-MM-dd", date).toString());
        String selectedDate = DateFormat.format("yyyy-MM-dd", date).toString();
        getDateEvents(selectedDate);
    }

    @Override
    public void onListScroll(Date date) {
        log("KBCalendar" + "Date : " + DateFormat.format("yyyy-MM-dd", date).toString());
    }

    private void getDateEvents(String Date) {
        RequestParams params = new RequestParams();
        params.put("event_date", Date);
        params.put("club_id", "");
        syncManager.getFromServer("date_wise_events", params, this);
    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);
        if (action.equalsIgnoreCase("upcoming_events") && controller.equalsIgnoreCase("user")) {
            if (status) {
                mEventsEmptyTV.setVisibility(View.GONE);
                mUpcomingEventsList.setVisibility(View.VISIBLE);
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
                            ArrayList<SingleItemModel> imageItem = new ArrayList<SingleItemModel>();
                            for (int j = 0; j < array1.length(); j++) {
                                obj2 = array1.getJSONObject(j);
                                String id = obj2.getString("id");
                                String img = obj2.getString("img");
                                imageItem.add(new SingleItemModel(id, img));
                            }
                            eventsData.setAllItemsInSection(imageItem);
                        }
                        eventsDataList.add(eventsData);
                    }
                    EventsListAdapter mAdapter = new EventsListAdapter((BaseActivity) getActivity(),
                            eventsDataList);
                    mUpcomingEventsList.setAdapter(mAdapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                mEventsEmptyTV.setVisibility(View.VISIBLE);
                mUpcomingEventsList.setVisibility(View.GONE);
            }
        } else if (action.equalsIgnoreCase("get_event_dates") && controller.equalsIgnoreCase("user")) {
            if (status) {
                try {
                    JSONArray datesArray = object.getJSONArray("GetData");
                    for (int a = 0; a < datesArray.length(); a++) {
                        datesList.add(datesArray.getJSONObject(a).getString("event_startdate"));
                    }
                    mKBCalendar.setEventsDate(datesList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    ArrayList<String> datesList = new ArrayList<>();
}
