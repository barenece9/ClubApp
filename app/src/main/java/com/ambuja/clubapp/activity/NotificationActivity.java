package com.ambuja.clubapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.adapter.AboutAdapter;
import com.ambuja.clubapp.adapter.NotificationListAdapter;
import com.ambuja.clubapp.data.AboutPageData;
import com.ambuja.clubapp.data.NotificationData;
import com.ambuja.clubapp.utils.Constants;
import com.wandrip.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;


public class NotificationActivity extends BaseActivity {

    private RecyclerView notificationRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Constants.userId = store.getString("userId");

        initUI();
    }

    private void initUI() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        notificationRV = (RecyclerView) findViewById(R.id.notificationRV);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        notificationRV.setLayoutManager(mLayoutManager);


        getNotification();

    }

    private void getNotification() {

        String action = "notifications";

        RequestParams params = new RequestParams();

        params.put("user_id", Constants.userId);

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
    public void onBackPressed() {

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);


        if (action.equalsIgnoreCase("notifications") && controller.equalsIgnoreCase("user")) {

            if (status) {


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

                    NotificationListAdapter mAdapter = new NotificationListAdapter(this, Constants.notificationDataList);
                    notificationRV.setAdapter(mAdapter);

                } catch (Exception e) {

                    e.printStackTrace();
                }



            }

        }
    }
}
