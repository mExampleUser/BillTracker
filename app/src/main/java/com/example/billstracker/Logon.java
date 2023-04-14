package com.example.billstracker;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Executor;

public class Logon extends AppCompatActivity {

    public static ArrayList<Login> userList = new ArrayList<>();
    public static ArrayList<Bills> billList = new ArrayList<>();
    static ArrayList <Biller> billers = new ArrayList<>();
    public static Login thisUser;
    public static boolean checkTrophies;
    boolean loggedIn;
    boolean biometricPreference;
    boolean alreadySignedIn;
    TextView firstWelcome, wrongUser, forgotPassword, loadingHeader, loadingMessage;
    Button expandLogin, firstRegister, login, registerButton;
    LinearLayout hiddenLogin, titleBar;
    Executor executor;
    AlertDialog dialog;
    SharedPreferences sp;
    TextView errorMessage;
    ConstraintLayout main;
    LinearLayout pb;
    Context mContext = this;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    String TAG = MainActivity2.class.getSimpleName();
    ImageView biometric;
    EditText password, username;
    CheckBox staySignedIn, check;
    BillerManager bm = new BillerManager();
    LottieAnimationView animationView, animationView2;
    com.google.android.gms.ads.AdView mAdView;
    String setName;
    GoogleSignInClient mGoogleSignInClient;
    BiometricManager biometricManager;
    int RC_SIGN_IN = 177;
    CallbackManager callbackManager;
    private static final String EMAIL = "email";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkTrophies = true;
        setContentView(R.layout.activity_logon);
        login = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.buttonRegister);
        biometric = findViewById(R.id.biometricButton);
        password = findViewById(R.id.etPassword);
        username = findViewById(R.id.etUserName);
        errorMessage = findViewById(R.id.errorMessage);
        main = findViewById(R.id.logonLayout);
        mContext = this.getBaseContext();
        firstWelcome = findViewById(R.id.firstWelcome);
        expandLogin = findViewById(R.id.expandLogin);
        firstRegister = findViewById(R.id.firstRegister);
        hiddenLogin = findViewById(R.id.hiddenLogin);
        wrongUser = findViewById(R.id.wrongUser);
        staySignedIn = findViewById(R.id.staySignedIn);
        animationView = findViewById(R.id.animationView);
        animationView2 = findViewById(R.id.animationView2);
        loadingHeader = findViewById(R.id.loadingHeader);
        loadingMessage = findViewById(R.id.loadingMessage);
        pb = findViewById(R.id.pb8);
        loggedIn = false;
        forgotPassword = findViewById(R.id.forgotPassword);
        check = findViewById(R.id.checkBox);
        titleBar = findViewById(R.id.titleBar);

        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getValuesFromSharedPreferences();

        addListeners();

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.facebookButton);
        if (loginButton != null) {
            loginButton.setPermissions(Collections.singletonList(EMAIL));
        }
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                load(EMAIL);
                firstWelcome.setVisibility(View.GONE);
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        Toast.makeText(Logon.this, loginResult.toString(), Toast.LENGTH_SHORT).show();
                        auth.signInWithCustomToken(loginResult.getAccessToken().getToken());
                        if (auth.getCurrentUser() != null) {
                            if (auth.getCurrentUser().getEmail() != null) {
                                load(auth.getCurrentUser().getEmail());
                                firstWelcome.setVisibility(View.GONE);
                            }
                        }
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        if (isLoggedIn) {
            FirebaseAuth auth = FirebaseAuth.getInstance();
            auth.signInWithCustomToken(accessToken.getToken());
            if (auth.getCurrentUser() != null) {
                if (auth.getCurrentUser().getEmail() != null) {
                    load(auth.getCurrentUser().getEmail());
                    firstWelcome.setVisibility(View.GONE);
                }
            }
        }
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            load(Objects.requireNonNull(account.getEmail()));
            firstWelcome.setVisibility(View.GONE);
            expandLogin.setVisibility(View.GONE);
            firstRegister.setVisibility(View.GONE);
            wrongUser.setVisibility(View.GONE);
        }
        else {
            promptInfo = new BiometricPrompt.PromptInfo.Builder().setTitle(getString(R.string.biometricAuthentication)).setNegativeButtonText(getString(R.string.cancel))
                    .setConfirmationRequired(false).build();

            executor = ContextCompat.getMainExecutor(this);

            biometricPrompt = new BiometricPrompt(this, executor, new BiometricPrompt.AuthenticationCallback() {

                @Override
                public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    sp.getString("email", "");
                    if (!sp.getString("email", "").equals("") && !sp.getString("password", "").equals("")) {
                        String email = sp.getString("email", "");
                        String pass = sp.getString("password", "");
                        hiddenLogin.setVisibility(View.VISIBLE);
                        expandLogin.setVisibility(View.GONE);
                        firstRegister.setVisibility(View.GONE);
                        firstWelcome.setVisibility(View.GONE);
                        wrongUser.setVisibility(View.GONE);
                        signIn(email, pass);
                    } else {
                        Toast.makeText(mContext, getString(R.string.enableAfterLoggingIn), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onAuthenticationFailed() {
                    super.onAuthenticationFailed();
                }
            });

            if (sp.contains("biometricPreference") && biometricPreference) {
                if (BiometricManager.from(mContext).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS && !alreadySignedIn) {
                    biometricPrompt.authenticate(promptInfo);
                }
            }
        }

        mGoogleSignInClient = GoogleSignIn.getClient(Logon.this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.sign_in_button) {
                    signInWithGoogle();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startForResult.launch(signInIntent);
        //startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    ActivityResultLauncher<Intent> startForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult o) {
            if (o.getData() != null) {

                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(o.getData());
                handleSignInResult(task);
            }
        }
    });
    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            load(Objects.requireNonNull(account.getEmail()));
            firstWelcome.setVisibility(View.GONE);
        } catch (ApiException e) {
            Toast.makeText(Logon.this, "Authentication Failed :" + e.getStatusCode(), Toast.LENGTH_SHORT).show();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText(R.string.googleSignInFailed);
        }
    }
    public void getValuesFromSharedPreferences () {

        sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
        boolean registered = sp.getBoolean("registered", false);
        biometricPreference = sp.getBoolean("biometricPreference", false);
        if (sp.contains("UserName") && !sp.getString("UserName", "").equals("")) {
            setName = sp.getString("name", "");
            check.setChecked(true);
            username.setText(sp.getString("UserName", ""));
        } else {
            check.setChecked(false);
        }

        if (!registered) {
            Intent welcome = new Intent(mContext, Welcome.class);
            welcome.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(welcome);
        }

        String firstName = "";
        if (setName != null && !setName.equals("")) {
            if (setName.contains(" ")) {
                firstName = setName.substring(0, setName.indexOf(' '));
            } else {
                firstName = setName;
            }
            firstName = firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase();
        }

        if (!firstName.equals("")) {
            firstWelcome.setText(String.format(Locale.getDefault(), "%s %s!", getString(R.string.welcomeBack), firstName));
            wrongUser.setVisibility(View.VISIBLE);
            wrongUser.setText(String.format(Locale.getDefault(), "%s %s? %s", getString(R.string.not), firstName, getString(R.string.clickRegister)));
        }
        else {
            wrongUser.setVisibility(View.GONE);
        }


        if (sp.contains("Stay Signed In") && sp.getBoolean("Stay Signed In", false)) {
            alreadySignedIn = true;
            expandLogin.setVisibility(View.GONE);
            firstRegister.setVisibility(View.GONE);
            firstWelcome.setVisibility(View.GONE);
            wrongUser.setVisibility(View.GONE);
            pb.setVisibility(View.VISIBLE);
            signIn(sp.getString("Username", ""), sp.getString("Password", ""));
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            username.setText(extras.getString("UserName"));
            password.setText(extras.getString("Password"));
        }
    }

    public void addListeners () {

        forgotPassword.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent fp = new Intent(mContext, ForgotPassword.class);
            fp.putExtra("UserName", username.getText());
            startActivity(fp);
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        expandLogin.setOnClickListener(view -> {
            hiddenLogin.setVisibility(View.VISIBLE);
            expandLogin.setVisibility(View.GONE);
            firstRegister.setVisibility(View.GONE);
            firstWelcome.setVisibility(View.GONE);
            wrongUser.setVisibility(View.GONE);
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                errorMessage.setText("");
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        firstRegister.setOnClickListener(view -> register());

        username.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(username.getWindowToken(), 0);
                password.requestFocus();
            }
            return false;
        });

        password.setOnKeyListener((view, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_ENTER) {
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(password.getWindowToken(), 0);
                if (password.getText().length() > 6 && username.getText().length() > 6) {
                    SharedPreferences.Editor editor = sp.edit();
                    if (check.isChecked()) {
                        editor.putString("UserName", username.getText().toString());
                    } else {
                        editor.putString("UserName", "");
                    }
                    if (staySignedIn.isChecked()) {
                        editor.putBoolean("Stay Signed In", true);
                        editor.putString("Username", username.getText().toString());
                        editor.putString("Password", password.getText().toString());
                    } else {
                        editor.putBoolean("Stay Signed In", false);
                    }
                    editor.apply();
                    pb.setVisibility(View.VISIBLE);
                    pb.bringToFront();
                    signIn(username.getText().toString(), password.getText().toString());
                }
                else {
                    errorMessage.setVisibility(View.VISIBLE);
                    errorMessage.setText(R.string.username_or_password_too_short);
                    errorMessage.setTextColor(getResources().getColor(R.color.fingerprint, getTheme()));
                }
            }
            return false;
        });

        login.setOnClickListener(view -> {

            if (password.getText().length() > 6 && username.getText().length() > 6) {
                pb.setVisibility(View.VISIBLE);

                SharedPreferences.Editor editor = sp.edit();
                if (check.isChecked()) {
                    editor.putString("UserName", username.getText().toString());
                } else {
                    editor.putString("UserName", "");
                }
                if (staySignedIn.isChecked()) {
                    editor.putBoolean("Stay Signed In", true);
                    editor.putString("Username", username.getText().toString());
                    editor.putString("Password", password.getText().toString());
                } else {
                    editor.putBoolean("Stay Signed In", false);
                }
                editor.apply();
                signIn(username.getText().toString(), password.getText().toString());
            }
            else {
                errorMessage.setVisibility(View.VISIBLE);
                errorMessage.setText(R.string.username_or_password_too_short);
                errorMessage.setTextColor(getResources().getColor(R.color.fingerprint, getTheme()));
            }
        });

        registerButton.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            Intent register = new Intent(mContext, Welcome.class);
            startActivity(register);
        });

        biometric.setOnClickListener(view -> {

            biometricManager = BiometricManager.from(mContext);
            if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
                Toast.makeText(mContext, getString(R.string.biometricsNotSupported), Toast.LENGTH_SHORT).show();
                return;
            }

            if (sp.contains("biometricPreference")) {
                if (!biometricPreference) {
                    Toast.makeText(mContext, getString(R.string.enableAfterLoggingIn), Toast.LENGTH_LONG).show();
                } else {
                    biometricManager = BiometricManager.from(mContext);
                    if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
                        Toast.makeText(mContext, getString(R.string.biometricsNotSupported), Toast.LENGTH_SHORT).show();
                    } else {
                        biometricPrompt.authenticate(promptInfo);
                    }
                }
            } else {
                Toast.makeText(mContext, getString(R.string.enableAfterLoggingIn), Toast.LENGTH_LONG).show();
            }
        });
    }
    public void checkBiometricPreference(String username) {

        SharedPreferences.Editor editor = sp.edit();
        boolean allowBiometricPrompt = true;
        boolean match = true;
        boolean biometricEligible = true;
        if (sp.contains("allowBiometricPrompt")) {
            allowBiometricPrompt = sp.getBoolean("allowBiometricPrompt", true);
        }

        biometricManager = BiometricManager.from(mContext);

        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) != BiometricManager.BIOMETRIC_SUCCESS) {
            biometricEligible = false;
        }

        String uName = sp.getString("email", "");
        if (!uName.equals(username)) {
            match = false;
        }

        if (allowBiometricPrompt && biometricEligible || !match && biometricEligible) {

            if (!biometricPreference || !match) {
                pb.setVisibility(View.GONE);
                androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(Logon.this);
                builder.setMessage(getString(R.string.willYouEnableBiometrics)).setTitle(getString(R.string.enableBiometrics1)).setPositiveButton(getString(R.string.yes), (dialogInterface, i) -> {
                    editor.putBoolean("biometricPreference", true);
                    editor.putBoolean("allowBiometricPrompt", true);
                    editor.putString("email", username);
                    editor.apply();
                    launchMainActivity();

                });
                builder.setNegativeButton(getString(R.string.notRightNow), (dialogInterface, i) -> {
                    dialog.dismiss();
                    launchMainActivity();
                });
                builder.setNeutralButton(getString(R.string.dontAskAgain), (dialogInterface, i) -> {
                    editor.putBoolean("biometricPreference", false);
                    editor.putBoolean("allowBiometricPrompt", false);
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.apply();
                    androidx.appcompat.app.AlertDialog.Builder builder1 = new androidx.appcompat.app.AlertDialog.Builder(mContext);
                    builder1.setMessage(getString(R.string.biometricsAreDisabled))
                            .setTitle(getString(R.string.biometricsDisabled)).setPositiveButton(getString(R.string.ok), (dialogInterface1, i1) -> launchMainActivity());
                    androidx.appcompat.app.AlertDialog alert = builder1.create();
                    alert.show();
                });
                androidx.appcompat.app.AlertDialog alert = builder.create();
                alert.show();
            } else {
                launchMainActivity();
            }

        } else {
            launchMainActivity();
        }
    }

    public void signIn(String username, String password) {

        pb.setVisibility(View.VISIBLE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "signInWithEmail:success");
                        checkIfEmailVerified(username);
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.getException());

                        hiddenLogin.setVisibility(View.VISIBLE);
                        errorMessage.setVisibility(View.VISIBLE);
                        firstWelcome.setVisibility(View.GONE);
                        errorMessage.setText(R.string.invalidLogin);
                        errorMessage.setTextColor(getResources().getColor(R.color.fingerprint, getTheme()));
                        pb.setVisibility(View.GONE);
                        wrongUser.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void checkIfEmailVerified(String username) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        assert user != null;
        if (user.isEmailVerified()) {
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("registered", true);
            edit.apply();
            load(username);
        } else {
            pb.setVisibility(View.GONE);
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(Logon.this);
            builder.setMessage(getString(R.string.verifyEmail)).setTitle(getString(R.string.emailNotVerified)).setPositiveButton(getString
                    (R.string.resendEmail), (dialogInterface, i) -> user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(mContext, getString(R.string.verificationEmailSent),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, getString(R.string.anErrorHasOccurred),
                                    Toast.LENGTH_SHORT).show();

                        }
                        pb.setVisibility(View.GONE);
                        FirebaseAuth.getInstance().signOut();
                        startActivity(getIntent());
                    }));
            builder.setNegativeButton(getString(R.string.ok), (dialogInterface, i) -> FirebaseAuth.getInstance().signOut());
            androidx.appcompat.app.AlertDialog alert = builder.create();
            alert.show();

        }
    }

    public void load(String userName) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userName.toLowerCase()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot doc = task.getResult();
                thisUser = doc.toObject(Login.class);
                updateUI(userName);
                    SaveUserData save = new SaveUserData();
                    save.saveUserData(Logon.this, thisUser);
            } else {
                Toast.makeText(mContext, getString(R.string.emailNotRegistered), Toast.LENGTH_SHORT).show();
                pb.setVisibility(View.GONE);
            }
        });
    }

    private void updateUI(String userName) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference login = db.collection("users").document(userName);
        DateFormatter df = new DateFormatter();
        String loginTime = df.createCurrentDateStringWithTime();
        if (thisUser == null) {
            Toast.makeText(mContext, getString(R.string.anErrorHasOccurred), Toast.LENGTH_SHORT).show();
            recreate();
        }
        thisUser.setLastLogin(loginTime);
        ArrayList <Bills> refreshBills = new ArrayList<>();
        Bills bill;
        if (thisUser.getBills() == null) {
            thisUser.setBills(new ArrayList<>());
        }
        if (thisUser.getTrophies() == null) {
            thisUser.setTrophies(new ArrayList<>());
        }
        if (thisUser.getBills().size() > 0) {
            for (Bills check : thisUser.getBills()) {
                bill = check;
                if (bill.getPayments() == null) {
                    Payments payment = new Payments(bill.getAmountDue(), bill.getDayDue(), false, bill.getBillerName(), Integer.parseInt(bm.id()), 0);
                    ArrayList <Payments> payments = new ArrayList<>();
                    payments.add(payment);
                    bill.setPayments(payments);
                }
                if (bill.isRecurring()) {
                    bill.setPayments(bm.refreshPayments(bill.getPayments(), bill.getDayDue(), bill.getFrequency()));
                }
                refreshBills.add(bill);
            }
        }
        thisUser.setBills(refreshBills);
        for (Bills bills: thisUser.getBills()) {
            switch (bills.getCategory()) {
                case "Auto Loan":
                    bills.setCategory("0");
                    break;
                case "Credit Card":
                    bills.setCategory("1");
                    break;
                case "Entertainment":
                    bills.setCategory("2");
                    break;
                case "Insurance":
                    bills.setCategory("3");
                    break;
                case "Miscellaneous":
                    bills.setCategory("4");
                    break;
                case "Mortgage":
                    bills.setCategory("5");
                    break;
                case "Personal Loan":
                case "Personal Loans":
                    bills.setCategory("6");
                    break;
                case "Utilities":
                    bills.setCategory("7");
                    break;
            }
            switch (bills.getFrequency()) {
                case "Daily":
                    bills.setFrequency("0");
                    break;
                case "Weekly":
                    bills.setFrequency("1");
                    break;
                case "Bi-Weekly":
                    bills.setFrequency("2");
                    break;
                case "Monthly":
                    bills.setFrequency("3");
                    break;
                case "Quarterly":
                    bills.setFrequency("4");
                    break;
                case "Yearly":
                    bills.setFrequency("5");
                    break;
            }
            if (bills.getAmountDue().contains("$")) {
                bills.setAmountDue(bills.getAmountDue().replaceAll("\\$", ""));
            }
            if (bills.getIcon() == null) {
                bills.setIcon("fixMe");
            }
            for (Payments payment: bills.getPayments()) {
                if (payment.getPaymentAmount().contains("$")) {
                    payment.setPaymentAmount(payment.getPaymentAmount().replaceAll("\\$", ""));
                }
            }
        }
        if (thisUser.getPayFrequency().equals("Weekly")) {
            thisUser.setPayFrequency("0");
        }
        else if (thisUser.getPayFrequency().equals("Bi-Weekly")) {
            thisUser.setPayFrequency("1");
        }
        else {
            thisUser.setPayFrequency("2");
        }
        Currency cur = Currency.getInstance(Locale.getDefault());
        thisUser.setCurrency(cur.getSymbol());
        thisUser.setTotalLogins(thisUser.getTotalLogins() + 1);
        login.set(thisUser);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("email", thisUser.getUserName());
        editor.putString("name", thisUser.getName());
        editor.putString("password", thisUser.getPassword());
        editor.apply();
        checkBiometricPreference(userName);

    }

    private void launchMainActivity() {

        pb.setVisibility(View.GONE);
        wrongUser.setVisibility(View.GONE);
        animationView.setVisibility(View.GONE);
        animationView2.setVisibility(View.VISIBLE);
        hiddenLogin.setVisibility(View.GONE);
        titleBar.setVisibility(View.GONE);
        loadingHeader.setVisibility(View.VISIBLE);
        loadingMessage.setVisibility(View.VISIBLE);
        loadingMessage.postDelayed(() -> {
            animationView2.animate().alpha(0f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
            loadingMessage.animate().alpha(0f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
            loadingMessage.postDelayed(() -> {
                loadingMessage.setText(R.string.ba);
                animationView2.setAnimation(R.raw.computing);
                animationView2.playAnimation();
                loadingMessage.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                animationView2.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                loadingMessage.postDelayed(() -> {
                    animationView2.animate().alpha(0f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                    loadingMessage.animate().alpha(0f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                    loadingMessage.postDelayed(() -> {
                        loadingMessage.setText(R.string.cd);
                        animationView2.setAnimation(R.raw.animation2);
                        animationView2.playAnimation();
                        loadingMessage.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                        animationView2.animate().alpha(1f).setDuration(1000).setInterpolator(new AccelerateInterpolator()).start();
                        loadingMessage.postDelayed(() -> {
                            if (!thisUser.isTermsAccepted()) {
                                Intent terms = new Intent (Logon.this, TermsAndConditions.class);
                                terms.putExtra("Type", "Existing User");
                                startActivity(terms);
                            }

                            else if (thisUser.getTotalLogins() == 1) {
                                Intent tutorial = new Intent(Logon.this, Instructions1.class);
                                tutorial.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(tutorial);
                            }

                            else {
                                Intent home = new Intent(mContext, MainActivity2.class);
                                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                home.putExtra("Name", thisUser.getName());
                                home.putExtra("UserName", thisUser.getUserName());
                                home.putExtra("Logged In", true);
                                startActivity(home);
                            }
                        }, 3000);
                    }, 1000);
                }, 3000);
            }, 1000);
        }, 3000);
    }

    public void register() {

        Intent register = new Intent(this, Welcome.class);
        startActivity(register);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pb.setVisibility(View.GONE);
    }
}