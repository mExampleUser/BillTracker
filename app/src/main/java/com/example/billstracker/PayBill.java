package com.example.billstracker;

import static com.example.billstracker.Logon.thisUser;
import static com.example.billstracker.MainActivity2.channelId;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class PayBill extends AppCompatActivity {

    Dialog dialog;
    TextView paymentDueDate, paymentAmountDue, editBiller, viewPayments, dueDateLabel, amountDueLabel, webAddress, tvBillerName, datePaidLabel, paymentDatePaid;
    ImageView paidIcon;
    com.google.android.material.imageview.ShapeableImageView payBillIcon;
    ConstraintLayout payBill;
    Button payButton;
    int paymentId;
    Payments pay;
    Bills bil;
    Context mContext = this;
    LinearLayout datePaidLayout, back, pb;
    DateFormatter df;
    String datePaid, billerName;
    boolean darkMode;
    FixNumber fn = new FixNumber();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill);
        back = findViewById(R.id.backPayBillLayout);
        payBillIcon = findViewById(R.id.payBillIcon);
        payBill = findViewById(R.id.payBill);
        tvBillerName = findViewById(R.id.billerName);
        payButton = findViewById(R.id.payButton);
        paymentAmountDue = findViewById(R.id.paymentAmountDue);
        editBiller = findViewById(R.id.editBillerButton);
        viewPayments = findViewById(R.id.viewPayments);
        dueDateLabel = findViewById(R.id.dueDateLabel);
        paidIcon = findViewById(R.id.paidIcon);
        pb = findViewById(R.id.pb10);
        amountDueLabel = findViewById(R.id.amountDueLabel);
        webAddress = findViewById(R.id.webAddress);
        paymentDueDate = findViewById(R.id.paymentDueDate1);
        datePaidLabel = findViewById(R.id.datePaidLabel);
        paymentDatePaid = findViewById(R.id.paymentDatePaid);
        datePaidLayout = findViewById(R.id.datePaidLayout);
        df = new DateFormatter();
        darkMode = false;
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            darkMode = true;
        }
        back.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            onBackPressed();
        });
        startPayBill();
    }

    public void startPayBill() {

        Bundle bundle = getIntent().getExtras();
        String dueDate = bundle.getString("Due Date", "");
        billerName = bundle.getString("Biller Name", "");
        boolean isPaid = bundle.getBoolean("Is Paid", false);
        paymentId = bundle.getInt("Payment Id");
        int todaysDate = df.currentDateAsInt();

        if (thisUser == null) {
            SaveUserData load = new SaveUserData();
            load.loadUserData(PayBill.this);
        }
        ArrayList<Bills> billList = thisUser.getBills();
        String website = "www.google.com";

        for (Bills bill : thisUser.getBills()) {
            if (bill.getBillerName().equals(billerName)) {
                bil = bill;
                for (Payments payment : bill.getPayments()) {
                    if (payment.getPaymentId() == paymentId) {
                        paymentDueDate.setText(df.convertIntDateToString(payment.getPaymentDate()));
                        pay = payment;
                        if (payment.isPaid()) {
                            datePaidLayout.setVisibility(View.VISIBLE);
                            paymentDatePaid.setText(df.convertIntDateToString(payment.getDatePaid()));
                            payButton.setText(R.string.unmarkAsPaid);
                        } else {
                            datePaidLayout.setVisibility(View.GONE);
                            payButton.setText(R.string.markAsPaid);
                            paidIcon.setImageDrawable(null);
                        }
                    }

                }
                website = bill.getWebsite();
            }
        }
        LoadIcon loadIcon = new LoadIcon();
        loadIcon.loadIcon(PayBill.this, payBillIcon, bil.getCategory(), bil.getIcon());
        tvBillerName.setText(pay.getBillerName());
        paymentAmountDue.setText(fn.addSymbol(pay.getPaymentAmount()));

        String finalWebsite = website;
        webAddress.setOnClickListener(view -> {
            String address = finalWebsite;
            if (!address.startsWith("http://") && !address.startsWith("https://")) {
                address = "http://" + address;
            }
            Intent launch = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
            startActivity(launch);
        });

        paymentDatePaid.setOnClickListener(view -> getDateFromUser(paymentDatePaid, false));

        paymentAmountDue.setOnClickListener(view -> {
            androidx.appcompat.app.AlertDialog.Builder alert = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            alert.setTitle(getString(R.string.changeAmount));
            alert.setMessage(getString(R.string.pleaseEnterYourPaymentAmount));
            LinearLayout ll = new LinearLayout(PayBill.this);
            final EditText input = new EditText(PayBill.this);
            input.setText(String.format(Locale.getDefault(),"  %s", fn.addSymbol(pay.getPaymentAmount())));
            input.setBackground(AppCompatResources.getDrawable(this, R.drawable.edit_text));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(50,50,50,50);
            ll.setLayoutParams(lp);
            ll.setPadding(30,0,0,0);
            input.setLayoutParams(lp);
            ll.addView(input);
            input.setFilters(new InputFilter[] {new InputFilter.LengthFilter(12)});
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            input.addTextChangedListener(new MoneyTextWatcher(input));
            alert.setView(ll);
            alert.setPositiveButton(getString(R.string.update), (dialogInterface, i) -> {
                AlertDialog.Builder alert1 = new AlertDialog.Builder(mContext);
                alert1.setTitle(getString(R.string.updateAllFutureBills));
                alert1.setMessage(getString(R.string.updatePaymentAmountForAll));
                String newAmountDue = fn.makeDouble(input.getText().toString());
                if (newAmountDue.equals("")) {
                    newAmountDue = "0.00";
                }
                String finalNewAmountDue = newAmountDue;
                alert1.setPositiveButton(getString(R.string.updateAll), (dialogInterface1, i12) -> {
                    pb.setVisibility(View.VISIBLE);

                    for (Bills bill : thisUser.getBills()) {
                        if (bill.getBillerName().equals(tvBillerName.getText().toString())) {
                            bill.setAmountDue(finalNewAmountDue);
                            for (Payments payment : bill.getPayments()) {
                                if (!payment.isPaid()) {
                                    payment.setPaymentAmount(finalNewAmountDue);
                                }
                            }
                        }
                    }
                    paymentAmountDue.setText(fn.addSymbol(finalNewAmountDue));
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    if (thisUser != null) {
                        SaveUserData save = new SaveUserData();
                        save.saveUserData(PayBill.this, thisUser);
                    }
                    db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                    pb.setVisibility(View.GONE);
                });
                alert1.setNeutralButton(getString(R.string.justThisOccurence), (dialogInterface12, i13) -> {
                    pb.setVisibility(View.VISIBLE);
                    for (Bills bill : thisUser.getBills()) {
                        if (bill.getBillerName().equals(tvBillerName.getText().toString())) {
                            for (Payments payment : bill.getPayments()) {
                                if (payment.getPaymentId() == (paymentId)) {
                                    payment.setPaymentAmount(finalNewAmountDue);
                                }
                            }
                        }
                    }
                    paymentAmountDue.setText(fn.addSymbol(finalNewAmountDue));
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    if (thisUser != null) {
                        SaveUserData save = new SaveUserData();
                        save.saveUserData(PayBill.this, thisUser);
                    }
                    db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                    pb.setVisibility(View.GONE);
                });
                alert1.setNegativeButton(getString(R.string.cancel), (dialogInterface13, i14) -> startPay(dueDate, billerName, fn.makeDouble(bil.getAmountDue()), isPaid, todaysDate));
                alert1.create();
                alert1.show();
            });
            alert.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {

            });
                    androidx.appcompat.app.AlertDialog builder = alert.create();
            builder.show();
        });

        editBiller.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);

            Intent edit = new Intent(mContext, EditBiller.class);
            edit.putExtra("userName", bil.getBillerName());
            edit.putExtra("website", bil.getWebsite());
            edit.putExtra("dueDate", pay.getPaymentDate());
            edit.putExtra("amountDue", pay.getPaymentAmount());
            edit.putExtra("frequency", bil.getFrequency());
            edit.putExtra("recurring", bil.isRecurring());
            edit.putExtra("Payment Id", paymentId);
            edit.putExtra("Paid", isPaid);
            startActivity(edit);
        });

        viewPayments.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);

            for (Bills bill : billList) {
                if (bill.getBillerName().equals(billerName)) {
                    Intent history = new Intent(mContext, PaymentHistory.class);
                    history.putExtra("User Id", thisUser.getid());
                    history.putExtra("Bill Id", bill.getBillsId());
                    startActivity(history);
                }
            }
        });

        payButton.setOnClickListener(view -> {

            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            if (!isPaid) {

                builder.setMessage(getString(R.string.areYouSureYouWantToMarkThisBillAsPaid)).setTitle(
                        getString(R.string.markAsPaid)).setPositiveButton(getString(R.string.markAsPaid), (dialogInterface, i) -> {
                    pb.setVisibility(View.VISIBLE);
                    for (Bills bill : billList) {
                        if (bill.getBillerName().equals(billerName)) {
                            ArrayList<Payments> find = bill.getPayments();
                            for (Payments payment : find) {
                                if (payment.getPaymentId() == (paymentId)) {
                                    payment.setPaid(true);
                                    payment.setDatePaid(todaysDate);
                                    bill.setPayments(find);
                                    bill.setDateLastPaid(todaysDate);
                                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    nm.cancel(paymentId);
                                    nm.cancel(paymentId + 1);
                                    nm.cancel(paymentId + 11);
                                    db.collection("users").document(thisUser.getUserName()).update("bills", billList);
                                    if (thisUser != null) {
                                        SaveUserData save = new SaveUserData();
                                        save.saveUserData(PayBill.this, thisUser);
                                    }
                                    Intent home = new Intent(mContext, MainActivity2.class);
                                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(home);
                                }
                            }
                        }
                    }
                }).setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
                });

                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();

            } else {

                builder.setMessage(getString(R.string.areYouSure));
                builder.setTitle(getString(R.string.unmarkAsPaid));
                builder.setPositiveButton(getString(R.string.unmarkAsPaid), (dialogInterface, i) -> {
                    pb.setVisibility(View.VISIBLE);
                    for (Bills bill : billList) {
                        if (bill.getBillerName().equals(billerName)) {
                            ArrayList<Payments> find = bill.getPayments();
                            for (Payments payment : find) {
                                if (payment.getPaymentId() == (paymentId)) {
                                    payment.setPaid(false);
                                    payment.setDatePaid(0);
                                    int highest = 0;
                                    for (Payments pay: bill.getPayments()) {
                                        if (pay.isPaid() && pay.getDatePaid() > highest) {
                                            highest = pay.getDatePaid();
                                        }
                                    }
                                    bill.setDateLastPaid(highest);
                                    bill.setPayments(find);
                                    scheduleNotifications(payment);
                                    if (thisUser != null) {
                                        SaveUserData save = new SaveUserData();
                                        save.saveUserData(PayBill.this, thisUser);
                                    }
                                    payBill.setBackground(new ColorDrawable(Color.TRANSPARENT));
                                    Intent home = new Intent(mContext, MainActivity2.class);
                                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(home);
                                }
                            }
                            ArrayList<Integer> dates = new ArrayList<>();
                            for (Payments pay : find) {
                                dates.add(pay.getDatePaid());
                            }
                            int lastPaid = Collections.max(dates);
                            bill.setDateLastPaid(lastPaid);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(thisUser.getUserName()).update("bills", billList);
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> dialog.dismiss());

                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });

        paymentDueDate.setOnClickListener(view -> getDateFromUser(paymentDueDate, true));
    }

    public void startPay(String dueDate, String billerName, String amountDue, boolean isPaid, int todaysDate) {
        pb.setVisibility(View.VISIBLE);

        Intent pay = new Intent(PayBill.this, PayBill.class);
        pay.putExtra("Due Date", dueDate);
        pay.putExtra("Biller Name", billerName);
        pay.putExtra("Amount Due", amountDue);
        pay.putExtra("Is Paid", isPaid);
        pay.putExtra("Payment Id", paymentId);
        pay.putExtra("Current Date", todaysDate);
        startActivity(pay);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        recreate();
        startPayBill();
    }

    public void refreshDate(int dueDate) {

        String billerName1 = tvBillerName.getText().toString();

        if (bil.isRecurring()) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(PayBill.this);
            builder.setCancelable(true);
            builder.setTitle(getString(R.string.changeDueDate));
            builder.setMessage(getString(R.string.changeAllOccurrences));
            builder.setPositiveButton(getString(R.string.changeAll), (dialogInterface, i) -> {
                pb.setVisibility(View.VISIBLE);

                ArrayList <Payments> newPaymentList = bil.getPayments();
                BillerManager billerManager = new BillerManager();
                Bills newBill = bil;
                newPaymentList = billerManager.generatePayments(newPaymentList, dueDate, bil.getFrequency(), "changeDate", pay.getPaymentAmount());
                newBill.setDayDue(pay.getPaymentDate());
                RemoveDuplicatePayments rdp = new RemoveDuplicatePayments();
                rdp.removeDuplicatePayments(newPaymentList);
                newBill.setPayments(newPaymentList);
                thisUser.getBills().remove(bil);
                thisUser.getBills().add(newBill);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                if (thisUser != null) {
                    SaveUserData save = new SaveUserData();
                    save.saveUserData(PayBill.this, thisUser);
                }
                pb.setVisibility(View.GONE);
            });
            builder.setNegativeButton(getString(R.string.cancel), (dialogInterface, i) -> {
            });
            builder.setNeutralButton(getString(R.string.justThisOne), (dialogInterface, i) -> {
                pb.setVisibility(View.VISIBLE);
                for (Bills bills : thisUser.getBills()) {
                    if (bills.getBillerName().equals(bil.getBillerName())) {
                        for (Payments payments : bills.getPayments()) {
                            if (payments.getPaymentId() == paymentId) {
                                payments.setPaymentDate(dueDate);
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                                if (thisUser != null) {
                                    SaveUserData save = new SaveUserData();
                                    save.saveUserData(PayBill.this, thisUser);
                                }
                                break;
                            }
                        }
                    }
                }
                pb.setVisibility(View.GONE);
            });
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            pb.setVisibility(View.VISIBLE);
            for (Bills bill : thisUser.getBills()) {
                if (bill.getBillerName().equals(tvBillerName.getText().toString())) {
                    for (Payments payment : bill.getPayments()) {
                        if (payment.getPaymentId() == paymentId) {
                            payment.setPaymentDate(dueDate);
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());
                            if (thisUser != null) {
                                SaveUserData save = new SaveUserData();
                                save.saveUserData(PayBill.this, thisUser);
                            }
                            break;
                        }
                    }
                }
            }
            pb.setVisibility(View.GONE);
        }
        pay.setPaymentDate(dueDate);
        bil.setBillerName(billerName1);
    }

    public void getDateFromUser(TextView dueDate, Boolean createPayments) {

        LocalDate local = df.convertIntDateToLocalDate(pay.getPaymentDate());
        int day = local.getDayOfMonth();
        int year = local.getYear();
        int month = local.getMonthValue();

        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(PayBill.this, R.style.MyDatePickerStyle, (datePicker1, i, i1, i2) -> {
            int fixMonth = i1 + 1;
            LocalDate selected = LocalDate.of(i, fixMonth, i2);
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
            String startDate = formatter.format(selected);
            dueDate.setText(formatter.format(selected));
            if (createPayments) {
                refreshDate(df.calcDateValue(selected));
            }
            else {
                datePaid = startDate;
                for (Bills bill: thisUser.getBills()) {
                    if (bill.getBillerName().equals(billerName)) {
                        for (Payments payment: bill.getPayments()) {
                            if (payment.getPaymentId() == paymentId) {
                                DateFormatter dateFormatter = new DateFormatter();
                                payment.setDatePaid(dateFormatter.convertDateStringToInt(datePaid));
                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                db.collection("users").document(thisUser.getUserName()).set(thisUser);
                            }
                        }
                    }
                }
            }
        }, year, month - 1, day);
        datePicker.setTitle("Select Date");
        datePicker.show();
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

    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.GONE);
    }
}