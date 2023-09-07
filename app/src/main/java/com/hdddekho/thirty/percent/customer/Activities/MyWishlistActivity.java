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
import com.hdddekho.thirty.percent.customer.Adapters.WishlistAdapter;
import com.hdddekho.thirty.percent.customer.Models.WishlistModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.WishlistResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyWishlistActivity extends AppCompatActivity {

    private Toolbar wishlist_toolbar;
    private LinearProgressIndicator wishlistProgress;
    private RecyclerView wishlistRecView;
    private List<WishlistModel> wishlistModel;
    private WishlistAdapter wishAdapter;
    private String Customer_ID;
    SharedPreferences customerPref;
    SharedPreferences.Editor customerEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_wishlist);

        findIds();
        makeToolbar();

        wishlistModel = new ArrayList<>();
        wishlistRecView.setLayoutManager(new LinearLayoutManager(this));
        wishlistProgress.setVisibility(View.VISIBLE);

        customerPref = getSharedPreferences("CUSTOMER", MODE_PRIVATE);
        customerEditor = customerPref.edit();

        Customer_ID = customerPref.getString("CUSTOMER_ID", "");


        fetchWishlist();




    }

    private void fetchWishlist() {
        RequestBody cusId = RequestBody.create(Customer_ID, MultipartBody.FORM);

        ApiClient.getApiInterface().fetchMyWishlist(cusId)
                .enqueue(new Callback<WishlistResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<WishlistResponse> call, Response<WishlistResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                wishlistProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    wishlistModel = response.body().getData();
                                    wishAdapter = new WishlistAdapter(MyWishlistActivity.this, wishlistModel);
                                    wishlistRecView.setAdapter(wishAdapter);
                                    wishAdapter.notifyDataSetChanged();

                                } else {
                                    wishlistProgress.setVisibility(View.GONE);
                                    Toast.makeText(MyWishlistActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                wishlistProgress.setVisibility(View.GONE);
                                Toast.makeText(MyWishlistActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<WishlistResponse> call, Throwable t) {
                        Toast.makeText(MyWishlistActivity.this, "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        fetchWishlist();
    }

    private void findIds() {
        wishlist_toolbar = findViewById(R.id.wishlist_toolbar);
        wishlistProgress = findViewById(R.id.wishlistProgress);
        wishlistRecView = findViewById(R.id.wishlistRecView);
    }
    private void makeToolbar() {
        setSupportActionBar(wishlist_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("My Wishlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wishlist_toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }

}