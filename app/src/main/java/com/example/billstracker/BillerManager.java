package com.example.billstracker;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BillerManager {

    DateFormatter df = new DateFormatter();

    public ArrayList <Payments> generatePayments (ArrayList <Payments> pay, int dueDateValue, String frequency, String type, String amountDue) {

        ArrayList <Payments> newPayments = new ArrayList<>();
        int dueDateValue1 = dueDateValue;
        int pays = 1;
        switch (frequency) {
            case "0":
                pays = 60;
                break;
            case "1":
            case "2":
            case "3":
                pays = 24;
                break;
            case "4":
                pays = 12;
                break;
            case "5":
                pays = 4;
                break;
        }

        ArrayList <Integer> exclude = new ArrayList<>();
        pay.sort(Comparator.comparing(Payments::getPaymentDate));
        for (Payments payment : pay) {
            if (payment.isPaid() || payment.getPaymentDate() == dueDateValue && !newPayments.contains(payment)) {
                newPayments.add(payment);
                exclude.add(payment.getPaymentDate());
            }
            if (type.equals("refresh")) {
                if (!exclude.contains(payment.getPaymentDate())) {
                    if (!newPayments.contains(payment)) {
                        newPayments.add(payment);
                    }
                    exclude.add(payment.getPaymentDate());
                }
            }
        }

        Payments clone = pay.get(0);
        String billerName = clone.getBillerName();
        Payments firstPayment;

        if (!exclude.contains(dueDateValue1)) {
            firstPayment = new Payments(amountDue, dueDateValue1, false, billerName, Integer.parseInt(id()), 0);
            if (!newPayments.contains(firstPayment)) {
                newPayments.add(firstPayment);
                exclude.add(dueDateValue1);
            }
        }

        for (int i = 0; i < pays; ++i) {
            switch (frequency) {
                case "0": {
                    dueDateValue1 = df.increaseIntDateByOneDay(dueDateValue1);
                    Payments additionalPayment = new Payments(amountDue, dueDateValue1, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDateValue1) && !newPayments.contains(additionalPayment)) {
                        newPayments.add(additionalPayment);
                        exclude.add(dueDateValue1);
                    }
                    break;
                }
                case "1": {
                    dueDateValue1 = df.increaseIntDateByOneWeek(dueDateValue1);
                    Payments additionalPayment = new Payments(amountDue, dueDateValue1, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDateValue1) && !newPayments.contains(additionalPayment)) {
                        newPayments.add(additionalPayment);
                        exclude.add(dueDateValue1);
                    }
                    break;
                }
                case "2": {
                    dueDateValue1 = df.increaseIntDateByOneWeek(dueDateValue1);
                    dueDateValue1 = df.increaseIntDateByOneWeek(dueDateValue1);
                    Payments additionalPayment = new Payments(amountDue, dueDateValue1, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDateValue1) && !newPayments.contains(additionalPayment)) {
                        newPayments.add(additionalPayment);
                        exclude.add(dueDateValue1);
                    }
                    break;
                }
                case "3": {
                    dueDateValue1 = df.increaseIntDateByOneMonth(dueDateValue1);
                    Payments additionalPayment = new Payments(amountDue, dueDateValue1, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDateValue1) && !newPayments.contains(additionalPayment)) {
                        newPayments.add(additionalPayment);
                        exclude.add(dueDateValue1);
                    }
                    break;
                }
                case "4": {
                    dueDateValue1 = df.increaseIntDateByThreeMonths(dueDateValue1);
                    Payments additionalPayment = new Payments(amountDue, dueDateValue1, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDateValue1) && !newPayments.contains(additionalPayment)) {
                        newPayments.add(additionalPayment);
                        exclude.add(dueDateValue1);
                    }
                    break;
                }
                case "5": {
                    dueDateValue1 = df.increaseIntDateByOneYear(dueDateValue1);
                    Payments additionalPayment = new Payments(amountDue, dueDateValue1, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDateValue1) && !newPayments.contains(additionalPayment)) {
                        newPayments.add(additionalPayment);
                        exclude.add(dueDateValue1);
                    }
                    break;
                }
            }
        }
        RemoveDuplicatePayments rdp = new RemoveDuplicatePayments();
        rdp.removeDuplicatePayments(newPayments);
        return newPayments;
    }

    public ArrayList <Payments> refreshPayments (ArrayList <Payments> list, int dueDate, String frequency) {

        int pays = 1;
        switch (frequency) {
            case "0":
                pays = 60;
                break;
            case "1":
            case "2":
            case "3":
                pays = 24;
                break;
            case "4":
                pays = 12;
                break;
            case "5":
                pays = 4;
                break;
        }

        int todayDate = df.currentDateAsInt();
        ArrayList <Integer> exclude = new ArrayList<>();
        list.sort(Comparator.comparing(Payments::getPaymentDate));
        for (Payments payment : list) {
            if (!exclude.contains(payment.getPaymentDate())) {
                exclude.add(payment.getPaymentDate());
                if (payment.getPaymentDate() > todayDate) {
                    --pays;
                }
                if (payment.getPaymentDate() > dueDate) {
                    dueDate = payment.getPaymentDate();
                }
            }
        }
        int max = Collections.max(exclude);
        int element = 0;
        for (Payments payment: list) {
            if (payment.getPaymentDate() == max) {
                element = list.indexOf(payment);
            }
        }

        Payments clone = list.get(element);
        String billerName = clone.getBillerName();
        String paymentAmount = clone.getPaymentAmount();

        for (int i = 0; i < pays; ++i) {
            switch (frequency) {
                case "0": {
                    dueDate = df.increaseIntDateByOneDay(dueDate);
                    Payments additionalPayment = new Payments(paymentAmount, dueDate, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDate)) {
                        list.add(additionalPayment);
                        exclude.add(dueDate);
                    }
                    break;
                }
                case "1": {
                    dueDate = df.increaseIntDateByOneWeek(dueDate);
                    Payments additionalPayment = new Payments(paymentAmount, dueDate, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDate)) {
                        list.add(additionalPayment);
                        exclude.add(dueDate);
                    }
                    break;
                }
                case "2": {
                    dueDate = df.increaseIntDateByOneWeek(dueDate);
                    dueDate = df.increaseIntDateByOneWeek(dueDate);
                    Payments additionalPayment = new Payments(paymentAmount, dueDate, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDate)) {
                        list.add(additionalPayment);
                        exclude.add(dueDate);
                    }
                    break;
                }
                case "3": {
                    dueDate = df.increaseIntDateByOneMonth(dueDate);
                    Payments additionalPayment = new Payments(paymentAmount, dueDate, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDate)) {
                        list.add(additionalPayment);
                        exclude.add(dueDate);
                    }
                    break;
                }
                case "4": {
                    dueDate = df.increaseIntDateByThreeMonths(dueDate);
                    Payments additionalPayment = new Payments(paymentAmount, dueDate, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDate)) {
                        list.add(additionalPayment);
                        exclude.add(dueDate);
                    }
                    break;
                }
                case "5": {
                    dueDate = df.increaseIntDateByOneYear(dueDate);
                    Payments additionalPayment = new Payments(paymentAmount, dueDate, false, billerName, Integer.parseInt(id()), 0);
                    if (!exclude.contains(dueDate)) {
                        list.add(additionalPayment);
                        exclude.add(dueDate);
                    }
                    break;
                }
            }
        }
        return list;
    }

    String id() {
        final String AB = "0123456789";
        SecureRandom rnd = new SecureRandom();
        int length = 9;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
