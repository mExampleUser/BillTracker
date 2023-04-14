package com.example.billstracker;

public class StringFixer {

    public String capitalize (String string) {

        if (string.length() > 2 && string.contains(" ")) {
            String[] split = string.split(" ");
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                sb.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
            }
            string = sb.toString().trim();
        }
        else if (string.length() < 1) {
            string = "Biller";
        }
        else {
            string = string.substring(0,1).toUpperCase() + string.substring(1).toLowerCase();
        }
        return string;
    }
    public String numbersOnly (String string) {
        StringBuilder sb = new StringBuilder();
        if (string.length() > 1) {
            if (!string.contains(".")) {
                string = string + ".00";
            }
            int n = string.indexOf(".");
            if (n == string.length() - 1) {
                string = string + "00";
            }
            String[] split = string.split("\\.");
            if (split[1] != null) {
                if (split[1].length() == 1) {
                    string = string + "0";
                } else if (split[1].length() == 0) {
                    string = string + "00";
                } else if (split[1].length() > 2) {
                    string = split[0] + "." + split[1].substring(0, 2);
                }
            }
            for (int i = 0; i < string.length(); ++i) {
                char c = string.charAt(i);
                if (Character.isDigit(c) || c == '.') {
                    sb.append(c);
                }
            }
            if (sb.length() == 0) {
                sb.append("");
            }
        }
        return sb.toString();
    }
}
