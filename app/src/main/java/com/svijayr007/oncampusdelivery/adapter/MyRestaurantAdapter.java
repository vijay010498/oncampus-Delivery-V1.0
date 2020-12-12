package com.svijayr007.oncampusdelivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.svijayr007.oncampusdelivery.R;
import com.svijayr007.oncampusdelivery.callback.IRecyclerClickListener;
import com.svijayr007.oncampusdelivery.eventBus.RestaurantSelectedEvent;
import com.svijayr007.oncampusdelivery.model.RestaurantModel;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class MyRestaurantAdapter extends RecyclerView.Adapter<MyRestaurantAdapter.MyViewHolder> {
    Context context;
    List<RestaurantModel> restaurantModelList;

    public MyRestaurantAdapter(Context context, List<RestaurantModel> restaurantModelList) {
        this.context = context;
        this.restaurantModelList = restaurantModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyRestaurantAdapter.MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_restaurant_items, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context)
                .load(restaurantModelList.get(position).getImageUrl())
                .placeholder(R.drawable.ic_restaurant)
                .into(holder.restaurantImage);
        holder.restaurant_name.setText(new StringBuilder()
        .append(restaurantModelList.get(position).getName()));

        holder.restaurant_campus_name.setText(new StringBuilder()
        .append(restaurantModelList.get(position).getCampus()));
        holder.setListener((view, pos) -> {
            EventBus.getDefault().post(new RestaurantSelectedEvent(restaurantModelList.get(pos))); //Subscribe in signup activity

        });

    }

    @Override
    public int getItemCount() {
        return restaurantModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Unbinder unbinder;
        @BindView(R.id.restaurantImage)
        CircleImageView restaurantImage;
        @BindView(R.id.restaurant_name)
        TextView restaurant_name;
        @BindView(R.id.restaurant_campus_name)
        TextView restaurant_campus_name;



        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            listener.onItemClickListener(view, getAdapterPosition());
        }
    }
}