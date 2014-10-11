package com.hackzurich.wishlist.model;

/**
 * Created by cotizo on 10/11/2014.
 */
public class Wish {
    private final Integer id;
    private final String content;
    private final String bought;

    public Wish(Integer id, String content, String bought) {
        this.id = id;
        this.content = content;
        this.bought = bought;
    }

    public Wish(String content) {
        this(-1, content, null);
    }

    public String getBought() {
        return bought;
    }

    public String getContent() {
        return content;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return String.format("Wish(id: %s, content: '%s', bought: %s)", this.id, this.content, this.bought);
    }
}
