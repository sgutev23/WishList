package com.hackzurich.wishlist.rest;

import com.hackzurich.wishlist.model.Registration;
import com.hackzurich.wishlist.model.Wish;
import com.hackzurich.wishlist.model.WishAndId;

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
    @POST("/addWish")
    void createWish(@Body WishAndId wish, Callback<String> cb);

    @GET("/getFriends/{id}")
    List<Integer> getFriendList(@Path("id") String userId );

    @GET("/getFriendWishlist/{id}")
    List<Wish> getWishList(@Path("id") String userId);

    @POST("/buyFriendWish/{id}/{item}")
    void changeWishState(@Path("id") String userId, @Path("item") int wishId);

    @POST("/register")
    void register(@Body Registration registration, Callback<String> cb);
}
