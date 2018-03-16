package com.ambuja.clubapp.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.adapter.AboutAdapter;
import com.ambuja.clubapp.data.AboutPageData;
import com.ambuja.clubapp.utils.Constants;

import org.json.JSONArray;
import org.json.JSONObject;


public class AboutFragment extends BaseFragment {


    private View rootView;
    private RecyclerView mAboutRV;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_about, container, false);

        initUI();


        return rootView;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


        if (isVisibleToUser) {

            getItemsList();

        } else {


        }


    }

    private void getItemsList() {

        syncManager.getFromServer("get_pages_list", null, this);

    }


    private void initUI() {

        mAboutRV = (RecyclerView) rootView.findViewById(R.id.aboutRV);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        mAboutRV.setLayoutManager(mLayoutManager);

       /* TextView mDownloadforms = (TextView) rootView.findViewById(R.id.downloadformsTV);
        TextView mGuestCharges = (TextView) rootView.findViewById(R.id.guestChargesTV);
        TextView mAdministrative = (TextView) rootView.findViewById(R.id.administrativeTV);
        TextView mManagingCommittee = (TextView) rootView.findViewById(R.id.managingCommitteeTV);
        TextView mSmartCard = (TextView) rootView.findViewById(R.id.smartCardTV);
        TextView mMembershipDetails = (TextView) rootView.findViewById(R.id.memberShipDetailsTV);
        TextView mClubOverview = (TextView) rootView.findViewById(R.id.clubOverviewTV);
        TextView mDisclaimer = (TextView) rootView.findViewById(R.id.disclaimerTV);
        TextView mContactUs = (TextView) rootView.findViewById(R.id.contactUsTV);


        mDownloadforms.setOnClickListener(downloadListener);
        mGuestCharges.setOnClickListener(guestListener);
        mAdministrative.setOnClickListener(adminListener);
        mManagingCommittee.setOnClickListener(committeeListener);
        mSmartCard.setOnClickListener(smartCardListener);
        mMembershipDetails.setOnClickListener(memberDetailsListener);
        mClubOverview.setOnClickListener(clubListener);
        mDisclaimer.setOnClickListener(disclaimerListener);
        mContactUs.setOnClickListener(contactUsListener);*/


    }

    private View.OnClickListener downloadListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener guestListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };


    private View.OnClickListener adminListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener committeeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener smartCardListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener memberDetailsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener clubListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener disclaimerListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };

    private View.OnClickListener contactUsListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
    };


    @Override
    public void onSyncSuccess(String controller, String action, boolean status, JSONObject object) {
        super.onSyncSuccess(controller, action, status, object);

        if (action.equalsIgnoreCase("get_pages_list") && controller.equalsIgnoreCase("user")) {

            if (status) {

                JSONObject obj;
                try {

                    Constants.aboutPagesList.clear();
                    JSONArray array = object.getJSONArray("get_pages_list");

                    for (int i = 0; i < array.length(); i++) {
                        AboutPageData aboutpageData = new AboutPageData();
                        obj = array.getJSONObject(i);
                        aboutpageData.id = obj.getString("id");
                        aboutpageData.page_title = obj.getString("page_title");

                        Constants.aboutPagesList.add(aboutpageData);
                    }

                    AboutAdapter mAdapter = new AboutAdapter((BaseActivity) getActivity(), Constants.aboutPagesList);
                    mAboutRV.setAdapter(mAdapter);

                } catch (Exception e) {

                    e.printStackTrace();
                }


            }
        }
    }
}
