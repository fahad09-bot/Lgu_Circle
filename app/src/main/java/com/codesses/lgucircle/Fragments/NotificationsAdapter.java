package com.codesses.lgucircle.Fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.NotificationItemLayoutBinding;
import com.codesses.lgucircle.model.Notification;
import com.codesses.lgucircle.viewHolder.NotificationVH;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationVH> {

    List<Notification> notificationList;
    Context mContext;

    public NotificationsAdapter(List<Notification> notificationList, Context mContext) {
        this.notificationList = notificationList;
        this.mContext = mContext;
    }

    @NonNull
    @NotNull
    @Override
    public NotificationVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        NotificationItemLayoutBinding binding = NotificationItemLayoutBinding.bind(LayoutInflater.from(mContext).inflate(R.layout.notification_item_layout, parent, false));
        return new NotificationVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull NotificationVH holder, int position) {
        holder.onBind(mContext, notificationList.get(position));
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
