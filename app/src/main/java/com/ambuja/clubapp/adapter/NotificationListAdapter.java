package com.ambuja.clubapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.activity.EventDetailsActivity;
import com.ambuja.clubapp.data.NotificationData;
import com.ambuja.clubapp.utils.Constants;

import java.util.ArrayList;


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    private BaseActivity mContext;
    private LayoutInflater inflater;


    public NotificationListAdapter(BaseActivity context, ArrayList<NotificationData> notificationData) {
        this.mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapter_notification_list, parent, false);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final NotificationData data = Constants.notificationDataList.get(position);

        holder.titleTV.setTag(position);
        holder.dayTV.setTag(position);
        holder.dateTV.setTag(position);
        holder.short_descriptionTV.setTag(position);
        holder.notificationLL.setTag(position);


        holder.dayTV.setText(data.day);
        holder.dateTV.setText(mContext.parseDateToddMMyyyy(data.started_on));
        holder.short_descriptionTV.setText(data.short_description);
        holder.titleTV.setText(data.title);


        holder.notificationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent intent = new Intent(mContext, EventDetailsActivity.class);
                mContext.store.setString("eventDetails", data.model_id);
                mContext.startActivity(intent);


            }
        });

    }

    @Override
    public int getItemCount() {

        return Constants.notificationDataList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dayTV, dateTV, short_descriptionTV, titleTV;
        LinearLayout notificationLL;

        MyViewHolder(View itemView) {
            super(itemView);
            notificationLL = (LinearLayout) itemView.findViewById(R.id.notificationLL);
            titleTV = (TextView) itemView.findViewById(R.id.titleTV);
            dayTV = (TextView) itemView.findViewById(R.id.dayTV);
            dateTV = (TextView) itemView.findViewById(R.id.dateTV);
            short_descriptionTV = (TextView) itemView.findViewById(R.id.short_descriptionTV);

        }
    }


}

