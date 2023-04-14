package com.example.billstracker;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    LinearLayout pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        EditText fpUsername = findViewById(R.id.fpUserName);
        Button submit = findViewById(R.id.btnSubmitBiller);
        LinearLayout back = findViewById(R.id.backForgotPasswordLayout);
        pb = findViewById(R.id.pb7);
        Context mContext = this;

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fpUsername.setText(extras.getString("UserName"));
        }

        back.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            onBackPressed();
        });

        submit.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            FirebaseAuth.getInstance().sendPasswordResetEmail(fpUsername.getText().toString())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                            builder.setMessage(getString(R.string.passwordResetLinkSent)).setTitle(getString(R.string.emailSent)).setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                                pb.setVisibility(View.GONE);
                                FirebaseAuth.getInstance().signOut();
                                onBackPressed();
                            });
                            androidx.appcompat.app.AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                            builder.setMessage(getString(R.string.emailNotRegistered)).setTitle(getString(R.string.emailNotFound)).setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {
                                pb.setVisibility(View.GONE);
                                FirebaseAuth.getInstance().signOut();
                            });
                            androidx.appcompat.app.AlertDialog alert = builder.create();
                            alert.show();
                        }
                    });

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.GONE);
    }

}