package com.ambuja.clubapp.data;

import java.util.ArrayList;

/**
 * Created by pulkitm on 8/4/2016.
 */
public class EventsData {

    public String id;
    public String event_name;


    public String event_startdate;
    public String event_starttime;
    public String event_enddate;
    public String event_endtime;
    public String event_description;
    public String club_name;
    public String featured_image;
    public String created_on;
    public int size;
    private ArrayList<SingleItemModel> allItemsInSection;
    public String facebook_link;
    public String booking_link;


    public String getFacebook_link() {
        return facebook_link;
    }

    public void setFacebook_link(String facebook_link) {
        this.facebook_link = facebook_link;
    }

    public String getBooking_link() {
        return booking_link;
    }

    public void setBooking_link(String booking_link) {
        this.booking_link = booking_link;
    }


    public ArrayList<SingleItemModel> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<SingleItemModel> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_startdate() {
        return event_startdate;
    }

    public void setEvent_startdate(String event_startdate) {
        this.event_startdate = event_startdate;
    }

    public String getEvent_starttime() {
        return event_starttime;
    }

    public void setEvent_starttime(String event_starttime) {
        this.event_starttime = event_starttime;
    }

    public String getEvent_enddate() {
        return event_enddate;
    }

    public void setEvent_enddate(String event_enddate) {
        this.event_enddate = event_enddate;
    }

    public String getEvent_endtime() {
        return event_endtime;
    }

    public void setEvent_endtime(String event_endtime) {
        this.event_endtime = event_endtime;
    }

    public String getEvent_description() {
        return event_description;
    }

    public void setEvent_description(String event_description) {
        this.event_description = event_description;
    }

    public String getClub_name() {
        return club_name;
    }

    public void setClub_name(String club_name) {
        this.club_name = club_name;
    }

    public String getFeatured_image() {
        return featured_image;
    }

    public void setFeatured_image(String featured_image) {
        this.featured_image = featured_image;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }


}
