package com.hdddekho.thirty.percent.customer.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Adapters.CategoryAdapter;
import com.hdddekho.thirty.percent.customer.Adapters.ProductsAdapter;
import com.hdddekho.thirty.percent.customer.Models.CategoryModel;
import com.hdddekho.thirty.percent.customer.Models.ProductModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.CategoryResponse;
import com.hdddekho.thirty.percent.customer.Response.ProductResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private View homeView;
    private RecyclerView homeFragCategoriesRec,newestProductRec,forYouProductRec;
    private List<CategoryModel> catList;
    private List<ProductModel> productList;
    private ProductsAdapter productsAdapter;
    private CategoryAdapter catAdapter;
    private LinearProgressIndicator catProgress;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeView = inflater.inflate(R.layout.fragment_home, container, false);

        catList = new ArrayList<>();
        productList = new ArrayList<>();

        catProgress = homeView.findViewById(R.id.catProgress);
        homeFragCategoriesRec = homeView.findViewById(R.id.homeFragCategoriesRec);
        newestProductRec = homeView.findViewById(R.id.newestProductRec);
        forYouProductRec = homeView.findViewById(R.id.forYouProductRec);

        homeFragCategoriesRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newestProductRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        forYouProductRec.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        catProgress.show();

        fetchCategories();
        fetchNewestProduct();
        fetchForYouProducts();

        return homeView;
    }

    private void fetchForYouProducts() {
        ApiClient.getApiInterface().fetchForYouProducts()
                .enqueue(new Callback<ProductResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                catProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    productList = response.body().getData();
                                    productsAdapter = new ProductsAdapter(getContext(), productList);
                                    forYouProductRec.setAdapter(productsAdapter);
                                    productsAdapter.notifyDataSetChanged();
                                } else {
                                    catProgress.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                catProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        catProgress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchNewestProduct() {


        ApiClient.getApiInterface().fetchNewProducts()
                .enqueue(new Callback<ProductResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                catProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    productList = response.body().getData();
                                    productsAdapter = new ProductsAdapter(getContext(), productList);
                                    newestProductRec.setAdapter(productsAdapter);
                                    productsAdapter.notifyDataSetChanged();
                                } else {
                                    catProgress.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                catProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        catProgress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCategories() {

        ApiClient.getApiInterface().getAllCategory()
                .enqueue(new Callback<CategoryResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                catProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    catList = response.body().getData();
                                    catAdapter = new CategoryAdapter(getContext(), catList);
                                    homeFragCategoriesRec.setAdapter(catAdapter);
                                    catAdapter.notifyDataSetChanged();
                                } else {
                                    catProgress.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                catProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<CategoryResponse> call, Throwable t) {
                        catProgress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}