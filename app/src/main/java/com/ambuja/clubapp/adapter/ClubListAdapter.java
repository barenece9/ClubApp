package com.ambuja.clubapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.data.ClubListData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ClubListAdapter extends ArrayAdapter<ClubListData> {

    private LayoutInflater inflater;
    private BaseActivity mContext;

    public ClubListAdapter(BaseActivity context, ArrayList<ClubListData> clubData) {
        super(context, 0, clubData);
        this.mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_club_list, null);

        }


        ImageView mClubImage = (ImageView) convertView.findViewById(R.id.clubImageIV);
      /*  TextView imageTextTV = (TextView) convertView.findViewById(R.id.imageTextTV);
        imageTextTV.setVisibility(View.GONE);*/
        ClubListData data = getItem(position);


        Picasso.with(mContext)
                .load(data.club_image).placeholder(R.drawable.placeholder).
                error(R.drawable.placeholder).into(mClubImage);

        //imageTextTV.setText(data.club_name);

        mClubImage.setTag(data);
        //imageTextTV.setTag(data);

        return convertView;
    }
}
