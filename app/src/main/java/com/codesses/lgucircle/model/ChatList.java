package com.codesses.lgucircle.model;

public class ChatList {
    public String id;
    public boolean isPicked;

    public ChatList() {
    }

    public ChatList(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
