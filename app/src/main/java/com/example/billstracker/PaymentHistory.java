package com.example.billstracker;

import static com.example.billstracker.Logon.thisUser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PaymentHistory extends AppCompatActivity {

    ArrayList<Payments> paymentsList = new ArrayList<>();
    View paymentHistory;
    Context mContext;
    boolean darkMode;
    DateFormatter df = new DateFormatter();
    TextView myStats, navHome, navViewBillers, displayUserName, displayEmail, ticketCounter, logout, header;
    Spinner dateRangeSpinner, billerNameSpinner;
    Bundle extras;
    ImageView help, settingsButton, drawerToggle;
    LinearLayout hideNav, navDrawer, pb, filterBox;
    FixNumber fn = new FixNumber();

    com.google.android.gms.ads.AdView adview;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        darkMode = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
        mContext = this;
        paymentsList = new ArrayList<>();

        if (thisUser == null) {
            SaveUserData load = new SaveUserData();
            load.loadUserData(PaymentHistory.this);
        }

        pb = findViewById(R.id.pb11);
        help = findViewById(R.id.help2);
        extras = getIntent().getExtras();
        adview = findViewById(R.id.adView4);
        header = findViewById(R.id.paymentHistoryHeader);
        logout = findViewById(R.id.logoutButton2);
        myStats = findViewById(R.id.myStats2);
        hideNav = findViewById(R.id.hideNavDrawer2);
        navHome = findViewById(R.id.navHome2);
        filterBox = findViewById(R.id.filterBox);
        navDrawer = findViewById(R.id.navDrawer2);
        displayEmail = findViewById(R.id.tvUserName3);
        drawerToggle = findViewById(R.id.drawerToggle2);
        ticketCounter = findViewById(R.id.ticketCounter2);
        navViewBillers = findViewById(R.id.navViewBillers2);
        settingsButton = findViewById(R.id.settingsButton2);
        displayUserName = findViewById(R.id.tvName2);
        dateRangeSpinner = findViewById(R.id.dateRangeSpinner);
        billerNameSpinner = findViewById(R.id.billerNameSpinner);

        displayUserName.setText(thisUser.getName());
        displayEmail.setText(thisUser.getUserName());

        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        CountTickets countTickets = new CountTickets();
        countTickets.countTickets(ticketCounter);

        help.setOnClickListener(view -> {
            Intent support = new Intent (PaymentHistory.this, Support.class);
            pb.setVisibility(View.VISIBLE);
            startActivity(support);
        });

        myStats.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent stats = new Intent(PaymentHistory.this, MyStats.class);
            startActivity(stats);
        });

        logout.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            GoogleSignIn.getClient(PaymentHistory.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
            SharedPreferences sp = PaymentHistory.this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("Stay Signed In", false);
            editor.putString("Username", "");
            editor.putString("Password", "");
            editor.apply();
            Intent validate = new Intent(PaymentHistory.this, Logon.class);
            validate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            validate.putExtra("Welcome", true);
            startActivity(validate);
        });

        settingsButton.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent settings = new Intent(mContext, Settings.class);
            startActivity(settings);
        });
        drawerToggle.setOnClickListener(view -> {
            if (navDrawer.getVisibility() == View.VISIBLE) {
                navDrawer.setVisibility(View.GONE);
            } else {
                navDrawer.setVisibility(View.VISIBLE);
                navDrawer.setFocusableInTouchMode(true);
                navDrawer.setClickable(true);
                hideNav.setOnClickListener(view1 -> navDrawer.setVisibility(View.GONE));
            }
        });

        navHome.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent home = new Intent(mContext, MainActivity2.class);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
        });

        navViewBillers.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent billers = new Intent(mContext, ViewBillers.class);
            startActivity(billers);
        });

        navDrawer.setOnTouchListener(new OnSwipeTouchListener(PaymentHistory.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                navDrawer.setVisibility(View.GONE);
            }
        });

        navDrawer.setVisibility(View.GONE);
        refreshPaymentsList();

        paymentsList.sort(new PaymentsComparator());

        String[] spinnerArray = new String[]{ getString(R.string.allPayments), getString(R.string.thisWeek), getString(R.string.thisMonth), getString(R.string.lastMonth),
                getString(R.string.thisYear), getString(R.string.lastYear), getString(R.string.twoYearsAgo) };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateRangeSpinner.setAdapter(adapter);

        ArrayList<String> spinnerArray1 = new ArrayList<>();
        String all1 = getString(R.string.allBillers);
        for (Bills bill : thisUser.getBills()) {
            if (!spinnerArray1.contains(bill.getBillerName())) {
                spinnerArray1.add(bill.getBillerName());
            }
        }
        Collections.sort(spinnerArray1);
        spinnerArray1.add(0, all1);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        billerNameSpinner.setAdapter(adapter1);

        if (extras != null) {
            String billId = extras.getString("Bill Id", "");
            for (Bills payment : thisUser.getBills()) {
                if (payment.getBillsId().equals(billId)) {
                    billerNameSpinner.setSelection(adapter1.getPosition(payment.getBillerName()));
                }
            }
        }

        billerNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                filterResults(dateRangeSpinner, billerNameSpinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dateRangeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                filterResults(dateRangeSpinner, billerNameSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        generateList();
    }

    public void filterResults(Spinner dateRangeSpinner, Spinner billerNameSpinner) {

        refreshPaymentsList();
        paymentsList.clear();

        int todaysDate = df.currentDateAsInt();
        LocalDate date = df.convertIntDateToLocalDate(todaysDate);
        while (date.getDayOfWeek() != DayOfWeek.SUNDAY) {
            date = date.minusDays(1);
        }

        String thisMonth1 = df.convertIntDateToLocalDate(df.currentDateAsInt()).getMonth().toString();
        String thisMonth = thisMonth1.charAt(0) + thisMonth1.substring(1).toLowerCase();
        String lastMonth1 = df.convertIntDateToLocalDate(df.currentDateAsInt()).minusMonths(1).getMonth().toString();
        String lastMonth = lastMonth1.charAt(0) + lastMonth1.substring(1).toLowerCase();
        String thisYear = String.valueOf(df.convertIntDateToLocalDate(df.currentDateAsInt()).getYear());
        String lastYear = String.valueOf(Integer.parseInt(thisYear) - 1);
        String twoYearsAgo = String.valueOf(Integer.parseInt(thisYear) - 2);

        for (Bills bill : thisUser.getBills()) {
            for (Payments payment : bill.getPayments()) {
                if (payment.isPaid()) {
                    if (!paymentsList.contains(payment)) {

                        if (dateRangeSpinner.getSelectedItem().equals(getString(R.string.allPayments)) || dateRangeSpinner.getSelectedItem().equals(getString(R.string.all))) {
                            if (billerNameSpinner.getSelectedItem().equals(getString(R.string.allBillers)) || billerNameSpinner.getSelectedItem().equals(payment.getBillerName()) || billerNameSpinner.getSelectedItem().equals("All Billers")) {
                                paymentsList.add(payment);
                            }
                        } else if (dateRangeSpinner.getSelectedItem().equals(getString(R.string.thisWeek))) {
                            if (df.convertIntDateToLocalDate(payment.getDatePaid()).isAfter(date.minusDays(1))) {
                                if (billerNameSpinner.getSelectedItem().equals(getString(R.string.allBillers)) || billerNameSpinner.getSelectedItem().equals(payment.getBillerName())) {
                                    paymentsList.add(payment);
                                }
                            }
                        } else if (dateRangeSpinner.getSelectedItem().equals(thisMonth)) {
                            if (df.convertIntDateToLocalDate(payment.getDatePaid()).isAfter(date.withDayOfMonth(1).minusDays(1))) {
                                if (billerNameSpinner.getSelectedItem().equals(getString(R.string.allBillers)) || billerNameSpinner.getSelectedItem().equals(payment.getBillerName())) {
                                    paymentsList.add(payment);
                                }
                            }
                        } else if (dateRangeSpinner.getSelectedItem().equals(lastMonth)) {
                            if (df.convertIntDateToLocalDate(payment.getDatePaid()).isAfter(date.minusMonths(1).withDayOfMonth(1).minusDays(1)) &&
                                    df.convertIntDateToLocalDate(payment.getDatePaid()).isBefore(date.withDayOfMonth(1).minusDays(1))) {
                                if (billerNameSpinner.getSelectedItem().equals(getString(R.string.allBillers)) || billerNameSpinner.getSelectedItem().equals(payment.getBillerName())) {
                                    paymentsList.add(payment);
                                }
                            }
                        } else if (dateRangeSpinner.getSelectedItem().equals(thisYear)) {
                            if (df.convertIntDateToLocalDate(payment.getDatePaid()).isAfter(date.withMonth(1).withDayOfMonth(1).minusDays(1))) {
                                if (billerNameSpinner.getSelectedItem().equals(getString(R.string.allBillers)) || billerNameSpinner.getSelectedItem().equals(payment.getBillerName())) {
                                    paymentsList.add(payment);
                                }
                            }
                        } else if (dateRangeSpinner.getSelectedItem().equals(lastYear)) {
                            if (df.convertIntDateToLocalDate(payment.getDatePaid()).isAfter(date.minusYears(1).withMonth(1).withDayOfMonth(1).minusDays(1)) &&
                                    df.convertIntDateToLocalDate(payment.getDatePaid()).isBefore(date.minusYears(1).withMonth(12).withDayOfMonth(31).plusDays(1))) {
                                if (billerNameSpinner.getSelectedItem().equals(getString(R.string.allBillers)) || billerNameSpinner.getSelectedItem().equals(payment.getBillerName())) {
                                    paymentsList.add(payment);
                                }
                            }
                        } else if (dateRangeSpinner.getSelectedItem().equals(twoYearsAgo)) {
                            if (df.convertIntDateToLocalDate(payment.getDatePaid()).isAfter(date.minusYears(2).withMonth(1).withDayOfMonth(1).minusDays(1)) &&
                                    df.convertIntDateToLocalDate(payment.getDatePaid()).isBefore(date.minusYears(2).withMonth(12).withDayOfMonth(31).plusDays(1))) {
                                if (billerNameSpinner.getSelectedItem().equals(getString(R.string.allBillers)) || billerNameSpinner.getSelectedItem().equals(payment.getBillerName())) {
                                    paymentsList.add(payment);
                                }
                            }
                        }
                    }
                }
            }
        }
        paymentsList.sort(Comparator.comparing(Payments::getDatePaid).reversed());
        generateList();
    }

    public void refreshPaymentsList() {

        for (Bills bill : thisUser.getBills()) {
            for (Payments payment : bill.getPayments()) {
                if (payment.isPaid()) {
                    if (!paymentsList.contains(payment)) {
                        paymentsList.add(payment);
                    }
                }
            }
        }
    }

    public void generateList() {

        LinearLayout paymentList1 = findViewById(R.id.paymentList);
        int counter = 0;
        paymentList1.invalidate();
        paymentList1.removeAllViews();
        paymentsList.sort(Comparator.comparing(Payments::getDatePaid).reversed());

        for (Payments payment : paymentsList) {

            paymentHistory = View.inflate(PaymentHistory.this, R.layout.payment_box, null);
            LinearLayout paymentBox1 = paymentHistory.findViewById(R.id.paymentBoxLayout1);
            TextView billerNameBox = paymentHistory.findViewById(R.id.billerNameBox);
            TextView paymentDateBox = paymentHistory.findViewById(R.id.paymentDateBox);
            TextView paymentAmountBox = paymentHistory.findViewById(R.id.paymentAmountBox);
            TextView paymentDateBox3 = paymentHistory.findViewById(R.id.paymentDateBox3);

            billerNameBox.setText(payment.getBillerName());
            paymentDateBox.setText(df.convertIntDateToString(payment.getDatePaid()));
            paymentAmountBox.setText(fn.addSymbol(payment.getPaymentAmount()));
            paymentDateBox3.setText(df.convertIntDateToString(payment.getPaymentDate()));
            paymentList1.addView(paymentBox1);
            TextView divider = new TextView(mContext);
            counter = counter + 1;
            divider.setHeight(30);
            paymentList1.addView(divider);
            paymentBox1.setOnClickListener(view -> {
                pb.setVisibility(View.VISIBLE);
                Intent pay = new Intent(mContext, PayBill.class);
                pay.putExtra("Due Date", df.convertIntDateToString(payment.getPaymentDate()));
                pay.putExtra("Biller Name", payment.getBillerName());
                pay.putExtra("Amount Due", payment.getPaymentAmount());
                pay.putExtra("Is Paid", payment.isPaid());
                pay.putExtra("Payment Id", payment.getPaymentId());
                pay.putExtra("Current Date", df.currentDateAsInt());
                startActivity(pay);
            });
            paymentList1.animate().translationY(0).setDuration(500);
            header.animate().translationX(0).translationY(0).setDuration(500);
            filterBox.animate().translationX(0).translationY(0).setDuration(500);
        }
        if (counter == 0) {
            ImageView searching = new ImageView(mContext);
            searching.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.noun_not_found_2503986));
            searching.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(400, 400);
            lp.setMargins(0, 100, 0, 0);
            lp.gravity = Gravity.CENTER;
            lp.setMargins(0, 100, 0, 0);
            searching.setForegroundGravity(Gravity.CENTER);
            searching.setLayoutParams(lp);
            TextView text = new TextView(mContext);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setTextSize(16);
            text.setText(R.string.noPaymentsFound);
            text.setPadding(0, 80, 0, 100);
            paymentList1.addView(searching);
            paymentList1.addView(text);
            paymentList1.animate().translationY(0).setDuration(500);
            header.animate().translationX(0).translationY(0).setDuration(500);
            filterBox.animate().translationX(0).translationY(0).setDuration(500);
        } else {
            TextView text = new TextView(mContext);
            text.setHeight(250);
            paymentList1.addView(text);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        navDrawer.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
    }
}