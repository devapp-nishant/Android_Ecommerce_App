package com.hdddekho.thirty.percent.customer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import com.hdddekho.thirty.percent.customer.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity {

    private TextInputEditText edtPhoneLogin;
    private CountryCodePicker countryCodePicker;
    String countryCode, phoneNumber;
    private AppCompatButton sendOtpBtn;
    private FirebaseAuth phoneAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private ProgressBar phoneProgressBar;
    private SharedPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        FirebaseApp.initializeApp(this);
        findIDs();

        countryCode = countryCodePicker.getSelectedCountryCode();

        phoneAuth = FirebaseAuth.getInstance();

        loginPreferences = getSharedPreferences("loginPref", MODE_PRIVATE);

        boolean isLoggedIn = loginPreferences.getBoolean("IS_LOGGED_IN", false);
        if (isLoggedIn) {
            startMainActivity();
        }

        sendOtpBtn.setOnClickListener(view -> {
            phoneNumber = Objects.requireNonNull(edtPhoneLogin.getText()).toString().trim().replace(" ", "");
            if (phoneNumber.isEmpty()) {
                edtPhoneLogin.requestFocus();
                edtPhoneLogin.setError("Phone is required");
            } else if (phoneNumber.length() != 10) {
                edtPhoneLogin.requestFocus();
                edtPhoneLogin.setError("Invalid phone");
            } else if (!Patterns.PHONE.matcher(phoneNumber).matches()) {
                edtPhoneLogin.requestFocus();
                edtPhoneLogin.setError("Invalid phone");
            } else if (!phoneNumber.startsWith("6") && !phoneNumber.startsWith("7") && !phoneNumber.startsWith("8") && !phoneNumber.startsWith("9")) {
                edtPhoneLogin.requestFocus();
                edtPhoneLogin.setError("Invalid phone");
            } else {
//                sendOtpBtn.setVisibility(View.GONE);
                sendOTP();
            }
        });


    }

    private void sendOTP() {

        phoneProgressBar.setVisibility(View.VISIBLE);
        sendOtpBtn.setVisibility(View.GONE);

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                phoneProgressBar.setVisibility(View.GONE);
                sendOtpBtn.setVisibility(View.VISIBLE);
                Toast.makeText(PhoneLoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                phoneProgressBar.setVisibility(View.GONE);
                sendOtpBtn.setVisibility(View.VISIBLE);
                Intent otpIntent = new Intent(PhoneLoginActivity.this, OTP_Activity.class);
                otpIntent.putExtra("phoneNumber", phoneNumber);
                otpIntent.putExtra("countryCode", countryCode);
                otpIntent.putExtra("verificationId", verificationId);
                startActivity(otpIntent);
            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(phoneAuth)
                        .setPhoneNumber("+" + countryCode + phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS)          // Timeout and unit
                        .setActivity(this)                                  // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)                           // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);


    }


    private void startMainActivity() {
        startActivity(new Intent(PhoneLoginActivity.this, MainActivity.class));
        finishAffinity();
    }

    private void findIDs() {
        countryCodePicker = findViewById(R.id.countryCodePicker);
        phoneProgressBar = findViewById(R.id.phoneProgressBar);
        edtPhoneLogin = findViewById(R.id.edtPhoneLogin);
        sendOtpBtn = findViewById(R.id.sendOtpBtn);
    }

}