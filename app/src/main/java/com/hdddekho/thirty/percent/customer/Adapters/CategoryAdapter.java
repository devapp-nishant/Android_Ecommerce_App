package com.hdddekho.thirty.percent.customer.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.hdddekho.thirty.percent.customer.Activities.AllProductActivity;
import com.hdddekho.thirty.percent.customer.Models.CategoryModel;
import com.hdddekho.thirty.percent.customer.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CatViewHolder> {
    Context context;
    List<CategoryModel> cModel;

    public CategoryAdapter(Context context, List<CategoryModel> cModel) {
        this.context = context;
        this.cModel = cModel;
    }

    @NonNull
    @Override
    public CatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new CatViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CatViewHolder holder, int position) {
        holder.catNameSample.setText(cModel.get(position).getCategory_name());

        Glide.with(context).load("https://30app.hdddekho.com/CategoryImages/" + cModel.get(position).getCategory_image()).into(holder.catImgSample);

        holder.itemView.setOnClickListener(view -> {
            Intent allProductIntent = new Intent(context, AllProductActivity.class);
            allProductIntent.putExtra("cat_id", cModel.get(position).getCategory_id());
            allProductIntent.putExtra("cat_name", cModel.get(position).getCategory_name());
            allProductIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(allProductIntent);
        });

    }

    @Override
    public int getItemCount() {
        return cModel.size();
    }

    public class CatViewHolder extends RecyclerView.ViewHolder {

        ImageView catImgSample;
        TextView catNameSample;
        public CatViewHolder(@NonNull View itemView) {
            super(itemView);

            catNameSample = itemView.findViewById(R.id.catNameSample);
            catImgSample = itemView.findViewById(R.id.catImgSample);

        }
    }
}