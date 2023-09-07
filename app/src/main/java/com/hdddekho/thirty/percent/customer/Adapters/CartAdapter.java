package com.hdddekho.thirty.percent.customer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdddekho.thirty.percent.customer.API.ApiClient;
import com.hdddekho.thirty.percent.customer.Models.CartModel;
import com.hdddekho.thirty.percent.customer.R;
import com.hdddekho.thirty.percent.customer.Response.SuccessResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    Context context;
    List<CartModel> cModel;
    int quantity;

    public CartAdapter(Context context, List<CartModel> cModel) {
        this.context = context;
        this.cModel = cModel;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position) {

        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));

        String pQuantity = cModel.get(position).getQuantity();
        String pDiscount = cModel.get(position).getDiscount();
        String pActualPrice = cModel.get(position).getMrp();
        int priceAfterQuantity = Integer.parseInt(pActualPrice) * Integer.parseInt(pQuantity);
        double discountPrice = Double.parseDouble(String.valueOf(priceAfterQuantity)) - (Double.parseDouble(String.valueOf(priceAfterQuantity)) * (Double.parseDouble(pDiscount) / 100));

        String formattedPrice = nf.format(discountPrice);

        holder.cartItemTotal.setText("\u20B9" +formattedPrice);
        holder.cartItemProductName.setText(cModel.get(position).getProduct_name());
        holder.cartItemQuantity.setText(cModel.get(position).getQuantity());
        Glide.with(context).load("https://30app.hdddekho.com/ProductImages/" + cModel.get(position).getImage1()).into(holder.cartItemImg);


        holder.cartItemIncreaseQuantity.setOnClickListener(view -> {
            quantity = Integer.parseInt(cModel.get(position).getQuantity());
            if (quantity < 5){
                RequestBody Cart_Id = RequestBody.create(cModel.get(position).getCart_id(), MultipartBody.FORM);
                ApiClient.getApiInterface().increaseCart(Cart_Id)
                        .enqueue(new Callback<SuccessResponse>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        if (response.body().getStatus().equals("01")) {
                                            quantity++;
                                            cModel.get(position).setQuantity(String.valueOf(quantity));
                                            notifyDataSetChanged();
                                            holder.cartItemQuantity.setText(quantity+1);
                                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e("exp", e.getLocalizedMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<SuccessResponse> call, Throwable t) {
                                Toast.makeText(context, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
            else
                Toast.makeText(context, "Quantity can't be increased more than 5!!", Toast.LENGTH_SHORT).show();

        });

        holder.cartItemDecreaseQuantity.setOnClickListener(view -> {
            quantity = Integer.parseInt(cModel.get(position).getQuantity());
            if (quantity > 1){
                RequestBody Cart_Id = RequestBody.create(cModel.get(position).getCart_id(), MultipartBody.FORM);
                ApiClient.getApiInterface().decreaseCart(Cart_Id)
                        .enqueue(new Callback<SuccessResponse>() {
                            @SuppressLint("NotifyDataSetChanged")
                            @Override
                            public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                                try {
                                    if (response.isSuccessful()) {
                                        assert response.body() != null;
                                        if (response.body().getStatus().equals("01")) {
                                            quantity--;
                                            cModel.get(position).setQuantity(String.valueOf(quantity));
                                            notifyDataSetChanged();
                                            holder.cartItemQuantity.setText(quantity-1);
                                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    Log.e("exp", e.getLocalizedMessage());
                                }
                            }

                            @Override
                            public void onFailure(Call<SuccessResponse> call, Throwable t) {

                            }
                        });
            }
            else
                Toast.makeText(context, "Quantity can't be decreased less than 1!!", Toast.LENGTH_SHORT).show();
        });

        holder.cartItemDelete.setOnClickListener(view -> {
            RequestBody cartId = RequestBody.create(cModel.get(position).getCart_id(), MultipartBody.FORM);
            RequestBody productId = RequestBody.create(cModel.get(position).getProduct_id(), MultipartBody.FORM);

            ApiClient.getApiInterface().deleteFromCart(cartId, productId)
                    .enqueue(new Callback<SuccessResponse>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onResponse(Call<SuccessResponse> call, Response<SuccessResponse> response) {
                            try {
                                if (response.isSuccessful()) {
                                    assert response.body() != null;
                                    if (response.body().getStatus().equals("01")) {
                                        cModel.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();

                                    } else {
                                        Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(context, "Something went wrong!!", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                Log.e("exp", e.getLocalizedMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<SuccessResponse> call, Throwable t) {
                            Toast.makeText(context, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }

    @Override
    public int getItemCount() {
        return cModel.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView cartItemImg, cartItemDecreaseQuantity, cartItemIncreaseQuantity, cartItemDelete;
        TextView cartItemProductName, cartItemQuantity, cartItemTotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);

            cartItemImg = itemView.findViewById(R.id.cartItemImg);
            cartItemTotal = itemView.findViewById(R.id.cartItemTotal);
            cartItemDelete = itemView.findViewById(R.id.cartItemDelete);
            cartItemQuantity = itemView.findViewById(R.id.cartItemQuantity);
            cartItemProductName = itemView.findViewById(R.id.cartItemProductName);
            cartItemDecreaseQuantity = itemView.findViewById(R.id.cartItemDecreaseQuantity);
            cartItemIncreaseQuantity = itemView.findViewById(R.id.cartItemIncreaseQuantity);

        }
    }
}
