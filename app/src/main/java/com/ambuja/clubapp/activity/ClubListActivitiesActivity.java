package com.ambuja.clubapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.adapter.ClubListAdapter;
import com.ambuja.clubapp.data.ClubListData;
import com.ambuja.clubapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;


public class ClubListActivitiesActivity extends BaseActivity {

    private ListView mClubList;
    private String clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club_list);
        initUI();
    }

    private void initUI() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.club_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mClubList = (ListView) findViewById(R.id.clubListGV);
        mClubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clubId = Constants.clubList.get(position).id;
                store.setString("clubIdActivities", clubId);
                Intent intent = new Intent(ClubListActivitiesActivity.this, HomeActivity.class);
                startActivity(intent);
                finishAffinity();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        getClubList();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return false;
    }

    private void getClubList() {

        syncManager.getFromServer("clubs_list", null, this);

    }

    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);

        if (action.equalsIgnoreCase("clubs_list") && controller.equalsIgnoreCase("user")) {

            if (status) {

                JSONObject obj;
                try {

                    Constants.clubList.clear();
                    JSONArray array = object.getJSONArray("clubs_list");

                    for (int i = 0; i < array.length(); i++) {
                        ClubListData clubListData = new ClubListData();
                        obj = array.getJSONObject(i);
                        clubListData.id = obj.getString("area_id");
                        clubListData.club_name = obj.getString("area_title");
                        clubListData.club_image = obj.getString("menu_logo");


                        Constants.clubList.add(clubListData);

                    }

                    ClubListAdapter mAdapter = new ClubListAdapter(this, Constants.clubList);
                    mClubList.setAdapter(mAdapter);


                } catch (Exception e) {

                    e.printStackTrace();
                }


            }


        }
    }
}
