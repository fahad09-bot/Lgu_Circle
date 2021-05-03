package com.codesses.lgucircle.viewHolder;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CurrentDateAndTime;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ReplyItemLayoutBinding;
import com.codesses.lgucircle.model.OpinionReply;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class ReplyViewHolder extends RecyclerView.ViewHolder {
    private ReplyItemLayoutBinding replyBinding;
    private Context context;


    public ReplyViewHolder(@NonNull ReplyItemLayoutBinding replyBinding) {
        super(replyBinding.getRoot());
        this.replyBinding = replyBinding;
    }


    /******************
     * Calling methods
     */
    public void replyBind(Context context, OpinionReply model) {
        this.context = context;
        getRepliedUserInfo(model.getReplied_by());

        replyBinding.replyText.setText(model.getReply());

        //        Display "Today" if current date equal to post date
        replyBinding.replyDate.setText(
                CurrentDateAndTime.currentDate().equals(model.getDate()) ?
                        context.getString(R.string.today) : model.getDate());

        replyBinding.replyTime.setText(model.getTime());
    }


    private void getRepliedUserInfo(String userId) {
        FirebaseRef.getUserRef()
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        Glide.with(context)
                                .load(user.getProfile_img())
                                .into(replyBinding.profileImage);

                        replyBinding.username.setText(user.getFirst_name() + " " + user.getLast_name());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
