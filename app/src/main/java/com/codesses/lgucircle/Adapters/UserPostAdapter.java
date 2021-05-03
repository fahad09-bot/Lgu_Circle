package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;


import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.UserFeedItemLayoutBinding;
import com.codesses.lgucircle.model.Post;
import com.codesses.lgucircle.viewHolder.UserPostViewHolder;

import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostViewHolder> {
    Context context;
    List<Post> list;

    public UserPostAdapter(Context context, List<Post> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        UserFeedItemLayoutBinding binding =
                DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.user_feed_item_layout, parent, false);
        return new UserPostViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserPostViewHolder holder, int position) {
        Post model = list.get(position);
        holder.bind(context, model);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
