package com.ambuja.clubapp.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.adapter.NotificationListAdapter;
import com.ambuja.clubapp.data.NotificationData;
import com.ambuja.clubapp.utils.Constants;
import com.wandrip.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;


public class EventNotificationFragment extends BaseFragment {

    private View rootView;
    private RecyclerView notificationRV;
    private TextView mEventsEmptyTV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_event_notification, container, false);

        initUI();

        getNotification();


        return rootView;
    }

    private void initUI() {

        Constants.userId = store.getString("userId");

       // store.setString("notifNumber", "0");

        mEventsEmptyTV = (TextView) rootView.findViewById(R.id.eventsEmptyTV);

        notificationRV = (RecyclerView) rootView.findViewById(R.id.notificationRV);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        notificationRV.setLayoutManager(mLayoutManager);

    }

    boolean isAttached = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        isAttached = true;
    }


/*    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isAttached) {


            if (isVisibleToUser) {

                getNotification();


            }

        }
    }*/


    private void getNotification() {

        String action = "notifications";

        RequestParams params = new RequestParams();

        params.put("user_id", Constants.userId);

        syncManager.getFromServer(action, params, this);

    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);


        if (action.equalsIgnoreCase("notifications") && controller.equalsIgnoreCase("user")) {

            if (status) {

                mEventsEmptyTV.setVisibility(View.GONE);
                notificationRV.setVisibility(View.VISIBLE);

                JSONObject obj;
                try {

                    Constants.notificationDataList.clear();
                    JSONArray array = object.getJSONArray("notifications");

                    for (int i = 0; i < array.length(); i++) {
                        NotificationData notificationData = new NotificationData();
                        obj = array.getJSONObject(i);
                        notificationData.id = obj.getString("id");
                        notificationData.to_user_id = obj.getString("to_user_id");
                        notificationData.message = obj.getString("message");
                        notificationData.model_type = obj.getString("model_type");
                        notificationData.model_id = obj.getString("model_id");
                        notificationData.state_id = obj.getString("state_id");
                        notificationData.type_id = obj.getString("type_id");
                        notificationData.create_user_id = obj.getString("create_user_id");
                        notificationData.started_on = obj.getString("started_on");
                        notificationData.day = obj.getString("day");
                        notificationData.title = obj.getString("title");
                        notificationData.short_description = obj.getString("short_description");
                        notificationData.expired_on = obj.getString("expired_on");


                        Constants.notificationDataList.add(notificationData);
                    }

                    NotificationListAdapter mAdapter = new NotificationListAdapter((BaseActivity) getActivity(), Constants.notificationDataList);
                    notificationRV.setAdapter(mAdapter);

                } catch (Exception e) {

                    e.printStackTrace();
                }


            } else {

                mEventsEmptyTV.setVisibility(View.VISIBLE);
                notificationRV.setVisibility(View.GONE);

            }

        }
    }

}
