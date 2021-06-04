package com.codesses.lgucircle.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.IdeaClick;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.IdeasItemLayoutBinding;
import com.codesses.lgucircle.model.Idea;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class IdeaVH extends RecyclerView.ViewHolder {


    IdeasItemLayoutBinding binding;
    public IdeaVH(@NonNull @NotNull IdeasItemLayoutBinding binding) {
        super(binding.getRoot());

        this.binding = binding;
    }

    public void onBind(Idea idea, IdeaClick ideaClick)
    {
        binding.pitch.setText(idea.getPitch());
        setUserData(idea);
        if(idea.getStatus() != 1)
        {
            binding.btnAccept.setVisibility(View.GONE);
            binding.btnReject.setVisibility(View.GONE);
        }
        else
        {
            binding.btnAccept.setVisibility(View.VISIBLE);
            binding.btnReject.setVisibility(View.VISIBLE);
        }

        binding.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ideaClick.onAccept(idea.getPitched_by(), getBindingAdapterPosition());
            }
        });

        binding.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ideaClick.onReject(idea.getI_id(), getBindingAdapterPosition());
            }
        });

    }

    private void setUserData(Idea idea) {
        FirebaseRef.getUserRef()
                .child(idea.getPitched_by())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        binding.userName.setText(user.getFirst_name() + " " + user.getLast_name());
                        binding.userEmail.setText(user.getEmail());
                        binding.userPhoneNo.setText(user.getPhone());
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }
}
