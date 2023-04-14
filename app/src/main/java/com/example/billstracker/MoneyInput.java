package com.example.billstracker;

import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.widget.EditText;

import java.util.Currency;
import java.util.Locale;

public class MoneyInput implements TextWatcher {
    //private final WeakReference<EditText> editTextWeakReference;
    EditText edit;
    String formatted;
    boolean start = false;
    int counter = 0;

    public MoneyInput(EditText editText) {

        edit = editText;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start1, int count, int after) {

        String symbol = Currency.getInstance(Locale.getDefault()).getSymbol();
        if (edit.getText().toString().contains(symbol) && counter == 0) {
            start = edit.getText().toString().startsWith(symbol);
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
        edit.setKeyListener(DigitsKeyListener.getInstance("01234567890,."));
        edit.removeTextChangedListener(this);
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
        String replaceAll = s.replaceAll("[$]", "").replaceAll(symbol, "").replaceAll(" ", "");
        if (!edit.getText().toString().contains(symbol)) {
            if (edit.getText().toString().length() < 2) {
                if (start) {
                    edit.setText(symbol);
                    edit.setSelection(edit.getText().toString().indexOf(symbol));
                }
                else {
                    edit.setText(" " + symbol);
                    edit.setSelection(edit.getText().toString().indexOf(symbol) - 1);
                }
            }
            else {
                if (start) {
                    formatted = symbol + replaceAll;
                }
                else {
                    formatted = replaceAll + " " + symbol;
                }
                edit.setText(formatted);
                if (start) {
                    edit.setSelection(formatted.length());
                } else {
                    if (formatted.length() >= 2) {
                        edit.setText(formatted);
                        edit.setSelection(edit.getText().toString().indexOf(symbol) - 1);
                    }
                    else {
                        edit.setSelection(formatted.indexOf(0));
                    }
                }
            }
            edit.addTextChangedListener(this);
        }
        else {
            if (start) {
                formatted = symbol + replaceAll;
            }
            else {
                formatted = replaceAll + " " + symbol;
            }
            edit.setText(formatted);
            if (start) {
                edit.setSelection(formatted.length());
            } else {
                if (formatted.length() >= 2) {
                    edit.setText(formatted);
                    edit.setSelection(edit.getText().toString().indexOf(symbol) - 1);
                }
                else {
                    edit.setSelection(formatted.indexOf(0));
                }
            }
            edit.addTextChangedListener(this);
        }
    }
}