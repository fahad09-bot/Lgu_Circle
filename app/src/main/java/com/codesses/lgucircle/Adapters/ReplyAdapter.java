package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.ReplyItemLayoutBinding;
import com.codesses.lgucircle.model.OpinionReply;
import com.codesses.lgucircle.viewHolder.ReplyViewHolder;

import java.util.List;


public class ReplyAdapter extends RecyclerView.Adapter<ReplyViewHolder> {
    private Context context;
    private List<OpinionReply> list;

    public ReplyAdapter(Context context, List<OpinionReply> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReplyItemLayoutBinding replyBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context), R.layout.reply_item_layout, parent, false);
        return new ReplyViewHolder(replyBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        OpinionReply model = list.get(position);
        holder.replyBind(context, model);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
