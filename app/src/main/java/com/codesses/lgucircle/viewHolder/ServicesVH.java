package com.codesses.lgucircle.viewHolder;

import android.content.Context;
import android.view.View;

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

import io.github.ponnamkarthik.richlinkpreview.ViewListener;

public class ServicesVH extends RecyclerView.ViewHolder {

    ServicesFeedItemBinding binding;
    public ServicesVH(@NonNull ServicesFeedItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(Service service, Context mContext, OnItemClick onItemClick)
    {
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

                }
            });
            binding.timestamp.setText(CurrentDateAndTime.currentDate().equals(service.getTime_stamp()) ?
                    mContext.getString(R.string.today) : String.valueOf(service.getTime_stamp()));
        }

        if (FirebaseRef.getCurrentUserId().equals(service.getPosted_by()))
            binding.comment.setVisibility(View.GONE);
        else
            binding.comment.setVisibility(View.VISIBLE);
        binding.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick.onClick(service.getPosted_by());
            }
        });

    }
}
