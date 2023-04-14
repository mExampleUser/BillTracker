package com.example.billstracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Instructions1 extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions1);
        Button goToPage2 = findViewById(R.id.goToPage2);
        TextView skip = findViewById(R.id.btnSkip);
        ConstraintLayout main = findViewById(R.id.instructions_1);
        ImageView arrow = findViewById(R.id.goToPage2Arrow);

        SharedPreferences sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("First Logon", false);
        editor.apply();

        goToPage2.setOnClickListener(view -> {
            Intent page2 = new Intent(Instructions1.this, Instructions2.class);
            startActivity(page2);
            overridePendingTransition(0, 0);
        });
        skip.setOnClickListener(view -> {
            Intent main1 = new Intent (Instructions1.this, MainActivity2.class);
            main1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main1);
            overridePendingTransition(0, 0);
        });

        main.setOnTouchListener(new OnSwipeTouchListener(Instructions1.this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent page2 = new Intent(Instructions1.this, Instructions2.class);
                startActivity(page2);
                overridePendingTransition(0, 0);
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return super.onTouch(view, motionEvent);
            }

            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();

            }
        });
        arrow.setOnClickListener(view -> {
            Intent page2 = new Intent(Instructions1.this, Instructions2.class);
            startActivity(page2);
            overridePendingTransition(0, 0);
        });
    }
}