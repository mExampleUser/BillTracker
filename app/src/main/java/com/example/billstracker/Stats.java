package com.example.billstracker;

public class Stats {

    private String billerName;
    private double totalPaymentsAmount;
    private int totalPaymentsMade;
    private String dateLastPaid;

    public Stats (String billerName, double totalPaymentsAmount, int totalPaymentsMade, String dateLastPaid) {

        setBillerName(billerName);
        setTotalPaymentsAmount(totalPaymentsAmount);
        setTotalPaymentsMade(totalPaymentsMade);
        setDateLastPaid(dateLastPaid);
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getDateLastPaid() {
        return dateLastPaid;
    }

    public void setDateLastPaid(String dateLastPaid) {
        this.dateLastPaid = dateLastPaid;
    }

    public double getTotalPaymentsAmount() {
        return totalPaymentsAmount;
    }

    public void setTotalPaymentsAmount(double totalPaymentsAmount) {
        this.totalPaymentsAmount = totalPaymentsAmount;
    }

    public int getTotalPaymentsMade() {
        return totalPaymentsMade;
    }

    public void setTotalPaymentsMade(int totalPaymentsMade) {
        this.totalPaymentsMade = totalPaymentsMade;
    }
}
