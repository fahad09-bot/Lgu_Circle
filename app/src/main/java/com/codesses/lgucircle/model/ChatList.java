package com.codesses.lgucircle.model;

public class ChatList {
    public String c_id;
    public boolean isPicked;

    public ChatList() {
    }

    public ChatList(String id) {
        this.c_id = id;
    }

    public String getId() {
        return c_id;
    }

    public void setId(String id) {
        this.c_id = id;
    }
}
