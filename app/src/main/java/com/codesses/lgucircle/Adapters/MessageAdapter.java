package com.codesses.lgucircle.Adapters;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnImageClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.model.Chat;
import com.codesses.lgucircle.viewHolder.ViewHolderMessage;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<ViewHolderMessage> {


    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    Context mContext;
    List<Chat> mChat;
    FirebaseUser fuser;
    View view;
    OnImageClick onImageClick;

    public MessageAdapter(Context mContext, List<Chat> mChat, OnImageClick onImageClick) {
        this.mChat = mChat;
        this.mContext = mContext;
        this.onImageClick = onImageClick;
    }


    @NonNull
    @Override
    public ViewHolderMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_right, parent, false);
        } else if (viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item_left, parent, false);
        }
        return new ViewHolderMessage(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderMessage holder, final int position) {
        Chat chat = mChat.get(position);
        holder.onBind(chat, onImageClick, mContext);
//        holder.setIsRecyclable(false);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!Constants.isMessagePicked) {
                    Constants.isMessagePicked = true;
                    Constants.selectedMessages = 1;
                    holder.constraintLayout.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
                    chat.setPicked(true);
                    onImageClick.onLongClick();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    @Override
    public int getItemViewType(int position) {
        fuser = FirebaseRef.getCurrentUser();
        if (mChat.get(position).getSender_id().equals(fuser.getUid())) {
            return MSG_TYPE_RIGHT;
        } else {
            return MSG_TYPE_LEFT;
        }
    }

//    @Override
//    public void onViewAttachedToWindow(final ViewHolderMessage holder) {
//        if (holder instanceof ViewHolderMessage) {
//            holder.setIsRecyclable(false);
//        }
//        super.onViewAttachedToWindow(holder);
//    }
//
//    @Override
//    public void onViewDetachedFromWindow(final ViewHolderMessage holder) {
//        if (holder instanceof ViewHolderMessage){
//            holder.setIsRecyclable(true);
//        }
//        super.onViewDetachedFromWindow(holder);
//    }
}