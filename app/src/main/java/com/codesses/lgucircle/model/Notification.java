package com.codesses.lgucircle.model;

import java.util.Date;

public class Notification implements Comparable<Notification> {
    private String sb_id, n_id, post_id, o_id, sent_by, sent_to, title, message;
    private long timestamp;
    private int is_read, type;

    public Notification() {
    }

    public String getTitle() {
        return title;
    }

    public String getSb_id() {
        return sb_id;
    }

    public String getN_id() {
        return n_id;
    }

    public String getSent_by() {
        return sent_by;
    }

    public String getSent_to() {
        return sent_to;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getIs_read() {
        return is_read;
    }

    public int getType() {
        return type;
    }


    @Override
    public int compareTo(Notification o) {
        return new Date(getTimestamp()).compareTo(new Date(o.getTimestamp()));
    }
}
