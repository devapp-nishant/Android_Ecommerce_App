
package com.hdddekho.thirty.percent.customer.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Models.CustomerModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.CustomerResponse;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private String customerId,customerName, customerPhone, customerEmail, customerAddress, customerCity, customerPincode;
    private TextView txtCusName,txtCusPhone,txtCusEmail,txtCusAddress;
    private MaterialCardView profileCard;
    private boolean isAddressAvailable;
    private Dialog signUpDialog;
    private List<CustomerModel> customerList;
    SharedPreferences customerPref;
    SharedPreferences.Editor customerEditor;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        txtCusName = findViewById(R.id.txtCusName);
        txtCusPhone = findViewById(R.id.txtCusPhone);
        txtCusEmail = findViewById(R.id.txtCusEmail);
        txtCusAddress = findViewById(R.id.txtCusAddress);
        profileCard = findViewById(R.id.profileCard);

        customerPref = this.getSharedPreferences("CUSTOMER", MODE_PRIVATE);
        customerEditor = customerPref.edit();

        customerId = customerPref.getString("CUSTOMER_ID", "");


        showSignUpDialog();
        fetchCustomer();

    }


    private void fetchCustomer() {

        RequestBody CusId = RequestBody.create(customerId, MultipartBody.FORM);

        ApiClient.getApiInterface().getCustomerID(CusId)
                .enqueue(new Callback<CustomerResponse>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        assert response.body() != null;

                        if (response.body().getStatus().equals("1")) {

                            customerList = response.body().getData();

                            for (int i = 0; i < customerList.size(); i++) {

                                if (customerList.get(i).getCustomer_id().equals(customerId)) {
                                    customerName = customerList.get(i).getCustomer_name();
                                    customerPhone = customerList.get(i).getCustomer_phone();
                                    customerEmail = customerList.get(i).getCustomer_email();
                                    customerAddress = customerList.get(i).getCustomer_address();
                                    customerCity = customerList.get(i).getCustomer_city();
                                    customerPincode= customerList.get(i).getCustomer_pincode();

                                    if (customerName != null || customerAddress != null){
                                        profileCard.setVisibility(View.VISIBLE);
                                        txtCusName.setText(customerName);
                                        txtCusPhone.setText(customerPhone);
                                        txtCusEmail.setText(customerEmail);
                                        txtCusAddress.setText(customerAddress + ", " + customerCity + ", " + customerPincode);
                                    }
                                    else {
                                        profileCard.setVisibility(View.GONE);
                                        signUpDialog.show();
                                    }


                                }
                            }

                        } else {
                            Toast.makeText(ProfileActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        Toast.makeText(ProfileActivity.this, "Something went wrong!\nPlease Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchCustomer();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fetchCustomer();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void showSignUpDialog() {
        signUpDialog = new Dialog(ProfileActivity.this);
        signUpDialog.setContentView(R.layout.sign_up_alert_design);
        signUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        signUpDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.login_alert_bg));
        signUpDialog.setCancelable(true);

        AppCompatButton alertLoginBtn = signUpDialog.findViewById(R.id.alertLoginBtn);
        ImageView loginCloseBtn = signUpDialog.findViewById(R.id.loginCloseBtn);

        alertLoginBtn.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, SignUpActivity.class));
        });

        loginCloseBtn.setOnClickListener(view -> {
            signUpDialog.dismiss();
            finish();
        });
    }

}