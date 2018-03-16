/*
 * Copyright (C) 2014 Kevin Bernage
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ambuja.clubapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.text.format.DateFormat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;

import java.util.ArrayList;
import java.util.Date;

public class KBCalendarAdapter extends BaseAdapter {
    private final Context mContext;
    private ArrayList<Date> mListDays;
    private int widthScreen;
    private int widthCell;
    private KBCalendar kbCalendar;
    private ArrayList<String> eventsDate = new ArrayList<>();

    public KBCalendarAdapter(KBCalendar kbCalendar, Context context, ArrayList<Date> listDays, int numberOfRow) {
        mContext = context;
        mListDays = listDays;
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        widthCell = widthScreen / numberOfRow;
        this.kbCalendar = kbCalendar;
    }

    @Override
    public int getCount() {
        return mListDays.size();
    }

    @Override
    public Integer getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setEventsDate(ArrayList<String> eventsDate) {
        this.eventsDate = eventsDate;
    }

    public static class ViewHolderDay {
        public TextView txtDayNumber;
        public TextView txtDayName;
        public RelativeLayout layoutBackground;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderDay holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.calendar_list_item, parent, false);
            convertView.setLayoutParams(new TwoWayView.LayoutParams(widthCell, convertView.getLayoutParams().height));
            holder = new ViewHolderDay();
            holder.txtDayNumber = (TextView) convertView.findViewById(R.id.dayNumber);
            holder.txtDayName = (TextView) convertView.findViewById(R.id.dayName);
            holder.layoutBackground = (RelativeLayout) convertView.findViewById(R.id.layoutBackground);

            holder.txtDayNumber.setTextColor(mContext.getResources().getColorStateList(R.color.text_selector));
            holder.layoutBackground.setBackgroundResource(R.drawable.date_selector);

            holder.txtDayName.setVisibility(View.GONE);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolderDay) convertView.getTag();
        }
        Date day = mListDays.get(position);

        if (day == kbCalendar.getCurrentDate()) {

            holder.layoutBackground.setBackgroundResource(R.drawable.date_selector);
           /* holder.txtDayNumber.setBackgroundResource(R.drawable.circle_new);
            holder.txtDayName.setBackgroundResource(R.drawable.red_circle);*/

        } /*else {

            holder.txtDayNumber.setBackgroundResource(R.drawable.transparent_wheel);
            holder.txtDayName.setBackgroundResource(R.drawable.transparent_wheel);

        }*/

        String selectedDate = DateFormat.format("yyyy-MM-dd", day).toString();
        if (eventsDate != null) {
            for (int a = 0; a < eventsDate.size(); a++) {
                if (selectedDate.equalsIgnoreCase(eventsDate.get(a))) {
                    holder.txtDayNumber.setBackgroundResource(R.drawable.circle_icon);
                    holder.txtDayName.setBackgroundResource(R.drawable.red_circle);
                    break;
                } else if (day == kbCalendar.getCurrentDate()) {

                    holder.layoutBackground.setBackgroundColor(Color.parseColor("#ff9518"));
                    holder.txtDayNumber.setTextColor(Color.parseColor("#ffffff"));

                } else {
                    holder.txtDayNumber.setBackgroundResource(R.drawable.transparent_wheel);
                    holder.txtDayName.setBackgroundResource(R.drawable.transparent_wheel);
                    holder.layoutBackground.setBackgroundColor(Color.parseColor("#ffffff"));
                    holder.txtDayNumber.setTextColor(Color.parseColor("#000000"));
                }
            }
        }

       /* if (day.before(kbCalendar.getDateStartCalendar()) || day.after(kbCalendar.getDateEndCalendar())) {

            holder.layoutBackground.setBackgroundColor(Color.WHITE);

            holder.txtDayName.setTextSize(kbCalendar.getDaySize() - 3);
            holder.txtDayNumber.setTextSize(kbCalendar.getDayNumberSize() - 3);
            holder.txtDayName.setTextColor(Color.LTGRAY);
            holder.txtDayNumber.setTextColor(Color.LTGRAY);
        } else {

            setBackgroundColorToView(holder.layoutBackground);

            holder.txtDayName.setTextSize(kbCalendar.getDaySize());
            holder.txtDayNumber.setTextSize(kbCalendar.getDayNumberSize());
            holder.txtDayName.setTextColor(Color.parseColor(kbCalendar.getHexColorDay()));
            holder.txtDayNumber.setTextColor(Color.parseColor(kbCalendar.getHexColorDayNumber()));
        }*/
        holder.txtDayNumber.setText(DateFormat.format(kbCalendar.getFormatDayNumber(), day).toString());
        holder.txtDayName.setText(DateFormat.format(kbCalendar.getFormatDay(), day).toString());

        return convertView;
    }

    private boolean isItemCenterOfView(int position) {
        if (position == KBCalendar.positionOfCenterItem) return true;
        else return false;
    }

    private void setBackgroundColorToView(View v) {
        v.setBackgroundColor(Color.parseColor(kbCalendar.getHexBackgroundColor()));
        double temp;
        int middleView = getX(v) + (v.getMeasuredWidth() / 2);
        if (middleView > widthScreen / 2) {
            temp = Math.sqrt((middleView - (widthScreen / 2)));
        } else {
            temp = Math.sqrt(((widthScreen / 2) - middleView));
        }
        int alpha = Math.round((long) temp * 255 / (long) Math.sqrt(widthScreen / 2));
        if (alpha > 255)
            v.getBackground().setAlpha(5);
        else
            v.getBackground().setAlpha(255 - alpha);
    }

    private int getX(View v) {
        int[] loc = new int[2];
        v.getLocationOnScreen(loc);
        return loc[0];
    }
}
