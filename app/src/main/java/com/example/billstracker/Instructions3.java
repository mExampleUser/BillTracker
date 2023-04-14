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

public class Instructions3 extends AppCompatActivity {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions3);
        ImageView back = findViewById(R.id.goToPageTwoArrow);
        ImageView forward = findViewById(R.id.goToPageFourArrow);
        TextView skip = findViewById(R.id.btnSkip3);
        ConstraintLayout main = findViewById(R.id.page3);

        back.setOnClickListener(view -> {
            Intent page1 = new Intent(Instructions3.this, Instructions2.class);
            startActivity(page1);
            overridePendingTransition(0, 0);
        });
        forward.setOnClickListener(view -> {
            Intent page3 = new Intent(Instructions3.this, Instructions4.class);
            startActivity(page3);
            overridePendingTransition(0, 0);
        });
        skip.setOnClickListener(view -> {
            Intent main1 = new Intent (Instructions3.this, MainActivity2.class);
            main1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(main1);
            overridePendingTransition(0, 0);
        });

        main.setOnTouchListener(new OnSwipeTouchListener(Instructions3.this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                Intent page3 = new Intent(Instructions3.this, Instructions4.class);
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
                Intent page1 = new Intent(Instructions3.this, Instructions2.class);
                startActivity(page1);
                overridePendingTransition(0, 0);
            }
        });
    }
}