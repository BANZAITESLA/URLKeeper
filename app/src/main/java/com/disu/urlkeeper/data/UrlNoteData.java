package com.disu.urlkeeper.data;

/**
 * 06/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */

public class UrlNoteData {
    private String id; // required|auto
    private String title; // required
    private String url; // required
    private String short_url;
    private String secret_note;
    private String visible_note;
    private String last_edited;
    private String categories;
    private boolean star;


    public UrlNoteData(){} // for firebase

    public UrlNoteData(String id, String title, String url, String short_url, String secret_note, String visible_note, boolean star) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.short_url = short_url;
        this.secret_note = secret_note;
        this.visible_note = visible_note;
        this.star = star;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getShort_url() {
        return short_url;
    }

    public void setShort_url(String short_url) {
        this.short_url = short_url;
    }

    public String getSecret_note() {
        return secret_note;
    }

    public void setSecret_note(String secret_note) {
        this.secret_note = secret_note;
    }

    public String getVisible_note() {
        return visible_note;
    }

    public void setVisible_note(String visible_note) {
        this.visible_note = visible_note;
    }

    public String getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(String last_edited) {
        this.last_edited = last_edited;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public boolean isStar() {
        return star;
    }

    public void setStar(boolean star) {
        this.star = star;
    }
}
