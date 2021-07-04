package com.codesses.lgucircle.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.activity.Services.ConversationAC;
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
        if (Constants.isChatPicked && user.isPicked())
            userItemBinding.checked.setVisibility(View.VISIBLE);

        Picasso.get().load(user.getProfile_img()).into(userItemBinding.userImage);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Constants.isChatPicked || Constants.selectedUsers.equals(0)) {
                    Constants.isChatPicked = false;
                    onConversationClick.onClick(user.getU_id());
                } else if (user.isPicked()){
                    userItemBinding.checked.setVisibility(View.GONE);
                    user.setPicked(false);
                    Constants.selectedUsers -= 1;
                    if (Constants.selectedUsers.equals(0))
                    {
                        ConversationAC.imageView.setVisibility(View.GONE);
                    }
                }
                else
                {
                    userItemBinding.checked.setVisibility(View.VISIBLE);
                    user.setPicked(true);
                    Constants.selectedUsers += 1;
                }
            }
        });
        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!Constants.isChatPicked) {
                    Constants.isChatPicked = true;
                    userItemBinding.checked.setVisibility(View.VISIBLE);
                    user.setPicked(true);
                    Constants.selectedUsers = 1;
                    onConversationClick.longClick();
                    return true;
                }
                return false;
            }
        });
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
