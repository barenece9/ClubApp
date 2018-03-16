package com.ambuja.clubapp.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.activity.BaseActivity;
import com.ambuja.clubapp.data.SingleItemModel;
import com.squareup.picasso.Picasso;
import com.wandrip.imageslider.SliderLayout;
import com.wandrip.imageslider.SliderTypes.DefaultSliderView;
import com.wandrip.imageslider.SliderTypes.TextSliderView;

import java.util.ArrayList;
import java.util.HashMap;


public class EventsImageAdapter extends RecyclerView.Adapter<EventsImageAdapter.MyViewHolder> {

    private BaseActivity mContext;
    private LayoutInflater inflater;
    private ArrayList<SingleItemModel> itemsList;
    private boolean isClicked;
    private HashMap<String, String> file_maps;

    public EventsImageAdapter(BaseActivity context, ArrayList<SingleItemModel> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
        file_maps = new HashMap<String, String>();
        for (int i = 0; i < itemsList.size(); i++) {
            file_maps.put(itemsList.get(i).getName(), itemsList.get(i).getUrl());
        }
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.adapter_events_image, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        holder.btnPlayIB.setTag(position);
        holder.eventFullImageIV.setTag(position);
        holder.card_view.setTag(position);

        SingleItemModel singleItem = itemsList.get(position);

        try {
            Picasso.with(mContext)
                    .load(singleItem.getUrl()).into(holder.eventFullImageIV);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.eventFullImageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPicAFavDialog(mContext);
            }
        });

        holder.btnPlayIB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ImageButton btnPlayIB;
        ImageView eventFullImageIV;
        CardView card_view;

        MyViewHolder(View itemView) {
            super(itemView);
            eventFullImageIV = (ImageView) itemView.findViewById(R.id.eventFullImageIV);
            btnPlayIB = (ImageButton) itemView.findViewById(R.id.btnPlayIB);
            card_view = (CardView) itemView.findViewById(R.id.card_view);
            btnPlayIB.setVisibility(View.GONE);
        }
    }

    protected void showPicAFavDialog(BaseActivity mContext) {

        final Dialog dialog;
        dialog = new Dialog(this.mContext, R.style.NewDialog);
        dialog.setCancelable(true);

        if (!isClicked) {
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        isClicked = true;

        View view = mContext.getLayoutInflater().inflate(R.layout.dialog_image_sliding,null);
        ViewPager pager = (ViewPager) view.findViewById(R.id.vpImages);
        //SliderLayout mDemoSlider = (SliderLayout) dialog.findViewById(R.id.slider);
        ArrayList<String> imageUrls = new ArrayList<>();
        for (String names : file_maps.values()) {
            imageUrls.add(names);
            /*TextSliderView textSliderView = new TextSliderView(mContext);
            // initialize a SliderLayout
            textSliderView
                    .description("")
                    .image(file_maps.get(names))
                    .setScaleType(DefaultSliderView.ScaleType.CenterInside);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", names);


            mDemoSlider.addSlider(textSliderView);*/
        }
        /*mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);*/
        CustomPagerAdapter imageAdapter = new CustomPagerAdapter(mContext, imageUrls);
        pager.setAdapter(imageAdapter);
        dialog.setContentView(view);
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        dialog.getWindow().setFormat(PixelFormat.TRANSLUCENT);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        Drawable d = new ColorDrawable(Color.BLACK);
        d.setAlpha(145);
        dialog.getWindow().setBackgroundDrawable(d);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show();
    }
}

