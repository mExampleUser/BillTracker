package com.example.billstracker;

import java.util.Comparator;

class PaymentsComparator implements Comparator<Payments> {

    public int compare(Payments t1, Payments t2) {

        if (t1.isPaid()) {
            return Boolean.compare(t1.isPaid(), t2.isPaid());
        }

        return t1.getPaymentDate() - t2.getPaymentDate();
    }

}

public class Payments {

    private String paymentAmount;
    private int paymentDate;
    private boolean paid;
    private String billerName;
    private int paymentId;
    private int datePaid;

    public Payments(String paymentAmount, int paymentDate, boolean paid, String billerName, int paymentId, int datePaid) {

        setPaymentAmount(paymentAmount);
        setPaymentDate(paymentDate);
        setPaid(paid);
        setBillerName(billerName);
        setPaymentId(paymentId);
        setDatePaid(datePaid);
    }

    public Payments() {

    }

    public String getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public int getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(int paymentDate) {
        this.paymentDate = paymentDate;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getDatePaid() {
        return datePaid;
    }

    public void setDatePaid(int datePaid) {
        this.datePaid = datePaid;
    }
}
