package com.hackzurich.wishlist.model;

/**
 * Created by cotizo on 10/11/2014.
 */
public class WishAndId {
    private final Wish wish;
    private final String id;

    public Wish getWish() {
        return wish;
    }

    public String getId() {
        return id;
    }

    public WishAndId(Wish wish, String id) {
        this.wish = wish;
        this.id = id;
    }
}
