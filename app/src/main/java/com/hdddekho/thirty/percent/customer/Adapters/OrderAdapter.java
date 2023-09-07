package com.hdddekho.thirty.percent.customer.Adapters;

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
import com.hdddekho.thirty.percent.customer.Activities.OrderDetailActivity;
import com.hdddekho.thirty.percent.customer.Models.OrderModel;
import com.hdddekho.thirty.percent.customer.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    Context context;
    List<OrderModel> oModel;

    public OrderAdapter(Context context, List<OrderModel> oModel) {
        this.context = context;
        this.oModel = oModel;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        holder.orderItemDate.setText(oModel.get(position).getOrder_date());
        holder.orderItemProductName.setText(oModel.get(position).getProduct_name());

        Glide.with(context).load("https://30app.hdddekho.com/ProductImages/" + oModel.get(position).getImage1()).into(holder.orderItemProductImg);

        holder.itemView.setOnClickListener(view -> {
            Intent orderIntent = new Intent(context, OrderDetailActivity.class);
            orderIntent.putExtra("order_id",oModel.get(position).getOrder_id());
            context.startActivity(orderIntent);
        });

    }

    @Override
    public int getItemCount() {
        return oModel.size();
    }

    public class OrderViewHolder extends RecyclerView.ViewHolder {

        ImageView orderItemProductImg;
        TextView orderItemProductName, orderItemDate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderItemDate = itemView.findViewById(R.id.orderItemDate);
            orderItemProductImg = itemView.findViewById(R.id.orderItemProductImg);
            orderItemProductName = itemView.findViewById(R.id.orderItemProductName);


        }
    }
}
