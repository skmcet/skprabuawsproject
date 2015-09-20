/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aws.action;

import java.io.Serializable;

/**
 *
 * @author shantha-2230
 */
public class Books implements Serializable {

    private static final long serialVersionUID = 1L;

    private String itemid;
    private String title;
    private String authors;
    private String publisher;
    private String release_date;
    private String list_price;

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getListPrice() {
        return list_price;
    }

    public void setList_price(String list_price) {
        this.list_price = list_price;
    }

    @Override
    public String toString() {
        return "Books [itemid=" + itemid + ", title=" + title + ", authors=" + authors + ",publisher=" + publisher + ", release_date=" + release_date + ", list_price=" + list_price + "]";
    }
}
