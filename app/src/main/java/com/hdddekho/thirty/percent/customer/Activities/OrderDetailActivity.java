package com.hdddekho.thirty.percent.customer.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Models.OrderModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.OrderResponse;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private String orderId, transactionId, orderStatus;
    private Toolbar order_detail_toolbar;
    private ImageView orderProductImg;
    private LinearProgressIndicator detailOrderProgress;
    private TextView cancelledText, tvOrderDate, tvOrderNum, tvTransactionNum, tvProductName, tvPaymentMode, tvMRP, tvDiscount, tvQuantity, tvTotalAmount;
    private List<OrderModel> orderList;
    private AppCompatButton cancel_order_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        findIds();
        makeToolbar();

        if (getIntent().getStringExtra("order_id") != null)
            orderId = getIntent().getStringExtra("order_id");

        orderList = new ArrayList<>();

        fetchMyOrder();



        cancel_order_btn.setOnClickListener(view -> {
            if (Objects.equals(transactionId, "NIL")) {
                CancelOrder();
            } else {
                RefundMoney();
            }
        });


    }

    private void RefundMoney() {
    }

    private void CancelOrder() {

        RequestBody ORDER_ID = RequestBody.create(orderId, MultipartBody.FORM);
        RequestBody ORDER_STATUS = RequestBody.create("Cancelled", MultipartBody.FORM);

        ApiClient.getApiInterface().cancelOrder(ORDER_ID,ORDER_STATUS)
                .enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getStatus().equals("01"))
                                    Toast.makeText(OrderDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(OrderDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(OrderDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(OrderDetailActivity.this, "Something went wrong! " + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchMyOrder() {
        ApiClient.getApiInterface().fetchOrders()
                .enqueue(new Callback<OrderResponse>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        if (response.body().getStatus().equals("1")) {
                            detailOrderProgress.setVisibility(View.GONE);
                            orderList = response.body().getData();

                            for (int i = 0; i < orderList.size(); i++) {
                                if (orderList.get(i).getOrder_id().equals(orderId)) {
                                    Glide.with(getApplicationContext()).load("https://30app.hdddekho.com/ProductImages/" + orderList.get(i).getImage1()).into(orderProductImg);

                                    transactionId = orderList.get(i).getTransaction_id();
                                    orderStatus = orderList.get(i).getOrder_status();

                                    if (Objects.equals(orderStatus, "Cancelled")) {
                                        cancel_order_btn.setVisibility(View.GONE);
                                        cancelledText.setVisibility(View.VISIBLE);
                                    }

                                    tvProductName.setText(orderList.get(i).getProduct_name());
                                    tvOrderDate.setText("Order Date - " + orderList.get(i).getOrder_date());
                                    tvOrderNum.setText("Order Number - " + orderList.get(i).getOrder_number());
                                    tvQuantity.setText("Quantity - " + orderList.get(i).getQuantity());
                                    tvDiscount.setText(orderList.get(i).getDiscount() + "% Off");
                                    tvMRP.setText("MRP - " + "\u20B9" + orderList.get(i).getMrp());
                                    tvPaymentMode.setText("Payment Mode - " + orderList.get(i).getPayment_mode());
                                    tvTotalAmount.setText("Total - " + "\u20B9" + orderList.get(i).getTotal_amount());
                                    if (!Objects.equals(orderList.get(i).getTransaction_id(), "NIL"))
                                        tvTransactionNum.setText("Transaction Id - " + orderList.get(i).getTransaction_id());
                                    else
                                        tvTransactionNum.setVisibility(View.GONE);
                                }
                            }

                        } else {
                            detailOrderProgress.setVisibility(View.GONE);
                            Toast.makeText(OrderDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        detailOrderProgress.setVisibility(View.GONE);
                        Toast.makeText(OrderDetailActivity.this, "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void findIds() {
        tvMRP = findViewById(R.id.tvMRP);
        tvDiscount = findViewById(R.id.tvDiscount);
        tvOrderNum = findViewById(R.id.tvOrderNum);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvOrderDate = findViewById(R.id.tvOrderDate);
        cancelledText = findViewById(R.id.cancelledText);
        tvPaymentMode = findViewById(R.id.tvPaymentMode);
        tvProductName = findViewById(R.id.tvProductName);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        orderProductImg = findViewById(R.id.orderProductImg);
        tvTransactionNum = findViewById(R.id.tvTransactionNum);
        cancel_order_btn = findViewById(R.id.cancel_order_btn);
        detailOrderProgress = findViewById(R.id.detailOrderProgress);
        order_detail_toolbar = findViewById(R.id.order_detail_toolbar);
    }

    private void makeToolbar() {
        setSupportActionBar(order_detail_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Order Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        order_detail_toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

}