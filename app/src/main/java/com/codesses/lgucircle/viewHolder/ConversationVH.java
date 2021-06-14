package com.codesses.lgucircle.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.model.Chat;
import com.codesses.lgucircle.Interfaces.OnConversationClick;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.UserItemBinding;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class ConversationVH extends RecyclerView.ViewHolder {

    UserItemBinding userItemBinding;
    String theLastMessage;


    public ConversationVH(@NonNull @NotNull UserItemBinding userItemBinding) {
        super(userItemBinding.getRoot());
        this.userItemBinding = userItemBinding;
    }

    public void onBind(User user, OnConversationClick onConversationClick) {
        userItemBinding.userName.setText(user.getFirst_name() + " " + user.getLast_name());
        if (user.getType().equals("staff")) {
            userItemBinding.type.setText(" (" + user.getType() + ")");
            userItemBinding.type.setVisibility(View.VISIBLE);
        } else {
            userItemBinding.type.setVisibility(View.GONE);
        }
        Picasso.get().load(user.getProfile_img()).into(userItemBinding.userImage);
        itemView.setOnClickListener(v -> onConversationClick.onClick(user.getU_id()));
        getLastMessage(user);
    }

    private void getLastMessage(User user) {
        theLastMessage = "default";
        FirebaseRef.getMessageRef()
                .child(FirebaseRef.getCurrentUserId())
                .child(FirebaseRef.getCurrentUserId() + user.getU_id())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Chat chat = snapshot.getValue(Chat.class);
                                if (FirebaseRef.getCurrentUser() != null && chat != null) {
                                    if (chat.getType() == 0 || chat.getType() == 2) {
                                        theLastMessage = chat.getMessage();
                                        userItemBinding.lastMessage.setVisibility(View.VISIBLE);
                                        userItemBinding.lastMessageImage.setVisibility(View.GONE);
                                        userItemBinding.lastMessage.setText(theLastMessage);
                                    } else if (chat.getType() == 1) {
                                        userItemBinding.lastMessageImage.setVisibility(View.VISIBLE);
                                        userItemBinding.lastMessage.setVisibility(View.GONE);
                                    } else {
                                        userItemBinding.lastMessageImage.setVisibility(View.GONE);
                                        userItemBinding.lastMessage.setVisibility(View.VISIBLE);
                                        userItemBinding.lastMessage.setText("No message");
                                    }
                                }

                            }
                        } else {
                            userItemBinding.lastMessageImage.setVisibility(View.GONE);
                            userItemBinding.lastMessage.setVisibility(View.VISIBLE);
                            userItemBinding.lastMessage.setText("No message");
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }


}
