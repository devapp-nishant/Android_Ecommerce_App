package com.hdddekho.thirty.percent.customer.Activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Adapters.MediaSliderAdapter;
import com.hdddekho.thirty.percent.customer.Models.CustomerModel;
import com.hdddekho.thirty.percent.customer.Models.ProductModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.CustomerResponse;
import com.hdddekho.thirty.percent.customer.Response.ProductResponse;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;
import com.hdddekho.thirty.percent.customer.Utils.MediaItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class ProductDetailActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private Toolbar product_detail_toolbar;
    double discountPrice, rate;
    String productId, catId, customerID, customerAddress, phoneNumber, QUANTITY, productName, productDesc, productOriginalPrice, discount, productDiscountPrice, productImg;
    private TextView productNameDetail, productDiscountText, productDescDetail, productRateDetail, productDiscountPriceTxt;
    private ImageView lineHeartImg;
    private boolean isInWishlist, isAddressAvailable;
    private AppCompatAutoCompleteTextView quantitySpinner;
    private AppCompatButton addToCartBtn, buyNowBtn;
    private List<CustomerModel> customerList;
    private Dialog signUpDialog;
    SharedPreferences loginPreferences;
    SharedPreferences.Editor profileEditor;
    private List<ProductModel> productList;
    private MediaSliderAdapter sliderAdapter;
    private final List<MediaItem> mediaItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        findIds();
        makeToolbar();
        showSignUpDialog();


        if (getIntent().getStringExtra("product_id") != null) {
            productId = getIntent().getStringExtra("product_id");
            catId = getIntent().getStringExtra("category_id");
        }

        loginPreferences = getSharedPreferences("PhonePref", Context.MODE_PRIVATE);
        profileEditor = loginPreferences.edit();

        phoneNumber = loginPreferences.getString("USER_PHONE", "");

        displayProductDetail();
        fetchCustomerIdByPhone();

        quantitySpinner.setShowSoftInputOnFocus(false);
        String[] quantities = {"1", "2", "3", "4", "5"};
        ArrayAdapter<String> quantityAdapter = new ArrayAdapter<>(ProductDetailActivity.this, R.layout.dropdown_item_list, quantities);
        quantitySpinner.setAdapter(quantityAdapter);

        quantitySpinner.setOnItemClickListener((adapterView, view, i, l) -> {
            QUANTITY = quantitySpinner.getText().toString();
        });

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                isProductAvailableInWishlist();
//            }
//        }, 200); // 0.1 Sec delay

        lineHeartImg.setOnClickListener(view -> {
            if (isInWishlist)
                deleteFromWishlist();
            else
                addToWishlist();
        });

        addToCartBtn.setOnClickListener(view -> {
            if (QUANTITY == null)
                quantitySpinner.setError("Quantity Required!");
            else
                AddToCart();
        });

        buyNowBtn.setOnClickListener(view -> {
            if (QUANTITY == null)
                quantitySpinner.setError("Quantity Required!");
            else if (!isAddressAvailable)
                signUpDialog.show();
            else
                GoToOrderDetailActivity();
        });

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void showSignUpDialog() {
        signUpDialog = new Dialog(ProductDetailActivity.this);
        signUpDialog.setContentView(R.layout.sign_up_alert_design);
        signUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        signUpDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.login_alert_bg));
        signUpDialog.setCancelable(true);

            AppCompatButton alertLoginBtn = signUpDialog.findViewById(R.id.alertLoginBtn);
            ImageView loginCloseBtn = signUpDialog.findViewById(R.id.loginCloseBtn);

            alertLoginBtn.setOnClickListener(view -> {
                startActivity(new Intent(ProductDetailActivity.this, SignUpActivity.class));
            });

            loginCloseBtn.setOnClickListener(view -> {
                signUpDialog.dismiss();
            });
    }

    private void GoToOrderDetailActivity() {
        Intent orderDetailIntent = new Intent(ProductDetailActivity.this, PlaceOrderActivity.class);
        orderDetailIntent.putExtra("productId", productId);
        orderDetailIntent.putExtra("customerId", customerID);
        orderDetailIntent.putExtra("categoryId", catId);
        orderDetailIntent.putExtra("productName", productName);
        orderDetailIntent.putExtra("productImg1", productImg);
        orderDetailIntent.putExtra("quantity", QUANTITY);
        orderDetailIntent.putExtra("discount", discount);
        orderDetailIntent.putExtra("rate", discountPrice);
        orderDetailIntent.putExtra("mrp", productOriginalPrice);

        startActivity(orderDetailIntent);
    }

    private void displayProductDetail() {

        ApiClient.getApiInterface().getAllProducts()
                .enqueue(new Callback<ProductResponse>() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onResponse(Call<ProductResponse> call, Response<ProductResponse> response) {
                        try {
                            if (response.isSuccessful()) {
//                              progressIndicator.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {

                                    productList = response.body().getData();

                                    for (int i = 0; i < productList.size(); i++) {

                                        if (productList.get(i).getProduct_id().equals(productId)) {

                                            // To set StrikeThrough
                                            productRateDetail.setPaintFlags(productRateDetail.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                                            productName = productList.get(i).getProduct_name();
                                            productDesc = productList.get(i).getProduct_desc();
                                            discount = productList.get(i).getDiscount();
                                            productOriginalPrice = productList.get(i).getMrp();

                                            rate = Double.parseDouble(productOriginalPrice);

                                            discountPrice = Double.parseDouble(productOriginalPrice) - (Double.parseDouble(productOriginalPrice) * (Double.parseDouble(discount) / 100));

                                            NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
                                            String formattedDiscountPrice = nf.format(discountPrice);
                                            String formattedOriginalPrice = nf.format(rate);

                                            productImg = "https://30app.hdddekho.com/ProductImages/" + productList.get(i).getImage1();

                                            productNameDetail.setText(productName);
                                            productDescDetail.setText(productDesc);
                                            productRateDetail.setText("\u20B9" + formattedOriginalPrice);
                                            productDiscountPriceTxt.setText("\u20B9" + formattedDiscountPrice);
                                            productDiscountText.setText(discount + "% Off");

                                            mediaItems.add(new MediaItem(MediaItem.TYPE_IMAGE,"https://30app.hdddekho.com/ProductImages/" + productList.get(i).getImage1()));
                                            mediaItems.add(new MediaItem(MediaItem.TYPE_VIDEO,"https://30app.hdddekho.com/ProductImages/" + productList.get(i).getVideo1()));
                                            mediaItems.add(new MediaItem(MediaItem.TYPE_IMAGE,"https://30app.hdddekho.com/ProductImages/" + productList.get(i).getImage2()));
                                            mediaItems.add(new MediaItem(MediaItem.TYPE_VIDEO,"https://30app.hdddekho.com/ProductImages/" + productList.get(i).getVideo2()));
                                            mediaItems.add(new MediaItem(MediaItem.TYPE_IMAGE,"https://30app.hdddekho.com/ProductImages/" + productList.get(i).getImage3()));
                                            mediaItems.add(new MediaItem(MediaItem.TYPE_IMAGE,"https://30app.hdddekho.com/ProductImages/" + productList.get(i).getImage4()));

                                            sliderAdapter = new MediaSliderAdapter(ProductDetailActivity.this, mediaItems, viewPager);
                                            viewPager.setAdapter(sliderAdapter);

                                            isProductAvailableInWishlist();

                                        }
                                    }

                                } else {
//                                    progressIndicator.setVisibility(View.GONE);
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
//                                progressIndicator.setVisibility(View.GONE);
                                Toast.makeText(ProductDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }


                    @Override
                    public void onFailure(Call<ProductResponse> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fetchCustomerIdByPhone() {
        RequestBody PHONE = RequestBody.create(phoneNumber, MultipartBody.FORM);

        ApiClient.getApiInterface().getCustomerPhoneData(PHONE)
                .enqueue(new Callback<CustomerResponse>() {
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        assert response.body() != null;

                        if (response.body().getStatus().equals("1")) {

                            customerList = response.body().getData();

                            for (int i = 0; i < customerList.size(); i++) {

                                if (customerList.get(i).getCustomer_phone().equals(phoneNumber)) {
                                    customerID = customerList.get(i).getCustomer_id();

                                    customerAddress = customerList.get(i).getCustomer_address();

                                    isAddressAvailable = customerAddress != null;

                                    SharedPreferences customerPref = getSharedPreferences("CUSTOMER", MODE_PRIVATE);
                                    SharedPreferences.Editor customerEditor = customerPref.edit();
                                    customerEditor.putString("CUSTOMER_ID", customerID);
                                    customerEditor.apply();

                                }
                            }

                        } else {
                            Toast.makeText(ProductDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Something went wrong!\n"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void AddToCart() {

        RequestBody cusId = RequestBody.create(customerID, MultipartBody.FORM);
        RequestBody proId = RequestBody.create(productId, MultipartBody.FORM);
        RequestBody quantity = RequestBody.create(QUANTITY, MultipartBody.FORM);

        ApiClient.getApiInterface().AddToCart(cusId, proId, quantity)
                .enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else if (response.body().getStatus().equals("01")) {
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(ProductDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void isProductAvailableInWishlist() {

        RequestBody cusId = RequestBody.create(customerID, MultipartBody.FORM);
        RequestBody proId = RequestBody.create(productId, MultipartBody.FORM);

        ApiClient.getApiInterface().checkWishlist(cusId, proId)
                .enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<SuccessResponse> call, @NonNull Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    lineHeartImg.setImageResource(R.drawable.red_heart);
                                    isInWishlist = true;

                                } else {
                                    lineHeartImg.setImageResource(R.drawable.line_heart);
                                    isInWishlist = false;
                                }
                            } else {
                                Toast.makeText(ProductDetailActivity.this, "Facing problem in displaying wishlist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<SuccessResponse> call, @NonNull Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addToWishlist() {

        RequestBody cusId = RequestBody.create(customerID, MultipartBody.FORM);
        RequestBody proId = RequestBody.create(productId, MultipartBody.FORM);

        ApiClient.getApiInterface().addWishlist(cusId, proId)
                .enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
//                              progressIndicator.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("01")) {
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    lineHeartImg.setImageResource(R.drawable.red_heart);

                                } else {
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    isInWishlist = false;
                                }
                            } else {
                                Toast.makeText(ProductDetailActivity.this, "Facing problem in displaying wishlist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteFromWishlist() {

        RequestBody cusId = RequestBody.create(customerID, MultipartBody.FORM);
        RequestBody proId = RequestBody.create(productId, MultipartBody.FORM);

        ApiClient.getApiInterface().deleteWishlist(cusId, proId)
                .enqueue(new Callback<SuccessResponse>() {
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
//                              progressIndicator.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("01")) {
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    lineHeartImg.setImageResource(R.drawable.line_heart);

                                } else {
                                    Toast.makeText(ProductDetailActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    isInWishlist = false;
                                }
                            } else {
                                Toast.makeText(ProductDetailActivity.this, "Facing problem in displaying wishlist", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(ProductDetailActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void findIds() {
        viewPager = findViewById(R.id.viewPager);
        product_detail_toolbar = findViewById(R.id.product_detail_toolbar);
        productNameDetail = findViewById(R.id.productNameDetail);
        productRateDetail = findViewById(R.id.productRateDetail);
        lineHeartImg = findViewById(R.id.lineHeartImg);
        quantitySpinner = findViewById(R.id.quantitySpinner);
        buyNowBtn = findViewById(R.id.buyNowBtn);
        addToCartBtn = findViewById(R.id.addToCartBtn);
        productDescDetail = findViewById(R.id.productDescDetail);
        productDiscountPriceTxt = findViewById(R.id.productDiscountPriceTxt);
        productDiscountText = findViewById(R.id.productDiscountText);
    }

    private void makeToolbar() {
        setSupportActionBar(product_detail_toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Product Detail");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        product_detail_toolbar.setNavigationOnClickListener(view -> {
            finish();
        });
    }


}