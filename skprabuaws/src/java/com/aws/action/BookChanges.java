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
public class BookChanges implements Serializable {

    private static final long serialVersionUID = 1L;

    private String itemid;
    private String attribute_name;
    private String attribute_value;

    public String getItemid() {
        return itemid;
    }

    public void setItemid(String itemid) {
        this.itemid = itemid;
    }

    public String getAttributeName() {
        return attribute_name;
    }

    public void setAttribute_Name(String attribute_name) {
        this.attribute_name = attribute_name;
    }

    public String getAttributeValue() {
        return attribute_value;
    }

    public void setAttribute_Value(String attribute_value) {
        this.attribute_value = attribute_value;
    }

    @Override
    public String toString() {
        return "Book Changes [itemid=" + itemid + ", Attribute Name=" + attribute_name + ", Attribute Value=" + attribute_value + "]";
    }
}
