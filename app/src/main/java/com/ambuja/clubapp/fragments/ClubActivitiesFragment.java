package com.ambuja.clubapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.adapter.ClubActivitiesAdapter;
import com.ambuja.clubapp.data.ClubActivityCategoryData;
import com.ambuja.clubapp.data.ClubActivitySubCategory;
import com.ambuja.clubapp.utils.Constants;
import com.ambuja.clubapp.utils.PinnedSectionListView;
import com.wandrip.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class ClubActivitiesFragment extends BaseFragment {

    private View rootView;
    private PinnedSectionListView mActivitiesList;
    private String clubId;
    private String header;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_club_activities, container, false);
        initUI();
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        clubId = store.getString("clubActivitiesId");
        getActivitiesList(clubId);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            clubId = store.getString("clubActivitiesId");
            getActivitiesList(clubId);
        }
    }

    boolean isAttached = false;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        isAttached = true;
    }


    private void getActivitiesList(String clubId) {

        String action = "clubfacilities_list";

        RequestParams params = new RequestParams();
        params.put("club_id", clubId);

        syncManager.getFromServer(action, params, this);

    }

    private void initUI() {

        mActivitiesList = (PinnedSectionListView) rootView.findViewById(R.id.activitiesListPSLV);
        mActivitiesList.setShadowVisible(false);


    }


    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);

        if (action.equalsIgnoreCase("clubfacilities_list") && controller.equalsIgnoreCase("user")) {

            if (status) {

                JSONObject obj;
                try {

                    Constants.clubActivitiesCategory.clear();

                    JSONArray array = object.getJSONArray("clubfacilities_list");

                    for (int i = 0; i < array.length(); i++) {


                        ClubActivityCategoryData categoryData = new ClubActivityCategoryData();
                        obj = array.getJSONObject(i);

                        HashMap<String, String> map = new HashMap<String, String>();
                        map.put("header", obj.getString("menu_title"));

                        header = obj.getString("menu_title");

                        categoryData.id = obj.getString("menu_id");
                        categoryData.parent_id = obj.getString("menu_type");
                        categoryData.club_id = obj.getString("menu_area_name");
                        categoryData.image = obj.getString("image");
                        categoryData.facility = obj.getString("menu_title");
                        categoryData.description = obj.getString("menu_content");
                        categoryData.status = obj.getString("menu_status");

                        JSONArray array1 = obj.getJSONArray("subcategory");

                        categoryData.size = String.valueOf(array1.length());

                        if (array1 != null) {

                            ArrayList<ClubActivitySubCategory> clubActivitySubCategory = new ArrayList<ClubActivitySubCategory>();
                            for (int j = 0; j < array1.length(); j++) {
                                JSONObject obj_inner = array1.getJSONObject(j);

                                String id = obj_inner.getString("menu_id");
                                String parent_id = obj_inner.getString("menu_type");
                                String club_id = obj_inner.getString("menu_area_name");
                                String image = obj_inner.getString("image");
                                String facility = obj_inner.getString("menu_title");
                                String menu_area = obj.getString("menu_area");
                                String description = obj_inner.getString("menu_content");

                                clubActivitySubCategory.add(new ClubActivitySubCategory(facility, image, id));
                            }
                            categoryData.setClubActyivityData(clubActivitySubCategory);
                        }
                        Constants.clubActivitiesCategory.add(categoryData);
                    }

                    ClubActivitiesAdapter mAdapter = new ClubActivitiesAdapter((BaseActivity) getActivity(),
                            android.R.layout.simple_list_item_1, android.R.id.text1, Constants.clubActivitiesCategory);
                    mActivitiesList.setAdapter(mAdapter);


                } catch (Exception e) {

                    e.printStackTrace();
                }


            }
        }
    }


}
