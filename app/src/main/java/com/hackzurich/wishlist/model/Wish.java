package com.hackzurich.wishlist.model;

/**
 * Created by cotizo on 10/11/2014.
 */
public class Wish {
    private final Integer id;
    private final String content;
    private final Boolean state;

    public Wish(Integer id, String content, Boolean state) {
        this.id = id;
        this.content = content;
        this.state = state;
    }

    public Boolean getState() {
        return state;
    }

    public String getContent() {
        return content;
    }

    public Integer getId() {
        return id;
    }
}
