package com.hackzurich.wishlist.rest;

import java.util.List;

import retrofit.http.GET;

/**
 * Created by cotizo on 10/11/2014.
 */
public interface WishlistBackend {
    @GET("/friends")
    List<Integer> getFriendList();
}
