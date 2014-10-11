package com.hackzurich.wishlist.model;

/**
 * Created by cotizo on 10/11/2014.
 */
public class UserNameIdPic {
    private final String id;
    private final String name;
    private final String picture;

    public UserNameIdPic(String id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }
}
