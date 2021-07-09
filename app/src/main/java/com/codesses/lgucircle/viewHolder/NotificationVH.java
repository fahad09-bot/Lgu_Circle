package com.codesses.lgucircle.viewHolder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.NotificationItemLayoutBinding;
import com.codesses.lgucircle.model.Notification;

import org.jetbrains.annotations.NotNull;

public class NotificationVH extends RecyclerView.ViewHolder {

    NotificationItemLayoutBinding binding;

    public NotificationVH(@NonNull @NotNull NotificationItemLayoutBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public void onBind(Context mContext, Notification notification)
    {
        binding.title.setText(notification.getTitle());
        binding.description.setText(notification.getMessage());

        switch (notification.getType()) {
            case 0:
                binding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_comment));
                break;
            case 1:
                binding.icon.setImageDrawable(mContext.getDrawable(R.drawable.accepted));
                break;

            case 2:
                binding.icon.setImageDrawable(mContext.getDrawable(R.drawable.reject));
                break;
            case 3:
                binding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_brain));
                break;
            case 4:
                binding.icon.setImageDrawable(mContext.getDrawable(R.drawable.ic_event));
                break;
            default:
                binding.icon.setImageDrawable(mContext.getDrawable(R.drawable.circle_grey));
        }
    }
}
