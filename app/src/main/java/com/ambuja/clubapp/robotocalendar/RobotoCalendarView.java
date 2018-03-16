package com.ambuja.clubapp.robotocalendar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ambuja.clubapp.R;
import com.ambuja.clubapp.robotocalendar.font.RobotoTypefaceManager;
import com.ambuja.clubapp.utils.Constants;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class RobotoCalendarView extends LinearLayout {

    int j = 0;
    // ************************************************************************************************************************************************************************
    // * Attributes
    // ************************************************************************************************************************************************************************
    // View
    private Context context;
    private TextView dateTitle;
    /*private ImageView leftButton;
    private ImageView rightButton;*/
    private View view;
    private int flag = 0;
    // Class
    private RobotoCalendarListener robotoCalendarListener;
    private Calendar currentCalendar;
    private Locale locale;
    ImageView dayOfMonthCircle;

    // Style
    private int monthTitleColor;
    private int monthTitleFont;
    private int dayOfWeekColor;
    private int dayOfWeekFont;
    private int dayOfMonthColor;
    private int dayOfMonthFont;

    public static final int RED_CIRCLE = R.drawable.red_circle;
    public static final int GREEN_CIRCLE = R.drawable.green_circle;
    public static final int BLUE_CIRCLE = R.drawable.blue_circle;
    private Calendar auxCalendar;
    private HorizontalScrollView horizontalView;
    int scrollTo = 0;

    // ************************************************************************************************************************************************************************
    // * Initialization methods
    // ************************************************************************************************************************************************************************

    public RobotoCalendarView(Context context) {
        super(context);
        this.context = context;
        onCreateView();
    }

    public RobotoCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        if (isInEditMode()) {
            return;
        }
        getAttributes(context, attrs);
        onCreateView();
    }

    @SuppressWarnings("ResourceAsColor")
    private void getAttributes(Context context, AttributeSet attrs) {

        try {
            TypedArray typedArray = context.obtainStyledAttributes(attrs,
                    R.styleable.RobotoCalendarView, 0, 0);
            monthTitleColor = typedArray.getColor(
                    R.styleable.RobotoCalendarView_monthTitleColor,
                    getResources().getColor(R.color.monthTitleColor));
            monthTitleFont = typedArray.getInt(
                    R.styleable.RobotoCalendarView_monthTitleFont,
                    R.string.monthTitleFont);
            dayOfWeekColor = typedArray.getColor(
                    R.styleable.RobotoCalendarView_dayOfWeekColor,
                    getResources().getColor(R.color.dayOfWeekColor));
            dayOfWeekFont = typedArray.getInt(
                    R.styleable.RobotoCalendarView_dayOfWeekFont,
                    R.string.dayOfWeekFont);
            dayOfMonthColor = typedArray.getColor(
                    R.styleable.RobotoCalendarView_dayOfMonthColor,
                    getResources().getColor(R.color.dayOfMonthColor));
            dayOfMonthFont = typedArray.getInt(
                    R.styleable.RobotoCalendarView_dayOfMonthFont,
                    R.string.dayOfMonthFont);
            typedArray.recycle();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public View onCreateView() {

        LayoutInflater inflate = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflate.inflate(R.layout.roboto_calendar_picker_layout, this,
                true);

        findViewsById(view);


        Constants.currentMonthIndex = 0;
        Constants.currentMonth = 1;

        updateCalendar();

        // initializeEventListeners();

        initializeComponentBehavior();


        return view;
    }

    private void findViewsById(View view) {
      /*  leftButton = (ImageView) view.findViewById(R.id.leftButton);
        rightButton = (ImageView) view.findViewById(R.id.rightButton);*/
        dateTitle = (TextView) view.findViewWithTag("dateTitle");
        currentCalendar = Calendar.getInstance(Locale.getDefault());

    }

    private void initializeEventListeners() {

        /*leftButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (robotoCalendarListener == null) {
                    throw new IllegalStateException(
                            "You must assing a valid RobotoCalendarListener first!");
                }
                robotoCalendarListener.onLeftButtonClick();
            }
        });

        rightButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (robotoCalendarListener == null) {
                    throw new IllegalStateException(
                            "You must assing a valid RobotoCalendarListener first!");
                }
                robotoCalendarListener.onRightButtonClick();
            }
        });*/
    }

    private void initializeComponentBehavior() {
        // Initialize calendar for current month
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        initializeCalendar(currentCalendar);
    }

    // ************************************************************************************************************************************************************************
    // * Private auxiliary methods
    // ************************************************************************************************************************************************************************

    @SuppressLint("DefaultLocale")
    private void initializeTitleLayout() {
        // Apply styles
        String font = getResources().getString(monthTitleFont);
        Typeface robotoTypeface = RobotoTypefaceManager
                .obtaintTypefaceFromString(context, font);
        int color = getResources().getColor(R.color.calendarBackgroundColor);
        dateTitle.setTypeface(robotoTypeface, Typeface.BOLD);
        dateTitle.setTextColor(color);

        String dateText = new DateFormatSymbols(locale).getMonths()[currentCalendar
                .get(Calendar.MONTH)].toString();
        dateText = dateText.substring(0, 1).toUpperCase()
                + dateText.subSequence(1, dateText.length());
        //dateTitle.setText(dateText + " " + currentCalendar.get(Calendar.YEAR));

        dateTitle.setText(dateText);
    }

    @SuppressLint("DefaultLocale")
    private void initializeWeekDaysLayout() {

        // Apply styles
        String font = getResources().getString(dayOfWeekFont);
        Typeface robotoTypeface = RobotoTypefaceManager
                .obtaintTypefaceFromString(context, font);
        int color = getResources().getColor(R.color.dayOfWeekColor);

        TextView dayOfWeek;
        String dayOfTheWeekString;
        String[] weekDaysArray = new DateFormatSymbols(locale)
                .getShortWeekdays();
        for (int i = 1; i < weekDaysArray.length; i++) {
            dayOfWeek = (TextView) view.findViewWithTag("dayOfWeek"
                    + getWeekIndex(i, currentCalendar));
            dayOfTheWeekString = weekDaysArray[i];

            // Check it for languages with only one week day lenght
            if (dayOfTheWeekString.length() > 1) {
                dayOfTheWeekString = dayOfTheWeekString.substring(0, 1)
                        .toUpperCase() + dayOfTheWeekString.subSequence(1, 2);
            }
            dayOfWeek.setText(dayOfTheWeekString);

            Log.e("day of week text : ", dayOfWeek.getText().toString());

            // Apply styles
            dayOfWeek.setTypeface(robotoTypeface);
            dayOfWeek.setTextColor(color);
        }
    }

    private void initializeDaysOfMonthLayout() {

        // Apply styles
        String font = getResources().getString(dayOfMonthFont);
        Typeface robotoTypeface = RobotoTypefaceManager
                .obtaintTypefaceFromString(context, font);
        int color = getResources().getColor(R.color.dayOfMonthColor);
        TextView dayOfMonthText;
        ImageView dayOfMonthImage;
        ViewGroup dayOfMonthContainer;

        for (int i = 1; i < 32; i++) {

            dayOfMonthContainer = (ViewGroup) view
                    .findViewWithTag("dayOfMonthContainer" + i);
            dayOfMonthText = (TextView) view.findViewWithTag("dayOfMonthText"
                    + i);
            dayOfMonthImage = (ImageView) view
                    .findViewWithTag("dayOfMonthImage" + i);

            dayOfMonthText.setVisibility(View.INVISIBLE);
            dayOfMonthImage.setVisibility(View.INVISIBLE);

            // Apply styles
            dayOfMonthText.setTypeface(robotoTypeface);
            dayOfMonthText.setTextColor(color);
            dayOfMonthText.setBackgroundResource(android.R.color.transparent);

            dayOfMonthContainer
                    .setBackgroundColor(getResources().getColor(R.color.club_app_btn_color));
            dayOfMonthContainer.setOnClickListener(null);
        }
    }

    private void setDaysInCalendar() {
        auxCalendar = Calendar.getInstance(locale);
        int cc = auxCalendar.get(Calendar.DAY_OF_MONTH);
        int cm = auxCalendar.get(Calendar.MONTH);

        int d = auxCalendar.get(Calendar.DAY_OF_MONTH);
        int m = auxCalendar.get(Calendar.MONTH);
        int y = auxCalendar.get(Calendar.YEAR);


        auxCalendar.setTime(currentCalendar.getTime());
        auxCalendar.set(Calendar.DAY_OF_MONTH, 1);
        final int firstDayOfMonth = auxCalendar.get(Calendar.DAY_OF_WEEK);

        //int firstDayOfMonth = 1;
        TextView dayOfMonthText;
        ViewGroup dayOfMonthContainer;


        // Calculate dayOfMonthIndex
        // int dayOfMonthIndex = getWeekIndex(firstDayOfMonth, auxCalendar);

        int dayOfMonthIndex = 1;
        int j = 0;
        for (int i = 1; i <= 43; i++, dayOfMonthIndex++) {


            dayOfMonthContainer = (ViewGroup) view
                    .findViewWithTag("dayOfMonthContainer" + dayOfMonthIndex);
            dayOfMonthText = (TextView) view.findViewWithTag("dayOfMonthText"
                    + dayOfMonthIndex);


            dayOfMonthCircle = (ImageView) view
                    .findViewWithTag("dayOfMonthImage" + dayOfMonthIndex);

            if (dayOfMonthText == null) {
                break;
            }

            scrollTo = dayOfMonthText.getWidth();
            Log.e("dayOfMonthText : ", dayOfMonthText + "");
            Log.e("Scroll To : ", scrollTo + "");
            horizontalView = (HorizontalScrollView) view.findViewById(R.id.hori_scroll_view);
            horizontalView.scrollTo(1000, 0);

            dayOfMonthCircle.setVisibility(View.GONE);
            dayOfMonthContainer.setOnClickListener(onDayOfMonthClickListener);
            dayOfMonthText.setVisibility(View.VISIBLE);
            //dayOfMonthText.setText(String.valueOf(i));

            if (i <= auxCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                dayOfMonthText.setText(String.valueOf(i));


            } else {
                j++;

                dayOfMonthText.setVisibility(View.GONE);

                /*if (j >= 1 && j <= 15) {
                    dayOfMonthText.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Constants.currentMonthIndex++;
                            Constants.currentMonth++;
                            updateCalendar();

                        }
                    });
                }*/

            }


           /* if (Constants.currentMonth == 1) {
                int max_date = auxCalendar
                        .getActualMaximum(Calendar.DAY_OF_MONTH);

               *//* if (cc == i) {
                    dayOfMonthText.setTextColor(Color.BLACK);
                    dayOfMonthText.setTypeface(null, Typeface.BOLD);

                } else if ((cc + 1) == i) {

                    dayOfMonthText.setTextColor(Color.BLACK);
                    dayOfMonthText.setTypeface(null, Typeface.BOLD);


                } else if ((cc + 2) == i) {


                    dayOfMonthText.setTextColor(Color.BLACK);
                    dayOfMonthText.setTypeface(null, Typeface.BOLD);


                } else if ((cc + 3) == i) {


                    dayOfMonthText.setTextColor(Color.BLACK);
                    dayOfMonthText.setTypeface(null, Typeface.BOLD);


                } else if ((cc + 4) == i) {


                    dayOfMonthText.setTextColor(Color.BLACK);
                    dayOfMonthText.setTypeface(null, Typeface.BOLD);


                } else if ((cc + 5) == i) {


                    dayOfMonthText.setTextColor(Color.BLACK);
                    dayOfMonthText.setTypeface(null, Typeface.BOLD);


                    if (i > max_date) {


                        if (flag == 0) {


                            int val = i - max_date;

                            for (int k = 1; k <= val; k++) {


                            }

                            flag++;


                        }

                    }

                } else {
                    dayOfMonthContainer.setOnClickListener(null);
                    dayOfMonthText.setTextColor(getResources().getColor(
                            R.color.back_color));

                }*//*

            } else {
                Log.e("else else **********", "else else*******");
                if (Constants.currentMonth == 2) {
                    Log.e("****<><>i<><>*****", "==" + i);

                    dayOfMonthContainer.setOnClickListener(null);
                    dayOfMonthText.setTextColor(getResources().getColor(
                            R.color.back_color));


                }

            }*/


        }
        // If the last week row has no visible days, hide it or show it in case
        ViewGroup weekRow = (ViewGroup) view.findViewWithTag("weekRow6");
        dayOfMonthText = (TextView) view.findViewWithTag("dayOfMonthText36");
        if (dayOfMonthText.getVisibility() == INVISIBLE) {
            weekRow.setVisibility(GONE);
        } else {
            weekRow.setVisibility(VISIBLE);
        }

    }

    private void clearDayOfMonthContainerBackground() {
        ViewGroup dayOfMonthContainer;
        for (int i = 1; i < 43; i++) {
            dayOfMonthContainer = (ViewGroup) view
                    .findViewWithTag("dayOfMonthContainer" + i);
            dayOfMonthContainer
                    .setBackgroundResource(android.R.color.transparent);
        }
    }

    private ViewGroup getDayOfMonthContainer(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ViewGroup dayOfMonthContainer = (ViewGroup) view
                .findViewWithTag("dayOfMonthContainer"
                        + (currentDay + monthOffset));
        return dayOfMonthContainer;
    }

    private TextView getDayOfMonthText(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        TextView dayOfMonth = (TextView) view.findViewWithTag("dayOfMonthText"
                + (currentDay + monthOffset));
        return dayOfMonth;
    }

    private ImageView getDayOfMonthImage(Calendar currentCalendar) {
        int monthOffset = getMonthOffset(currentCalendar);
        int currentDay = currentCalendar.get(Calendar.DAY_OF_MONTH);
        ImageView dayOfMonth = (ImageView) view
                .findViewWithTag("dayOfMonthImage" + (currentDay + monthOffset));
        return dayOfMonth;
    }

    private int getMonthOffset(Calendar currentCalendar) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentCalendar.getTime());
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        int firstDayWeekPosition = calendar.getFirstDayOfWeek();
        int dayPosition = calendar.get(Calendar.DAY_OF_WEEK);

        if (firstDayWeekPosition == 1) {
            return dayPosition - 1;
        } else {

            if (dayPosition == 1) {
                return 6;
            } else {
                return dayPosition - 2;
            }
        }
    }

    private int getWeekIndex(int weekIndex, Calendar currentCalendar) {
        int firstDayWeekPosition = currentCalendar.getFirstDayOfWeek();

        if (firstDayWeekPosition == 1) {
            return weekIndex;
        } else {

            if (weekIndex == 1) {
                return 7;
            } else {
                return weekIndex - 1;
            }
        }
    }


    private OnClickListener onDayOfMonthClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // Extract day selected
            ViewGroup dayOfMonthContainer = (ViewGroup) view;
            String tagId = (String) dayOfMonthContainer.getTag();
            tagId = tagId.substring(19, tagId.length());
            TextView dayOfMonthText = (TextView) view
                    .findViewWithTag("dayOfMonthText" + tagId);

            // Fire event
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentCalendar.getTime());
                calendar.set(Calendar.DAY_OF_MONTH,
                        Integer.valueOf(dayOfMonthText.getText().toString()));


                if (robotoCalendarListener == null) {
                    throw new IllegalStateException(
                            "You must assing a valid RobotoCalendarListener first!");
                } else {
                    robotoCalendarListener.onDateSelected(calendar.getTime());
                }
            } catch (Exception e) {

                e.printStackTrace();
            }

        }
    };

    public interface RobotoCalendarListener {

        public void onDateSelected(Date date);

        public void onRightButtonClick();

        public void onLeftButtonClick();
    }

    public void setRobotoCalendarListener(
            RobotoCalendarListener robotoCalendarListener) {
        this.robotoCalendarListener = robotoCalendarListener;
    }

    @SuppressLint("DefaultLocale")
    public void initializeCalendar(Calendar currentCalendar) {
        this.currentCalendar = currentCalendar;
        locale = context.getResources().getConfiguration().locale;
        // Set date title
        initializeTitleLayout();
        // Set weeks days titles
        initializeWeekDaysLayout();
        // Initialize days of the month
        initializeDaysOfMonthLayout();
        // Set days in calendar
        setDaysInCalendar();
    }

    public void markDayAsCurrentDay(Date currentDate) {
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTime(currentDate);
        TextView dayOfMonth = getDayOfMonthText(currentCalendar);

        Typeface robotoTypeface = RobotoTypefaceManager
                .obtaintTypefaceFromString(context,
                        getResources()
                                .getString(R.string.currentDayOfMonthFont));
        dayOfMonth.setTypeface(robotoTypeface);
        dayOfMonth.setBackgroundResource(R.drawable.red_wheel);
        //dayOfMonth.setPadding(30, 10, 30, 10);
        dayOfMonth.setTextColor(Color.WHITE);
    }

    public void markDayAsSelectedDay(Date currentDate, int month, int year, String event_type) {
        // Clear previous marks

        //clearDayOfMonthContainerBackground();
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTime(currentDate);
        ViewGroup dayOfMonthContainer = getDayOfMonthContainer(currentCalendar);

        int currentMonth = auxCalendar.get(Calendar.MONTH) + 1;
        int currentYear = auxCalendar.get(Calendar.YEAR);


        Log.i("Current Year : ", " " + currentYear);
        Log.e("Current Month :  ", "" + "" + currentMonth);

        Log.i("PHP Year : ", " " + year);
        Log.e("PHP Month :  ", "" + "" + month);

        if (currentMonth == month && currentYear == year && event_type.equalsIgnoreCase("Public")) {
            dayOfMonthContainer.setBackgroundResource(R.drawable.green_circle);
        } else if (currentMonth == month && currentYear == year && event_type.equalsIgnoreCase("Private")) {
            dayOfMonthContainer.setBackgroundResource(R.drawable.red_circle);
        } else {
            dayOfMonthContainer.setBackground(null);

        }

    }

    public void markDayWithStyle(int style, Date currentDate) {
        Locale locale = context.getResources().getConfiguration().locale;
        Calendar currentCalendar = Calendar.getInstance(locale);
        currentCalendar.setTime(currentDate);
        ImageView dayOfMonthImage = getDayOfMonthImage(currentCalendar);
        // Draw day with style
        dayOfMonthImage.setVisibility(View.VISIBLE);
        dayOfMonthImage.setImageDrawable(null);
        dayOfMonthImage.setBackgroundResource(style);
    }

    public void updateCalendar() {
        System.out.println("insde the updatecalendar");
        currentCalendar = Calendar.getInstance(Locale.getDefault());
        currentCalendar.add(Calendar.MONTH, Constants.currentMonthIndex);
        initializeCalendar(currentCalendar);
        if (Constants.currentMonthIndex == 0) {
            markDayAsCurrentDay(currentCalendar.getTime());
        }
    }
}
