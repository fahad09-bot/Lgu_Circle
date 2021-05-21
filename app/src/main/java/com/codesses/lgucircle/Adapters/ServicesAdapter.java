package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.ServicesFeedItemBinding;
import com.codesses.lgucircle.model.Service;
import com.codesses.lgucircle.viewHolder.ServicesVH;

import java.util.List;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesVH> {

    Context mContext;
    List<Service> servicesList;
    OnItemClick onItemClick;

    public ServicesAdapter(Context mContext, List<Service> servicesList, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.servicesList = servicesList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ServicesVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ServicesFeedItemBinding binding = DataBindingUtil.bind(LayoutInflater.from(mContext).inflate(R.layout.services_feed_item, parent, false));
        return new ServicesVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicesVH holder, int position) {
        holder.onBind(servicesList.get(position), mContext, onItemClick);
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }
}
