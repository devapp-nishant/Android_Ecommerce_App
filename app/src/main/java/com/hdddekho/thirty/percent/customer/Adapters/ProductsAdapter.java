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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdddekho.thirty.percent.customer.Activities.ProductDetailActivity;
import com.hdddekho.thirty.percent.customer.Models.ProductModel;
import com.hdddekho.thirty.percent.customer.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductViewHolder>{

    Context context;
    List<ProductModel> pModel;

    public ProductsAdapter(Context context, List<ProductModel> pModel) {
        this.context = context;
        this.pModel = pModel;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {

        String productName = pModel.get(position).getProduct_name();
        String discount = pModel.get(position).getDiscount();
        String productOriginalPrice = pModel.get(position).getMrp();

        double rate = Double.parseDouble(productOriginalPrice);

        double discountPrice = Double.parseDouble(productOriginalPrice) - (Double.parseDouble(productOriginalPrice) * (Double.parseDouble(discount) / 100));

        NumberFormat nf = NumberFormat.getInstance(new Locale("en", "IN"));
        String formattedDiscountPrice = nf.format(discountPrice);
        String formattedOriginalPrice = nf.format(rate);


        holder.productItemName.setText(productName);
        holder.productItemActualPrice.setText("\u20B9" + formattedOriginalPrice);
        holder.productItemDiscoutPrice.setText("\u20B9" + formattedDiscountPrice);
        holder.productItemCat.setText(pModel.get(position).getCategory_name());
        holder.productItemDiscount.setText(pModel.get(position).getDiscount()+"% Off");
        holder.productItemDiscoutPrice.setText("\u20B9" +discountPrice);


        Glide.with(context).load("https://30app.hdddekho.com/ProductImages/" + pModel.get(position).getImage1()).into(holder.productItemImg);

        holder.itemView.setOnClickListener(view -> {
            Intent detailProductIntent = new Intent(context, ProductDetailActivity.class);
            detailProductIntent.putExtra("product_id", pModel.get(position).getProduct_id());
            detailProductIntent.putExtra("category_id", pModel.get(position).getCat_id());
            detailProductIntent.putExtra("category_name", pModel.get(position).getCategory_name());

            detailProductIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(detailProductIntent);
        });

    }

    @Override
    public int getItemCount() {
        return pModel.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterProducts(List<ProductModel> filterProduct) {
        pModel = filterProduct;
        notifyDataSetChanged();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView productItemImg;
        TextView productItemCat, productItemName, productItemDiscount, productItemActualPrice, productItemDiscoutPrice;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            productItemImg = itemView.findViewById(R.id.productItemImg);
            productItemCat = itemView.findViewById(R.id.productItemCat);
            productItemName = itemView.findViewById(R.id.productItemName);
            productItemDiscount = itemView.findViewById(R.id.productItemDiscount);
            productItemActualPrice = itemView.findViewById(R.id.productItemActualPrice);
            productItemDiscoutPrice = itemView.findViewById(R.id.productItemDiscoutPrice);

            productItemActualPrice.setPaintFlags(productItemActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        }
    }
}
