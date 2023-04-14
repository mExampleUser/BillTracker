package com.example.billstracker;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.TextViewCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Locale;
import java.util.Objects;

public class WelcomeEmail extends AppCompatActivity {

    LinearLayout pb;
    ScrollView scrollEmail;
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_email);
        boolean darkMode = false;
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            darkMode = true;
        }

        TextView userName = findViewById(R.id.welcomeName);
        TextView error = findViewById(R.id.emailError);
        EditText email = findViewById(R.id.etWelcomeEmail);
        Button submit = findViewById(R.id.btnWelcomeEmail);
        TextView back = findViewById(R.id.goBack);
        lottie = findViewById(R.id.animationView6);
        scrollEmail = findViewById(R.id.emailScroll);
        pb = findViewById(R.id.pb16);

        if (scrollEmail.getVisibility() == View.VISIBLE) {
            lottie.setVisibility(View.GONE);
        }
        else {
            lottie.postDelayed(() -> {
                lottie.setVisibility(View.GONE);
                scrollEmail.setVisibility(View.VISIBLE);
            }, 3000);
        }

        email.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(email, InputMethodManager.SHOW_IMPLICIT);

        submit.setEnabled(false);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("Name");
        String nickName = name;
        if (name.contains(" ")) {
            int index = name.indexOf(' ');
            if (index > -1) {
                nickName = name.substring(0, index).trim();
            }
        }

        userName.setText(String.format(Locale.getDefault(), "%s %s!", getString(R.string.hey), nickName));

        back.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            onBackPressed();
        });

        email.setOnKeyListener((view, n, keyEvent) -> {
            if (n == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(email.getWindowToken(), 0);
            }
            return false;
        });

        boolean finalDarkMode = darkMode;
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
                submit.setTextColor(getResources().getColor(R.color.white, getTheme()));
                submit.setEnabled(false);
                email.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (email.getText().toString().length() == 0) {
                    error.setVisibility(View.INVISIBLE);
                }
                else if (email.getText().toString().length() < 6) {
                    error.setVisibility(View.VISIBLE);
                    error.setText(R.string.emailTooShort);
                }
                else if (!email.getText().toString().contains("@")
                        || !email.getText().toString().endsWith(".com") && !email.getText().toString().endsWith(".net") &&
                        !email.getText().toString().endsWith(".edu") && !email.getText().toString().endsWith(".gov")) {
                    error.setText(R.string.enterValidEmail);
                    error.setVisibility(View.VISIBLE);
                }
                else {
                    mAuth.fetchSignInMethodsForEmail(email.getText().toString()).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            boolean check = !Objects.requireNonNull(task.getResult().getSignInMethods()).isEmpty();
                            error.setVisibility(View.VISIBLE);
                            if (!check) {
                                error.setText(R.string.usernameAvailable);
                                email.setCompoundDrawablesWithIntrinsicBounds(0, 0,R.drawable.checkmarksmall,0);
                                TextViewCompat.setCompoundDrawableTintList(error, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                                submit.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.button, getTheme())));
                                submit.setEnabled(true);
                                if (finalDarkMode) {
                                    submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blueGrey, getTheme())));
                                }
                                else {
                                    submit.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button, getTheme())));
                                }

                                submit.setOnClickListener(view -> {
                                    pb.setVisibility(View.VISIBLE);
                                    Intent password = new Intent(getApplicationContext(), CreatePassword.class);
                                    password.putExtra("Name", name);
                                    password.putExtra("Email", email.getText().toString());
                                    startActivity(password);
                                });
                                email.setOnKeyListener((view, n, keyEvent) -> {
                                    if (n == KeyEvent.KEYCODE_ENTER) {
                                        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        mgr.hideSoftInputFromWindow(email.getWindowToken(), 0);
                                    }
                                    return false;
                                });
                            }
                            else {
                                error.setText(R.string.usernameUnavailable);
                            }
                        }
                    });
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