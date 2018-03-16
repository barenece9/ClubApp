package com.ambuja.clubapp.data;

/**
 * Created by pulkitm on 7/15/2016.
 */

public class Item {

    public static final int ITEM = 0;
    public static final int SECTION = 1;

    public final int type;
    public final String text;
    public final String image;
    public final String clubId;

    public int sectionPosition;
    public int listPosition;


    public Item(int type, String text, String image, String clubId) {
        this.type = type;
        this.text = text;
        this.image = image;
        this.clubId = clubId;

    }

    @Override
    public String toString() {
        return text;
    }
}
