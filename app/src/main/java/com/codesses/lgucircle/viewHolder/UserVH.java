package com.codesses.lgucircle.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnUserClick;
import com.codesses.lgucircle.databinding.UserSearchItemBinding;
import com.codesses.lgucircle.model.User;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class UserVH extends RecyclerView.ViewHolder {
    UserSearchItemBinding binding;


    public UserVH(@NonNull @NotNull UserSearchItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(User user, OnUserClick onUserClick) {
        Picasso.get().load(user.getProfile_img()).into(binding.userImage);
        binding.userName.setText(user.getFirst_name() + " " + user.getLast_name());
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onUserClick.onUserClick(user.getU_id());
            }
        });

    }
}
