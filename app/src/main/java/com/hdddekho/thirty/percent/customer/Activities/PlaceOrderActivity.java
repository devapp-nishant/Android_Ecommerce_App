package com.hdddekho.thirty.percent.customer.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Models.CustomerModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.CustomerResponse;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;
import com.hdddekho.thirty.percent.customer.Utils.OrderNumber;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlaceOrderActivity extends AppCompatActivity implements PaymentResultListener {

    private Toolbar place_order_toolbar;
    private List<CustomerModel> customerList;
    private String formattedDiscountPrice, CustomerId, productName, customerName, customerAddress, customerEmail, customerPhone, customerCity, zipCode,
            productImage, quantity, discount, price, productId, ORDER_NUMBER, orderStatus, deliveryStatus, TotalPrice;
    private CheckBox paymentModeCheckBox;
    private double priceAfterQuantity;
    private ProgressDialog orderDialog;
    private AppCompatButton continueOrderBtn;
    private TextView customerNameTxtView, totalPriceTxtView, txtDiscount, txtFinalPrice, customerAddTxtView, customerPhoneTxtView, txtPriceItem, txtPriceTotalItem, productNameTxtView;
    private ImageView productImg;
    private Dialog paymentDialog;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_order);

        findIds();
        makeToolbar();
        paymentDialogDisplay();

        orderStatus = "Ordered";
        deliveryStatus = "Pending";


        orderDialog = new ProgressDialog(this);
        orderDialog.setMessage("Please wait..");
        orderDialog.setCancelable(false);

        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));

        productName = getIntent().getStringExtra("productName");
        productId = getIntent().getStringExtra("productId");
        CustomerId = getIntent().getStringExtra("customerId");
        productImage = getIntent().getStringExtra("productImg1");
        quantity = getIntent().getStringExtra("quantity");
        discount = getIntent().getStringExtra("discount");
        price = getIntent().getStringExtra("mrp");
        priceAfterQuantity = Integer.parseInt(price) * Integer.parseInt(quantity);
        double discountPrice = Double.parseDouble(String.valueOf(priceAfterQuantity)) - (Double.parseDouble(String.valueOf(priceAfterQuantity)) * (Double.parseDouble(discount) / 100));
