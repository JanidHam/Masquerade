package com.digma.masquerade.digma.domain;

/**
 * Created by janidham on 12/01/16.
 */
public class InfoCarnaval {

    private int id, day, month, imageDrawable;
    private String title, description, hourBegin;

    public InfoCarnaval(int id, int day, int month, int imageDrawable, String title, String description, String hourBegin) {
        this.id = id;
        this.day = day;
        this.month = month;
        this.imageDrawable = imageDrawable;
        this.title = title;
        this.description = description;
        this.hourBegin = hourBegin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(int imageDrawable) {
        this.imageDrawable = imageDrawable;
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

    public String getHourBegin() {
        return hourBegin;
    }

    public void setHourBegin(String hourBegin) {
        this.hourBegin = hourBegin;
    }

}
