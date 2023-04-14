package com.example.billstracker;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.util.Currency;
import java.util.Locale;

public class MoneyTextWatcher implements TextWatcher {
    EditText edit;
    String formatted;
    boolean start = false;
    int counter = 0;

    public MoneyTextWatcher(EditText editText) {

        edit = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start1, int count, int after) {
        String symbol = Currency.getInstance(Locale.getDefault()).getSymbol();
        if (edit.getText().toString().contains(symbol) && counter == 0) {
            start = edit.getText().toString().startsWith("  " + symbol);
            ++counter;
        }
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String symbol = Currency.getInstance(Locale.getDefault()).getSymbol();
        if (edit == null) return;
        String s = editable.toString();
        if (s.isEmpty()) return;
        boolean period = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '.') {
                if (!period) {
                    period = true;
                    sb.append(s.charAt(i));
                }
            }
            else {
                sb.append(s.charAt(i));
            }
        }
        s = sb.toString();
        edit.removeTextChangedListener(this);
        if (edit.getText().toString().length() < 3) {
            //edit.clearComposingText();
            edit.setText("  " + symbol);
            if (start) {
                edit.setSelection(edit.getText().length());
            }
            else {
                edit.setSelection(edit.getText().toString().indexOf(symbol) - 1);
            }
            edit.addTextChangedListener(this);
        }
        else {
            String cleanString = s.replaceAll("[$]", "").replaceAll(symbol, "").replaceAll(" ", "");
            if (start) {
                formatted = "  " + symbol + cleanString;
            }
            else {
                formatted = "  " + cleanString + " " + symbol;
            }
            edit.setText(formatted);
            if (start) {
                edit.setSelection(formatted.length());
            } else {
                edit.setSelection(formatted.length() - 2);
            }
            edit.addTextChangedListener(this);
        }
    }
}