//        float discountPrice = Float.parseFloat(String.valueOf(priceAfterQuantity)) - (Float.parseFloat(String.valueOf(priceAfterQuantity)) * (Float.parseFloat(discount) / 100));

        // I'm using this TotalPrice separately to send in database //
        TotalPrice = String.valueOf(discountPrice);
        //--------------------------------------------------------//

        String formattedPrice = nf.format(priceAfterQuantity);
        formattedDiscountPrice = nf.format(discountPrice);

        fetchCustomer();

        Glide.with(this).load(productImage).into(productImg);
        productNameTxtView.setText(productName);
        txtPriceItem.setText("Price (" + quantity + " item)");
        txtPriceTotalItem.setText("\u20B9" + formattedPrice);
        txtDiscount.setText(discount + "% Off");
        txtFinalPrice.setText("\u20B9" + formattedDiscountPrice);

        totalPriceTxtView.setText("Total\n" + "\u20B9" + formattedDiscountPrice);


        continueOrderBtn.setOnClickListener(view -> {
            if (paymentModeCheckBox.isChecked())
                PlaceOrder("NIL", "COD");
            else {
                PayOnline();
            }
        });


    }

    private void PayOnline() {
        Checkout checkout = new Checkout();
//        checkout.setImage(R.mipmap.ic_launcher);
//        final Activity activity = this;
        JSONObject json = new JSONObject();
        try {
            int amount = Math.round(Float.parseFloat(TotalPrice) * 100);

            json.put("name", "30%");
            json.put("image", R.drawable.app_logo_);
            json.put("description", "Get everything at discounted price");
            json.put("currency", "INR");
            json.put("amount", amount);

            JSONObject prefill = new JSONObject();
            prefill.put("email", customerEmail);
            prefill.put("contact", customerPhone);

            json.put("prefill", prefill);

            checkout.open(PlaceOrderActivity.this, json);
        } catch (Exception e) {
            Toast.makeText(PlaceOrderActivity.this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void paymentDialogDisplay() {
        paymentDialog = new Dialog(PlaceOrderActivity.this);
        paymentDialog.setContentView(R.layout.order_success_alert);
        paymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paymentDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.login_alert_bg));
        paymentDialog.setCancelable(true);

        AppCompatButton okBtn = paymentDialog.findViewById(R.id.okPaymentBtn);
        ImageView loginCloseBtn = paymentDialog.findViewById(R.id.loginCloseBtn);

        okBtn.setOnClickListener(view -> {
            paymentDialog.dismiss();
            startActivity(new Intent(PlaceOrderActivity.this, MyOrdersActivity.class));
        });

        loginCloseBtn.setOnClickListener(view -> {
            paymentDialog.dismiss();
        });
    }

    private void PlaceOrder(String TransId, String PaymentMode) {

        OrderNumber orderNumber = new OrderNumber();
        ORDER_NUMBER = "ORD-" + orderNumber.GenerateOrderNumber(10);

        RequestBody cusId = RequestBody.create(CustomerId, MultipartBody.FORM);
        RequestBody proId = RequestBody.create(productId, MultipartBody.FORM);
        RequestBody QUANTITY = RequestBody.create(quantity, MultipartBody.FORM);
        RequestBody orderNo = RequestBody.create(ORDER_NUMBER, MultipartBody.FORM);
        RequestBody transId = RequestBody.create(TransId, MultipartBody.FORM);
        RequestBody pMode = RequestBody.create(PaymentMode, MultipartBody.FORM);
        RequestBody totalAmount = RequestBody.create(TotalPrice, MultipartBody.FORM);
        RequestBody OrderStatus = RequestBody.create(orderStatus, MultipartBody.FORM);
        RequestBody DeliveryStatus = RequestBody.create(deliveryStatus, MultipartBody.FORM);

        ApiClient.getApiInterface().placeOrder(cusId, proId, QUANTITY, orderNo, transId, pMode, totalAmount, OrderStatus, DeliveryStatus)
                .enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getStatus().equals("01")) {
                                    orderDialog.dismiss();
                                    paymentDialog.show();
                                } else {
                                    orderDialog.dismiss();
                                    Toast.makeText(PlaceOrderActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                orderDialog.dismiss();
                                Toast.makeText(PlaceOrderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(PlaceOrderActivity.this, "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void fetchCustomer() {

        RequestBody CusId = RequestBody.create(CustomerId, MultipartBody.FORM);

        ApiClient.getApiInterface().getCustomerID(CusId)
                .enqueue(new Callback<CustomerResponse>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        assert response.body() != null;

                        if (response.body().getStatus().equals("1")) {

                            customerList = response.body().getData();

                            for (int i = 0; i < customerList.size(); i++) {

                                if (customerList.get(i).getCustomer_id().equals(CustomerId)) {
                                    customerName = customerList.get(i).getCustomer_name();
                                    customerPhone = customerList.get(i).getCustomer_phone();
                                    customerEmail = customerList.get(i).getCustomer_email();
                                    customerAddress = customerList.get(i).getCustomer_address();
                                    customerCity = customerList.get(i).getCustomer_city();
                                    zipCode = customerList.get(i).getCustomer_pincode();

                                    customerNameTxtView.setText(customerName);
                                    customerAddTxtView.setText(customerAddress + "," + " " + customerCity + "," + " " + zipCode);
                                    customerPhoneTxtView.setText(customerPhone);
                                }
                            }

                        } else {
                            Toast.makeText(PlaceOrderActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        Toast.makeText(PlaceOrderActivity.this, "Something went wrong!\nPlease Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void findIds() {
        productImg = findViewById(R.id.productImg);
        txtPriceItem = findViewById(R.id.txtPriceItem);
        txtFinalPrice = findViewById(R.id.txtFinalPrice);
        txtDiscount = findViewById(R.id.txtDiscount);
        continueOrderBtn = findViewById(R.id.continueOrderBtn);
        txtPriceTotalItem = findViewById(R.id.txtPriceTotalItem);
        totalPriceTxtView = findViewById(R.id.totalPriceTxtView);
        customerAddTxtView = findViewById(R.id.customerAddTxtView);
        productNameTxtView = findViewById(R.id.productNameTxtView);
        place_order_toolbar = findViewById(R.id.place_order_toolbar);
        paymentModeCheckBox = findViewById(R.id.paymentModeCheckBox);
        customerNameTxtView = findViewById(R.id.customerNameTxtView);
        customerPhoneTxtView = findViewById(R.id.customerPhoneTxtView);
    }

    private void makeToolbar() {
        setSupportActionBar(place_order_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Order Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        place_order_toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }


    @Override
    public void onPaymentSuccess(String transactionId) {
        PlaceOrder(transactionId, "Online");

    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }
}