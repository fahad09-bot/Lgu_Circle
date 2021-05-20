package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnUserClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.UserSearchItemBinding;
import com.codesses.lgucircle.model.User;
import com.codesses.lgucircle.viewHolder.UserVH;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserVH> {

    List<User> userList;
    Context mContext;
    OnUserClick onUserClick;

    public UserAdapter(List<User> userList, Context mContext, OnUserClick onUserClick) {
        this.userList = userList;
        this.mContext = mContext;
        this.onUserClick = onUserClick;
    }

    @NonNull
    @NotNull
    @Override
    public UserVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        UserSearchItemBinding binding = UserSearchItemBinding.bind(LayoutInflater.from(mContext).inflate(R.layout.user_search_item, parent, false));
        return new UserVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserVH holder, int position) {
        holder.onBind(userList.get(position), onUserClick);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void filterUsers(List<User> filteredList)
    {
        userList = filteredList;
        notifyDataSetChanged();
    }
}
