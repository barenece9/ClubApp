package com.ambuja.clubapp.utils;

import android.net.Uri;

import com.ambuja.clubapp.data.AboutPageData;
import com.ambuja.clubapp.data.ClubActivityCategoryData;
import com.ambuja.clubapp.data.ClubActivitySubCategory;
import com.ambuja.clubapp.data.ClubListData;
import com.ambuja.clubapp.data.EventsData;
import com.ambuja.clubapp.data.NotificationData;

import java.util.ArrayList;
import java.util.HashMap;

public class Constants {


    public static String userId;

    public static final String SENDER_ID = "437766264095";

    public static final String DISPLAY_MESSAGE_ACTION = "com.ambuja.clubapp.DISPLAY_MESSAGE";

    public static final String EXTRA_MESSAGE = "message";

    public static String SERVER_URL_REMOTE = "http://neotiahospitality.com/api/api.php?method=";

    public static String SERVER_URL_REMOTE_NEW = "http://61.16.131.204/phpdemo/ambuja/api/api.php?method=";

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static Uri mUri;

    public static int MAP_ZOOM = 15;

    public static int currentMonth = 1;

    public static int currentMonthIndex = 0;

    public static String name;

    public static String id;

    public static Uri image;

    public static ArrayList<ClubListData> clubList = new ArrayList<ClubListData>();

    public static ArrayList<AboutPageData> aboutPagesList = new ArrayList<AboutPageData>();

    public static ArrayList<NotificationData> notificationDataList = new ArrayList<NotificationData>();

    public static ArrayList<ClubActivityCategoryData> clubActivitiesCategory = new ArrayList<ClubActivityCategoryData>();

    public static ArrayList<EventsData> eventsList = new ArrayList<EventsData>();

    //public static ArrayList<ClubActivitySubCategory> clubActivitySubCategory = new ArrayList<ClubActivitySubCategory>();

    //   public static ArrayList<EventImageData> eventImage = new ArrayList<EventImageData>();

    public static ArrayList<HashMap<String, String>> eventImage = new ArrayList<HashMap<String, String>>();

    public static ArrayList<HashMap<String, String>> LIST = new ArrayList<HashMap<String, String>>();

    public static HashMap<String, String> file_maps = new HashMap<String, String>();
    public static String clubId;

    // public static ArrayList<HashMap<String, String>> pinnedList = new ArrayList<HashMap<String, String>>();

    //public static ArrayList<HashMap<String, String>> header = new ArrayList<HashMap<String, String>>();
    //public static ArrayList<HashMap<String, String>> section = new ArrayList<HashMap<String, String>>();

}