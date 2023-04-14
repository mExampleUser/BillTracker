package com.example.billstracker;

import static com.example.billstracker.Logon.thisUser;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.Objects;

public class Settings extends AppCompatActivity {

    static String name;
    static String username;
    Context mContext = this;
    LinearLayout pb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ImageView back = findViewById(R.id.backSettings);
        pb = findViewById(R.id.pb1);
        back.setOnClickListener(view -> {
            pb.setVisibility(View.VISIBLE);
            onBackPressed();
            pb.setVisibility(View.GONE);
        });
        personalize();
        LinearLayout edit = findViewById(R.id.llUserProfileEdit);
        LinearLayout delete = findViewById(R.id.llDeleteAccount);
        LinearLayout about = findViewById(R.id.llAbout);
        LinearLayout logout = findViewById(R.id.llLogout);
        edit.setOnClickListener(view -> {
            try {
                profileEdit(view);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        delete.setOnClickListener(this::deleteUser);
        about.setOnClickListener(view -> {
            Intent getAbout = new Intent(mContext, About.class);
            startActivity(getAbout);
        });
        logout.setOnClickListener(view -> {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
            builder.setCancelable(true);
            builder.setTitle(getString(R.string.confirmLogout));
            builder.setMessage(getString(R.string.areYouSureYouWantToLogout));
            builder.setPositiveButton(getString(R.string.confirm),
                    (dialog, which) -> {
                        pb.setVisibility(View.VISIBLE);
                        GoogleSignIn.getClient(Settings.this, GoogleSignInOptions.DEFAULT_SIGN_IN).signOut();
                        Toast.makeText(mContext, getString(R.string.youHaveLoggedOutSuccessfully), Toast.LENGTH_LONG).show();
                        SharedPreferences sp = mContext.getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("Stay Signed In", false);
                        editor.putString("Username", "");
                        editor.putString("Password", "");
                        editor.apply();
                        Intent validate = new Intent(mContext, Logon.class);
                        validate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(validate);
                        pb.setVisibility(View.GONE);
                    });
            builder.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> {

            });
                    androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    public void personalize() {

        String setName = MainActivity2.name;
        String setUsername = MainActivity2.userName;
        TextView uName = findViewById(R.id.replaceWithUsername);
        uName.setText(setName);
        name = setName;
        username = setUsername;

    }

    public void profileEdit(View view) throws IOException {
        pb.setVisibility(View.VISIBLE);
        SharedPreferences sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
        String setName = sp.getString("KEY_NAME", "");
        String setUsername = sp.getString("KEY_USERNAME", "");
        name = setName;
        username = setUsername;
        Intent edit = new Intent(mContext, EditProfile.class);
        edit.putExtra("Name", name);
        edit.putExtra("Username", username);
        startActivity(edit);
        pb.setVisibility(View.GONE);
    }

    public void deleteUser(View view) {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(mContext);
        builder.setCancelable(true);
        builder.setTitle(getString(R.string.confirmDeletion1));
        builder.setMessage(getString(R.string.confirmUserDeletion));
        builder.setPositiveButton(getString(R.string.confirm),
                (dialog, which) -> {
            pb.setVisibility(View.VISIBLE);
                    SharedPreferences sp = getSharedPreferences("shared preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putBoolean("biometricPreference", false);
                    editor.putBoolean("allowBiometricPrompt", false);
                    editor.putString("email", "");
                    editor.putString("password", "");
                    editor.putString("name", "");
                    editor.apply();
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    Objects.requireNonNull(auth.getCurrentUser()).delete();
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection("users").document(thisUser.getUserName()).delete();
                    thisUser = null;
                    Toast.makeText(mContext, getString(R.string.profileDeletedSuccessfully), Toast.LENGTH_LONG).show();
                    Intent validate = new Intent(mContext, Logon.class);
                    validate.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    validate.putExtra("Welcome", true);
                    startActivity(validate);
                    pb.setVisibility(View.GONE);
                });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

}