package com.example.billstracker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

public class CreatePassword extends AppCompatActivity {

    EditText password, pass2;
    Button submit;
    LinearLayout match, error, pb;
    TextView length, matchError, uppercase, lowercase, number, back;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_password);

        password = findViewById(R.id.etWelcomePassword);
        pass2 = findViewById(R.id.etWelcomePassword2);
        submit = findViewById(R.id.btnWelcomePassword);
        error = findViewById(R.id.passError);
        match = findViewById(R.id.match);
        length = findViewById(R.id.length);
        matchError = findViewById(R.id.matchError);
        uppercase = findViewById(R.id.uppercase);
        lowercase = findViewById(R.id.lowercase);
        number = findViewById(R.id.number);
        back = findViewById(R.id.back);
        pb = findViewById(R.id.pb4);
        extras = getIntent().getExtras();

        String name = extras.getString("Name");
        String email = extras.getString("Email");

        back.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            onBackPressed();
        });
        submit.setEnabled(false);

        password.setOnKeyListener((view, n, keyEvent) -> {
            if (n == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
            }
            return false;
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                error.setVisibility(View.VISIBLE);
                password.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                if (password.getText().toString().length() == 0) {
                    error.setVisibility(View.GONE);
                    match.setVisibility(View.GONE);
                    submit.setEnabled(false);
                    return;
                } else {
                    length.setText(R.string.notSixCharacters);
                }

                char ch;
                boolean length1 = false;
                boolean capital = false;
                boolean lowercase1 = false;
                boolean number1 = false;

                for (int n = 0; n < password.getText().toString().length(); n++) {
                    ch = password.getText().toString().charAt(n);
                    if (Character.isUpperCase(ch)) {
                        capital = true;
                    } else if (Character.isLowerCase(ch)) {
                        lowercase1 = true;
                    } else if (Character.isDigit(ch)) {
                        number1 = true;
                    }
                }

                if (password.getText().toString().length() < 6) {
                    length.setText(R.string.notSixCharacters);
                    length.setTextColor(getResources().getColor(R.color.blackAndWhite, getTheme()));
                    submit.setEnabled(false);
                    length.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                } else {
                    length.setText(R.string.isSixCharacters);
                    length1 = true;
                    length.setTextColor(getResources().getColor(R.color.green, getTheme()));
                    length.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmarksmall,0,0,0);
                    TextViewCompat.setCompoundDrawableTintList(length, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                }
                submit.setEnabled(false);
                submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                if (!capital) {
                    uppercase.setText(R.string.noCapitalLetter);
                    uppercase.setTextColor(getTheme().getResources().getColor(R.color.blackAndWhite, getTheme()));
                    submit.setEnabled(false);
                    uppercase.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                } else {
                    uppercase.setText(R.string.isCapitalLetter);
                    uppercase.setTextColor(getResources().getColor(R.color.green, getTheme()));
                    uppercase.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmarksmall,0,0,0);
                    TextViewCompat.setCompoundDrawableTintList(uppercase, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                }
                if (!lowercase1) {
                    lowercase.setText(R.string.noLowercaseLetter);
                    lowercase.setTextColor(getTheme().getResources().getColor(R.color.blackAndWhite, getTheme()));
                    submit.setEnabled(false);
                    lowercase.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                } else {
                    lowercase.setText(R.string.isLowercaseLetter);
                    lowercase.setTextColor(getResources().getColor(R.color.green, getTheme()));
                    lowercase.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmarksmall,0,0,0);
                    TextViewCompat.setCompoundDrawableTintList(lowercase, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                }
                if (!number1) {
                    number.setText(R.string.noNumber);
                    number.setTextColor(getTheme().getResources().getColor(R.color.blackAndWhite, getTheme()));
                    submit.setEnabled(false);
                    number.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                } else {
                    number.setText(R.string.isNumber);
                    number.setTextColor(getResources().getColor(R.color.green, getTheme()));
                    number.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmarksmall,0,0,0);
                    TextViewCompat.setCompoundDrawableTintList(number, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                }
                if (length1 && capital && lowercase1 && number1) {
                    error.setVisibility(View.GONE);
                    match.setVisibility(View.VISIBLE);
                    match.requestFocus();
                }
                else {
                    error.setVisibility(View.VISIBLE);
                    submit.setEnabled(false);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                    pass2.setText("");
                    match.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        pass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pass2.getText().length() == 0) {
                    matchError.setVisibility(View.GONE);
                }
                else {
                    matchError.setVisibility(View.VISIBLE);
                    matchError.setText(R.string.passwordsDontMatch);
                    matchError.setTextColor(getTheme().getResources().getColor(R.color.blackAndWhite, getTheme()));
                    matchError.setCompoundDrawablesWithIntrinsicBounds(0,0,0,0);
                    submit.setEnabled(false);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                }
                if (pass2.getText().toString().equals(password.getText().toString())) {
                    submit.setEnabled(true);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.fingerprint, getTheme())));
                    password.setOnKeyListener((view, n, keyEvent) -> {
                        if (n == KeyEvent.KEYCODE_ENTER) {
                            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
                        }
                        return false;
                    });
                    matchError.setText(R.string.passwordsMatch);
                    matchError.setTextColor(getResources().getColor(R.color.green, getTheme()));
                    matchError.setCompoundDrawablesWithIntrinsicBounds(R.drawable.checkmarksmall,0,0,0);
                    TextViewCompat.setCompoundDrawableTintList(matchError, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                    match.setVisibility(View.GONE);
                    password.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.checkmarksmall, 0);
                    password.setPadding(20,0,20,0);
                    TextViewCompat.setCompoundDrawableTintList(password, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                    submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button, getTheme())));
                    submit.requestFocus();
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(pass2.getWindowToken(), 0);
                    submit.setOnClickListener(view -> {
                        pb.setVisibility(View.VISIBLE);
                        Intent terms = new Intent(CreatePassword.this, TermsAndConditions.class);
                        terms.putExtra("Name", name);
                        terms.putExtra("Email", email);
                        terms.putExtra("Password", password.getText().toString());
                        terms.putExtra("Type", "New User");
                        startActivity(terms);
                    });
                }
                else {
                    submit.setEnabled(false);
                    submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.lightblue, getTheme())));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.GONE);
    }
}