package com.hdddekho.thirty.percent.customer.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.material.textfield.TextInputEditText;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Models.CustomerModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.CustomerResponse;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    private TextInputEditText edtSignUpName, edtSignUpEmail, edtSignUpPhone, edtSignUpAddress, edtSignUpCity, edtSignUpPin;
    private AppCompatButton SignUpBtn;
    private ProgressBar signUpProgress;
    private String CustomerId, name, email, phone, address, city, zipCode;
    private List<CustomerModel> customerList;
    private SharedPreferences customerPref;
    private SharedPreferences.Editor customerEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findIds();

        customerPref = getSharedPreferences("CUSTOMER", MODE_PRIVATE);
        customerEditor = customerPref.edit();

        CustomerId = customerPref.getString("CUSTOMER_ID", "");

        fetchCustomerData();

        SignUpBtn.setOnClickListener(view -> {
            name = edtSignUpName.getText().toString().trim();
            email = edtSignUpEmail.getText().toString().trim();
            address = edtSignUpAddress.getText().toString().trim();
            city = edtSignUpCity.getText().toString().trim();
            zipCode = edtSignUpPin.getText().toString().trim();

            RequestBody CUS_ID = RequestBody.create(CustomerId, MultipartBody.FORM);
            RequestBody NAME = RequestBody.create(name, MultipartBody.FORM);
            RequestBody EMAIL = RequestBody.create(email, MultipartBody.FORM);
            RequestBody ADDRESS = RequestBody.create(address, MultipartBody.FORM);
            RequestBody CITY = RequestBody.create(city, MultipartBody.FORM);
            RequestBody ZIP = RequestBody.create(zipCode, MultipartBody.FORM);

            ApiClient.getApiInterface().signUpCustomer(CUS_ID,NAME,EMAIL,ADDRESS,CITY,ZIP)
                    .enqueue(new Callback<SuccessResponse>() {
                        @Override
                        public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                            try {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    if (response.body().getStatus().equals("01")) {
                                        // After Placing Order from Cart, cart must be clear !!
                                        Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(SignUpActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(SignUpActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("exp", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessResponse> call, Throwable t) {
                            Toast.makeText(SignUpActivity.this, "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });

    }

    private void fetchCustomerData() {

        RequestBody CusId = RequestBody.create(CustomerId, MultipartBody.FORM);

        ApiClient.getApiInterface().getCustomerID(CusId)
                .enqueue(new Callback<CustomerResponse>() {
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        assert response.body() != null;

                        if (response.body().getStatus().equals("1")) {

                            customerList = response.body().getData();

                            for (int i = 0; i < customerList.size(); i++) {

                                if (customerList.get(i).getCustomer_id().equals(CustomerId)) {
                                    name = customerList.get(i).getCustomer_name();
                                    email = customerList.get(i).getCustomer_email();
                                    phone = customerList.get(i).getCustomer_phone();
                                    address = customerList.get(i).getCustomer_address();
                                    city = customerList.get(i).getCustomer_city();
                                    zipCode = customerList.get(i).getCustomer_pincode();

                                    if (name != null)
                                        edtSignUpName.setText(name);
                                    if (email!= null)
                                        edtSignUpEmail.setText(email);
                                    if (phone!= null)
                                        edtSignUpPhone.setText(phone);
                                    if (address!= null)
                                        edtSignUpAddress.setText(address);
                                    if (city!= null)
                                        edtSignUpCity.setText(city);
                                    if (zipCode!= null)
                                        edtSignUpPin.setText(zipCode);



                                }
                            }

                        } else {
                            Toast.makeText(SignUpActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        Toast.makeText(SignUpActivity.this, "Something went wrong!\nPlease Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void findIds() {
        SignUpBtn = findViewById(R.id.SignUpBtn);
        signUpProgress = findViewById(R.id.signUpProgress);
        edtSignUpName = findViewById(R.id.edtSignUpName);
        edtSignUpEmail = findViewById(R.id.edtSignUpEmail);
        edtSignUpPhone = findViewById(R.id.edtSignUpPhone);
        edtSignUpAddress = findViewById(R.id.edtSignUpAddress);
        edtSignUpCity = findViewById(R.id.edtSignUpCity);
        edtSignUpPin = findViewById(R.id.edtSignUpPin);
    }
}