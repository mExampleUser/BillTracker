package com.example.billstracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;

public class Welcome extends AppCompatActivity {

    LinearLayout pb;
    SharedPreferences sp;
    ActivityResultLauncher<String> launcher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
            }
    );
    EditText name;
    TextView error, alreadyRegistered;
    Button getStarted;
    boolean darkMode;
    int nightModeFlags;
    TextView intro;
    ImageView filledLogo;
    LinearLayout signupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        name = findViewById(R.id.etWelcomeName);
        sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
        error = findViewById(R.id.nameError);
        getStarted = findViewById(R.id.getStarted);
        pb = findViewById(R.id.pb15);
        alreadyRegistered = findViewById(R.id.alreadyRegistered);
        filledLogo = findViewById(R.id.filledLogo);
        intro = findViewById(R.id.intro);
        signupName = findViewById(R.id.signupName);
        darkMode = false;
        nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            darkMode = true;
        }

        alreadyRegistered.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent login = new Intent(getApplicationContext(), Logon.class);
            login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("registered", true);
            editor.apply();
            startActivity(login);
        });

        name.setOnKeyListener((view, n, keyEvent) -> {
            if (n == KeyEvent.KEYCODE_ENTER) {
                if (name.getText().toString().length() < 3) {
                    error.setVisibility(View.VISIBLE);
                }
                else {
                    InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(name.getWindowToken(), 0);
                    getStarted.performClick();
                    pb.setVisibility(View.VISIBLE);
                }
            }
            return false;
        });

        boolean finalDarkMode = darkMode;
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (name.getText().toString().length() == 0) {
                    error.setVisibility(View.GONE);
                } else if (name.getText().toString().length() > 0 && name.getText().toString().length() < 3) {
                    error.setText(R.string.namemustBe3Characters);
                    error.setVisibility(View.VISIBLE);
                    getStarted.setEnabled(false);
                    getStarted.setBackgroundTintList(ColorStateList.valueOf(getTheme().getResources().getColor(R.color.grey, getTheme())));
                } else {
                    error.setVisibility(View.GONE);
                    getStarted.setEnabled(true);
                    if (finalDarkMode) {
                        getStarted.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blueGrey, getTheme())));
                    }
                    else {
                        getStarted.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.button, getTheme())));
                    }
                    name.setOnKeyListener((view, n, keyEvent) -> {
                        if (n == KeyEvent.KEYCODE_ENTER) {
                            if (name.getText().toString().length() < 3) {
                                error.setVisibility(View.VISIBLE);
                            }
                            else {
                                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                mgr.hideSoftInputFromWindow(name.getWindowToken(), 0);
                                getStarted.performClick();
                                pb.setVisibility(View.VISIBLE);
                            }
                        }
                        return false;
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        getStarted.setOnClickListener(view -> {
            if (name.getText().toString().length() < 3) {
                error.setVisibility(View.VISIBLE);
            }
            else {
                error.setVisibility(View.GONE);
                pb.setVisibility(View.VISIBLE);
                Intent email = new Intent(getApplicationContext(), WelcomeEmail.class);
                email.putExtra("Name", name.getText().toString());
                startActivity(email);
            }
        });

        if (!NotificationManagerCompat.from(getApplicationContext()).areNotificationsEnabled()) {
            requestPermissionLauncher();
        }
        else {
            initialize();
        }
    }

    private void initialize() {

        intro.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
        intro.postDelayed(() -> {
            intro.animate().alpha(0f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
            intro.postDelayed(() -> {
                intro.setText(R.string.aa);
                intro.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                intro.postDelayed(() -> {
                    intro.animate().alpha(0f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                    intro.postDelayed(() -> {
                                    intro.setText(R.string.dd);
                                    intro.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                                    intro.postDelayed(() -> {
                                        filledLogo.animate().translationY(0).setDuration(4000);
                                        intro.animate().translationY(0).setDuration(2000);
                                        intro.postDelayed(() -> {
                                            signupName.animate().translationX(0).setDuration(4000);
                                            name.animate().translationX(0).setDuration(4000);
                                            alreadyRegistered.animate().translationY(0).setDuration(4000);
                                            getStarted.animate().translationY(0).setDuration(4000);
                                            intro.animate().alpha(0f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                                            intro.postDelayed(() -> {
                                                intro.setText(R.string.ee);
                                                intro.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                                                name.setEnabled(true);
                                                alreadyRegistered.setEnabled(true);
                                                getStarted.setEnabled(true);
                                            }, 3500);
                                        }, 2500);
                                    }, 2500);
                    }, 2500);
                }, 2500);
            }, 2500);
        }, 2500);
        //filledLogo.animate().translationY(600).setDuration(5000);
    }

    private void requestPermissionLauncher() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (this.checkSelfPermission(android.Manifest.permission.ACCESS_NOTIFICATION_POLICY) != PackageManager.PERMISSION_GRANTED) {

                    launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("Notification Preference", true);
                    editor.apply();
                    initialize();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.GONE);
    }
}