package com.hackzurich.wishlist.model;

/**
 * Created by cotizo on 10/11/2014.
 */
public class UserInfo {
    private final String id;
    private final String name;
    private final String picture;
    private final String birthday;
    private final String numOfWishes;

    public UserInfo(String id, String name, String birthday, String numOfWishes, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.birthday = birthday;
        this.numOfWishes = numOfWishes;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPictureUrl() {
        return picture;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getNumOfItems() {
        return numOfWishes;
    }
}
