package com.aurorasphere.agri;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 20;

    private ImageView googleAuth;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore firebaseFirestore;
    private GoogleSignInClient googleSignInClient;
    private ProgressDialog progressDialog;
    private EditText edit_name,edit_age, edit_module_code;
    private RadioGroup gender;
    String username,name,age,module,selectedGender;
    int genderId;
    RadioButton selectedRadioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edit_name = findViewById(R.id.edit_name);
        edit_age = findViewById(R.id.edit_age);
        edit_module_code = findViewById(R.id.edit_module_code);
        gender = findViewById(R.id.gender_rg);
        googleAuth = findViewById(R.id.google_auth);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        genderId = gender.getCheckedRadioButtonId();
        selectedRadioButton = findViewById(genderId);

        if (user != null) {
            SharedPreferences prefs = getSharedPreferences("onboarding_prefs", MODE_PRIVATE);
            boolean isFirstTime = prefs.getBoolean("first_time", true);
            if (isFirstTime) {
                startActivity(new Intent(MainActivity.this, OnboardingActivity.class));
                finish();
                return;
            }else {
                startActivity(new Intent(MainActivity.this, MainActivity2.class));
                finish();
                return;
            }

        }

        GoogleSignInOptions mGoogleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions);

        // Sign out of the GoogleSignInClient
        googleSignInClient.signOut();

        // Disable click initially
        googleAuth.setEnabled(false);
        googleAuth.setAlpha(0.5f); // Make it look visually disabled
        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkFields();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        edit_name.addTextChangedListener(textWatcher);
        edit_age.addTextChangedListener(textWatcher);
        edit_module_code.addTextChangedListener(textWatcher);
        gender.setOnCheckedChangeListener((group, checkedId) -> checkFields());

        googleAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

        // Initialize the ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.setCancelable(false);
    }
    private void checkFields() {
        name = edit_name.getText().toString().trim();
        age = edit_age.getText().toString().trim();
        module = edit_module_code.getText().toString().trim();

        boolean genderSelected = gender.getCheckedRadioButtonId() != -1;
        if (genderSelected) {
            int genderId = gender.getCheckedRadioButtonId();
            RadioButton selectedRadioButton = findViewById(genderId);
            selectedGender = selectedRadioButton.getText().toString();
        } else {
            selectedGender = ""; // or null if you prefer
        }
        boolean allFilled = !name.isEmpty() && !age.isEmpty() && !module.isEmpty() && genderSelected;

        googleAuth.setEnabled(allFilled);
        googleAuth.setAlpha(allFilled ? 1.0f : 0.5f); // Visual feedback: full or dimmed
    }
    private void googleSignIn() {

        progressDialog.show();  // Show the progress dialog
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuth(account.getIdToken());
                } else {
                    progressDialog.dismiss();  // Hide the progress dialog
                    Toast.makeText(this, getString(R.string.google_failed), Toast.LENGTH_SHORT).show();
                }
            } catch (ApiException e) {
                progressDialog.dismiss();  // Hide the progress dialog
                Toast.makeText(this, getString(R.string.google_failed) + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Google sign-in failed", e);
            }
        }
    }

    private void firebaseAuth(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                checkUserInFirestore(user);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, getString(R.string.authentication_failed), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Firebase authentication failed", task.getException());
                        }
                    }
                });
    }
    private void checkUserInFirestore(FirebaseUser user) {
        String userId = user.getUid();
        firebaseFirestore.collection("users").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (!documentSnapshot.exists()) {
                                createUserInFirestore(user);
                            } else {
                                navigateToMainActivity(user, documentSnapshot);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, getString(R.string.failed_to_check_user_existence), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to check user existence", task.getException());
                        }
                    }
                });
    }
    private void createUserInFirestore(FirebaseUser user) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", user.getUid());
        hashMap.put("user_name", user.getDisplayName());
        hashMap.put("name", name);
        hashMap.put("age", age);
        hashMap.put("module_code", module);
        hashMap.put("gender", selectedGender);

        username=user.getDisplayName();

        firebaseFirestore.collection("users").document(user.getUid())
                .set(hashMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            navigateToMainActivity(user, null);
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, getString(R.string.failed_to_store_data), Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "Failed to store user data", task.getException());
                        }
                    }
                });
    }
    private void navigateToMainActivity(FirebaseUser user, @Nullable DocumentSnapshot documentSnapshot) {

        if (documentSnapshot != null) {
//            plant = documentSnapshot.getLong("growPlant");
            username = documentSnapshot.getString("name");
        } else {
//            food = 0L;
//            plant = 0L;
        }

//        if (food != null && plant != null) {
//            db.insertInitialOrder(Math.toIntExact(food), Math.toIntExact(plant));
//            Log.d("Firestore", "data fetched");
//        } else {
//            Log.d("Firestore", "Current data: null");
//        }
        progressDialog.dismiss();  // Hide the progress dialog
        Intent intent = new Intent(MainActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish();
    }
}