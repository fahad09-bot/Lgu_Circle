package com.codesses.lgucircle.model;

public class User {
    private String department,
            email, first_name,
            last_name, password,
            phone, profile_img,
            u_id, roll_no, type, bio = "", fcmToken;

    public String getBio() {
        return bio;
    }

    private boolean isPicked = false;

    public boolean isPicked() {
        return isPicked;
    }

    public void setPicked(boolean picked) {
        isPicked = picked;
    }

    public User() {
    }

    public String getType() {
        return type;
    }

    public String getRoll_no() {
        return roll_no;
    }

    public String getU_id() {
        return u_id;
    }

    public String getDepartment() {
        return department;
    }

    public String getEmail() {
        return email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getProfile_img() {
        return profile_img;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public String getFull_name() {
        return getFirst_name() + " " + getLast_name();
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public void setRoll_no(String roll_no) {
        this.roll_no = roll_no;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
