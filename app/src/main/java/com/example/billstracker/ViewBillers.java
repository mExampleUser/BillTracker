package com.example.billstracker;

import static com.example.billstracker.Logon.thisUser;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class ViewBillers extends AppCompatActivity {

    ArrayList<Bills> listBills = new ArrayList<>();
    Context mContext;
    boolean darkMode;
    DateFormatter df = new DateFormatter();
    TextView myStats, navHome, navPaymentHistory,displayUserName, displayEmail, ticketCounter, logout, header, subHeader;
    ImageView settingsButton, drawerToggle, help;
    LinearLayout navDrawer, hideNav, pb;
    FixNumber fn = new FixNumber();
    com.google.android.gms.ads.AdView adview;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_billers);

        mContext = this;
        darkMode = false;

        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            darkMode = true;
        }

        pb = findViewById(R.id.pb14);
        help = findViewById(R.id.help1);
        adview = findViewById(R.id.adView3);
        myStats = findViewById(R.id.myStats1);
        navHome = findViewById(R.id.navHome1);
        logout = findViewById(R.id.logoutButton1);
        header = findViewById(R.id.viewBillersHeader);
        hideNav = findViewById(R.id.hideNavDrawer1);
        subHeader = findViewById(R.id.textView43);
        navDrawer = findViewById(R.id.navDrawer1);
        displayEmail = findViewById(R.id.tvUserName1);
        drawerToggle = findViewById(R.id.drawerToggle1);
        ticketCounter = findViewById(R.id.ticketCounter1);
        settingsButton = findViewById(R.id.settingsButton1);
        displayUserName = findViewById(R.id.tvName1);
        navPaymentHistory = findViewById(R.id.navPaymentHistory1);

        MobileAds.initialize(this, initializationStatus -> {
        });

        AdRequest adRequest = new AdRequest.Builder().build();
        adview.loadAd(adRequest);

        if (thisUser == null) {
            SaveUserData load = new SaveUserData();
            load.loadUserData(ViewBillers.this);
        }

        displayUserName.setText(thisUser.getName());
        displayEmail.setText(thisUser.getUserName());

        CountTickets countTickets = new CountTickets();
        countTickets.countTickets(ticketCounter);

        help.setOnClickListener(view -> {
            Intent support = new Intent (ViewBillers.this, Support.class);
            pb.setVisibility(View.VISIBLE);
            startActivity(support);
        });

        myStats.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent stats = new Intent(ViewBillers.this, MyStats.class);
            startActivity(stats);
        });

        logout.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            GoogleSignIn.getClient(ViewBillers.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
            SharedPreferences sp = ViewBillers.this.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("Stay Signed In", false);
            editor.putString("Username", "");
            editor.putString("Password", "");
            editor.apply();
            Intent validate = new Intent(ViewBillers.this, Logon.class);
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
                navDrawer.setElevation(7);
                hideNav.setOnClickListener(view1 -> navDrawer.setVisibility(View.GONE));
            }
        });

        navHome.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent home = new Intent(mContext, MainActivity2.class);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(home);
        });

        navPaymentHistory.setOnClickListener(view -> {
            Intent payments = new Intent(mContext, PaymentHistory.class);
            pb.setVisibility(View.VISIBLE);
            startActivity(payments);
        });

        navDrawer.setOnTouchListener(new OnSwipeTouchListener(ViewBillers.this) {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                navDrawer.setVisibility(View.GONE);
            }
        });

        navDrawer.setVisibility(View.GONE);
        listBills();
    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
        listBills();
    }

    public void listBills() {

        Context mContext = this;
        LinearLayout linearLayout = findViewById(R.id.llViewBillers);
        LinearLayout text = findViewById(R.id.noBillers);
        linearLayout.invalidate();
        linearLayout.removeAllViews();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ArrayList <String> spinnerArray1 = new ArrayList<>(Arrays.asList(getString(R.string.autoLoan), getString(R.string.creditCard), getString(R.string.entertainment),
                getString(R.string.insurance), getString(R.string.miscellaneous), getString(R.string.mortgage), getString(R.string.personalLoans), getString(R.string.utilities)));
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (thisUser.getBills() == null) {
            thisUser.setBills(new ArrayList<>());
        } else {
            listBills = thisUser.getBills();
        }
        String userId = thisUser.getid();
        int counter = 0;
        if (listBills.size() > 0) {
            listBills.sort(Comparator.comparing(Bills::getBillerName));
            for (Bills bills : listBills) {
                counter = counter + 1;
                if (counter <= 50) {
                    String recurring;
                    String billId = bills.getBillsId();
                    String dueDate;
                    if (bills.isRecurring()) {
                        recurring = "Yes";
                        dueDate = calcNextDateDue(bills.getDayDue(), bills.getFrequency());
                    } else {
                        recurring = "No";
                        dueDate = df.convertIntDateToString(bills.getDayDue());
                    }
                    View viewBiller = View.inflate(ViewBillers.this, R.layout.view_biller, null);
                    ImageView billerArrow = viewBiller.findViewById(R.id.billerArrow);
                    TextView viewBillerName = viewBiller.findViewById(R.id.viewBillerName);
                    LinearLayout billerDetails = viewBiller.findViewById(R.id.billerDetails);
                    String lastPaid;
                    String fixDate = dueDate.replaceFirst("/", " ");
                    String finalDate = fixDate.replaceAll("/", ", ");
                    if (bills.getDateLastPaid() == 0) {
                        lastPaid = "No payments reported";
                    } else {
                        lastPaid = df.convertIntDateToString(bills.getDateLastPaid());
                    }
                    TextView category = viewBiller.findViewById(R.id.showCategory);
                    TextView showBillerName = viewBiller.findViewById(R.id.showBillerName);
                    TextView showAmountDue = viewBiller.findViewById(R.id.showAmountDue);
                    TextView showNextDueDate = viewBiller.findViewById(R.id.showNextDueDate);
                    TextView showDateLastPaid = viewBiller.findViewById(R.id.showDateLastPaid);
                    TextView showRecurring = viewBiller.findViewById(R.id.showRecurring);
                    TextView frequency = viewBiller.findViewById(R.id.showFrequency);
                    TextView showWebsite = viewBiller.findViewById(R.id.showWebsite);
                    Button btnVisitWebsite = viewBiller.findViewById(R.id.btnVisitWebsite);
                    Button btnEditBiller = viewBiller.findViewById(R.id.btnEditBiller);
                    Button btnViewPaymentHistory = viewBiller.findViewById(R.id.btnPaymentHistory);
                    Button btnDeleteBiller = viewBiller.findViewById(R.id.btnDeleteBiller);
                    com.google.android.material.imageview.ShapeableImageView iconView = viewBiller.findViewById(R.id.iconView);
                    viewBillerName.setText(bills.getBillerName());
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(20,20,20,20);
                    viewBiller.setLayoutParams(lp);
                    linearLayout.addView(viewBiller);
                    final boolean[] showing = {false};
                    viewBiller.setOnClickListener(view1 -> {
                        if (showing[0]) {
                            billerArrow.setRotation(billerArrow.getRotation() - 90);
                            billerDetails.setVisibility(View.GONE);
                            showing[0] = false;
                        } else {
                            billerArrow.setRotation(billerArrow.getRotation() + 90);
                            LoadIcon loadIcon = new LoadIcon();
                            loadIcon.loadIcon(ViewBillers.this, iconView, bills.getCategory(), bills.getIcon());
                            showBillerName.setVisibility(View.GONE);
                            String cat = bills.getCategory();
                            switch (cat) {
                                case "0":
                                    category.setText(getString(R.string.autoLoan));
                                    break;
                                case "1":
                                    category.setText(getString(R.string.creditCard));
                                    break;
                                case "2":
                                    category.setText(getString(R.string.entertainment));
                                    break;
                                case "3":
                                    category.setText(getString(R.string.insurance));
                                    break;
                                case "4":
                                    category.setText(getString(R.string.miscellaneous));
                                    break;
                                case "5":
                                    category.setText(getString(R.string.mortgage));
                                    break;
                                case "6":
                                    category.setText(getString(R.string.personalLoans));
                                    break;
                                case "7":
                                    category.setText(getString(R.string.utilities));
                                    break;
                            }
                            showBillerName.setText(bills.getBillerName());
                            showAmountDue.setText(fn.addSymbol(bills.getAmountDue()));
                            showNextDueDate.setText(finalDate);
                            showDateLastPaid.setText(lastPaid);
                            showRecurring.setText(recurring);
                            String freq = bills.getFrequency();
                            switch (freq) {
                                case "0":
                                    frequency.setText(getString(R.string.daily));
                                    break;
                                case "1":
                                    frequency.setText(getString(R.string.weekly));
                                    break;
                                case "2":
                                    frequency.setText(getString(R.string.biweekly));
                                    break;
                                case "3":
                                    frequency.setText(getString(R.string.monthly));
                                    break;
                                case "4":
                                    frequency.setText(getString(R.string.quarterly));
                                    break;
                                case "5":
                                    frequency.setText(getString(R.string.yearly));
                                    break;
                            }
                            showWebsite.setText(HtmlCompat.fromHtml(bills.getWebsite(), HtmlCompat.FROM_HTML_MODE_LEGACY));
                            billerDetails.setVisibility(View.VISIBLE);
                            showing[0] = true;
                            btnEditBiller.setOnClickListener(view11 -> {
                                pb.setVisibility(View.VISIBLE);
                                Intent edit1 = new Intent(ViewBillers.this, EditBiller.class);
                                edit1.putExtra("userName", bills.getBillerName());
                                edit1.putExtra("website", bills.getWebsite());
                                edit1.putExtra("dueDate", bills.getDayDue());
                                edit1.putExtra("amountDue", bills.getAmountDue());
                                edit1.putExtra("frequency", bills.getFrequency());
                                edit1.putExtra("recurring", bills.isRecurring());
                                edit1.putExtra("Payment Id", 1);
                                edit1.putExtra("Paid", false);
                                startActivity(edit1);
                            });
                            btnVisitWebsite.setOnClickListener(view112 -> {
                                pb.setVisibility(View.VISIBLE);
                                String address = bills.getWebsite();
                                if (!address.startsWith("http://") && !address.startsWith("https://")) {
                                    address = "http://" + address;
                                }
                                Intent launch = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
                                startActivity(launch);
                            });
                            btnViewPaymentHistory.setOnClickListener(view113 -> {
                                pb.setVisibility(View.VISIBLE);
                                Intent history = new Intent(mContext, PaymentHistory.class);
                                history.putExtra("User Id", userId);
                                history.putExtra("Bill Id", billId);
                                startActivity(history);
                            });
                            btnDeleteBiller.setOnClickListener(view114 -> {
                                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                                builder.setMessage(getString(R.string.confirmDeletion)).setTitle(
                                        getString(R.string.deleteBiller)).setPositiveButton(getString(R.string.deleteBiller), (dialogInterface, i) -> {
                                    pb.setVisibility(View.VISIBLE);
                                    listBills.remove(bills);
                                    thisUser.setBills(listBills);
                                    db.collection("users").document(thisUser.getUserName()).update("bills", listBills);
                                    if (thisUser != null) {
                                        SaveUserData save = new SaveUserData();
                                        save.saveUserData(ViewBillers.this, thisUser);
                                    }
                                    Toast.makeText(mContext, getString(R.string.billerWasDeletedSuccessfully), Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, ViewBillers.class);
                                    startActivity(intent);
                                }).setNegativeButton(getString(R.string.dontDelete), (dialogInterface, i) -> {

                                });

                                androidx.appcompat.app.AlertDialog alert = builder.create();
                                alert.show();
                            });
                        }
                    });
                }
            }
            TextView spacer = new TextView(ViewBillers.this);
            spacer.setHeight(200);
            linearLayout.addView(spacer);
            linearLayout.animate().translationY(0).setDuration(500);
            header.animate().translationX(0).setDuration(500);
            subHeader.animate().translationX(0).setDuration(500);
        } else {
            linearLayout.addView(text);
            linearLayout.animate().translationY(0).setDuration(500);
            header.animate().translationX(0).setDuration(500);
            subHeader.animate().translationX(0).setDuration(500);
            text.setOnClickListener(view -> {
                Intent addBiller1 = new Intent(mContext, AddBiller.class);
                startActivity(addBiller1);
            });
        }
    }


    public String calcNextDateDue(int date, String frequency) {

        int currentDate = df.currentDateAsInt();
        if (Objects.equals(frequency, "0")) {
            while (date < currentDate) {
                date = date + 1;
            }
        } else if (Objects.equals(frequency, "1")) {
            while (date < currentDate) {
                date = date + 7;
            }
        } else if (Objects.equals(frequency, "2")) {
            while (date < currentDate) {
                date = date + 14;
            }
        } else if (Objects.equals(frequency, "3")) {
            LocalDate today = df.convertIntDateToLocalDate(df.currentDateAsInt());
            LocalDate date1 = df.convertIntDateToLocalDate(date);
            while (date1.isBefore(today)) {
                date1 = date1.plusMonths(1);
            }
            date = df.calcDateValue(date1);
        } else if (Objects.equals(frequency, "4")) {
            LocalDate today = df.convertIntDateToLocalDate(df.currentDateAsInt());
            LocalDate date1 = df.convertIntDateToLocalDate(date);
            while (date1.isBefore(today)) {
                date1 = date1.plusMonths(3);
            }
            date = df.calcDateValue(date1);
        } else if (Objects.equals(frequency, "5")) {
            LocalDate today = df.convertIntDateToLocalDate(df.currentDateAsInt());
            LocalDate date1 = df.convertIntDateToLocalDate(date);
            while (date1.isBefore(today)) {
                date1 = date1.plusYears(1);
            }
            date = df.calcDateValue(date1);
        }

        return df.convertIntDateToString(date);
    }

    @Override
    protected void onResume() {
        super.onResume();
        navDrawer.setVisibility(View.GONE);
        pb.setVisibility(View.GONE);
    }
}