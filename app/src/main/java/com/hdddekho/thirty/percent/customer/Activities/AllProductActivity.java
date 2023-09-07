package com.hdddekho.thirty.percent.customer.Activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Adapters.ProductsAdapter;
import com.hdddekho.thirty.percent.customer.Models.ProductModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.ProductResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllProductActivity extends AppCompatActivity {

    private String cat_id, cat_name;
    private ProductsAdapter productsAdapter;
    private List<ProductModel> productModel;
    private Toolbar all_products_toolbar;
    private SearchView catProductsSearchBar;
    private LinearProgressIndicator catProductSearchProgress;
    private RecyclerView catProductRecView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_product);
        
        if (getIntent().getStringExtra("cat_id") != null) {
            cat_id = getIntent().getStringExtra("cat_id");
            cat_name = getIntent().getStringExtra("cat_name");
        }

        findIds();
        makeToolbar();

        productModel = new ArrayList<>();
        catProductRecView.setLayoutManager(new GridLayoutManager(AllProductActivity.this, 2));

        catProductSearchProgress.setVisibility(View.VISIBLE);

        showCategoryProducts();

        catProductsSearchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterProduct(newText);
                return true;
            }
        });

    }

    private void showCategoryProducts() {

        RequestBody CatId = RequestBody.create(cat_id, MultipartBody.FORM);

        ApiClient.getApiInterface().fetchCategoryProducts(CatId)
                .enqueue(new Callback<ProductResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                catProductSearchProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {

                                    productModel = response.body().getData();
                                    productsAdapter = new ProductsAdapter(getApplicationContext(), productModel);
                                    catProductRecView.setAdapter(productsAdapter);
                                    productsAdapter.notifyDataSetChanged();

                                } else {
                                    catProductSearchProgress.setVisibility(View.GONE);
                                    Toast.makeText(AllProductActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                catProductSearchProgress.setVisibility(View.GONE);
                                Toast.makeText(AllProductActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        catProductSearchProgress.setVisibility(View.GONE);
                        Toast.makeText(AllProductActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void filterProduct(String newText) {
        List<ProductModel> plist = new ArrayList<>();
        for (ProductModel list : productModel) {
            if (list.getProduct_desc().toLowerCase().contains(newText.toLowerCase())) {
                plist.add(list);
            }
        }
        if (!plist.isEmpty()) {
            productsAdapter.filterProducts(plist);
        }
    }

    private void makeToolbar() {
        setSupportActionBar(all_products_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(cat_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        all_products_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    private void findIds() {
        all_products_toolbar = findViewById(R.id.all_products_toolbar);
        catProductsSearchBar = findViewById(R.id.catProductsSearchBar);
        catProductSearchProgress = findViewById(R.id.catProductSearchProgress);
        catProductRecView = findViewById(R.id.catProductRecView);
    }
}