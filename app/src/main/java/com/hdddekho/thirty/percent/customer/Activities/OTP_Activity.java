package com.hdddekho.thirty.percent.customer.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;

import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OTP_Activity extends AppCompatActivity {

    private String otp1, otp2, otp3, otp4, otp5, otp6, phoneNumber, countryCode, verificationId;
    private ProgressBar otpProgressBar;
    private TextView tvPhone;
    private TextInputEditText edtOTP1, edtOTP2, edtOTP3, edtOTP4, edtOTP5, edtOTP6;
    private AppCompatButton verifyOtpBtn;
    private SharedPreferences loginPreferences, profilePreference;
    private SharedPreferences.Editor profileEditor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        findIds();

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        countryCode = getIntent().getStringExtra("countryCode");
        verificationId = getIntent().getStringExtra("verificationId");

        loginPreferences = getSharedPreferences("loginPref", MODE_PRIVATE);

        profilePreference = this.getSharedPreferences("PhonePref", MODE_PRIVATE);
        profileEditor = profilePreference.edit();

        tvPhone.setText("Enter One Time Password we have sent on\n" + "+" + countryCode + "-" + phoneNumber);

        switchToNextEditText();


        verifyOtpBtn.setOnClickListener(view -> {

            otp1 = Objects.requireNonNull(edtOTP1.getText()).toString().trim();
            otp2 = Objects.requireNonNull(edtOTP2.getText()).toString().trim();
            otp3 = Objects.requireNonNull(edtOTP3.getText()).toString().trim();
            otp4 = Objects.requireNonNull(edtOTP4.getText()).toString().trim();
            otp5 = Objects.requireNonNull(edtOTP5.getText()).toString().trim();
            otp6 = Objects.requireNonNull(edtOTP6.getText()).toString().trim();

            otpProgressBar.setVisibility(View.VISIBLE);
            verifyOtpBtn.setVisibility(View.GONE);

            if (otp1.isEmpty() || otp2.isEmpty() || otp3.isEmpty() || otp4.isEmpty() || otp5.isEmpty() || otp6.isEmpty()) {
                Toast.makeText(this, "Please enter OTP to proceed", Toast.LENGTH_SHORT).show();
            } else {
                if (verificationId != null) {
                    String enteredOtp = Objects.requireNonNull(edtOTP1.getText()).toString().trim() +
                            Objects.requireNonNull(edtOTP2.getText()).toString().trim() +
                            Objects.requireNonNull(edtOTP3.getText()).toString().trim() +
                            Objects.requireNonNull(edtOTP4.getText()).toString().trim() +
                            Objects.requireNonNull(edtOTP5.getText()).toString().trim() +
                            Objects.requireNonNull(edtOTP6.getText()).toString().trim();

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOtp);
                    FirebaseAuth.getInstance()
                            .signInWithCredential(credential)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        otpProgressBar.setVisibility(View.GONE);
                                        verifyOtpBtn.setVisibility(View.GONE);
                                        loginPreferences.edit().putBoolean("IS_LOGGED_IN", true).apply();
                                        profileEditor.putString("USER_PHONE", phoneNumber);
                                        profileEditor.apply();
                                        addPhoneToDatabase();

                                    } else {
                                        otpProgressBar.setVisibility(View.GONE);
                                        verifyOtpBtn.setVisibility(View.VISIBLE);
                                        Toast.makeText(OTP_Activity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                }
            }


        });

    }



    private void addPhoneToDatabase() {
        RequestBody Phone = RequestBody.create(phoneNumber, MultipartBody.FORM);

        ApiClient.getApiInterface().loginCustomer(Phone)
                .enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    Toast.makeText(OTP_Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    savePreference();
                                    startActivity(new Intent(OTP_Activity.this, MainActivity.class));
                                    finishAffinity();
                                } else if (response.body().getStatus().equals("01")) {
                                    Toast.makeText(OTP_Activity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    savePreference();
                                    startActivity(new Intent(OTP_Activity.this, MainActivity.class));
                                    finishAffinity();
                                } else {
                                    Toast.makeText(OTP_Activity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }

                            } else {
                                Toast.makeText(OTP_Activity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(OTP_Activity.this, "Something went wrong!"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void savePreference() {
        profileEditor.putString("USER_PHONE", phoneNumber);
        profileEditor.apply();
    }

    private void switchToNextEditText() {
        edtOTP1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtOTP2.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtOTP3.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtOTP4.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtOTP5.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtOTP5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                edtOTP6.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void startMainActivity() {
        startActivity(new Intent(OTP_Activity.this, MainActivity.class));
        finish();
    }

    private void findIds() {
        tvPhone = findViewById(R.id.tvPhone);
        edtOTP1 = findViewById(R.id.edtOTP1);
        edtOTP2 = findViewById(R.id.edtOTP2);
        edtOTP3 = findViewById(R.id.edtOTP3);
        edtOTP4 = findViewById(R.id.edtOTP4);
        edtOTP5 = findViewById(R.id.edtOTP5);
        edtOTP6 = findViewById(R.id.edtOTP6);
        verifyOtpBtn = findViewById(R.id.verifyOtpBtn);
        otpProgressBar = findViewById(R.id.otpProgressBar);
    }
}