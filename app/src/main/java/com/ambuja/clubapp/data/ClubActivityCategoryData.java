package com.ambuja.clubapp.data;

import java.util.ArrayList;

/**
 * Created by pulkitm on 8/18/2016.
 */
public class ClubActivityCategoryData {
    public String id;
    public String parent_id;
    public String club_id;
    public String image;
    public String facility;
    public String description;
    public String status;
    public String size;
    private ArrayList<ClubActivitySubCategory> clubActyivityData;

   public ArrayList<ClubActivitySubCategory> getClubActyivityData() {
        return clubActyivityData;
    }

    public void setClubActyivityData(ArrayList<ClubActivitySubCategory> clubActyivityData) {
        this.clubActyivityData = clubActyivityData;
    }


}
