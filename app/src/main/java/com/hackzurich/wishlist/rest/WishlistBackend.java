package com.hackzurich.wishlist.rest;

import com.hackzurich.wishlist.model.Registration;
import com.hackzurich.wishlist.model.UserNameId;
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
    List<UserNameId> getFriendList(@Path("id") String userId );

    @GET("/getFriendWishlist/{id}")
    List<Wish> getWishList(@Path("id") String userId);

    @POST("/buyFriendWish/{myId}/{friendId}/{item}")
    void changeWishState(
            @Path("myId")     String myId,
            @Path("friendId") String friendId,
            @Path("item")     String wishId,
            Callback<String> cb);

    @POST("/register")
    void register(@Body Registration registration, Callback<String> cb);
}
