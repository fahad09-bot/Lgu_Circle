package com.codesses.lgucircle.viewHolder;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.activity.CommentActivity;
import com.codesses.lgucircle.activity.ImageViewActivity;
import com.codesses.lgucircle.databinding.UserFeedItemLayoutBinding;
import com.codesses.lgucircle.model.Post;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

public class UserPostViewHolder extends RecyclerView.ViewHolder {
    private UserFeedItemLayoutBinding binding;
    private Context mContext;
    String timeAgo;

    User currentUser;

    public UserPostViewHolder(@NonNull UserFeedItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }


    public void bind(Context context, Post model, OnItemClick onItemClick) {
        mContext = context;
        getUserInfo(model.getPosted_by());
        Gson gson = new Gson();
        currentUser = gson.fromJson(SharedPrefManager.getInstance(mContext).getSharedData(SharedPrefKey.USER), User.class);
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

        if (model.getPosted_by().equals(currentUser.getU_id())) {
            binding.options.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClick.onMenuClick(v, model.getP_id());
                }
            });
        }
        else
        {
            binding.options.setVisibility(View.INVISIBLE);
        }


//         Opinion
        binding.comment.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CommentActivity.class);
            intent.putExtra(mContext.getString(R.string.post_id), model.getP_id());
            mContext.startActivity(intent);
        });

        TimeAgoHandle(model.getTimestamp(), binding.timestamp);
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

    public void TimeAgoHandle(long time, TextView timestamp) {
        long seconds = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - time);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - time);
        long hours = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - time);
        long days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - time);


        if (seconds < 60) {
            timeAgo = "Just now";
        } else if (minutes < 60) {
            timeAgo = minutes + "m";
        } else if (hours < 24) {
            timeAgo = hours + "hr";
        } else if (days % 7 == 0) {
            long week = days / 7;
            timeAgo = week + "w";
        } else {
            timeAgo = days + "d";
        }
        timestamp.setText(timeAgo);

    }
}
