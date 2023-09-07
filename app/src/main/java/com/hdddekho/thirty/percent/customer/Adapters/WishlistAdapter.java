package com.hdddekho.thirty.percent.customer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdddekho.thirty.percent.customer.Activities.ProductDetailActivity;
import com.hdddekho.thirty.percent.customer.Models.WishlistModel;
import com.hdddekho.thirty.percent.customer.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    Context context;
    List<WishlistModel> wModel;

    public WishlistAdapter(Context context, List<WishlistModel> wModel) {
        this.context = context;
        this.wModel = wModel;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item, parent, false);
        return new WishlistViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {

        String productName = wModel.get(position).getProduct_name();
        String discount = wModel.get(position).getDiscount();
        String productOriginalPrice = wModel.get(position).getMrp();

        double rate = Double.parseDouble(productOriginalPrice);

        double discountPrice = Double.parseDouble(productOriginalPrice) - (Double.parseDouble(productOriginalPrice) * (Double.parseDouble(discount) / 100));

        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
        String formattedDiscountPrice = nf.format(discountPrice);
        String formattedOriginalPrice = nf.format(rate);

        holder.wishItemProductName.setText(productName);
        holder.wishItemDiscount.setText(discount+"% Off");
        holder.wishItemFinalPrice.setText("\u20B9" +formattedDiscountPrice);
        holder.wishItemPrice.setText("\u20B9" +formattedOriginalPrice);

        Glide.with(context).load("https://30app.hdddekho.com/ProductImages/" + wModel.get(position).getImage1()).into(holder.wishItemImg);

        holder.wishItemViewProductBtn.setOnClickListener(view -> {
            Intent wishIntent = new Intent(context, ProductDetailActivity.class);
            wishIntent.putExtra("product_id",wModel.get(position).getProduct_id());
            context.startActivity(wishIntent);
        });
    }

    @Override
    public int getItemCount() {
        return wModel.size();
    }

    public class WishlistViewHolder extends RecyclerView.ViewHolder {

        TextView wishItemProductName,wishItemPrice,wishItemDiscount,wishItemFinalPrice;
        ImageView wishItemImg;
        AppCompatButton wishItemViewProductBtn;
        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            wishItemImg = itemView.findViewById(R.id.wishItemImg);
            wishItemPrice = itemView.findViewById(R.id.wishItemPrice);
            wishItemDiscount = itemView.findViewById(R.id.wishItemDiscount);
            wishItemFinalPrice = itemView.findViewById(R.id.wishItemFinalPrice);
            wishItemProductName = itemView.findViewById(R.id.wishItemProductName);
            wishItemViewProductBtn = itemView.findViewById(R.id.wishItemViewProductBtn);

            wishItemPrice.setPaintFlags(wishItemPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }
}
