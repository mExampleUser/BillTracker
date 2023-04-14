package com.example.billstracker;

import static com.example.billstracker.Logon.billList;
import static com.example.billstracker.Logon.checkTrophies;
import static com.example.billstracker.Logon.thisUser;
import static com.example.billstracker.R.layout.main_activity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;

import me.thanel.swipeactionview.SwipeActionView;
import me.thanel.swipeactionview.SwipeGestureListener;

public class MainActivity2 extends AppCompatActivity {

    static String name, userName, channelId;
    int month, year, day, counter, todayDateValue, sunday;
    ArrayList<Payments> dueThisMonth = new ArrayList<>();
    Context mContext;
    long delay;
    boolean even;
    LocalDate selectedDate;
    LinearLayout noResults;
    LinearLayout navDrawer;
    LinearLayout hideNav;
    LinearLayout billsList1;
    LinearLayout hideForNavDrawer;
    LinearLayout pb;
    LinearLayout paymentConfirm, trophyRow;
    LinearLayout trophyContainer;
    ImageView drawerToggle, settingsButton, help;
    TextView displayUserName, displayEmail, navHome, navViewBillers, navPaymentHistory, selectedMonth, backMonth, forwardMonth, admin, payNext, myStats, ticketCounter, contactUs,
            popupMessage;
    ScrollView scroll;
    TextView fab;
    SharedPreferences sp;
    DateFormatter dateFormatter = new DateFormatter();
    ArrayList <Payments> today = new ArrayList<>();
    boolean previousMonth;
    FixNumber fn = new FixNumber();
    LinearLayout billsList;
    ArrayList<Payments> earlier = new ArrayList<>(), laterThisWeek = new ArrayList<>(), later = new ArrayList<>();
    double earlyTotal = 0, todayTotal = 0, laterThisWeekTotal = 0, laterTotal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(main_activity);

