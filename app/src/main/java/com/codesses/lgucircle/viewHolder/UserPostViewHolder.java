package com.codesses.lgucircle.viewHolder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.CommentActivity;
import com.codesses.lgucircle.activity.ImageViewActivity;
import com.codesses.lgucircle.databinding.UserFeedItemLayoutBinding;
import com.codesses.lgucircle.model.Comment;
import com.codesses.lgucircle.model.Post;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class UserPostViewHolder extends RecyclerView.ViewHolder {
    private UserFeedItemLayoutBinding binding;
    private Context mContext;

    public UserPostViewHolder(@NonNull UserFeedItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }


    public void bind(Context context, Post model) {
        mContext = context;
        getUserInfo(model.getPosted_by());

        binding.status.setVisibility(View.GONE);

        binding.timestamp.setText(String.valueOf(model.getTimestamp()));
        if (!TextUtils.isEmpty(model.getStatus())) {
            binding.status.setVisibility(View.VISIBLE);
            binding.status.setText(model.getStatus());
        }
        if (!TextUtils.isEmpty(model.getImage())) {
            binding.postImage.setVisibility(View.VISIBLE);
            Glide.with(mContext).load(model.getImage()).into(binding.postImage);
        } else {
            binding.postImage.setVisibility(View.GONE);
        }

        binding.postImage.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ImageViewActivity.class);
            intent.putExtra(mContext.getString(R.string.intent_open_full_screen_image), model.getImage());
            mContext.startActivity(intent);
        });
        /*****************
         * Click listener
         */
//         Post image
        binding.postImage.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, ImageViewActivity.class);
            Pair<View, String> pairs = new Pair<>(binding.postImage, "Transition");
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) mContext, pairs);
            intent.putExtra(mContext.getString(R.string.intent_open_full_screen_image), model.getImage());
            mContext.startActivity(intent, options.toBundle());
        });

//         Opinion
        binding.comment.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra(mContext.getString(R.string.post_id), model.getP_id());
            mContext.startActivity(intent);
        });

    }


    private void getUserInfo(String userId) {
        FirebaseRef.getUserRef()
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User user = dataSnapshot.getValue(User.class);

//                            Glide.with(mContext).load(user.getProfile_img()).into(binding.profileImage);
                            binding.username.setText(user.getFirst_name() + " " + user.getLast_name());
                        }
                    }

                    /*****************
                     * Click listener
                     */
//
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}
