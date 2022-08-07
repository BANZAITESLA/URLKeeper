package com.disu.urlkeeper.data;

/**
 * 06/08/2022 | 10119239 | DEA INESIA SRI UTAMI | IF6
 */

public class UrlNoteData {
    private String title; // required
    private String url; // required
    private String shorten_url;
    private String secret_note;
    private String visible_note;
    private String last_edited;
    private String categories;


    public UrlNoteData(){} // for firebase

    public UrlNoteData(String title, String url, String shorten_url, String secret_note, String visible_note, String last_edited, String categories) {
        this.title = title;
        this.url = url;
        this.shorten_url = shorten_url;
        this.secret_note = secret_note;
        this.visible_note = visible_note;
        this.last_edited = last_edited;
        this.categories = categories;
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

    public String getShorten_url() {
        return shorten_url;
    }

    public void setShorten_url(String shorten_url) {
        this.shorten_url = shorten_url;
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
}
