package com.example.billstracker;

import static android.content.ContentValues.TAG;
import static com.example.billstracker.Logon.billers;
import static com.example.billstracker.Logon.thisUser;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.TextViewCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class EditBiller extends AppCompatActivity {

    LinearLayout backEditBillerLayout, pb;
    TextView billerNameHeader, editDueDate, billerNameError, websiteError, amountDueError, useDefault;
    EditText editWebsite, editAmountDue;
    AutoCompleteTextView editBillerName;
    Spinner editFrequency, sCategory;
    SwitchCompat editRecurring;
    com.google.android.material.imageview.ShapeableImageView editBillerIcon;
    Button submitBiller;
    Bundle extras;
    String billerName;
    String website;
    String amountDue;
    String frequency;
    int dueDate;
    boolean recurring, dueDateChanged, frequencyChanged, recurringChanged;
    Context mContext;
    ConstraintLayout photoChooser;
    DateFormatter df = new DateFormatter();
    boolean custom;
    LoadIcon loadIcon = new LoadIcon();
    Bills bil;
    FixNumber fn = new FixNumber();
    ActivityResultLauncher<PickVisualMediaRequest> pickMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_biller);

        boolean darkMode = false;
        int nightModeFlags = getApplicationContext().getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
            darkMode = true;
        }

        if (billers == null || billers.size() == 0) {
            loadBillers();
        }

        pickMedia = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::loadImage);

        pb = findViewById(R.id.pb5);
        extras = getIntent().getExtras();
        mContext = this;
        sCategory = findViewById(R.id.category1);
        useDefault = findViewById(R.id.useDefaultIcon);
        editWebsite = findViewById(R.id.editWebsite);
        editDueDate = findViewById(R.id.editDueDate);
        submitBiller = findViewById(R.id.btnSubmitEdit);
        photoChooser = findViewById(R.id.editBillerPhotoChooser);
        websiteError = findViewById(R.id.websiteError1);
        editAmountDue = findViewById(R.id.editAmountDue);
        editFrequency = findViewById(R.id.editFrequency);
        editRecurring = findViewById(R.id.editRecurring);
        editBillerName = findViewById(R.id.editBillerName);
        amountDueError = findViewById(R.id.amountDueError1);
        editBillerIcon = findViewById(R.id.editBillerIcon1);
        billerNameError = findViewById(R.id.billerNameError1);
        billerNameHeader = findViewById(R.id.billerNameHeader);
        backEditBillerLayout = findViewById(R.id.backEditBillerLayout);

        dueDateChanged = false;
        recurringChanged = false;
        frequencyChanged = false;
        custom = false;

        billerName = extras.getString("userName");
        website = extras.getString("website");
        dueDate = extras.getInt("dueDate");
        amountDue = extras.getString("amountDue");
        frequency = extras.getString("frequency");
        recurring = extras.getBoolean("recurring");

        editBillerName.setText(billerName);
        editWebsite.setText(website);
        editDueDate.setText(df.convertIntDateToString(dueDate));
        editAmountDue.setText(String.format(Locale.getDefault(), "%s%s", thisUser.getCurrency(), amountDue));

        ArrayList <String> knownBillers = new ArrayList<>();
        for (Biller bills: billers) {
            knownBillers.add(bills.getBillerName());
        }
        Set<String> set = new HashSet<>(knownBillers);
        knownBillers.clear();
        knownBillers.addAll(set);
        ArrayAdapter <String> knownBillersAdapter = new ArrayAdapter<>(EditBiller.this, android.R.layout.simple_list_item_1, knownBillers);
        editBillerName.setAdapter(knownBillersAdapter);

        for (Bills bills: thisUser.getBills()) {
            if (bills.getBillerName().equals(billerName)) {
                bil = bills;
            }
        }
        if (darkMode) {
            editBillerIcon.setBackground(AppCompatResources.getDrawable(EditBiller.this, R.drawable.circle));
            editBillerIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.tiles, getTheme())));
        }
        loadIcon.loadIcon(EditBiller.this, editBillerIcon, bil.getCategory(), bil.getIcon());
        editBillerIcon.setContentPadding(40,40,40,40);
        if (bil.getIcon().contains("custom")) {
            custom = true;
            useDefault.setVisibility(View.VISIBLE);
        }

        billerNameHeader.setText(billerName);

        backEditBillerLayout.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            onBackPressed();
        });

        editRecurring.setChecked(recurring);

        String[] spinnerArray = new String[]{getString(R.string.daily), getString(R.string.weekly), getString(R.string.biweekly), getString(R.string.monthly),
                getString(R.string.quarterly), getString(R.string.yearly)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        editFrequency.setAdapter(adapter);
        editFrequency.setSelection(Integer.parseInt(frequency));

        String[] spinnerArray1 = new String[]{getString(R.string.autoLoan), getString(R.string.creditCard), getString(R.string.entertainment),
                getString(R.string.insurance), getString(R.string.miscellaneous), getString(R.string.mortgage), getString(R.string.personalLoans), getString(R.string.utilities)};
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray1);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategory.setAdapter(adapter1);

        sCategory.setSelection(Integer.parseInt(bil.getCategory()));
        ArrayList<Integer> icons = new ArrayList<>(Arrays.asList(R.drawable.auto, R.drawable.credit_card, R.drawable.entertainment, R.drawable.insurance,
                R.drawable.invoice, R.drawable.mortgage, R.drawable.personal_loan, R.drawable.utilities));
        final int[] userSelection = {Integer.parseInt(bil.getCategory())};
        editWebsite.setText(bil.getWebsite());
        final boolean[] found = {false};
        sCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!found[0] && !custom) {
                    userSelection[0] = adapter1.getPosition(sCategory.getSelectedItem().toString());
                    loadIcon.loadDefault(EditBiller.this, icons.get(adapter1.getPosition(sCategory.getSelectedItem().toString())), editBillerIcon);
                    editBillerIcon.setContentPadding(100,100,100,100);
                    custom = false;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        useDefault.setOnClickListener(v -> {
            userSelection[0] = sCategory.getSelectedItemPosition();
            loadIcon.loadDefault(EditBiller.this, icons.get(adapter1.getPosition(sCategory.getSelectedItem().toString())), editBillerIcon);
            editBillerIcon.setContentPadding(100,100,100,100);
            useDefault.setVisibility(View.GONE);
            custom = false;
        });

        photoChooser.setOnClickListener(v -> pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build()));

        editDueDate.setOnClickListener(view -> {

            dueDateChanged = true;
            getDateFromUser(editDueDate, editAmountDue);
        });

        editFrequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                frequencyChanged = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        editRecurring.setOnClickListener(view -> recurringChanged = true);

        ArrayList <String> billerNames = new ArrayList<>();
        for (Bills bills: thisUser.getBills()) {
            billerNames.add(bills.getBillerName().toLowerCase(Locale.getDefault()).trim());
        }

        final boolean[] nameLength = {true};
        final boolean[] websiteLength = {true};
        final boolean[] amountDue = {true};
        final boolean[] dueDate = {true};

        boolean finalDarkMode = darkMode;

        editBillerName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (Biller biller : billers) {
                    if (editBillerName.getText().toString().trim().toLowerCase().contains(biller.getBillerName().trim().toLowerCase())) {
                        found[0] = true;
                        loadIcon.loadImageFromDatabase(EditBiller.this, editBillerIcon, biller.getIcon());
                        editBillerIcon.setContentPadding(100,100,100,100);
                        custom = true;
                        editWebsite.setText(biller.getWebsite());
                        int selection = adapter1.getPosition(biller.getType());
                        sCategory.setSelection(selection);
                        useDefault.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editBillerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                billerNameHeader.setText(editBillerName.getText());
                editBillerName.removeTextChangedListener(this);
                String name = editBillerName.getText().toString().toLowerCase(Locale.getDefault()).trim();
                for (String bill: billerNames) {
                    if (bill.equals(name)) {
                        editBillerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
                        nameLength[0] = false;
                        submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lightblue)));
                        billerNameError.setVisibility(View.VISIBLE);
                        billerNameError.setText(R.string.billerExists);
                    }
                }
                if (name.length() > 1 && !custom) {
                    nameLength[0] = true;
                    for (Biller biller : billers) {
                        if (name.contains(biller.getBillerName().toLowerCase())) {
                            found[0] = true;
                            loadIcon.loadImageFromDatabase(EditBiller.this, editBillerIcon, biller.getIcon());
                            editBillerIcon.setContentPadding(100,100,100,100);
                            custom = true;
                            editWebsite.setText(biller.getWebsite());
                            int selection = adapter1.getPosition(biller.getType());
                            sCategory.setSelection(selection);
                            useDefault.setVisibility(View.VISIBLE);
                            break;
                        } else {
                            custom = false;
                            found[0] = false;
                            loadIcon.loadDefault(EditBiller.this, icons.get(userSelection[0]), editBillerIcon);
                            editBillerIcon.setContentPadding(100,100,100,100);
                            editWebsite.setText("");
                            sCategory.setSelection(userSelection[0]);
                            useDefault.setVisibility(View.GONE);
                        }
                    }
                }
                if (name.isEmpty()) {
                    editBillerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
                    found[0] = false;
                    billerNameError.setVisibility(View.VISIBLE);
                    billerNameError.setText(R.string.billerNameCantBeBlank);
                    nameLength[0] = false;
                }
                else {
                    for (Bills bill: thisUser.getBills()) {
                        if (bill.getBillerName().equals(editBillerName.getText().toString())) {
                            editBillerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0,0);
                            nameLength[0] = false;
                            submitBiller.setEnabled(false);
                            submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lightblue)));
                            billerNameError.setVisibility(View.VISIBLE);
                            billerNameError.setText(R.string.billerExists);
                        }
                        else {
                            editBillerName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmarksmall,0);
                            billerNameError.setVisibility(View.GONE);
                            nameLength[0] = true;
                            if (websiteLength[0] && amountDue[0] && dueDate[0]) {
                                submitBiller.setEnabled(true);
                                if (finalDarkMode) {
                                    submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blueGrey)));
                                }
                                else {
                                    submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.button)));
                                }
                            }
                        }
                    }
                }
                editBillerName.addTextChangedListener(this);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editWebsite.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editWebsite.getText().toString().length() < 7 && editWebsite.getText().toString().length() > 0) {
                    editWebsite.setCompoundDrawablesWithIntrinsicBounds(0,0, 0, 0);
                    submitBiller.setEnabled(false);
                    websiteLength[0] = false;
                    submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lightblue)));
                    websiteError.setVisibility(View.VISIBLE);
                }
                else if (editWebsite.getText().toString().length() == 0) {
                    editWebsite.setCompoundDrawablesWithIntrinsicBounds(0,0, 0, 0);
                    websiteError.setVisibility(View.GONE);
                    websiteLength[0] = false;
                }
                else {
                    editWebsite.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.checkmarksmall, 0);
                    websiteError.setVisibility(View.GONE);
                    websiteLength[0] = true;
                    if (nameLength[0] && amountDue[0] && dueDate[0]) {
                        submitBiller.setEnabled(true);
                        if (finalDarkMode) {
                            submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blueGrey)));
                        } else {
                            submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.button)));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editDueDate.setOnClickListener(view -> {
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                editDueDate.setTextColor(getResources().getColor(R.color.white, getTheme()));
            }
            else {
                editDueDate.setTextColor(getResources().getColor(R.color.black, getTheme()));
            }
            getDateFromUser(editDueDate, editAmountDue);

        });

        editDueDate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editDueDate.getText().toString().length() < 5) {
                    submitBiller.setEnabled(false);
                    dueDate[0] = false;
                    submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lightblue)));
                    editDueDate.setError(getString(R.string.dueDateCantBeBlank));
                    editDueDate.setCompoundDrawablesWithIntrinsicBounds(0,0, 0, 0);
                }
                else {
                    editDueDate.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.checkmarksmall, 0);
                    TextViewCompat.setCompoundDrawableTintList(editDueDate, ColorStateList.valueOf(getResources().getColor(R.color.green, getTheme())));
                    editDueDate.setError(null);
                    dueDate[0] = true;
                    if (nameLength[0] && amountDue[0] && websiteLength[0]) {
                        submitBiller.setEnabled(true);
                        if (finalDarkMode) {
                            submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blueGrey)));
                        } else {
                            submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.button)));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editAmountDue.setText(fn.addSymbol(bil.getAmountDue()));
        editAmountDue.addTextChangedListener( new MoneyInput(editAmountDue));

        editAmountDue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int n, int i1, int i2) {

                if (editAmountDue.getText().toString().length() < 2 && editAmountDue.getText().toString().length() > 0) {
                    editAmountDue.setCompoundDrawablesWithIntrinsicBounds(0,0, 0, 0);
                    submitBiller.setEnabled(false);
                    amountDue[0] = false;
                    submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.lightblue)));
                    amountDueError.setVisibility(View.VISIBLE);
                }
                else if (editAmountDue.getText().toString().length() == 0) {
                    editAmountDue.setCompoundDrawablesWithIntrinsicBounds(0,0, 0, 0);
                    amountDueError.setVisibility(View.GONE);
                    amountDue[0] = false;
                }
                else {
                    editAmountDue.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.checkmarksmall,0);
                    amountDueError.setVisibility(View.GONE);
                    amountDue[0] = true;
                    if (nameLength[0] && dueDate[0] && websiteLength[0]) {
                        submitBiller.setEnabled(true);
                        if (finalDarkMode) {
                            submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blueGrey)));
                        } else {
                            submitBiller.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.button)));
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editFrequency.setOnFocusChangeListener((view, b) -> {

            if (view.hasFocus()) {
                editFrequency.performClick();
            }
        });

        submitBiller.setOnClickListener(view -> {

            pb.setVisibility(View.VISIBLE);

            String billerName = editBillerName.getText().toString();
            String website = editWebsite.getText().toString();
            String amount = fn.makeDouble(editAmountDue.getText().toString());
            String frequency = String.valueOf(editFrequency.getSelectedItemPosition());
            String category = String.valueOf(sCategory.getSelectedItemPosition());
            boolean recurring = editRecurring.isChecked();
            int paymentId1 = notificationId();

            String icon1 = "";
            BillerImage billerImage = new BillerImage();
            if (custom) {
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        icon1 = billerImage.storeImage(EditBiller.this, editBillerIcon.getDrawable(), billerName, custom);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else {
                icon1 = String.valueOf(icons.get(adapter1.getPosition(sCategory.getSelectedItem().toString())));
            }
            if (editAmountDue.getText().toString().length() == 0) {
                amount = "0.00";
            }

            int dueDateValue = df.convertDateStringToInt(editDueDate.getText().toString());
            int datePaid = 0;
            ArrayList<Payments> newPayments = new ArrayList<>();
            bil.setIcon(icon1);
            Bills newBiller = bil;

            if (dueDateChanged || frequencyChanged || recurringChanged) {
                if (recurring) {
                    if (newBiller.getPayments() == null) {
                        newBiller.setPayments(new ArrayList<>());
                    }
                        for (Payments payment : newBiller.getPayments()) {
                            if (payment.isPaid()) {
                                payment.setBillerName(billerName);
                                newPayments.add(payment);
                            }
                        }
                        if (newPayments.isEmpty()) {
                            Payments firstPayment = new Payments(amount, dueDateValue, false, billerName, paymentId1, datePaid);
                            newPayments.add(firstPayment);
                        }
                        BillerManager bm = new BillerManager();
                    ArrayList <Payments> generate = bm.generatePayments(newPayments, dueDateValue, frequency, "edit", amount);
                    newPayments.addAll(generate);
                    newBiller.getPayments().clear();
                    newPayments = removeDuplicates(newPayments);
                    newBiller.setPayments(newPayments);
                } else {
                    Payments payment = new Payments(amount, dueDateValue, false, billerName, paymentId1, datePaid);
                    newBiller.getPayments().clear();
                    newBiller.getPayments().add(payment);
                }
            }
            newBiller.setBillerName(billerName);
            newBiller.setAmountDue(amount);
            newBiller.setDayDue(dueDateValue);
            newBiller.setWebsite(website);
            newBiller.setFrequency(frequency);
            newBiller.setCategory(category);
            newBiller.setRecurring(recurring);
            newBiller.setIcon(icon1);
            thisUser.getBills().remove(bil);
            thisUser.getBills().add(newBiller);
            if (thisUser != null) {
                SaveUserData save = new SaveUserData();
                save.saveUserData(EditBiller.this, thisUser);
            }
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(thisUser.getUserName()).update("bills", thisUser.getBills());

            Intent launchMain = new Intent(mContext, ViewBillers.class);
            launchMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(launchMain);
        });
    }

    public void loadImage (Uri uri) {
        if (uri != null) {
            custom = true;
            useDefault.setVisibility(View.VISIBLE);
            editBillerIcon.setImageTintList(null);
            loadIcon.loadImageFromDatabase(EditBiller.this, editBillerIcon, String.valueOf(uri));
            editBillerIcon.setContentPadding(100,100,100,100);
            Log.d("PhotoPicker", "Selected URI: " + uri);
        } else {
            Log.d("PhotoPicker", "No media selected");
        }
    }

    public ArrayList <Payments> removeDuplicates (ArrayList <Payments> payments) {

        ArrayList<Payments> noRepeat = new ArrayList<>();

        for (Payments payment: payments) {
            boolean found = false;
            for (Payments e: noRepeat) {
                if (e.getPaymentId() == payment.getPaymentId()) {
                    found = true;
                    break;
                }
            }
            if (!found) noRepeat.add(payment);
        }
        return noRepeat;
    }

    int notificationId() {
        final String AB = "0123456789";
        SecureRandom rnd = new SecureRandom();
        int length = 7;
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return Integer.parseInt(String.valueOf(sb));
    }

    public void loadBillers () {

        if (billers == null || billers.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("billers").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        Biller bill = document.toObject(Biller.class);
                        billers.add(bill);
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            });
        }
    }

    public void getDateFromUser(TextView dueDate, EditText amountDue) {

        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        int day = today.getDayOfMonth();
        int year = today.getYear();
        int month = today.getMonthValue();

        DatePickerDialog datePicker;
        datePicker = new DatePickerDialog(EditBiller.this, R.style.MyDatePickerStyle, (datePicker1, i, i1, i2) -> {
            int fixMonth = i1 + 1;
            LocalDate date = LocalDate.of(i, fixMonth, i2);
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
            String startDate = formatter.format(date);
            dueDate.setText(startDate);
            amountDue.requestFocus();
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.showSoftInput(amountDue, InputMethodManager.RESULT_SHOWN);
        }, year, month - 1, day);
        datePicker.setTitle(getString(R.string.selectDate));
        datePicker.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.GONE);
    }
}