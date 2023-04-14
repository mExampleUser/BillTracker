package com.example.billstracker;

public class Biller {

    private String billerName;
    private String website;
    private String icon;
    private String type;

    public Biller(String billerName, String website, String icon, String type) {

        setBillerName(billerName);
        setWebsite(website);
        setIcon(icon);
        setType(type);
    }

    public Biller() {

    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}