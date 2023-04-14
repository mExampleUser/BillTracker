package com.example.billstracker;

import java.util.ArrayList;

public class RemoveDuplicatePayments {

    public ArrayList <Payments> removeDuplicatePayments (ArrayList <Payments> payments) {

        ArrayList <Payments> newList = new ArrayList<>();
        if (!payments.isEmpty()) {
            for (Payments payment : payments) {
                if (!newList.contains(payment)) {
                    newList.add(payment);
                }
            }
        }
        return newList;
    }
}
