package com.hackzurich.wishlist.model;

/**
 * Created by cotizo on 10/11/2014.
 */
public class Registration {
    private final String fbId;
    private final String token;

    public String getFbId() {
        return fbId;
    }

    public String getToken() {
        return token;
    }

    public Registration(String fbId, String token) {
        this.fbId = fbId;
        this.token = token;
    }


}
