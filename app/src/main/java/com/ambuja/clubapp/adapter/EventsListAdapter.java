package com.ambuja.clubapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.data.EventsData;

import java.util.ArrayList;


public class EventsListAdapter extends RecyclerView.Adapter<EventsListAdapter.MyViewHolder> {

    private BaseActivity mContext;
    private LayoutInflater inflater;
    private String pageSubstring;

    private ArrayList<EventsData> eventsData;


    public EventsListAdapter(BaseActivity context, ArrayList<EventsData> eventsData) {
        this.mContext = context;
        this.eventsData = eventsData;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapter_events_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        //final EventsData data = Constants.eventsList.get(position);

        holder.eventHeaderTV.setTag(position);
        holder.eventDateTV.setTag(position);
        holder.eventTimeTV.setTag(position);
        holder.eventsDescriptionTV.setTag(position);
        holder.continueTV.setTag(position);
        holder.lessTV.setTag(position);
        holder.EventsImagesRV.setTag(position);
        holder.eventsLL.setTag(position);
        holder.eventShareIV.setTag(position);
        holder.eventBookBT.setTag(position);
        holder.eventFBBT.setTag(position);

        final ArrayList singleSectionItems = eventsData.get(position).getAllItemsInSection();

        if (eventsData.get(position).getBooking_link().equalsIgnoreCase("")) {
            holder.eventBookBT.setVisibility(View.GONE);
        } else {
            holder.eventBookBT.setVisibility(View.VISIBLE);
        }
        if (eventsData.get(position).getFacebook_link().equalsIgnoreCase("")) {
            holder.eventFBBT.setVisibility(View.GONE);
        } else {
            holder.eventFBBT.setVisibility(View.GONE);
        }
        holder.eventHeaderTV.setText(eventsData.get(position).getEvent_name());

        if (!eventsData.get(position).getEvent_enddate().equalsIgnoreCase("")) {

            String text = eventsData.get(position).getEvent_startdate() + " " + eventsData.get(position).getEvent_starttime() + "\n" +
                    eventsData.get(position).getEvent_enddate() + " " + eventsData.get(position).getEvent_endtime();
            holder.eventDateTV.setText(text);
            holder.eventTimeTV.setText(eventsData.get(position).getEvent_endtime());
            holder.eventTimeTV.setVisibility(View.GONE);
        } else {
            holder.eventDateTV.setText(eventsData.get(position).getCreated_on());
            holder.eventTimeTV.setVisibility(View.GONE);
        }
        if (eventsData.get(position).getEvent_description().length() > 200) {
            pageSubstring = eventsData.get(position).getEvent_description().substring(0, 130);
            holder.eventsDescriptionTV.setText(pageSubstring);
            holder.continueTV.setVisibility(View.VISIBLE);
            holder.lessTV.setVisibility(View.GONE);
        } else {
            holder.continueTV.setVisibility(View.GONE);
            holder.lessTV.setVisibility(View.GONE);
            holder.eventsDescriptionTV.setText(eventsData.get(position).getEvent_description());
        }
        if (eventsData.get(position).getFeatured_image() != null && !eventsData.get(position).getFeatured_image().isEmpty()) {
            holder.EventsImagesRV.setVisibility(View.VISIBLE);
            EventsImageAdapter mAdapter = new EventsImageAdapter(mContext, singleSectionItems);
            holder.EventsImagesRV.setAdapter(mAdapter);
        } else {
            holder.EventsImagesRV.setVisibility(View.GONE);
        }

        holder.continueTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.continueTV.setVisibility(View.GONE);
                holder.lessTV.setVisibility(View.VISIBLE);
                holder.eventsDescriptionTV.setText(eventsData.get(position).getEvent_description());
            }
        });

        holder.lessTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.continueTV.setVisibility(View.VISIBLE);
                holder.lessTV.setVisibility(View.GONE);
                holder.eventsDescriptionTV.setText(pageSubstring);
            }
        });

        holder.eventShareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (eventsData.get(position).getFeatured_image() != null
                        && !eventsData.get(position).getFeatured_image().isEmpty()) {
                    mContext.shareWallData(eventsData.get(position).getFeatured_image(),
                            eventsData.get(position).getEvent_description(), eventsData.get(position).getEvent_name());
                } else {
                    mContext.shareTextApp(eventsData.get(position).getEvent_name(),
                            eventsData.get(position).getEvent_description());
                }
            }
        });

        holder.eventBookBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(eventsData.get(position).getBooking_link()));
                mContext.startActivity(intent);
            }
        });

        holder.eventFBBT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(eventsData.get(position).getFacebook_link()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != eventsData ? eventsData.size() : 0);
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView lessTV;
        private final TextView eventHeaderTV;
        private final TextView eventDateTV;
        private final TextView eventTimeTV;
        private final TextView eventsDescriptionTV;
        private final TextView continueTV;
        private final RecyclerView EventsImagesRV;
        private final ImageView eventShareIV;
        private final LinearLayout eventsLL;
        private final Button eventBookBT;
        private final Button eventFBBT;

        MyViewHolder(View itemView) {
            super(itemView);
            eventsLL = (LinearLayout) itemView.findViewById(R.id.eventsLL);
            eventHeaderTV = (TextView) itemView.findViewById(R.id.eventHeaderTV);
            eventDateTV = (TextView) itemView.findViewById(R.id.eventDateTV);
            eventTimeTV = (TextView) itemView.findViewById(R.id.eventTimeTV);
            eventsDescriptionTV = (TextView) itemView.findViewById(R.id.eventsDescriptionTV);
            continueTV = (TextView) itemView.findViewById(R.id.continueTV);
            lessTV = (TextView) itemView.findViewById(R.id.lessTV);
            // mDemoSlider = (SliderLayout) itemView.findViewById(R.id.slider);
            EventsImagesRV = (RecyclerView) itemView.findViewById(R.id.EventsImagesRV);
            eventShareIV = (ImageView) itemView.findViewById(R.id.eventShareIV);
            eventBookBT = (Button) itemView.findViewById(R.id.eventBookBT);
            eventFBBT = (Button) itemView.findViewById(R.id.eventFBBT);
            continueTV.setVisibility(View.VISIBLE);
            lessTV.setVisibility(View.GONE);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
            EventsImagesRV.setLayoutManager(mLayoutManager);
        }
    }

}


