package com.ambuja.clubapp.data;

/**
 * Created by pulkitm on 8/18/2016.
 */
public class ClubActivitySubCategory {
    public String id;
    public String parent_id;
    public String club_id;
    public String image;
    public String facility;
    public String description;
    public String status;


    public ClubActivitySubCategory(String facility, String image, String id) {
        this.facility = facility;
        this.image = image;
        this.id = id;

    }

}
