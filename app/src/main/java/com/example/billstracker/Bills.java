package com.example.billstracker;

import java.util.ArrayList;

public class Bills {

    private String billerName;
    private String website;
    private String amountDue;
    private int dayDue;
    private int dateLastPaid;
    private String billsId;
    private String frequency;
    private boolean recurring;
    private ArrayList<Payments> payments;
    private String category;
    private String icon;

    public Bills(String billerName, String amountDue, int dayDue, int dateLastPaid, String billsId, boolean recurring, String frequency, String website,
                 ArrayList<Payments> payments, String category, String icon) {

        setBillerName(billerName);
        setWebsite(website);
        setAmountDue(amountDue);
        setDayDue(dayDue);
        setDateLastPaid(dateLastPaid);
        setBillsId(billsId);
        setFrequency(frequency);
        setRecurring(recurring);
        setPayments(payments);
        setCategory(category);
        setIcon(icon);
    }

    public Bills() {

    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(String amountDue) {
        this.amountDue = amountDue;
    }

    public int getDayDue() {
        return dayDue;
    }

    public void setDayDue(int dayDue) {
        this.dayDue = dayDue;
    }

    public int getDateLastPaid() {
        return dateLastPaid;
    }

    public void setDateLastPaid(int dateLastPaid) {
        this.dateLastPaid = dateLastPaid;
    }

    public String getBillsId() {
        return billsId;
    }

    public void setBillsId(String billsId) {
        this.billsId = billsId;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public void setRecurring(boolean recurring) {
        this.recurring = recurring;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public ArrayList<Payments> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payments> payments) {
        this.payments = payments;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
