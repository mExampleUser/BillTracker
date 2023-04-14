package com.example.billstracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Instructions4 extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions4);
        ImageView page3 = findViewById(R.id.goToPageThreeArrow);
        Button ready = findViewById(R.id.btnReady);
        ConstraintLayout main = findViewById(R.id.page4);

        page3.setOnClickListener(view -> {
            Intent page31 = new Intent(Instructions4.this, Instructions3.class);
            startActivity(page31);
            overridePendingTransition(0, 0);
        });
        main.setOnTouchListener(new OnSwipeTouchListener(Instructions4.this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
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
                Intent page3 = new Intent(Instructions4.this, Instructions3.class);
                startActivity(page3);
                overridePendingTransition(0, 0);
            }
        });
        ready.setOnClickListener(view -> {
            Intent main1 = new Intent(Instructions4.this, MainActivity2.class);
            main1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(main1);
            overridePendingTransition(0, 0);
        });
    }
}