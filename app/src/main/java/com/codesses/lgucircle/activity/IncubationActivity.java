package com.codesses.lgucircle.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.Services.ConversationAC;
import com.codesses.lgucircle.databinding.IncubationBinding;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;


public class IncubationActivity extends AppCompatActivity {
    AppCompatActivity mContext;

    IncubationBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.incubation);

        //  TODO: Click Listeners
        binding.btnBack.setOnClickListener(v -> onBackPressed());
        binding.register.setOnClickListener(this::registerIdea);

        //  TODO: Get current user data
        getCurrentUserData();
    }

    private void getCurrentUserData() {
        FirebaseRef.getUserRef()
                .child(FirebaseRef.getUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);

                        binding.iName.setText(user.getFirst_name() + " " + user.getLast_name());
                        binding.iEmail.setText(user.getEmail());
                        binding.iPhoneNo.setText(user.getPhone());
                        binding.department.setText(user.getDepartment());
                        binding.rollNo.setText(user.getRoll_no());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, "Alert!: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void registerIdea(View view) {

        new AlertDialog
                .Builder(mContext)
                .setTitle(getString(R.string.register_idea))
                .setMessage(getString(R.string.idea_confirmation))
                .setPositiveButton("DONE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendDataToFirebase();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("CANCEL", null)
                .show();
    }

    private void sendDataToFirebase() {
        String pitch = binding.pitch.getText().toString();

        if (!TextUtils.isEmpty(pitch)) {
            String pitch_id = FirebaseRef.getIdeaRef().push().getKey();
            HashMap<String, Object> ideaMap = new HashMap<>();
            ideaMap.put("i_id", pitch_id);
            ideaMap.put("pitched_by", FirebaseRef.getCurrentUserId());
            ideaMap.put("pitch", pitch);
            ideaMap.put("status", 1);
            FirebaseRef.getIdeaRef().child(pitch_id)
                    .updateChildren(ideaMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            binding.pitch.setText("");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }


    }
}
