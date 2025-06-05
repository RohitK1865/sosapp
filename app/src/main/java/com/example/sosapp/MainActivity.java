package com.example.sosapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH = 100;
    private static final int REQUEST_CALL_PERMISSION = 1;

    private TextView resultText;
    private Button listenBtn;

    private String userName = "Loading...";
    private boolean isListening = true;

    private String sosPhrase = "help me"; // default fallback
    private String emergencyContact1 = null;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find views
        resultText = findViewById(R.id.result_text);
        listenBtn = findViewById(R.id.listen_btn);

        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.topAppBar);
        setSupportActionBar(toolbar);

        // 🔧 Initialize Firebase user
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            // Set the status bar color to your desired color (example: dark purple)
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.your_status_bar_color));
        }

        if (currentUser == null) {
            Toast.makeText(this, "No user found", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        fetchUserDataFromFirebase();

        listenBtn.setOnClickListener(v -> {
            if (isListening) {
                startSpeechRecognition();
            } else {
                Toast.makeText(this, "Listening is OFF", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchUserDataFromFirebase() {
        String uid = currentUser.getUid();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userDoc = db.collection("users").document(uid);

        userDoc.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String fetchedName = documentSnapshot.getString("name");
                if (fetchedName != null && !fetchedName.isEmpty()) {
                    userName = fetchedName;
                } else {
                    userName = "Unknown";
                }

                String fetchedSosPhrase = documentSnapshot.getString("sosPhrase");
                String fetchedContact1 = documentSnapshot.getString("emergencyContact1");

                if (fetchedSosPhrase != null && !fetchedSosPhrase.isEmpty()) {
                    sosPhrase = fetchedSosPhrase;
                }

                if (fetchedContact1 != null && !fetchedContact1.isEmpty()) {
                    emergencyContact1 = fetchedContact1;
                }

                invalidateOptionsMenu(); // refresh menu to show username

            } else {
                userName = "Unknown";
                invalidateOptionsMenu();
                Toast.makeText(this, "User data not found in database", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            userName = "Unknown";
            invalidateOptionsMenu();
            Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show();
        });
    }


    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your SOS phrase");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH);
        } catch (Exception e) {
            Toast.makeText(this, "Speech recognition not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String spokenText = results.get(0).toLowerCase();
                resultText.setText(spokenText);

                if (spokenText.contains(sosPhrase.toLowerCase())) {
                    makePhoneCall(emergencyContact1);
                }
            }
        }
    }

    private void makePhoneCall(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            Toast.makeText(this, "No emergency contact found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PERMISSION);
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CALL_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall(emergencyContact1);
            } else {
                Toast.makeText(this, "Call permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // No onCreateOptionsMenu needed because menu is set in XML on MaterialToolbar

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem userItem = menu.findItem(R.id.menu_username);
        userItem.setTitle("User: " + userName);  // always show current userName or default

        MenuItem toggleItem = menu.findItem(R.id.menu_toggle_listening);
        toggleItem.setChecked(isListening);
        toggleItem.setTitle(isListening ? "Listening: ON" : "Listening: OFF");

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_toggle_listening) {
            isListening = !item.isChecked();
            item.setChecked(isListening);
            item.setTitle(isListening ? "Listening: ON" : "Listening: OFF");
            Toast.makeText(this, isListening ? "Listening enabled" : "Listening disabled", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.menu_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}