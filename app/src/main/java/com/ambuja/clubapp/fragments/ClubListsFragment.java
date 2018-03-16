package com.ambuja.clubapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.activity.HomeActivity;
import com.ambuja.clubapp.adapter.ClubListAdapter;
import com.ambuja.clubapp.data.ClubListData;
import com.ambuja.clubapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;


public class ClubListsFragment extends BaseFragment {

    private ListView mClubList;
    private View rootView;
    private String clubId;
    private String clubName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_club_lists, container, false);
        initUI();
        return rootView;
    }

    private void initUI() {
        mClubList = (ListView) rootView.findViewById(R.id.clubListGV);
        mClubList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                clubId = Constants.clubList.get(position).id;
                clubName = Constants.clubList.get(position).club_name;

                store.setString("clubActivitiesId", clubId);
                store.setString("clubName", clubName);
                ((HomeActivity) getActivity()).sitchPager();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        getClubList();
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

                    ClubListAdapter mAdapter = new ClubListAdapter((BaseActivity) getActivity(), Constants.clubList);
                    mClubList.setAdapter(mAdapter);


                } catch (Exception e) {

                    e.printStackTrace();
                }


            }


        }
    }
}
