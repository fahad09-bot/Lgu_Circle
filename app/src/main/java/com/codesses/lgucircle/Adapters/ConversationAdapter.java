package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnConversationClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.UserItemBinding;
import com.codesses.lgucircle.model.User;
import com.codesses.lgucircle.viewHolder.ConversationVH;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationVH> {

    List<User> userList;
    Context mContext;
    OnConversationClick onConversationClick;


    public ConversationAdapter(List<User> userList, Context mContext, OnConversationClick onConversationClick) {
        this.userList = userList;
        this.mContext = mContext;
        this.onConversationClick = onConversationClick;
    }

    @NonNull
    @NotNull
    @Override
    public ConversationVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        UserItemBinding binding = UserItemBinding.bind(LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false));

        return new ConversationVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ConversationVH holder, int position) {
        holder.onBind(userList.get(position), onConversationClick);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


}
