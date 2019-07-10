package com.multazamgsd.takin.model;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Event {
    private String id;
    private String title;
    private String description;
    private String publisher;
    private String date;
    private String time_start;
    private String time_end;
    private String location_name;
    private String location_address;
    private String location_lat;
    private String location_long;
    private String type;
    private String point;
    private String price;
    private String ticket_total;
    private String ticket_sold;
    private String photo_url;

    public Event() {}

    public Event(String id, String title, String description, String publisher, String date, String time_start, String time_end, String location_name, String location_address, String location_lat, String location_long, String type, String point, String price, String ticket_total, String ticket_sold, String photo_url) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.date = date;
        this.time_start = time_start;
        this.time_end = time_end;
        this.location_name = location_name;
        this.location_address = location_address;
        this.location_lat = location_lat;
        this.location_long = location_long;
        this.type = type;
        this.point = point;
        this.price = price;
        this.ticket_total = ticket_total;
        this.ticket_sold = ticket_sold;
        this.photo_url = photo_url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime_start() {
        return time_start;
    }

    public void setTime_start(String time_start) {
        this.time_start = time_start;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getLocation_name() {
        return location_name;
    }

    public void setLocation_name(String location_name) {
        this.location_name = location_name;
    }

    public String getLocation_address() {
        return location_address;
    }

    public void setLocation_address(String location_address) {
        this.location_address = location_address;
    }

    public String getLocation_lat() {
        return location_lat;
    }

    public void setLocation_lat(String location_lat) {
        this.location_lat = location_lat;
    }

    public String getLocation_long() {
        return location_long;
    }

    public void setLocation_long(String location_long) {
        this.location_long = location_long;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPoint() {
        return point;
    }

    public void setPoint(String point) {
        this.point = point;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTicket_total() {
        return ticket_total;
    }

    public void setTicket_total(String ticket_total) {
        this.ticket_total = ticket_total;
    }

    public String getTicket_sold() {
        return ticket_sold;
    }

    public void setTicket_sold(String ticket_sold) {
        this.ticket_sold = ticket_sold;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }
}
