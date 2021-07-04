package com.codesses.lgucircle.viewHolder;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CurrentDateAndTime;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ServicesFeedItemBinding;
import com.codesses.lgucircle.model.Service;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class ServiceVH extends RecyclerView.ViewHolder {

    ServicesFeedItemBinding binding;
    String timeAgo;
    public ServiceVH(@NonNull ServicesFeedItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(Service service, Context mContext, OnItemClick onItemClick)
    {
        TimeAgoHandle(service.getTime_stamp(), binding.timestamp);
        FirebaseRef.getUserRef().child(service.getPosted_by()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                binding.username.setText(user.getFirst_name() + " " + user.getLast_name());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onMenuClick(v, service.getS_id());
            }
        });

        CharSequence description = service.getDescription();
        binding.serviceDescription.setTrimLines(6);
        binding.serviceDescription.setText(description);
        if(service.getUrl().isEmpty())
            binding.richLinkView.setVisibility(View.GONE);
        else
        {
            binding.richLinkView.setVisibility(View.VISIBLE);
            binding.richLinkView.setLink(service.getUrl(), new ViewListener() {
                @Override
                public void onSuccess(boolean status) {

                }

                @Override
                public void onError(Exception e) {
                    Log.e("ERROR_TAG", e.getMessage());
                }
            });
        }

        if (FirebaseRef.getCurrentUserId().equals(service.getPosted_by())) {
            binding.comment.setVisibility(View.INVISIBLE);
            binding.comment.setEnabled(false);

        }
        else
            binding.comment.setVisibility(View.VISIBLE);
        binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(service.getPosted_by());
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
