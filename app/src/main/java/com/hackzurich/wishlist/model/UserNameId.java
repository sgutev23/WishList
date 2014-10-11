package com.hackzurich.wishlist.model;

/**
 * Created by cotizo on 10/11/2014.
 */
public class UserNameId {
    private final String id;
    private final String name;

    public UserNameId(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

}
