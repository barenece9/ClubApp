package com.ambuja.clubapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.AboutPageActivity;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.data.AboutPageData;
import com.ambuja.clubapp.utils.Constants;

import java.util.ArrayList;


public class AboutAdapter extends RecyclerView.Adapter<AboutAdapter.MyViewHolder> {

    private BaseActivity mContext;
    private LayoutInflater inflater;


    public AboutAdapter(BaseActivity context, ArrayList<AboutPageData> aboutData) {
        this.mContext = context;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.adapter_about, parent, false);

        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final AboutPageData data = Constants.aboutPagesList.get(position);

        holder.aboutListTV.setTag(position);

        holder.aboutListTV.setText(data.page_title);


        holder.aboutListTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(data.page_title.equalsIgnoreCase("Contact Us")){

                    mContext.openContactUsDialog();

                } else {

                    Intent intent = new Intent(mContext,AboutPageActivity.class);

                    mContext.gotoPageDetail(data.id);

                }

               /* int pos = (Integer) view.getTag();
                AboutPageData pageId = Constants.aboutPagesList.get(pos);*/




            }
        });

    }

    @Override
    public int getItemCount() {

        return Constants.aboutPagesList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView aboutListTV;


        MyViewHolder(View itemView) {
            super(itemView);
            aboutListTV = (TextView) itemView.findViewById(R.id.aboutListTV);

        }
    }


}

