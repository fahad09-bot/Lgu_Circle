package com.codesses.lgucircle.viewHolder;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codesses.lgucircle.Interfaces.OnReplyClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CurrentDateAndTime;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.CommentsItemLayoutBinding;
import com.codesses.lgucircle.model.Comment;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import com.google.firebase.database.ValueEventListener;


public class CommentViewHolder extends RecyclerView.ViewHolder {
    private CommentsItemLayoutBinding binding;
    private Context mContext;
    public RecyclerView replyRecyclerView;

    public CommentViewHolder(@NonNull CommentsItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
        replyRecyclerView = binding.replyRecyclerView;
    }


    /******************
     * Calling methods
     */
    public void bind(Context context, Comment model, OnReplyClick onReplyClick) {
        mContext = context;
        getUserInfo(model.getCommented_by());

        binding.opinionText.setText(model.getComment());

//        Display "Today" if current date equal to post date
        binding.date.setText(
                CurrentDateAndTime.currentDate().equals(model.getDate()) ?
                        context.getString(R.string.today) : model.getDate());

        binding.time.setText(model.getTime());


//        Reply click listener
        binding.replyIcon.setOnClickListener(v -> {
            onReplyClick.onClick(true, model.getC_id());
        });
    }


    private void getUserInfo(String userId) {
        FirebaseRef.getUserRef()
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        Glide.with(mContext)
                                .load(user.getProfile_img())
                                .into(binding.profileImage);

                        binding.username.setText(user.getFirst_name() + " " + user.getLast_name());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


}
