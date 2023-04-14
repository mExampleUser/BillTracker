package com.example.billstracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class SetBiometrics extends AppCompatActivity {

    AlertDialog dialog;
    LinearLayout pb;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_biometrics);
        mContext = this;
        TextView biometricError = findViewById(R.id.biometricError);
        Button enable = findViewById(R.id.btnEnable);
        Button notNow = findViewById(R.id.btnNotNow);
        pb = findViewById(R.id.pb12);
        TextView back = findViewById(R.id.tvGoBack);

        Bundle extras = getIntent().getExtras();
        String name = extras.getString("Name");
        String email = extras.getString("Email").toLowerCase();
        String password = extras.getString("Password");
        String acceptedOn = extras.getString("Terms Accepted On");

        BiometricManager biometricManager = BiometricManager.from(getApplicationContext());
        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            biometricError.setVisibility(View.VISIBLE);
            enable.setVisibility(View.GONE);
            notNow.setText(R.string.continueNow);
        }

        enable.setOnClickListener(view -> {

            pb.setVisibility(View.VISIBLE);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String lastLogin = "01-01-2022";

            LocalDateTime loginTime = LocalDateTime.now();
            DateTimeFormatter formattedLoginTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");

            String dateRegistered = loginTime.format(formattedLoginTime);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getString(R.string.emailAlreadyRegistered),
                            Toast.LENGTH_SHORT).show();
                } else {
                    String userId = id();
                    ArrayList<Bills> bills = new ArrayList<>();
                    ArrayList <Trophy> trophies = new ArrayList<>();
                    Login user1 = new Login(email.toLowerCase(), password, name, false, lastLogin, dateRegistered, userId, bills, 0, "0", 0, "3", true, acceptedOn, "$", trophies);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(email.toLowerCase()).set(user1);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    assert user != null;
                    SharedPreferences sp = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("biometricPreference", true);
                    editor.putBoolean("allowBiometricPrompt", true);
                    editor.putBoolean("registered", true);
                    editor.putString("email", email);
                    editor.putString("password", password);
                    editor.apply();
                    user.updateProfile(update);
                    Logon.userList.add(user1);
                    sendVerificationEmail();
                }
            });
        });

        notNow.setOnClickListener(view -> {

            pb.setVisibility(View.VISIBLE);
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            String lastLogin = "01-01-2022";

            LocalDateTime loginTime = LocalDateTime.now();
            DateTimeFormatter formattedLoginTime = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");

            String dateRegistered = loginTime.format(formattedLoginTime);
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                Log.d("TAG", "createUserWithEmail:onComplete:" + task.isSuccessful());
                if (!task.isSuccessful()) {
                    pb.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), getString(R.string.emailAlreadyRegistered),
                            Toast.LENGTH_SHORT).show();
                } else {
                    String userId = id();
                    ArrayList<Bills> bills = new ArrayList<>();
                    ArrayList <Trophy> trophies = new ArrayList<>();
                    Login user1 = new Login(email.toLowerCase(), password, name, false, lastLogin, dateRegistered, userId, bills, 0, "0", 0, "3", true, acceptedOn, "$", trophies);
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(email.toLowerCase()).set(user1);
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    UserProfileChangeRequest update = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    assert user != null;
                    SharedPreferences sp = getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("biometricPreference", false);
                    editor.putBoolean("allowBiometricPrompt", false);
                    editor.putBoolean("registered", true);
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.apply();
                    user.updateProfile(update);
                    Logon.userList.add(user1);
                    sendVerificationEmail();
                }
            });
        });

        back.setOnClickListener(view -> onBackPressed());
    }

    String id() {
        final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();
        int length = 20;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }

    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        pb.setVisibility(View.GONE);
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                        builder.setMessage(getString(R.string.accountCreatedSuccessfullyMessage)).setTitle(getString(R.string.accountCreatedSuccessfully))
                                .setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                                    pb.setVisibility(View.VISIBLE);

                                    SharedPreferences sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sp.edit();
                                    editor.putBoolean("First Logon", true);
                                    editor.apply();
                                    FirebaseAuth.getInstance().signOut();
                                    Intent logon = new Intent(mContext, Logon.class);
                                    startActivity(logon);
                                });
                        builder.setNegativeButton(getString(R.string.openEmail), (dialogInterface, i) -> {
                            pb.setVisibility(View.VISIBLE);
                            FirebaseAuth.getInstance().signOut();
                            finish();
                            SharedPreferences sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putBoolean("First Logon", true);
                            editor.apply();
                            Intent email = new Intent(Intent.ACTION_MAIN);
                            email.addCategory(Intent.CATEGORY_APP_EMAIL);
                            email.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(email);
                        });
                        dialog = builder.create();
                        dialog.show();
                    } else {
                        SharedPreferences sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("First Logon", true);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), getString(R.string.anErrorHasOccurred),
                                Toast.LENGTH_SHORT).show();
                        overridePendingTransition(0, 0);
                        finish();
                        overridePendingTransition(0, 0);
                        startActivity(getIntent());

                    }
                });
    }
    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.GONE);
    }
}