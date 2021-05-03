package com.codesses.lgucircle.model;

public class OpinionReply {
    private String r_id, replied_by, reply, date, time;

    public OpinionReply() {
    }

    public OpinionReply(String r_id, String replied_by, String reply, String date, String time) {
        this.r_id = r_id;
        this.replied_by = replied_by;
        this.reply = reply;
        this.date = date;
        this.time = time;
    }

    public String getR_id() {
        return r_id;
    }

    public String getReplied_by() {
        return replied_by;
    }

    public String getReply() {
        return reply;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
