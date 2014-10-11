package com.hackzurich.wishlist.rest;

import com.hackzurich.wishlist.model.Wish;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by cotizo on 10/11/2014.
 */
public interface WishlistBackend {
    @POST("/wish")
    void createWish(@Body Wish wish, Callback<Void> cb);

    @GET("/friends")
    List<Integer> getFriendList();

    @GET("/friends/{id}/list")
    List<Wish> getWishList(@Path("id") int userId);

    @POST("/friends/{id}/list/{item}/{state}")
    void changeWishState(@Path("id") int userId, @Path("item") int wishId, @Path("state") boolean state);
}