        SaveUserData save = new SaveUserData();
        if (thisUser == null) {
            save.loadUserData(MainActivity2.this);
        }
        for (Bills bills: thisUser.getBills()) {
            for (Payments payments: bills.getPayments()) {
                if (!payments.isPaid() && payments.getPaymentDate() < dateFormatter.currentDateAsInt() + 14) {
                    scheduleNotifications(payments);
                }
            }
        }
        initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void initialize () {

        mContext = MainActivity2.this;
        userName = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getEmail();
        name = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName();
        drawerToggle = findViewById(R.id.drawerToggle);
        navDrawer = findViewById(R.id.navDrawer);
        pb = findViewById(R.id.progressBar9);
        even = false;
        hideNav = findViewById(R.id.hideNavDrawer);
        settingsButton = findViewById(R.id.settingsButton);
        contactUs = findViewById(R.id.contactUs);
        trophyRow = findViewById(R.id.trophyRow);
        navHome = findViewById(R.id.navHome);
        navViewBillers = findViewById(R.id.navViewBillers);
        navPaymentHistory = findViewById(R.id.navPaymentHistory);
        selectedMonth = findViewById(R.id.selectedMonth);
        backMonth = findViewById(R.id.backMonth);
        trophyContainer = findViewById(R.id.trophyContainer);
        forwardMonth = findViewById(R.id.forgotPasswordHeader);
        billsList1 = findViewById(R.id.billsList1);
        displayUserName = findViewById(R.id.tvName);
        delay = 2000;
        displayEmail = findViewById(R.id.tvUserName2);
        noResults = findViewById(R.id.noResults);
        fab = findViewById(R.id.floatingActionButton);
        sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
        admin = findViewById(R.id.admin);
        hideForNavDrawer = findViewById(R.id.hideForNavDrawer);
        scroll = findViewById(R.id.scroll);
        payNext = findViewById(R.id.payNext);
        paymentConfirm = findViewById(R.id.paymentConfirm);
        myStats = findViewById(R.id.myStats);
        popupMessage = findViewById(R.id.popupMessage);
        help = findViewById(R.id.helpMe);
        previousMonth = false;

        if (selectedDate == null) {
            selectedDate = dateFormatter.convertIntDateToLocalDate(dateFormatter.currentDateAsInt());
        }
        if (thisUser != null) {
            SaveUserData save = new SaveUserData();
            thisUser = save.loadUserData(MainActivity2.this);
        }

        ticketCounter = findViewById(R.id.ticketCounter);
        CountTickets countTickets = new CountTickets();
        countTickets.countTickets(ticketCounter);

        CheckTrophies check = new CheckTrophies();
        check.checkTrophies(MainActivity2.this, trophyContainer);
        checkTrophies = false;

        ArrayList <Integer> trophies = new ArrayList<>(Arrays.asList(R.drawable.trophy, R.drawable.trophy2, R.drawable.trophy3, R.drawable.trophy4, R.drawable.medal2,
                R.drawable.medal3, R.drawable.medal4, R.drawable.early_bird_removebg_preview, R.drawable.medal));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(120, 120);

        for (Trophy trophy: thisUser.getTrophies()) {

            ImageView image = new ImageView(MainActivity2.this);
            image.setImageDrawable(AppCompatResources.getDrawable(MainActivity2.this, trophies.get(trophy.getType() - 1)));
            image.setLayoutParams(lp);
            trophyRow.addView(image);
        }

        help.setOnClickListener(view -> {
            Intent support = new Intent (MainActivity2.this, Support.class);
            support.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pb.setVisibility(View.VISIBLE);
            startActivity(support);
        });

        contactUs.setOnClickListener(view -> {
            Intent support = new Intent (MainActivity2.this, Support.class);
            support.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            pb.setVisibility(View.VISIBLE);
            startActivity(support);
        });
        trophyRow.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, AwardCase.class);
            startActivity(intent);
        });

        myStats.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent stats = new Intent(MainActivity2.this, MyStats.class);
            startActivity(stats);
        });

        if (thisUser.getAdmin()) {
            admin.setEnabled(true);
            admin.setOnClickListener(view -> {
                Intent admin = new Intent(MainActivity2.this, Administrator.class);
                startActivity(admin);
            });
        }
        else {
            admin.setEnabled(false);
        }

        fab.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent addBiller = new Intent(mContext, AddBiller.class);
            startActivity(addBiller);
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
            startActivity(home);
        });

        navViewBillers.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent billers = new Intent(mContext, ViewBillers.class);
            startActivity(billers);
        });

        navPaymentHistory.setOnClickListener(view -> {
            Intent payments = new Intent(mContext, PaymentHistory.class);
            pb.setVisibility(View.VISIBLE);
            startActivity(payments);
        });

        navDrawer.setOnTouchListener(new OnSwipeTouchListener(MainActivity2.this) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                navDrawer.setVisibility(View.GONE);
            }
        });

        backMonth.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            selectedDate = selectedDate.minusMonths(1);
            setCurrentMonth(selectedMonth);
            previousMonth = true;
            listBills(billsList1);
            pb.setVisibility(View.GONE);
        });

        forwardMonth.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            selectedDate = selectedDate.plusMonths(1);
            setCurrentMonth(selectedMonth);
            listBills(billsList1);
            pb.setVisibility(View.GONE);
        });

        navDrawer.setVisibility(View.GONE);

        createNotificationChannel();
        getValues();
        getCurrentMonth();
        counter = 0;

        refreshUser();
        dueThisMonth = whatsDueThisMonth(dueThisMonth, selectedDate);
        listBills(billsList1);

        payNext.setOnClickListener(view -> {

            pb.setVisibility(View.VISIBLE);
            Payments next = new Payments();
            next.setPaymentDate(dateFormatter.currentDateAsInt() + 60);
            boolean found = false;
            for (Bills bills: thisUser.getBills()) {
                bills.getPayments().sort(Comparator.comparing(Payments::getPaymentDate));
                for (Payments payment: bills.getPayments()) {
                    if (!payment.isPaid() && payment.getPaymentDate() < next.getPaymentDate()) {
                        next = payment;
                        found = true;
                    }
                }
            }

            if (found) {
                Intent pay = new Intent(mContext, PayBill.class);
                pay.putExtra("Due Date", dateFormatter.convertIntDateToString(next.getPaymentDate()));
                pay.putExtra("Biller Name", next.getBillerName());
                pay.putExtra("Amount Due", next.getPaymentAmount());
                pay.putExtra("Is Paid", next.isPaid());
                pay.putExtra("Payment Id", next.getPaymentId());
                pay.putExtra("Current Date", dateFormatter.currentDateAsInt());
                startActivity(pay);
            }
            else {
                pb.setVisibility(View.GONE);
                androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                alert.setTitle(getString(R.string.noBillsDue));
                alert.setMessage(getString(R.string.noUpcomingBills));
                alert.setPositiveButton(getString(R.string.ok), (dialogInterface, i) -> {

                });
                androidx.appcompat.app.AlertDialog builder = alert.create();
                builder.show();
            }
        });
    }

    public static boolean isVisible(final View view) {

        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        final Rect actualPosition = new Rect();
        view.getGlobalVisibleRect(actualPosition);
        final Rect screen = new Rect(0, 0, Resources.getSystem().getDisplayMetrics().widthPixels, Resources.getSystem().getDisplayMetrics().heightPixels);
        return actualPosition.intersects(screen.left, screen.top, screen.right, screen.bottom);
    }

    public void getValues() {

        String setName = sp.getString("name", "");
        String setUsername = sp.getString("email", "");
        displayUserName.setText(setName);
        displayEmail.setText(setUsername);
        String firstName;
        if (name.contains(" ")) {
            firstName = name.substring(0, name.indexOf(' '));
        } else {
            firstName = name;
        }
        firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        admin.setText(String.format(Locale.getDefault(), "%s %s %s", getString(R.string.good), dateFormatter.currentPhaseOfDay(MainActivity2.this), firstName));

    }
    public void getCurrentMonth() {

        if (selectedDate != null) {
            month = selectedDate.getMonth().getValue();
            day = 1;
            year = selectedDate.getYear();
        } else {
            LocalDateTime loginTime = LocalDateTime.now();
            DateTimeFormatter formattedMonth = DateTimeFormatter.ofPattern("MM", Locale.getDefault());
            month = Integer.parseInt(loginTime.format(formattedMonth));
            DateTimeFormatter formattedYear = DateTimeFormatter.ofPattern("yyyy", Locale.getDefault());
            year = Integer.parseInt(loginTime.format(formattedYear));
            DateTimeFormatter formattedDay = DateTimeFormatter.ofPattern("dd", Locale.getDefault());
            day = Integer.parseInt(loginTime.format(formattedDay));
        }
        setCurrentMonth(selectedMonth);
    }

    public void setCurrentMonth(TextView selectedMonth) {

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault());
        selectedMonth.setText(dtf.format(selectedDate));
    }

    public void listBills(LinearLayout billsList1) {

        billsList1.removeAllViews();
        billsList1.invalidate();
        billList = thisUser.getBills();
        billsList = findViewById(R.id.billsList1);
        dueThisMonth = whatsDueThisMonth(dueThisMonth, selectedDate);
        LinearLayout billsList = findViewById(R.id.billsList1);
        sunday = dateFormatter.calcDateValue(dateFormatter.convertIntDateToLocalDate(dateFormatter.currentDateAsInt()).with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)));
        todayDateValue = dateFormatter.currentDateAsInt();
        counter = 0;

        billsList.animate().translationY(0).setDuration(500);
        admin.animate().translationX(0).setDuration(500);

        if (dueThisMonth.size() > 0) {
            for (Payments due : dueThisMonth) {
                counter = counter + 1;
                if (due.isPaid()) {
                    earlier.add(due);
                    earlyTotal = earlyTotal + Double.parseDouble(fn.makeDouble(due.getPaymentAmount()));
                } else if (due.getPaymentDate() == todayDateValue || !due.isPaid() && due.getPaymentDate() < todayDateValue) {
                    today.add(due);
                    todayTotal = todayTotal + Double.parseDouble(fn.makeDouble(due.getPaymentAmount()));
                } else if (due.getPaymentDate() <= sunday + 7) {
                    laterThisWeek.add(due);
                    laterThisWeekTotal = laterThisWeekTotal + Double.parseDouble(fn.makeDouble(due.getPaymentAmount()));
                } else {
                    later.add(due);
                    laterTotal = laterTotal + Double.parseDouble(fn.makeDouble(due.getPaymentAmount()));
                }
            }
            earlier.sort(Comparator.comparingInt(Payments::getDatePaid));
            today.sort(new PaymentsComparator());
            laterThisWeek.sort(new PaymentsComparator());
            later.sort(new PaymentsComparator());

            generateBillBoxes();

            TextView divider = new TextView(mContext);
            LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
            divider.setLayoutParams(layout);
            divider.setHeight(200);
            billsList.addView(divider);
            ConstraintLayout header = findViewById(R.id.linearLayout3);
            header.animate().translationX(0).setDuration(500);
            billsList.animate().translationY(0).setDuration(500);

        } else {
            noResults.setVisibility(View.VISIBLE);
            billsList1.addView(noResults);
        }
        previousMonth = false;
        dueThisMonth.clear();
        earlier.clear();
        today.clear();
        laterThisWeek.clear();
        later.clear();
        earlyTotal = 0;
        todayTotal = 0;
        laterThisWeekTotal = 0;
        laterTotal = 0;
    }

    public void generateBillBoxes () {

        boolean earlyHeader = false, laterThisWeekHeader = false, todayHeader = false, laterHeader = false;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 10, 0, 10);

        for (Payments td : today) {

            View billBox;
            ImageView check;
            com.google.android.material.imageview.ShapeableImageView billIconLater;
            TextView tvDueDate, tvBillerName, tvAmountDue;
            View billBox1;
            ImageView check1;
            com.google.android.material.imageview.ShapeableImageView billIconToday;
            TextView tvDueDate1, tvBillerName1, tvAmountDue1;
            View header = View.inflate(MainActivity2.this, R.layout.bill_box_header, null);
            TextView timeFrame2 = header.findViewById(R.id.timeFrame), totalDue2 = header.findViewById(R.id.totalDue);

            if (!previousMonth) {
                billBox = View.inflate(MainActivity2.this, R.layout.bill_box_later, null);
                check = billBox.findViewById(R.id.checkMark); billIconLater = billBox.findViewById(R.id.billIconLater);
                tvDueDate = billBox.findViewById(R.id.tvDueDate); tvBillerName = billBox.findViewById(R.id.tvBillerName); tvAmountDue = billBox.findViewById(R.id.amountDue);
                billBox1 = View.inflate(MainActivity2.this, R.layout.bill_box_today, null);
                check1 = billBox1.findViewById(R.id.checkMark);
                billIconToday = (billBox1.findViewById(R.id.billIconToday));
                tvDueDate1 = billBox1.findViewById(R.id.tvDueDate);
                tvBillerName1 = billBox1.findViewById(R.id.tvBillerName);
                tvAmountDue1 = billBox1.findViewById(R.id.amountDue);
            }
            else {
                billBox = View.inflate(MainActivity2.this, R.layout.bill_box_later2, null);
                check = billBox.findViewById(R.id.checkMark1); billIconLater = billBox.findViewById(R.id.billIconLater1);
                tvDueDate = billBox.findViewById(R.id.tvDueDate1); tvBillerName = billBox.findViewById(R.id.tvBillerName1); tvAmountDue = billBox.findViewById(R.id.amountDue1);
                billBox1 = View.inflate(MainActivity2.this, R.layout.bill_box_today2, null);
                check1 = billBox1.findViewById(R.id.checkMark1);
                billIconToday = (billBox1.findViewById(R.id.billIconToday1));
                tvDueDate1 = billBox1.findViewById(R.id.tvDueDate1);
                tvBillerName1 = billBox1.findViewById(R.id.tvBillerName1);
                tvAmountDue1 = billBox1.findViewById(R.id.amountDue1);
            }

            if (!todayHeader) {
                buildHeader(billsList, timeFrame2, totalDue2, todayTotal, getString(R.string.today));
                todayHeader = true;
                billsList.addView(header);
            }

            int daysLate = todayDateValue - td.getPaymentDate();
            if (!td.isPaid() && daysLate > 0) {

                billBox.setBackgroundResource(R.drawable.border_styles_red);
                if (daysLate == 1) {
                    tvDueDate.setText(R.string.dueYesterday);
                } else {
                    tvDueDate.setText(String.format(Locale.getDefault(), "%s %d %s", getString(R.string.due), daysLate, getString(R.string.daysAgo)));
                }
                check.setVisibility(View.VISIBLE);
                billBoxValues(tvBillerName, td, tvAmountDue, billsList, billBox, billIconLater);
                billBox.setElevation(15);
                billBox.setLayoutParams(lp);
                swipeViewListener(billBox, td, "bill_box_later");
                billBox.setOnClickListener(view -> {
                    pb.setVisibility(View.VISIBLE);
                    startPay(tvDueDate, tvBillerName, tvAmountDue, td, todayDateValue);
                });

            }
            else {
                billBox1.setBackgroundResource(R.drawable.border_styles_yellow);
                tvDueDate1.setText(R.string.dueToday);
                check1.setVisibility(View.VISIBLE);
                billBoxValues(tvBillerName1, td, tvAmountDue1, billsList, billBox1, billIconToday);
                billBox1.setLayoutParams(lp);
                billBox1.setElevation(15);
                swipeViewListener(billBox1, td, "bill_box_today");
                billBox1.setOnClickListener(view -> {
                    pb.setVisibility(View.VISIBLE);
                    startPay(tvDueDate1, tvBillerName1, tvAmountDue1, td, todayDateValue);
                });
            }
        }
        for (Payments late : laterThisWeek) {

            View billBox2;
            TextView tvDueDate2, tvBillerName2, tvAmountDue2;
            ImageView check2;
            com.google.android.material.imageview.ShapeableImageView billIcon;
            if (!previousMonth) {
                billBox2 = View.inflate(MainActivity2.this, R.layout.bill_box, null);
                tvDueDate2 = billBox2.findViewById(R.id.tvDueDate); tvBillerName2 = billBox2.findViewById(R.id.tvBillerName); tvAmountDue2 = billBox2.findViewById(R.id.amountDue);
                check2 = billBox2.findViewById(R.id.checkMark); billIcon = billBox2.findViewById(R.id.billIcon);
            }
            else {
                billBox2 = View.inflate(MainActivity2.this, R.layout.bill_box2, null);
                tvDueDate2 = billBox2.findViewById(R.id.tvDueDate1); tvBillerName2 = billBox2.findViewById(R.id.tvBillerName1); tvAmountDue2 = billBox2.findViewById(R.id.amountDue1);
                check2 = billBox2.findViewById(R.id.checkMark1); billIcon = billBox2.findViewById(R.id.billIcon1);
            }

            View header10 = View.inflate(MainActivity2.this, R.layout.bill_box_header, null);
            TextView timeFrame11 = header10.findViewById(R.id.timeFrame), totalDue11 = header10.findViewById(R.id.totalDue);

            swipeViewListener(billBox2, late, "bill_box");

            if (!laterThisWeekHeader) {
                buildHeader(billsList, timeFrame11, totalDue11, laterThisWeekTotal, getString(R.string.laterThisWeek));
                laterThisWeekHeader = true;
                billsList.addView(header10);
            }
            billBox2.setBackgroundResource(R.drawable.border_styles_white);
            if (late.getPaymentDate() - todayDateValue == 1) {
                tvDueDate2.setText(R.string.dueTomorrow);
            } else if (late.getPaymentDate() - sunday >= 1 && late.getPaymentDate() - sunday <= 7) {
                LocalDate local = dateFormatter.convertIntDateToLocalDate(late.getPaymentDate());
                tvDueDate2.setText(String.format("%s %s", getString(R.string.due), local.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault())));
            } else {
                tvDueDate2.setText(String.format(Locale.getDefault(), "%s %s", getString(R.string.due), dateFormatter.convertIntDateToString(late.getPaymentDate())));
            }
            check2.setVisibility(View.INVISIBLE);
            billBoxValues(tvBillerName2, late, tvAmountDue2, billsList, billBox2, billIcon);
            billBox2.setLayoutParams(lp);
            billBox2.setElevation(15);
            billBox2.setOnClickListener(view -> {
                pb.setVisibility(View.VISIBLE);
                startPay(tvDueDate2, tvBillerName2, tvAmountDue2, late, todayDateValue);
            });
        }
        for (Payments late : later) {

            View billBox3;
            TextView tvDueDate3, tvBillerName3, tvAmountDue3;
            ImageView check3;
            com.google.android.material.imageview.ShapeableImageView billIcon;
            if (!previousMonth) {
                billBox3 = View.inflate(MainActivity2.this, R.layout.bill_box, null);
                tvDueDate3 = billBox3.findViewById(R.id.tvDueDate); tvBillerName3 = billBox3.findViewById(R.id.tvBillerName); tvAmountDue3 = billBox3.findViewById(R.id.amountDue);
                check3 = billBox3.findViewById(R.id.checkMark); billIcon = billBox3.findViewById(R.id.billIcon);
            }
            else {
                billBox3 = View.inflate(MainActivity2.this, R.layout.bill_box2, null);
                tvDueDate3 = billBox3.findViewById(R.id.tvDueDate1); tvBillerName3 = billBox3.findViewById(R.id.tvBillerName1); tvAmountDue3 = billBox3.findViewById(R.id.amountDue1);
                check3 = billBox3.findViewById(R.id.checkMark1); billIcon = billBox3.findViewById(R.id.billIcon1);
            }
            View header4 = View.inflate(MainActivity2.this, R.layout.bill_box_header, null);
            TextView timeFrame3 = header4.findViewById(R.id.timeFrame), totalDue3 = header4.findViewById(R.id.totalDue);

            swipeViewListener(billBox3, late, "bill_box");

            if (!laterHeader) {
                buildHeader(billsList, timeFrame3, totalDue3, laterTotal, getString(R.string.laterThisMonth));
                laterHeader = true;
                billsList.addView(header4);
            }
            billBox3.setBackgroundResource(R.drawable.border_styles_white);
            if (late.getPaymentDate() - todayDateValue == 1) {
                tvDueDate3.setText(R.string.dueTomorrow);
            } else if (late.getPaymentDate() - todayDateValue > 1 && late.getPaymentDate() - todayDateValue < 8) {
                String days = String.valueOf(late.getPaymentDate() - todayDateValue);
                tvDueDate3.setText(String.format(Locale.getDefault(), "%s %s %s", getString(R.string.dueIn), days, getString(R.string.days)));
            } else {
                tvDueDate3.setText(String.format(Locale.getDefault(), "%s %s", getString(R.string.due), dateFormatter.convertIntDateToString(late.getPaymentDate())));
            }

            check3.setVisibility(View.INVISIBLE);
            billBoxValues(tvBillerName3, late, tvAmountDue3, billsList, billBox3, billIcon);
            billBox3.setLayoutParams(lp);
            billBox3.setElevation(15);
            billBox3.setOnClickListener(view -> {
                pb.setVisibility(View.VISIBLE);
                startPay(tvDueDate3, tvBillerName3, tvAmountDue3, late, todayDateValue);
            });
        }

        for (Payments early : earlier) {

            View billBox4;
            TextView tvDueDate4, tvBillerName4, tvAmountDue4;
            com.google.android.material.imageview.ShapeableImageView billIcon;
            if (!previousMonth) {
                billBox4 = View.inflate(MainActivity2.this, R.layout.bill_box, null);
                tvDueDate4 = billBox4.findViewById(R.id.tvDueDate); tvBillerName4 = billBox4.findViewById(R.id.tvBillerName); tvAmountDue4 = billBox4.findViewById(R.id.amountDue);
                billIcon = billBox4.findViewById(R.id.billIcon);
            }
            else {
                billBox4 = View.inflate(MainActivity2.this, R.layout.bill_box2, null);
                tvDueDate4 = billBox4.findViewById(R.id.tvDueDate1); tvBillerName4 = billBox4.findViewById(R.id.tvBillerName1); tvAmountDue4 = billBox4.findViewById(R.id.amountDue1);
                billIcon = billBox4.findViewById(R.id.billIcon1);
            }

            View header2 = View.inflate(MainActivity2.this, R.layout.bill_box_header, null);
            TextView timeFrame1 = header2.findViewById(R.id.timeFrame), totalDue1 = header2.findViewById(R.id.totalDue);

            swipeViewListener(billBox4, early, "bill_box");

            if (earlier.size() > 0 && !earlyHeader) {
                buildHeader(billsList, timeFrame1, totalDue1, earlyTotal, getString(R.string.earlierThisMonth));
                earlyHeader = true;
                billsList.addView(header2);
            }
            billBox4.setBackgroundResource(R.drawable.border_styles_green);
            tvDueDate4.setText(String.format(Locale.getDefault(), "%s %s", getString(R.string.paid), dateFormatter.convertIntDateToString(early.getDatePaid())));
            billBoxValues(tvBillerName4, early, tvAmountDue4, billsList, billBox4, billIcon);
            billBox4.setLayoutParams(lp);
            billBox4.setElevation(10);
            billBox4.setOnClickListener(view -> {
                pb.setVisibility(View.VISIBLE);
                startPay(tvDueDate4, tvBillerName4, tvAmountDue4, early, todayDateValue);
            });
        }
    }

    private void swipeViewListener (View view, Payments payment, String type) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        view.setClipToOutline(true);
        boolean paid = false;
        SwipeActionView swipeView;
        TextView paidBox;
        if (type.equals("bill_box")) {
            if (!previousMonth) {
                swipeView = view.findViewById(R.id.paidSwipeView);
                paidBox = view.findViewById(R.id.markAsPaidBox);
            }
            else {
                swipeView = view.findViewById(R.id.paidSwipeView1);
                paidBox = view.findViewById(R.id.markAsPaidBox1);
            }
            if (payment.isPaid()) {
                paid = true;
                paidBox.setText(R.string.unmarkAsPaid);
                paidBox.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
            }
            else {
                paidBox.setText(R.string.markAsPaid);
                paidBox.setBackgroundColor(getResources().getColor(R.color.payBill, getTheme()));
            }
        }
        else if (type.equals("bill_box_later")) {
            if (!previousMonth) {
                swipeView = view.findViewById(R.id.billBox42);
                paidBox = view.findViewById(R.id.markAsPaidBoxLater);
            }
            else {
                swipeView = view.findViewById(R.id.billBox421);
                paidBox = view.findViewById(R.id.markAsPaidBoxLater1);
            }
            if (payment.isPaid()) {
                paid = true;
                paidBox.setText(R.string.unmarkAsPaid);
                paidBox.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
            }
            else {
                paidBox.setText(R.string.markAsPaid);
                paidBox.setBackgroundColor(getResources().getColor(R.color.payBill, getTheme()));
            }
        }
        else {
            if (!previousMonth) {
                swipeView = view.findViewById(R.id.billBox41);
                paidBox = view.findViewById(R.id.markAsPaidBoxToday);
            }
            else {
                swipeView = view.findViewById(R.id.billBox411);
                paidBox = view.findViewById(R.id.markAsPaidBoxToday1);
            }
            if (payment.isPaid()) {
                paid = true;
                paidBox.setText(R.string.unmarkAsPaid);
                paidBox.setBackgroundColor(getResources().getColor(R.color.red, getTheme()));
            }
            else {
                paidBox.setText(R.string.markAsPaid);
                paidBox.setBackgroundColor(getResources().getColor(R.color.payBill, getTheme()));
            }
        }

        boolean finalPaid = paid;
        swipeView.setSwipeGestureListener(new SwipeGestureListener() {
            @Override
            public boolean onSwipedLeft(@NonNull SwipeActionView swipeActionView) {
                for (Bills bill : billList) {
                    if (bill.getBillerName().equals(payment.getBillerName())) {
                        Intent history = new Intent(MainActivity2.this, PaymentHistory.class);
                        history.putExtra("User Id", thisUser.getid());
                        history.putExtra("Bill Id", bill.getBillsId());
                        startActivity(history);
                    }
                }
                return true;
            }

            @Override
            public boolean onSwipedRight(@NonNull SwipeActionView swipeActionView) {
                if (!finalPaid) {
                    for (Bills bill: thisUser.getBills()) {
                        for (Payments payments: bill.getPayments()) {
                            if (payments.getPaymentId() == payment.getPaymentId()) {
                                payments.setPaid(true);
                                payments.setDatePaid(dateFormatter.currentDateAsInt());
                                bill.setDateLastPaid(dateFormatter.currentDateAsInt());
                                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                nm.cancel(payment.getPaymentId());
                                nm.cancel(payment.getPaymentId() + 1);
                                nm.cancel(payment.getPaymentId() + 11);
                            }
                        }
                    }
                    dueThisMonth = whatsDueThisMonth(dueThisMonth, selectedDate);
                    listBills(billsList1);
                    popupMessage.setText(String.format(Locale.getDefault(), "%s %s %s", getString(R.string.paymentFor), payment.getBillerName(), getString(R.string.markedAsPaid)));
                    paymentConfirm.setVisibility(View.VISIBLE);
                    paymentConfirm.postDelayed(() -> {
                        paymentConfirm.setVisibility(View.GONE);
                        db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                        if (thisUser != null) {
                            SaveUserData save = new SaveUserData();
                            save.saveUserData(MainActivity2.this, thisUser);
                        }
                    }, 5000);
                    paymentConfirm.setOnClickListener(view12 -> {
                        for (Bills bill: thisUser.getBills()) {
                            for (Payments payments: bill.getPayments()) {
                                if (payments.getPaymentId() == payment.getPaymentId()) {
                                    payments.setPaid(false);
                                    payments.setDatePaid(0);
                                    int highest = 0;
                                    for (Payments pay: bill.getPayments()) {
                                        if (pay.isPaid() && pay.getDatePaid() > highest) {
                                            highest = pay.getDatePaid();
                                        }
                                    }
                                    bill.setDateLastPaid(highest);
                                    db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                                    if (thisUser != null) {
                                        SaveUserData save = new SaveUserData();
                                        save.saveUserData(MainActivity2.this, thisUser);
                                    }
                                }
                            }
                        }
                        dueThisMonth = whatsDueThisMonth(dueThisMonth, selectedDate);
                        listBills(billsList1);
                        paymentConfirm.setVisibility(View.GONE);
                    });
                }
                else {
                    int datePaid = payment.getDatePaid();
                    for (Bills bill: thisUser.getBills()) {
                        for (Payments payments: bill.getPayments()) {
                            if (payments.getPaymentId() == payment.getPaymentId()) {
                                payments.setPaid(false);
                                payments.setDatePaid(0);
                                int highest = 0;
                                for (Payments pay: bill.getPayments()) {
                                    if (pay.isPaid() && pay.getDatePaid() > highest) {
                                        highest = pay.getDatePaid();
                                    }
                                }
                                bill.setDateLastPaid(highest);
                            }
                        }
                    }
                    dueThisMonth = whatsDueThisMonth(dueThisMonth, selectedDate);
                    listBills(billsList1);
                    popupMessage.setText(String.format(Locale.getDefault(), "%s %s %s", getString(R.string.paymentFor), payment.getBillerName(), getString(R.string.unmarkedAsPaid)));
                    paymentConfirm.setVisibility(View.VISIBLE);
                    paymentConfirm.postDelayed(() -> {
                        paymentConfirm.setVisibility(View.GONE);
                        db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                        if (thisUser != null) {
                            SaveUserData save = new SaveUserData();
                            save.saveUserData(MainActivity2.this, thisUser);
                        }
                    }, 5000);
                    paymentConfirm.setOnClickListener(view1 -> {
                        for (Bills bill: thisUser.getBills()) {
                            for (Payments payments: bill.getPayments()) {
                                if (payments.getPaymentId() == payment.getPaymentId()) {
                                    payments.setPaid(true);
                                    payments.setDatePaid(datePaid);
                                    bill.setDateLastPaid(datePaid);
                                    FirebaseFirestore db1 = FirebaseFirestore.getInstance();
                                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    nm.cancel(payment.getPaymentId());
                                    nm.cancel(payment.getPaymentId() + 1);
                                    nm.cancel(payment.getPaymentId() + 11);
                                    db1.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                                    if (thisUser != null) {
                                        SaveUserData save = new SaveUserData();
                                        save.saveUserData(MainActivity2.this, thisUser);
                                    }
                                }
                            }
                        }
                        dueThisMonth = whatsDueThisMonth(dueThisMonth, selectedDate);
                        listBills(billsList1);
                        paymentConfirm.setVisibility(View.GONE);
                    });
                }
                return true;
            }

            @Override
            public void onSwipeLeftComplete(@NonNull SwipeActionView swipeActionView) {

            }

            @Override
            public void onSwipeRightComplete(@NonNull SwipeActionView swipeActionView) {

            }
        });
    }

    private void buildHeader(LinearLayout billsList, TextView timeFrame2, TextView totalDue2, double todayTotal, String timePeriod) {

        TextView divider = new TextView(mContext);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        divider.setLayoutParams(layout);
        divider.setHeight(40);
        billsList.addView(divider);
        timeFrame2.setText(timePeriod);
        timeFrame2.setTextColor(getResources().getColor(R.color.blackAndWhite, getTheme()));
        totalDue2.setText(fn.addSymbol(String.valueOf(todayTotal)));
        totalDue2.setTextColor(getResources().getColor(R.color.blackAndWhite, getTheme()));
    }

    private void billBoxValues(TextView billerName, Payments payment, TextView amountDue, LinearLayout billsList, View billBox, com.google.android.material.imageview.ShapeableImageView icon) {

        for (Bills bill: thisUser.getBills()) {
            if (bill.getBillerName().equals(payment.getBillerName())) {
                LoadIcon loadIcon = new LoadIcon();
                loadIcon.loadIcon(MainActivity2.this, icon, bill.getCategory(), bill.getIcon());
                break;
            }
        }
        billerName.setText(payment.getBillerName());
        amountDue.setText(fn.addSymbol(payment.getPaymentAmount()));
        billBox.setDuplicateParentStateEnabled(true);
        billsList.addView(billBox);
        TextView divider = new TextView(mContext);
        divider.setHeight(10);
        billsList.addView(divider);
        billBox.animate().translationX(0).setDuration(500);
    }

    private void startPay(TextView tvDueDate1, TextView tvBillerName1, TextView tvAmountDue1, Payments td, int todayDateValue) {
        StringFixer sf = new StringFixer();
        Intent pay = new Intent(mContext, PayBill.class);
        pay.putExtra("Due Date", tvDueDate1.getText().toString());
        pay.putExtra("Biller Name", tvBillerName1.getText().toString());
        pay.putExtra("Amount Due", sf.numbersOnly(tvAmountDue1.getText().toString()));
        pay.putExtra("Is Paid", td.isPaid());
        pay.putExtra("Payment Id", td.getPaymentId());
        pay.putExtra("Current Date", todayDateValue);
        startActivity(pay);
    }

    public ArrayList<Payments> whatsDueThisMonth(ArrayList<Payments> dueThisMonth, LocalDate selectedDate) {

        dueThisMonth.clear();
        TextView tvTotal = findViewById(R.id.tvTotal), tvRemaining = findViewById(R.id.tvRemaining);
        int todaysDate = dateFormatter.currentDateAsInt();
        double total = 0, remaining = 0;
        LocalDate todayLocalDate = dateFormatter.convertIntDateToLocalDate(dateFormatter.currentDateAsInt());
        int currentMonthEnd = dateFormatter.calcDateValue(todayLocalDate.withDayOfMonth(todayLocalDate.getMonth().length(todayLocalDate.isLeapYear())));
        int counter = 0, monthStart = dateFormatter.calcDateValue(selectedDate.withDayOfMonth(1)),
                monthEnd = dateFormatter.calcDateValue(selectedDate.withDayOfMonth(selectedDate.getMonth().length(selectedDate.isLeapYear())));
        ArrayList<Bills> bills = thisUser.getBills();
        for (Bills bill : bills) {
            ArrayList<Payments> payments = bill.getPayments();
            if (bill.getPayments() == null) {
                payments = new ArrayList<>();
                bill.setPayments(payments);
            }
            payments.sort(new PaymentsComparator());

            if (payments.size() > 0) {
                for (Payments payment : payments) {
                    if (payment.getPaymentDate() >= monthStart && payment.getPaymentDate() <= monthEnd && !payment.isPaid() && todaysDate < monthEnd || payment.getDatePaid() >= monthStart &&
                            payment.getDatePaid() <= monthEnd || payment.getPaymentDate() >= monthStart && payment.getPaymentDate() <= monthEnd && !dueThisMonth.contains(payment) && todaysDate < monthEnd ||
                            !payment.isPaid() && payment.getPaymentDate() < currentMonthEnd && selectedDate.getMonth().equals(dateFormatter.convertIntDateToLocalDate(todaysDate).getMonth())) {
                        if (!dueThisMonth.contains(payment)) {
                            dueThisMonth.add(payment);
                        }
                        ++counter;
                        if (!payment.isPaid()) {
                            remaining = remaining + Double.parseDouble(fn.makeDouble(payment.getPaymentAmount().replaceAll(",", ".").replaceAll(" ", "").replaceAll("\\s", "")));
                        }
                        total = total + Double.parseDouble(fn.makeDouble(payment.getPaymentAmount().replaceAll(",", ".").replaceAll(" ", "").replaceAll("\\s", "")));
                    }
                }
            }
        }
        if (counter == 0) {
            noResults.setVisibility(View.VISIBLE);
        }
        String fixTotal = (getString(R.string.total) + " " + fn.addSymbol(String.valueOf(total))), fixRemaining = (getString(R.string.remaining) + " " + fn.addSymbol(String.valueOf(remaining)));
        tvTotal.setText(fixTotal);
        ProgressBar pb8 = findViewById(R.id.progressBar8);
        int progress;
        if (remaining == 0) {
            progress = 100;
        } else {
            progress = 100 - ((int) (remaining) * 100) / ((int) total);
        }
        pb8.post(() -> {
            ObjectAnimator animation = ObjectAnimator.ofInt(pb8, "progress", progress);
            animation.setDuration(500);
            animation.setInterpolator(new AccelerateDecelerateInterpolator());
            animation.start();
        });
        tvRemaining.setText(fixRemaining);
        return dueThisMonth;
    }

    private void createNotificationChannel() {

        CharSequence name = (getString(R.string.billTracker));
        String description = (getString(R.string.billTrackerNotificationChannel));
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        channelId = thisUser.getid();
        NotificationChannel channel = new NotificationChannel(channelId, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    public void scheduleNotifications (Payments payment) {

        DateFormatter df = new DateFormatter();

        long currentDate = df.currentDateLong();
        long today = df.convertIntDateToLong(payment.getPaymentDate(), 8, 0);
        long tomorrow = df.convertIntDateToLong(payment.getPaymentDate() - 1, 8, 0);
        long threeDay = df.convertIntDateToLong(payment.getPaymentDate() - 3, 8, 0);
        String amount = fn.addSymbol(payment.getPaymentAmount());
        String billerName = payment.getBillerName();
        int paymentId = payment.getPaymentId();

        if (today >= currentDate) {
            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("title", getString(R.string.billDue));
            intent.putExtra("message", getString(R.string.yourBillFor) + " " + amount + " " + getString(R.string.at) + " " + billerName + " " + getString(R.string.isDueToday));
            intent.putExtra("channel id", channelId);
            intent.putExtra("notification id", paymentId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, paymentId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            intent.putExtra("pi", pendingIntent);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, today, pendingIntent);
        }

        if (tomorrow >= currentDate + 1) {
            Intent intent1 = new Intent(this, NotificationReceiver.class);
            intent1.putExtra("title", getString(R.string.billDue));
            intent1.putExtra("message", getString(R.string.yourBillFor) + " " + amount + " " + getString(R.string.at) + " " + billerName + " " + getString(R.string.isDueTomorrow));
            intent1.putExtra("channel id", channelId);
            intent1.putExtra("notification id", paymentId + 1);
            PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this, paymentId + 1, intent1, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            intent1.putExtra("pi", pendingIntent1);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, tomorrow, pendingIntent1);
        }

        if (threeDay >= currentDate + 3) {
            Intent intent2 = new Intent(this, NotificationReceiver.class);
            intent2.putExtra("title", getString(R.string.billDue));
            intent2.putExtra("message", getString(R.string.yourBillFor) + " " + amount + " " + getString(R.string.at) + " " + billerName + " " + getString(R.string.isDueInThreeDays));
            intent2.putExtra("channel id", channelId);
            intent2.putExtra("notification id", paymentId + 2);
            PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this, paymentId + 2, intent2, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            intent2.putExtra("pi", pendingIntent2);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, threeDay, pendingIntent2);
        }
    }

    public void refreshUser() {

        SaveUserData save = new SaveUserData();
        save.loadUserData(MainActivity2.this);
    }

    public void logout(View view) {

        Context mContext = this;
        GoogleSignIn.getClient(MainActivity2.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
        SharedPreferences sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("Stay Signed In", false);
        editor.putString("Username", "");
        editor.putString("Password", "");
        editor.apply();
        Intent validate = new Intent(mContext, Logon.class);
        validate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        validate.putExtra("Welcome", true);
        recreate();
        startActivity(validate);
    }

    @Override
    protected void onResume() {
        super.onResume();

        navDrawer.setVisibility(View.GONE);
        hideForNavDrawer.setVisibility(View.VISIBLE);
        LinearLayout pb = findViewById(R.id.progressBar9);
        pb.setVisibility(View.GONE);
        SaveUserData save = new SaveUserData();
        if (thisUser == null) {
            save.loadUserData(MainActivity2.this);
        }
        getValues();
        getCurrentMonth();
        counter = 0;
        refreshUser();
        dueThisMonth = whatsDueThisMonth(dueThisMonth, selectedDate);
        listBills(billsList1);
    }

}