package com.example.billstracker;

import static com.example.billstracker.Logon.thisUser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.VolleyLog;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

public class MyStats extends AppCompatActivity {

    boolean darkMode;
    Button enterIncome, changeIncome, submitPay, btnAddNewBiller;
    EditText dtiIncomeAmount;
    LinearLayout bubbles, dtiLayout, statsLayout, statsView, dtiChart, noIncomeBox, addIncomeBox, addBillerLayout, pieChartLayout, nextPaymentDueLayout, navDrawer, hideNav, pb;
    LocalDate selectedDate;
    Spinner spinner, dtiFrequency;
    TextView hideMe, dtiRatio, dtiRating, nameHeader, nextPaymentDue, totalAmountPaid, totalBillersAdded, totalPaymentsMade, dtiExplanation, totalAmountPaidLabel, totalPaymentsMadeLabel,
            totalBillersAddedLabel, navHome, navViewBillers, navPaymentHistory, displayUserName, displayEmail, ticketCounter, logout;
    DateFormatter df = new DateFormatter();
    ScrollView scroll;
    ImageView settingsButton, drawerToggle, help, closeIncome;
    com.google.android.material.imageview.ShapeableImageView icon;
    PieChart pieChart, pieChart1;
    FixNumber fn = new FixNumber();
    com.google.android.gms.ads.AdView adview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_stats);

        pb = findViewById(R.id.pb9);
        help = findViewById(R.id.help3);
        icon = findViewById(R.id.statIcon);
        hideMe = findViewById(R.id.textView67);
        scroll = findViewById(R.id.statScroll);
        bubbles = findViewById(R.id.bubbles);
        spinner = findViewById(R.id.spinner5);
        navHome = findViewById(R.id.navHome3);
        adview = findViewById(R.id.adView5);
        logout = findViewById(R.id.logoutButton3);
        hideNav = findViewById(R.id.hideNavDrawer3);
        pieChart = findViewById(R.id.pieChart);
        dtiRatio = findViewById(R.id.dtiRatio);
        dtiLayout = findViewById(R.id.dtiLayout);
        dtiRating = findViewById(R.id.dtiRating);
        pieChart1 = findViewById(R.id.pieChart1);
        statsView = findViewById(R.id.statsView);
        navDrawer = findViewById(R.id.navDrawer3);
        dtiChart = findViewById(R.id.dtiChartLayout);
        submitPay = findViewById(R.id.btnSubmitPay);
        nameHeader = findViewById(R.id.textView30);
        closeIncome = findViewById(R.id.closeIncome);
        statsLayout = findViewById(R.id.statsLayout);
        noIncomeBox = findViewById(R.id.noIncomeBox);
        enterIncome = findViewById(R.id.btnEnterIncome);
        drawerToggle = findViewById(R.id.drawerToggle3);
        addIncomeBox = findViewById(R.id.addIncomeBox);
        dtiFrequency = findViewById(R.id.dtiPayFrequency);
        changeIncome = findViewById(R.id.btnChangeIncome);
        displayEmail = findViewById(R.id.tvUserName4);
        ticketCounter = findViewById(R.id.ticketCounter3);
        navViewBillers = findViewById(R.id.navViewBillers3);
        settingsButton = findViewById(R.id.settingsButton3);
        nextPaymentDue = findViewById(R.id.nextPaymentDue);
        pieChartLayout = findViewById(R.id.pieChartLayout);
        dtiExplanation = findViewById(R.id.textView65);
        addBillerLayout = findViewById(R.id.addBillerLayout);
        btnAddNewBiller = findViewById(R.id.btnAddBiller);
        dtiIncomeAmount = findViewById(R.id.dtiIncomeAmount);
        totalAmountPaid = findViewById(R.id.totalAmountPaid);
        displayUserName = findViewById(R.id.tvName3);
        totalBillersAdded = findViewById(R.id.totalBillersAdded);
        totalPaymentsMade = findViewById(R.id.totalPaymentsMade);
        navPaymentHistory = findViewById(R.id.navPaymentHistory3);
        nextPaymentDueLayout = findViewById(R.id.nextPaymentDueLayout);
        totalAmountPaidLabel = findViewById(R.id.textView33);
        totalPaymentsMadeLabel = findViewById(R.id.textView32);
        totalBillersAddedLabel = findViewById(R.id.textView31);

        displayUserName.setText(thisUser.getName());
        displayEmail.setText(thisUser.getUserName());

        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getUid());
        if (thisUser.getAdmin()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("tickets").get().addOnCompleteListener(task -> {
                int counter = 0;
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(VolleyLog.TAG, document.getId() + " => " + document.getData());
                        SupportTicket ticket = document.toObject(SupportTicket.class);
                        if (ticket.getAgentUid() != null) {
                            if (ticket.getAgentUid().equals(uid) || ticket.getAgentUid().equals("Unassigned")) {
                                counter = counter + ticket.getUnreadByAgent();
                            }
                        }
                    }
                    if (counter > 0) {
                        ticketCounter.setVisibility(View.VISIBLE);
                        ticketCounter.setText(String.valueOf(counter));
                    }
                    else {
                        ticketCounter.setVisibility(View.GONE);
                    }
                }
            });
        }
        else {
            final boolean[] found = {false};
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("tickets").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(VolleyLog.TAG, document.getId() + " => " + document.getData());
                        SupportTicket ticket = document.toObject(SupportTicket.class);
                        if (ticket.getAgentUid() != null) {
                            if (ticket.getUserUid().equals(uid)) {
                                if (ticket.getUnreadByUser() > 0) {
                                    ticketCounter.setText(String.valueOf(ticket.getUnreadByUser()));
                                    ticketCounter.setVisibility(View.VISIBLE);
                                }
                                found[0] = true;
                                break;
                            }
                        }
                    }
                    if (!found[0]) {
                        ticketCounter.setVisibility(View.GONE);
                    }
                }
            });
        }
        initialize();

    }

    @SuppressLint("ClickableViewAccessibility")
    public void initialize () {

        ArrayList <String> spinnerArray = new ArrayList<>();
        for (Bills bill: thisUser.getBills()) {
            for (Payments payment: bill.getPayments()) {
                String month = df.convertIntDateToLocalDate(payment.getPaymentDate()).getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
                String year = String.valueOf(df.convertIntDateToLocalDate(payment.getPaymentDate()).getYear());
                String date = month + " " + year;
                if (!spinnerArray.contains(date)) {
                    spinnerArray.add(date);
                }
            }
        }
        selectedDate = LocalDate.now(ZoneId.systemDefault());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        String currentMonth = selectedDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
        String currentYear = String.valueOf(selectedDate.getYear());
        String currentDate = currentMonth + " " + currentYear;
        if (spinnerArray.contains(currentDate)) {
            spinner.setSelection(spinnerArray.indexOf(currentDate));
        }

        if (thisUser.getIncome() > 0) {
            dtiChart.setVisibility(View.VISIBLE);
            noIncomeBox.setVisibility(View.GONE);
            setupPieChart1();
            loadPieChartData1();
        }

        ArrayList <String> arrayAdapter = new ArrayList<>();
        arrayAdapter.add(getString(R.string.weekly));
        arrayAdapter.add(getString(R.string.biweekly));
        arrayAdapter.add(getString(R.string.monthly));
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, arrayAdapter);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dtiFrequency.setAdapter(adapter2);

        if (thisUser.getBills().size() == 0) {
            pieChartLayout.setVisibility(View.GONE);
            addBillerLayout.setVisibility(View.VISIBLE);
            btnAddNewBiller.setOnClickListener(view -> {
                pb.setVisibility(View.VISIBLE);
                Intent addBiller = new Intent(MyStats.this, AddBiller.class);
                startActivity(addBiller);
            });
        }
        else {
            setupPieChart();
            loadPieChartData();
        }

        darkMode = false;
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            darkMode = true;
        }

        String name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        if (name != null && name.contains(" ")) {
            int space = name.indexOf(' ');
            nameHeader.setText(String.format(Locale.getDefault(), "%s%s", name.substring(0, space), getString(R.string.sStats)));
        }
        else if (name != null && !name.contains(" ")) {
            nameHeader.setText(String.format(Locale.getDefault(), "%s%s", name, getString(R.string.sStats)));
        }
        else {
            nameHeader.setText(getString(R.string.myStats));
        }

        dtiIncomeAmount.setText(fn.addSymbol(String.valueOf(thisUser.getIncome())));
        dtiFrequency.setSelection(Integer.parseInt(thisUser.getPayFrequency()));

        help.setOnClickListener(view -> {
            Intent support = new Intent (MyStats.this, Support.class);
            pb.setVisibility(View.VISIBLE);
            startActivity(support);
        });

        logout.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            GoogleSignIn.getClient(MyStats.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
            SharedPreferences sp = MyStats.this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("Stay Signed In", false);
            editor.putString("Username", "");
            editor.putString("Password", "");
            editor.apply();
            Intent validate = new Intent(MyStats.this, Logon.class);
            validate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            validate.putExtra("Welcome", true);
            startActivity(validate);
        });

        settingsButton.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent settings = new Intent(MyStats.this, Settings.class);
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
            Intent home = new Intent(MyStats.this, MainActivity2.class);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
        });

        navViewBillers.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent billers = new Intent(MyStats.this, ViewBillers.class);
            startActivity(billers);
        });

        navPaymentHistory.setOnClickListener(view -> {
            Intent payments = new Intent(MyStats.this, PaymentHistory.class);
            pb.setVisibility(View.VISIBLE);
            startActivity(payments);
        });

        navDrawer.setOnTouchListener(new OnSwipeTouchListener(MyStats.this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                navDrawer.setVisibility(View.GONE);
            }
        });

        navDrawer.setVisibility(View.GONE);

        enterIncome.setOnClickListener(view -> {
            enterIncome.setVisibility(View.GONE);
            hideMe.setVisibility(View.GONE);
            addIncomeBox.setVisibility(View.VISIBLE);
        });

        submitPay.setOnClickListener(view -> updatePay());

        changeIncome.setOnClickListener(view -> {
            dtiIncomeAmount.setText(fn.addSymbol(String.valueOf(thisUser.getIncome())));
            dtiIncomeAmount.addTextChangedListener(new MoneyInput(dtiIncomeAmount));
            noIncomeBox.setVisibility(View.VISIBLE);
            addIncomeBox.setVisibility(View.VISIBLE);
            addIncomeBox.animate().translationX(0);
            dtiChart.setVisibility(View.GONE);
            enterIncome.setVisibility(View.GONE);
            hideMe.setVisibility(View.GONE);
        });

        closeIncome.setOnClickListener(v -> {
            noIncomeBox.setVisibility(View.GONE);
            addIncomeBox.animate().translationX(1000);
            dtiChart.setVisibility(View.VISIBLE);
            enterIncome.setVisibility(View.VISIBLE);
            hideMe.setVisibility(View.VISIBLE);
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDate = LocalDate.parse(spinner.getSelectedItem().toString() + " 1", DateTimeFormatter.ofPattern("MMMM yyyy d", Locale.getDefault()));
                loadPieChartData();
                loadPieChartData1();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        createLists();
    }

    public void updatePay () {
        pb.setVisibility(View.VISIBLE);
        double amount;
        String fix = fn.makeDouble(dtiIncomeAmount.getText().toString());
        DecimalFormat df = new DecimalFormat();
        amount = Double.parseDouble(String.format(fix.replaceAll("\\s", "").replaceAll(",", "."), df));
        thisUser.setIncome(amount);
        thisUser.setPayFrequency(String.valueOf(dtiFrequency.getSelectedItemPosition()));
        dtiFrequency.setSelection(Integer.parseInt(thisUser.getPayFrequency()));
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(dtiIncomeAmount.getWindowToken(), 0);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(thisUser.getUserName()).set(thisUser);
        SaveUserData save = new SaveUserData();
        save.saveUserData(MyStats.this, thisUser);
        recreate();
    }

    public void setupPieChart() {
        pieChart.setDrawHoleEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(8);
        pieChart.canResolveTextAlignment();
        pieChart.setDrawEntryLabels(false);
        pieChart.setNoDataText(getString(R.string.noDataFound));
        pieChart.setEntryLabelColor(getResources().getColor(R.color.black, getTheme()));
        pieChart.setNoDataTextColor(getResources().getColor(R.color.blackAndWhite, getTheme()));
        pieChart.setExtraOffsets(5,0,0,0);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setDrawInside(false);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setTextColor(getResources().getColor(R.color.blackAndWhite, getTheme()));
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setEnabled(true);
    }

    public void loadPieChartData() {
        ArrayList <PieEntry> entries = new ArrayList<>();
        double auto = 0, creditCard = 0, entertainment = 0, insurance = 0, miscellaneous = 0, mortgage = 0, personalLoan = 0, utilities = 0, total = 0;
        int monthStart = df.calcDateValue(selectedDate.withDayOfMonth(1));
        int monthEnd = df.calcDateValue(selectedDate.withDayOfMonth(selectedDate.getMonth().length(true)));
        for (Bills bill: thisUser.getBills()) {
            for (Payments payment: bill.getPayments()) {
            if (payment.getPaymentDate() >= monthStart && payment.getPaymentDate() <= monthEnd) {
                double value = Double.parseDouble(bill.getAmountDue());
                total = total + value;
            switch (bill.getCategory()) {
                case "0":
                    auto = auto + value;
                    break;
                case "1":
                    creditCard = creditCard + value;
                    break;
                case "2":
                    entertainment = entertainment + value;
                    break;
                case "3":
                    insurance = insurance + value;
                    break;
                case "4":
                    miscellaneous = miscellaneous + value;
                    break;
                case "5":
                    mortgage = mortgage + value;
                    break;
                case "6":
                    personalLoan = personalLoan + value;
                    break;
                case "7":
                    utilities = utilities + value;
                    break;
            }
            }
            }
        }

        if (auto != 0) {
            entries.add(new PieEntry((float) ((auto * 100) / total), getString(R.string.autoLoan) + " " + fn.addSymbol(String.valueOf(auto))));
        }
        if (creditCard != 0) {
            entries.add(new PieEntry((float) ((creditCard * 100) / total), getString(R.string.creditCard) + " " + fn.addSymbol(String.valueOf(creditCard))));
        }
        if (entertainment != 0) {
            entries.add(new PieEntry((float) ((entertainment * 100) / total), getString(R.string.entertainment) + " " + fn.addSymbol(String.valueOf(entertainment))));
        }
        if (insurance != 0) {
            entries.add(new PieEntry((float) ((insurance * 100) / total), getString(R.string.insurance) + " " + fn.addSymbol(String.valueOf(insurance))));
        }
        if (miscellaneous != 0) {
            entries.add(new PieEntry((float) ((miscellaneous * 100) / total), getString(R.string.miscellaneous) + " " + fn.addSymbol(String.valueOf(miscellaneous))));
        }
        if (mortgage != 0) {
            entries.add(new PieEntry((float) ((mortgage * 100) / total), getString(R.string.mortgage) + " " + fn.addSymbol(String.valueOf(mortgage))));
        }
        if (personalLoan != 0) {
            entries.add(new PieEntry((float) ((personalLoan * 100) / total), getString(R.string.personalLoans) + " " + fn.addSymbol(String.valueOf(personalLoan))));
        }
        if (utilities != 0) {
            entries.add(new PieEntry((float) ((utilities * 100) / total), getString(R.string.utilities) + " " + fn.addSymbol(String.valueOf(utilities))));
        }

        ArrayList <Integer> colors = new ArrayList<>();
        for (int color: ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }
        for (int color: MyStats.this.getResources().getIntArray(R.array.pieChart)) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.total) + " " + fn.addSymbol(String.valueOf(total)));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(8f);
        data.setValueTextColor(getResources().getColor(R.color.black, getTheme()));

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    public void setupPieChart1() {
        pieChart1.setDrawHoleEnabled(false);
        pieChart1.setUsePercentValues(true);
        pieChart1.setEntryLabelTextSize(8);
        pieChart1.canResolveTextAlignment();
        pieChart1.setDrawEntryLabels(false);
        pieChart1.setNoDataText(getString(R.string.dtiNotCalculated));
        pieChart1.setEntryLabelColor(getResources().getColor(R.color.black, getTheme()));
        pieChart1.setNoDataTextColor(getResources().getColor(R.color.blackAndWhite, getTheme()));
        pieChart1.setExtraOffsets(5,0,0,0);
        pieChart1.getDescription().setEnabled(false);

        Legend l = pieChart1.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        l.setDrawInside(false);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setTextColor(getResources().getColor(R.color.blackAndWhite, getTheme()));
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setEnabled(true);
    }

    public void loadPieChartData1() {
        ArrayList <PieEntry> entries = new ArrayList<>();
        double bills = 0, income;

        double daysInMonth = selectedDate.getMonth().length(true);
        int monthStart = df.calcDateValue(selectedDate.withDayOfMonth(1));
        int monthEnd = df.calcDateValue(selectedDate.withDayOfMonth(selectedDate.getMonth().length(true)));
        if (thisUser.getPayFrequency().equals("0")) {
            income = (daysInMonth / 7) * thisUser.getIncome();
        }
        else if (thisUser.getPayFrequency().equals("1")) {
            income = (daysInMonth / 14) * thisUser.getIncome();
        }
        else {
            income = thisUser.getIncome();
        }

        for (Bills bill: thisUser.getBills()) {
            for (Payments payment: bill.getPayments()) {
                if (payment.getPaymentDate() >= monthStart && payment.getPaymentDate() <= monthEnd) {
                    bills = bills + Double.parseDouble(payment.getPaymentAmount());
                }
            }
        }

        double disposable = (income - bills) / income * 100;
        double billPercentage = bills / income * 100;
        DecimalFormat df = new DecimalFormat("###.##");
        if (billPercentage < 50 && billPercentage > 40) {
            dtiRating.setText(getString(R.string.yourDtiRatingGood));
            dtiExplanation.setText(getString(R.string.lendersPreferAbove50));
        }
        else if (billPercentage <= 40) {
            dtiRating.setText(getString(R.string.yourDtiRatingExcellent));
            dtiExplanation.setText(getString(R.string.dtiIsBelowLimit));
        }
        else if (billPercentage >= 50 && billPercentage < 60) {
            dtiRating.setText(getString(R.string.dtiRatingIsFair));
            dtiExplanation.setText(getString(R.string.dtiTooHigh));
        }
        else {
            dtiRating.setText(getString(R.string.dtiRatingPoor));
            dtiExplanation.setText(getString(R.string.dtiIsWayTooHigh));
        }
        if (income > bills) {
            entries.add(new PieEntry((float) disposable, getString(R.string.disposableIncome) + " " + fn.addSymbol(String.valueOf(income - bills))));
        }
        entries.add(new PieEntry((float) billPercentage, getString(R.string.bills) + " " + fn.addSymbol(String.valueOf(bills))));
        dtiRatio.setText(String.format(Locale.getDefault(),"%s %s%%", getString(R.string.yourDebtToIncomeRatioIs), df.format(billPercentage)));

        ArrayList <Integer> colors = new ArrayList<>();

        for (int color: MyStats.this.getResources().getIntArray(R.array.pieChart)) {
            colors.add(color);
        }

        PieDataSet dataSet = new PieDataSet(entries, getString(R.string.incomeAmount) + " " + fn.addSymbol(String.valueOf(income)));
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(8f);
        data.setValueTextColor(getResources().getColor(R.color.black, getTheme()));

        pieChart1.invalidate();
        pieChart1.setData(data);

        pieChart1.animateY(1400, Easing.EaseInOutQuad);
    }

    public void createLists () {

        LoadIcon loadIcon = new LoadIcon();
        double totalPayments = 0;
        int singleBillerPaymentsMade;
        double singleBillerTotalPayments;
        int billerCounter = 0;
        int paymentCounter = 0;
        ArrayList<Stats> stats = new ArrayList<>();
        ArrayList<String> billers1 = new ArrayList<>();
        boolean latest;
        String latestPayment = "";
        nextPaymentDueLayout.setVisibility(View.GONE);

        if (thisUser.getBills() != null && thisUser.getBills().size() > 0) {
            for (Bills bill : thisUser.getBills()) {
                billers1.add(bill.getBillerName());
                latest = false;
                singleBillerPaymentsMade = 0;
                singleBillerTotalPayments = 0;
                billerCounter = billerCounter + 1;
                if (bill.getPayments() != null && bill.getPayments().size() > 0) {
                    bill.getPayments().sort(Comparator.comparing(Payments::getDatePaid).reversed());
                    for (Payments payment : bill.getPayments()) {
                        if (payment.isPaid()) {
                            String paymentAmount = payment.getPaymentAmount();
                            if (!latest) {
                                latestPayment = df.convertIntDateToString(payment.getDatePaid());
                                latest = true;
                            }
                            if (payment.isPaid()) {
                                if (payment.getPaymentAmount().contains("$")) {
                                    paymentAmount = paymentAmount.replaceAll("\\$", "");
                                }
                                double paymentDouble = Double.parseDouble(paymentAmount);
                                totalPayments = totalPayments + paymentDouble;
                                paymentCounter = paymentCounter + 1;
                                singleBillerPaymentsMade = singleBillerPaymentsMade + 1;
                                singleBillerTotalPayments = singleBillerTotalPayments + Double.parseDouble(String.valueOf(paymentDouble));
                            }
                        }
                    }
                    stats.add(new Stats(bill.getBillerName(), singleBillerTotalPayments, singleBillerPaymentsMade, latestPayment));
                }
            }
        }

        totalAmountPaid.setText(fn.addSymbol(String.valueOf(totalPayments)));
        totalPaymentsMade.setText(String.valueOf(paymentCounter));
        totalBillersAdded.setText(String.valueOf(billerCounter));
        bubbles.removeAllViews();
        bubbles.invalidate();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        lp.gravity = Gravity.CENTER;
        int dpPerCharacter;
        int totalSpace;
        Configuration configuration = getApplicationContext().getResources().getConfiguration();
        if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            totalSpace = configuration.screenWidthDp;
            dpPerCharacter = totalSpace / 20;
        }
        else {
            totalSpace = configuration.screenWidthDp;
            dpPerCharacter = totalSpace / 70;
        }
        int spaceUsed;

        LinearLayout row = new LinearLayout(MyStats.this);
        row.setLayoutParams(lp);
        row.setOrientation(LinearLayout.HORIZONTAL);
        bubbles.addView(row);
        View bubble1 = View.inflate(MyStats.this, R.layout.biller_bubble, null);
        TextView billerName1 = bubble1.findViewById(R.id.textView49);
        billerName1.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blueGrey, getTheme())));
        billerName1.setTextSize(14);
        LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        bubble1.setLayoutParams(lp1);
        bubble1.setPadding(15,15,15,15);
        row.addView(bubble1);
        spaceUsed = billerName1.length() * dpPerCharacter;
        double finalTotalPayments = totalPayments;
        int finalPaymentCounter = paymentCounter;
        int finalBillerCounter = billerCounter;
        bubble1.setOnClickListener(view -> {
            String name = thisUser.getName();
            if (name != null && name.contains(" ")) {
                int space = name.indexOf(' ');
                nameHeader.setText(String.format(Locale.getDefault(), "%s%s", name.substring(0, space), getString(R.string.sStats)));
            }
            else if (name != null && !name.contains(" ")) {
                nameHeader.setText(String.format(Locale.getDefault(), "%s%s", name, getString(R.string.sStats)));
            }
            else {
                nameHeader.setText(getString(R.string.myStats));
            }
            nextPaymentDueLayout.setVisibility(View.GONE);
            totalBillersAddedLabel.setText(getString(R.string.totalBillersAdded));
            totalAmountPaid.setText(fn.addSymbol(String.valueOf(finalTotalPayments)));
            totalPaymentsMade.setText(String.valueOf(finalPaymentCounter));
            totalBillersAdded.setText(String.valueOf(finalBillerCounter));
            icon.setBackground(null);
            icon.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.kisspng_statistics_icon_ppt_element_5aa15ffeaab9b8_8839433215205253106993, getTheme()));
            icon.setImageTintList(null);
            scroll.post(() -> scroll.smoothScrollTo(0, statsView.getTop()));
        });
        row.setLayoutParams(lp1);

        if (thisUser.getBills().size() > 0) {
            for (String bills : billers1) {
                View bubble = View.inflate(MyStats.this, R.layout.biller_bubble, null);
                TextView billerName = bubble.findViewById(R.id.textView49);
                billerName.setText(String.format(Locale.getDefault()," %s ", bills));
                billerName.setTextSize(14);
                spaceUsed = spaceUsed + billerName.length() * dpPerCharacter;
                bubble.setPadding(15, 15, 15, 15);
                for (Stats stat : stats) {
                    if (stat.getBillerName().equals(bills) && stat.getTotalPaymentsMade() < 1) {
                        billerName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.grey, getTheme())));
                    }
                }
                for (Bills bill2 : thisUser.getBills()) {
                    for (Payments payment : bill2.getPayments()) {
                        if (!payment.isPaid() && payment.getBillerName().equals(bills)) {
                            if (df.convertIntDateToLocalDate(df.currentDateAsInt()).isEqual(df.convertIntDateToLocalDate(payment.getPaymentDate()))) {
                                billerName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow, getTheme())));
                            } else if (df.convertIntDateToLocalDate(df.currentDateAsInt()).isAfter(df.convertIntDateToLocalDate(payment.getPaymentDate()))) {
                                billerName.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.red, getTheme())));
                            }
                        }
                    }
                }
                if (spaceUsed < totalSpace) {
                    row.addView(bubble);
                    bubble.setOnClickListener(view -> {
                        for (Stats stat : stats) {
                            if (stat.getBillerName().equals(bills)) {
                                nameHeader.setText(stat.getBillerName());
                                totalAmountPaid.setText(fn.addSymbol(String.valueOf(stat.getTotalPaymentsAmount())));
                                totalPaymentsMade.setText(String.valueOf(stat.getTotalPaymentsMade()));
                                totalBillersAddedLabel.setText(getString(R.string.lastPaymentMadeOn));
                                if (stat.getTotalPaymentsMade() == 0) {
                                    totalBillersAdded.setText("n/a");
                                } else {
                                    totalBillersAdded.setText(stat.getDateLastPaid());
                                }
                                nextPaymentDueLayout.setVisibility(View.VISIBLE);
                                for (Bills bill : thisUser.getBills()) {
                                    if (bill.getBillerName().equals(stat.getBillerName())) {
                                        int earliestDate = 1000000;
                                        for (Payments payment : bill.getPayments()) {
                                            if (!payment.isPaid() && payment.getPaymentDate() < earliestDate) {
                                                earliestDate = payment.getPaymentDate();

                                            }
                                        }
                                        if (earliestDate == 1000000) {
                                            nextPaymentDue.setText(getString(R.string.noPaymentsAreDue));
                                        } else {
                                            nextPaymentDue.setText(df.convertIntDateToString(earliestDate));
                                        }
                                    }
                                }
                                for (Bills bills2: thisUser.getBills()) {
                                    if (bills2.getBillerName().equals(bills)) {
                                        loadIcon.loadIcon(MyStats.this, icon, bills2.getCategory(), bills2.getIcon());
                                        if (darkMode) {
                                            icon.setForegroundGravity(Gravity.CENTER);
                                            icon.setMinimumHeight(icon.getWidth());
                                            icon.setPadding(40, 40, 40, 40);
                                            icon.setClipToOutline(true);
                                        }
                                    }
                                }
                                scroll.post(() -> scroll.smoothScrollTo(0, statsView.getTop()));
                            }
                        }
                    });
                } else {
                    row = new LinearLayout(MyStats.this);
                    row.setLayoutParams(lp);
                    row.setOrientation(LinearLayout.HORIZONTAL);
                    bubbles.addView(row);
                    row.addView(bubble);
                    bubble.setOnClickListener(view -> {
                        for (Stats stat : stats) {
                            if (stat.getBillerName().equals(bills)) {
                                nameHeader.setText(stat.getBillerName());
                                totalAmountPaid.setText(fn.addSymbol(String.valueOf(stat.getTotalPaymentsAmount())));
                                totalPaymentsMade.setText(String.valueOf(stat.getTotalPaymentsMade()));
                                totalBillersAddedLabel.setText(getString(R.string.lastPaymentMadeOn));
                                if (stat.getTotalPaymentsMade() == 0) {
                                    totalBillersAdded.setText("n/a");
                                } else {
                                    totalBillersAdded.setText(stat.getDateLastPaid());
                                }
                                nextPaymentDueLayout.setVisibility(View.VISIBLE);
                                for (Bills bill : thisUser.getBills()) {
                                    if (bill.getBillerName().equals(stat.getBillerName())) {
                                        int earliestDate = 1000000;
                                        for (Payments payment : bill.getPayments()) {
                                            if (!payment.isPaid() && payment.getPaymentDate() < earliestDate) {
                                                earliestDate = payment.getPaymentDate();

                                            }
                                        }
                                        if (earliestDate == 1000000) {
                                            nextPaymentDue.setText(getString(R.string.noPaymentsDue));
                                        } else {
                                            nextPaymentDue.setText(df.convertIntDateToString(earliestDate));
                                        }
                                    }
                                }
                                for (Bills bills3: thisUser.getBills()) {
                                    if (bills.equals(bills3.getBillerName())) {
                                        loadIcon.loadIcon(MyStats.this, icon, bills3.getCategory(), bills3.getIcon());
                                        if (darkMode) {
                                            icon.setForegroundGravity(Gravity.CENTER);
                                            icon.setMinimumHeight(icon.getWidth());
                                            icon.setPadding(40, 40, 40, 40);
                                            icon.setClipToOutline(true);
                                        }
                                    }
                                }
                                scroll.post(() -> scroll.smoothScrollTo(0, statsView.getTop()));
                            }
                        }
                    });
                    spaceUsed = 0;
                }
            }
        }
        TextView spacer = new TextView(MyStats.this);
        spacer.setHeight(200);
        bubbles.addView(spacer);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onResume() {
        super.onResume();
        initialize();
        navDrawer.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
    }
}