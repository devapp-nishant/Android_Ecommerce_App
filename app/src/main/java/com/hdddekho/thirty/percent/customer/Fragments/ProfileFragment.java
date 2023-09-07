package com.hdddekho.thirty.percent.customer.Fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Activities.MyOrdersActivity;
import com.hdddekho.thirty.percent.customer.Activities.MyWishlistActivity;
import com.hdddekho.thirty.percent.customer.Activities.Privacy_Policy_Activity;
import com.hdddekho.thirty.percent.customer.Activities.ProfileActivity;
import com.hdddekho.thirty.percent.customer.Activities.Terms_Condition_Activity;
import com.hdddekho.thirty.percent.customer.Models.CustomerModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.CustomerResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    View profileFragment;
    private TextView txtName;
    private String Customer_ID, customerName;
    private List<CustomerModel> customerList;
    private ConstraintLayout personalInfoCard, wishlistCard, ordersCard, termsCard, privacyCard;
    SharedPreferences customerPref;
    SharedPreferences.Editor customerEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        profileFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        findIds();

        customerPref = requireContext().getSharedPreferences("CUSTOMER", MODE_PRIVATE);
        customerEditor = customerPref.edit();

        Customer_ID = customerPref.getString("CUSTOMER_ID", "");

        fetchCustomerData();

        personalInfoCard.setOnClickListener(view -> {
            Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
            profileIntent.putExtra("customerId",Customer_ID);
            startActivity(profileIntent);
        });

        wishlistCard.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), MyWishlistActivity.class));
        });

        ordersCard.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), MyOrdersActivity.class));
        });

        termsCard.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Terms_Condition_Activity.class));
        });

        privacyCard.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), Privacy_Policy_Activity.class));
        });


        return profileFragment;
    }

    private void findIds() {
        txtName = profileFragment.findViewById(R.id.txtName);
        termsCard = profileFragment.findViewById(R.id.termsCard);
        ordersCard = profileFragment.findViewById(R.id.ordersCard);
        privacyCard = profileFragment.findViewById(R.id.privacyCard);
        wishlistCard = profileFragment.findViewById(R.id.wishlistCard);
        personalInfoCard = profileFragment.findViewById(R.id.personalInfoCard);
    }

    private void fetchCustomerData() {

        RequestBody CusId = RequestBody.create(Customer_ID, MultipartBody.FORM);

        ApiClient.getApiInterface().getCustomerID(CusId)
                .enqueue(new Callback<CustomerResponse>() {
                    @Override
                    public void onResponse(Call<CustomerResponse> call, Response<CustomerResponse> response) {
                        assert response.body() != null;

                        if (response.body().getStatus().equals("1")) {

                            customerList = response.body().getData();

                            for (int i = 0; i < customerList.size(); i++) {

                                if (customerList.get(i).getCustomer_id().equals(Customer_ID)) {
                                    customerName = customerList.get(i).getCustomer_name();

                                    if (customerName != null)
                                        txtName.setText("Hi,\n" + customerName);

                                }
                            }

                        } else {
                            Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<CustomerResponse> call, Throwable t) {
                        Toast.makeText(getContext(), "Something went wrong!\nPlease Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
    }


}