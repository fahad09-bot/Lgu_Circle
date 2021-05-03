package com.codesses.lgucircle.model;

public class Post {
    private String p_id, posted_by, posted_by_role, status, image, video;
    private long timestamp;
    private int type;

    public Post() {
    }

    public String getP_id() {
        return p_id;
    }

    public int getType() {
        return type;
    }

    public String getPosted_by() {
        return posted_by;
    }

    public String getPosted_by_role() {
        return posted_by_role;
    }

    public String getStatus() {
        return status;
    }

    public String getImage() {
        return image;
    }

    public String getVideo() {
        return video;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
