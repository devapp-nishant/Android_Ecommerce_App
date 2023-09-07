package com.hdddekho.thirty.percent.customer.Activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Adapters.OrderAdapter;
import com.hdddekho.thirty.percent.customer.Models.OrderModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.OrderResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersActivity extends AppCompatActivity {

    private String Customer_ID;
    private Toolbar orders_toolbar;
    private LinearProgressIndicator orderProgress;
    private RecyclerView ordersRecView;
    private List<OrderModel> orderList;
    private OrderAdapter orderAdapter;
    SharedPreferences customerPref;
    SharedPreferences.Editor customerEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        findIds();
        makeToolbar();

        orderList = new ArrayList<>();
        ordersRecView.setLayoutManager(new LinearLayoutManager(this));
        orderProgress.setVisibility(View.VISIBLE);

        customerPref = getSharedPreferences("CUSTOMER", MODE_PRIVATE);
        customerEditor = customerPref.edit();

        Customer_ID = customerPref.getString("CUSTOMER_ID", "");

        fetchMyOrders();

    }

    private void fetchMyOrders() {
        ApiClient.getApiInterface().fetchOrders()
                .enqueue(new Callback<OrderResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                        try {
                            if (response.isSuccessful()){
                                orderProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {

                                    orderList = response.body().getData();

                                    for (int i = 0; i < orderList.size(); i++) {

                                        if (orderList.get(i).getCustomer_id().equals(Customer_ID)) {
                                            orderAdapter = new OrderAdapter(MyOrdersActivity.this, orderList);
                                            ordersRecView.setAdapter(orderAdapter);
                                            orderAdapter.notifyDataSetChanged();
                                        }
                                    }

                                } else {
                                    orderProgress.setVisibility(View.GONE);
                                    Toast.makeText(MyOrdersActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                orderProgress.setVisibility(View.GONE);
                                Toast.makeText(MyOrdersActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            Log.e("exp",e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<OrderResponse> call, Throwable t) {
                        orderProgress.setVisibility(View.GONE);
                        Toast.makeText(MyOrdersActivity.this, "Something went wrong!\n"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void makeToolbar() {

        setSupportActionBar(orders_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Orders");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        orders_toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

    private void findIds() {
        orders_toolbar = findViewById(R.id.orders_toolbar);
        ordersRecView = findViewById(R.id.ordersRecView);
        orderProgress = findViewById(R.id.orderProgress);
    }
}