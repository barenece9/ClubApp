package com.ambuja.clubapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.ActivitiesDetailActivity;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.activity.UpcomingEventsActivity;
import com.ambuja.clubapp.data.ClubActivityCategoryData;
import com.ambuja.clubapp.data.Item;
import com.ambuja.clubapp.utils.Constants;
import com.ambuja.clubapp.utils.PinnedSectionListView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ClubActivitiesAdapter extends ArrayAdapter<Item>
        implements PinnedSectionListView.PinnedSectionListAdapter {

    private final LayoutInflater inflater;
    private BaseActivity mContext;
    private ArrayList<ClubActivityCategoryData> categoryData;


    public ClubActivitiesAdapter(BaseActivity context, int resource, int textViewResourceId,
                                 ArrayList<ClubActivityCategoryData> categoryData) {
        super(context, resource, textViewResourceId);
        this.categoryData = categoryData;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        generateDataset(false);
    }

    public void generateDataset(boolean clear) {

        if (clear) clear();
        prepareSections(categoryData.size() - 1);

        int sectionPosition = 0, listPosition = 0;

        for (char i = 0; i < categoryData.size(); i++) {
            Item section = new Item(Item.SECTION, categoryData.get(i).facility, "", "");
            section.sectionPosition = sectionPosition;
            section.listPosition = listPosition++;
            onSectionAdded(section, sectionPosition);
            add(section);

            int size = Integer.parseInt(categoryData.get(i).size);
            if (size > 0) {
                ArrayList activityData = categoryData.get(i).getClubActyivityData();
                for (int j = 0; j < activityData.size(); j++) {
                    String imageUrl = categoryData.get(i).getClubActyivityData().get(j).image;
                    String textData = categoryData.get(i).getClubActyivityData().get(j).facility;
                    String clubId = categoryData.get(i).getClubActyivityData().get(j).id;

                    Item item = new Item(Item.ITEM, textData, imageUrl, clubId);
                    item.sectionPosition = sectionPosition;
                    item.listPosition = listPosition++;
                    add(item);
                }
            }
            sectionPosition++;
        }
    }

    protected void prepareSections(int sectionsNumber) {

    }

    protected void onSectionAdded(Item section, int sectionPosition) {

    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.adapter_club_activities, null);
        }

        LinearLayout headerLL = (LinearLayout) convertView.findViewById(R.id.headerLL);
        TextView headerTV = (TextView) convertView.findViewById(R.id.headerTV);
        ImageView categoryImageIV = (ImageView) convertView.findViewById(R.id.categoryImageIV);
        TextView imageTextTV = (TextView) convertView.findViewById(R.id.imageTextTV);
        LinearLayout imageTextLL = (LinearLayout) convertView.findViewById(R.id.imageTextLL);

        final Item data = getItem(position);
        headerLL.setTag(data);
        headerTV.setTag(data);
        categoryImageIV.setTag(data);
        imageTextTV.setTag(data);
        imageTextLL.setTag(data);

        if (data.type == Item.SECTION) {
            headerLL.setVisibility(View.VISIBLE);
            headerTV.setVisibility(View.VISIBLE);
            categoryImageIV.setVisibility(View.GONE);
            imageTextTV.setVisibility(View.GONE);
            imageTextLL.setVisibility(View.GONE);
            headerTV.setText(data.text);
            mContext.store.setString("activityHeading", data.text);

        } else {
            headerLL.setVisibility(View.GONE);
            headerTV.setVisibility(View.GONE);
            //categoryImageIV.setVisibility(View.VISIBLE);
            imageTextTV.setVisibility(View.VISIBLE);
            imageTextLL.setVisibility(View.VISIBLE);
            imageTextTV.setText(data.text);
            if (data.image != null && !data.image.equalsIgnoreCase("")) {
                categoryImageIV.setVisibility(View.VISIBLE);
                Picasso.with(mContext)
                        .load(data.image).fit().centerCrop().placeholder(R.drawable.placeholder).
                        error(R.drawable.placeholder).into(categoryImageIV);
            } else {
                categoryImageIV.setVisibility(View.GONE);
            }
        }

        imageTextTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (data.text.equalsIgnoreCase("events")) {
                    Intent intent = new Intent(mContext, UpcomingEventsActivity.class);
                    mContext.startActivity(intent);
                } else {
                    Intent intent = new Intent(mContext, ActivitiesDetailActivity.class);
                    mContext.store.setString("activityDetailsId", data.clubId);
                    mContext.startActivity(intent);
                }
            }
        });

        categoryImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ActivitiesDetailActivity.class);
                mContext.store.setString("activityDetailsId", data.clubId);
                mContext.startActivity(intent);
            }
        });

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return (null != Constants.clubActivitiesCategory ? Constants.clubActivitiesCategory.size() : 0);
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).type;
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == Item.SECTION;
    }

}