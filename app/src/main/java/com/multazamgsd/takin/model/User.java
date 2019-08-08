package com.multazamgsd.takin.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    private String uid;
    private String auth_type;
    private String email;
    private String first_name;
    private String last_name;
    private String institution;
    private String id_no;
    private String phone_number;
    private String photo;
    private String last_login;
    private String point;
    private String password;

    public User() {}

    public User(String uid, String auth_type, String email, String first_name, String last_name, String institution, String id_no, String phone_number, String photo, String last_login, String point, String password) {
        this.uid = uid;
        this.auth_type = auth_type;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.institution = institution;
        this.id_no = id_no;
        this.phone_number = phone_number;
        this.photo = photo;
        this.last_login = last_login;
        this.point = point;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAuth_type() {
        return auth_type;
    }

    public void setAuth_type(String auth_type) {
        this.auth_type = auth_type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getId_no() {
        return id_no;
    }

    public void setId_no(String id_no) {
        this.id_no = id_no;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLast_login() {
        return last_login;
    }

    public void setLast_login(String last_login) {
        this.last_login = last_login;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
