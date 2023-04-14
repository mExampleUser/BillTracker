package com.example.billstracker;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Instructions2 extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions2);
        ImageView page1 = findViewById(R.id.goToPageOneArrow);
        ImageView page3 = findViewById(R.id.goToPage3Arrow);
        TextView skip = findViewById(R.id.btnSkip2);
        ConstraintLayout main = findViewById(R.id.page2);


        page1.setOnClickListener(view -> {
            Intent page11 = new Intent(Instructions2.this, Instructions1.class);
            startActivity(page11);
            overridePendingTransition(0, 0);
        });
        page3.setOnClickListener(view -> {
            Intent page31 = new Intent(Instructions2.this, Instructions3.class);
            startActivity(page31);
            overridePendingTransition(0, 0);
        });
        skip.setOnClickListener(view -> {
            Intent main1 = new Intent (Instructions2.this, MainActivity2.class);
            main1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main1);
            overridePendingTransition(0, 0);
        });

        main.setOnTouchListener(new OnSwipeTouchListener(Instructions2.this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent page3 = new Intent(Instructions2.this, Instructions3.class);
                startActivity(page3);
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
                Intent page1 = new Intent(Instructions2.this, Instructions1.class);
                startActivity(page1);
                overridePendingTransition(0, 0);
            }
        });
    }
}