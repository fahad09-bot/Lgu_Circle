package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.ServicesFeedItemBinding;
import com.codesses.lgucircle.model.Service;
import com.codesses.lgucircle.viewHolder.ServiceVH;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceVH> {

    Context mContext;
    List<Service> servicesList;
    OnItemClick onItemClick;

    public ServiceAdapter(Context mContext, List<Service> servicesList, OnItemClick onItemClick) {
        this.mContext = mContext;
        this.servicesList = servicesList;
        this.onItemClick = onItemClick;
    }

    @NonNull
    @Override
    public ServiceVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ServicesFeedItemBinding binding = DataBindingUtil.bind(LayoutInflater.from(mContext).inflate(R.layout.services_feed_item, parent, false));
        return new ServiceVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceVH holder, int position) {
        holder.onBind(servicesList.get(position), mContext, onItemClick);
    }

    @Override
    public int getItemCount() {
        return servicesList.size();
    }
}
