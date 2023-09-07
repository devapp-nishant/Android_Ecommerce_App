package com.hdddekho.thirty.percent.customer.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Activities.MainActivity;
import com.hdddekho.thirty.percent.customer.Activities.PlaceOrderActivity;
import com.hdddekho.thirty.percent.customer.Activities.ProductDetailActivity;
import com.hdddekho.thirty.percent.customer.Activities.SignUpActivity;
import com.hdddekho.thirty.percent.customer.Adapters.CartAdapter;
import com.hdddekho.thirty.percent.customer.Models.CartModel;
import com.hdddekho.thirty.percent.customer.Models.CustomerModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.CartResponse;
import com.hdddekho.thirty.percent.customer.Response.CustomerResponse;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;
import com.hdddekho.thirty.percent.customer.Utils.OrderNumber;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements PaymentResultListener {

    View cartView;
    int cartTotalPrice,i;
    double subtotal;
    private Dialog signUpDialog;
    private boolean isAddressAvailable;
    private LottieAnimationView cartLottie;
    private ConstraintLayout paymentModeLayout;
    private LinearLayoutCompat linearLayout;
    private TextView txtTotalPrice;
    private CheckBox cartFragPaymentModeCheckBox;
    private LinearProgressIndicator cartProgress;
    private List<CartModel> cartList;
    private List<CustomerModel> customerList;
    private CartAdapter cartAdapter;
    private RecyclerView cartRec;
    private String Customer_ID, customerAddress,TotalPrice,customerEmail, customerPhone, ORDER_NUMBER, orderStatus, deliveryStatus;
    private AppCompatButton cartOrderBtn;
    private Dialog paymentDialog;
    SharedPreferences customerPref;
    SharedPreferences.Editor customerEditor;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        cartView = inflater.inflate(R.layout.fragment_cart, container, false);
        findIds();

        customerPref = requireContext().getSharedPreferences("CUSTOMER", MODE_PRIVATE);
        customerEditor = customerPref.edit();

        Customer_ID = customerPref.getString("CUSTOMER_ID", "");

        orderStatus = "Ordered";
        deliveryStatus = "Pending";

        cartList = new ArrayList<>();
        cartRec.setLayoutManager(new LinearLayoutManager(getContext()));

        cartProgress.setVisibility(View.VISIBLE);

        fetchCustomer();
        showSignUpDialog();
        fetchMyCart();
        paymentDialogDisplay();

        cartOrderBtn.setOnClickListener(view -> {
            if (!isAddressAvailable)
                signUpDialog.show();
            else if (cartFragPaymentModeCheckBox.isChecked())
                placeOrder("NIL","COD");
            else
                PayOnline();
        });

        return cartView;


    }
    @SuppressLint("UseCompatLoadingForDrawables")
    private void showSignUpDialog() {
        signUpDialog = new Dialog(getContext());
        signUpDialog.setContentView(R.layout.sign_up_alert_design);
        signUpDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        signUpDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.login_alert_bg));
        signUpDialog.setCancelable(true);

        AppCompatButton alertLoginBtn = signUpDialog.findViewById(R.id.alertLoginBtn);
        ImageView loginCloseBtn = signUpDialog.findViewById(R.id.loginCloseBtn);

        alertLoginBtn.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), SignUpActivity.class));
        });

        loginCloseBtn.setOnClickListener(view -> {
            signUpDialog.dismiss();
        });
    }


    private void PayOnline() {
        Checkout checkout = new Checkout();
        JSONObject json = new JSONObject();

            for (i = 0; i < cartList.size(); i++) {
                String pQuantity = cartList.get(i).getQuantity();
                String pActualPrice = cartList.get(i).getMrp();
                int priceAfterQuantity = Integer.parseInt(pActualPrice) * Integer.parseInt(pQuantity);

                // Calculate the subtotal price for the current product with quantity and discount
                double productSubtotal = priceAfterQuantity * 0.7; // 30% discount

                subtotal += productSubtotal;
            }


        try {
            int amount = Math.round(Float.parseFloat(String.valueOf(subtotal * 100)));

            json.put("name", "30%");
            json.put("image", R.drawable.app_logo_);
            json.put("description","Get everything at discounted price");
            json.put("currency","INR");
            json.put("amount",amount);

            JSONObject prefill = new JSONObject();
            prefill.put("email",customerEmail);
            prefill.put("contact",customerPhone);

            json.put("prefill",prefill);

            checkout.open(getActivity(),json);

        }catch (Exception e){
            Toast.makeText(getActivity(), "Error: "+e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void paymentDialogDisplay() {
        paymentDialog = new Dialog(getContext());
        paymentDialog.setContentView(R.layout.order_success_alert);
        paymentDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paymentDialog.getWindow().setBackgroundDrawable(requireContext().getDrawable(R.drawable.login_alert_bg));
        paymentDialog.setCancelable(true);

        AppCompatButton okBtn = paymentDialog.findViewById(R.id.okPaymentBtn);
        ImageView loginCloseBtn = paymentDialog.findViewById(R.id.loginCloseBtn);

        okBtn.setOnClickListener(view -> {
            paymentDialog.dismiss();
        });

        loginCloseBtn.setOnClickListener(view -> {
            paymentDialog.dismiss();
        });
    }

    private void fetchCustomer() {

        RequestBody CusId = RequestBody.create(Customer_ID, MultipartBody.FORM);

        ApiClient.getApiInterface().getCustomerID(CusId)
                .enqueue(new Callback<CustomerResponse>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        assert response.body() != null;

                        if (response.body().getStatus().equals("1")) {

                            customerList = response.body().getData();

                            for (int i = 0; i < customerList.size(); i++) {
                                if (customerList.get(i).getCustomer_id().equals(Customer_ID)) {
                                    customerPhone = customerList.get(i).getCustomer_phone();
                                    customerEmail = customerList.get(i).getCustomer_email();
                                    customerAddress = customerList.get(i).getCustomer_address();

                                    isAddressAvailable = customerAddress != null;
                                }
                            }

                        } else {
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong!\n"+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void placeOrder(String TransId, String PaymentMode) {

        for (CartModel cList : cartList) {

            String pId = cList.getProduct_id();
            String pQuantity = cList.getQuantity();
            String pDiscount = cList.getDiscount();
            String pActualPrice = cList.getMrp();
            int priceAfterQuantity = Integer.parseInt(pActualPrice) * Integer.parseInt(pQuantity);
            double discountPrice = Double.parseDouble(String.valueOf(priceAfterQuantity)) - (Double.parseDouble(String.valueOf(priceAfterQuantity)) * (Double.parseDouble(pDiscount) / 100));
            TotalPrice = String.valueOf(discountPrice);

            OrderNumber orderNumber = new OrderNumber();
            ORDER_NUMBER = "Ord-" + orderNumber.GenerateOrderNumber(10);

            RequestBody cusId = RequestBody.create(Customer_ID, MultipartBody.FORM);
            RequestBody proId = RequestBody.create(pId, MultipartBody.FORM);
            RequestBody QUANTITY = RequestBody.create(pQuantity, MultipartBody.FORM);
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
                                        paymentDialog.show();
                                        // After Placing Order from Cart, cart must be clear !!
                                        EmptyCart();
                                    } else {
                                        Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("exp", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessResponse> call, Throwable t) {
                            Toast.makeText(getContext(), "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


        }
    }

    private void EmptyCart() {
        RequestBody CusId = RequestBody.create(Customer_ID, MultipartBody.FORM);

        ApiClient.getApiInterface().emptyCart(CusId)
                .enqueue(new Callback<SuccessResponse>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                assert response.body() != null;
                                if (response.body().getStatus().equals("01")) {
                                    cartAdapter.notifyDataSetChanged();
//                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            startActivity(new Intent(getContext(), MainActivity.class));
                                        }
                                    }, 1500);
                                } else {
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<SuccessResponse> call, Throwable t) {
                        Toast.makeText(getContext(), t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void fetchMyCart() {

        RequestBody cusId = RequestBody.create(Customer_ID, MultipartBody.FORM);

        ApiClient.getApiInterface().fetchCart(cusId)
                .enqueue(new Callback<CartResponse>() {
                    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
                    @Override
                    public void onResponse(Call<CartResponse> call, Response<CartResponse> response) {
                        try {
                            if (response.isSuccessful()) {
                                cartProgress.setVisibility(View.GONE);
                                assert response.body() != null;
                                if (response.body().getStatus().equals("1")) {
                                    cartList = response.body().getData();

                                    cartAdapter = new CartAdapter(getContext(), cartList);
                                    cartRec.setAdapter(cartAdapter);
                                    paymentModeLayout.setVisibility(View.VISIBLE);
                                    linearLayout.setVisibility(View.VISIBLE);
                                    cartAdapter.notifyDataSetChanged();


                                } else {
                                    cartProgress.setVisibility(View.GONE);
                                    cartLottie.setVisibility(View.VISIBLE);
                                    paymentModeLayout.setVisibility(View.GONE);
                                    linearLayout.setVisibility(View.GONE);
                                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                cartProgress.setVisibility(View.GONE);
                                Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e("exp", e.getLocalizedMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<CartResponse> call, Throwable t) {
                        cartProgress.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Something went wrong!\n" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void findIds() {
        cartRec = cartView.findViewById(R.id.cartRec);
        cartLottie = cartView.findViewById(R.id.cartLottie);
        linearLayout = cartView.findViewById(R.id.linearLayout);
        cartProgress = cartView.findViewById(R.id.cartProgress);
        cartOrderBtn = cartView.findViewById(R.id.cartOrderBtn);
        paymentModeLayout = cartView.findViewById(R.id.constraintLayout);
        cartFragPaymentModeCheckBox = cartView.findViewById(R.id.cartFragPaymentModeCheckBox);
    }


    @Override
    public void onPaymentSuccess(String s) {
        placeOrder(s,"Online");
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
    }

}