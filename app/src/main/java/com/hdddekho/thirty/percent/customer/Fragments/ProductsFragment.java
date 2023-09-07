package com.hdddekho.thirty.percent.customer.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductsFragment extends Fragment {
    private View productsView;
    private RecyclerView allProductsRecView;
    private LinearProgressIndicator searchProgress;
    private SearchView searchBar;
    private List<ProductModel> productList;
    private ProductsAdapter productAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productsView = inflater.inflate(R.layout.fragment_products, container, false);
        findIds();

        productList = new ArrayList<>();
        allProductsRecView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        searchProgress.setVisibility(View.VISIBLE);

        showAllProducts();

        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        return productsView;
    }

    private void findIds() {
        allProductsRecView = productsView.findViewById(R.id.allProductsRecView);
        searchProgress = productsView.findViewById(R.id.searchProgress);
        searchBar = productsView.findViewById(R.id.searchBar);
    }

    private void filterProduct(String newText) {
        List<ProductModel> plist = new ArrayList<>();
        for (ProductModel list : productList) {
            if (list.getProduct_desc().toLowerCase().contains(newText.toLowerCase())) {
                plist.add(list);
            }
        }
        if (!plist.isEmpty()) {
            productAdapter.filterProducts(plist);
        }
    }


    private void showAllProducts() {

        ApiClient.getApiInterface().getAllProducts()
                .enqueue(new Callback<ProductResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                searchProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    productList = response.body().getData();
                                    productAdapter = new ProductsAdapter(getContext(), productList);
                                    allProductsRecView.setAdapter(productAdapter);
                                    productAdapter.notifyDataSetChanged();

                                } else {
                                    searchProgress.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                searchProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }


                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}