package com.codesses.lgucircle.activity.Services;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.Chat;
import com.codesses.lgucircle.Adapters.MessageAdapter;
import com.codesses.lgucircle.Dialogs.MenuDialog;
import com.codesses.lgucircle.Dialogs.MsgImgUpDialog;
import com.codesses.lgucircle.Dialogs.ProgressDialog;
import com.codesses.lgucircle.Interfaces.SendData;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.CheckEmptyFields;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivityServicesChatBinding;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ServicesChatAC extends AppCompatActivity {

    ActivityServicesChatBinding binding;
    AppCompatActivity mContext;
    MessageAdapter messageAdapter;
    String userId;
    User user;
    String message;
    List<Chat> messageList = new ArrayList<>();
    Uri selectedImage;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_services_chat);
        userId = getIntent().getStringExtra(Constants.USER_ID);
        binding.btnBack.setOnClickListener(v -> finish());
        getUserData();

        messageAdapter = new MessageAdapter(mContext, messageList);
        binding.chatRecycler.setAdapter(messageAdapter);
        binding.send.setOnClickListener(this::sendMessage);
        binding.attach.setOnClickListener(this::openDialogue);
        makingChatList();

        setAdapter();
    }



    private void openDialogue(View view) {
        MenuDialog menuDialog = new MenuDialog(mContext, new SendData() {
            @Override
            public void onSendData(Uri selectedImage) {
                openDialogueWithImage(selectedImage);
            }

            @Override
            public void onSendData(String message, Uri image) {

            }
        });

        menuDialog.show(mContext.getSupportFragmentManager(), "");
    }


    private void openDialogueWithImage(Uri selectedImage) {
        MsgImgUpDialog msgImgUpDialog = new MsgImgUpDialog(mContext, user.getProfile_img(), selectedImage, new SendData() {
            @Override
            public void onSendData(Uri selectedImage) {

            }

            @Override
            public void onSendData(String message, Uri image) {
                ServicesChatAC.this.message = message;
                ServicesChatAC.this.selectedImage = image;
                uploadImage();
            }
        });
        msgImgUpDialog.show(mContext.getSupportFragmentManager(), "");

    }

    private void uploadImage() {
        ProgressDialog.ShowProgressDialog(mContext, R.string.sending, R.string.please_wait);
        StorageReference reference = FirebaseRef.getMessageStorage();
        UploadTask uploadTask = reference.putFile(selectedImage);

        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

//                Toast.makeText(PostUploadAV.this, "", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            imageUrl = task.getResult().toString();
                            sendMessage();

                        } else {
                            Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(e -> {
            ProgressDialog.DismissProgressDialog();
            Toast.makeText(mContext, "Warning: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void getUserData() {
        FirebaseRef.getUserRef().child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                binding.userName.setText(user.getFirst_name() + " " + user.getLast_name());
                Picasso.get().load(user.getProfile_img()).into(binding.userImage);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(View view) {
        ProgressDialog.ShowProgressDialog(mContext, R.string.sending, R.string.please_wait);
        if (CheckEmptyFields.isEditText(mContext, binding.message.getText().toString(), binding.message)) {
            String messageId = FirebaseRef.getMessageRef().push().getKey();

            HashMap<String, Object> message = new HashMap<>();
            message.put("m_id", messageId);
            message.put("sender_id", FirebaseRef.getCurrentUserId());
            message.put("receiver_id", userId);
            message.put("type", 0);
            message.put("timestamp", String.valueOf(System.currentTimeMillis()));

            FirebaseRef.getMessageRef().child(FirebaseRef.getCurrentUserId()).push().setValue(message);
            FirebaseRef.getMessageRef().child(userId).push().setValue(message);
            binding.message.setText("");
            ProgressDialog.DismissProgressDialog();
        }


    }

    private void sendMessage() {
        String messageId = FirebaseRef.getMessageRef().push().getKey();

        HashMap<String, Object> msg = new HashMap<>();
        msg.put("m_id", messageId);
        msg.put("sender_id", FirebaseRef.getCurrentUserId());
        msg.put("receiver_id", userId);
        msg.put("messageImage", selectedImage.toString());
        if (TextUtils.isEmpty(this.message))
            msg.put("type", 1);
        else {
            msg.put("type", 2);
            msg.put("message", this.message);
        }
        msg.put("timestamp", String.valueOf(System.currentTimeMillis()));

        FirebaseRef.getMessageRef().child(FirebaseRef.getCurrentUserId()).push().setValue(msg);
        FirebaseRef.getMessageRef().child(userId).push().setValue(msg);
        binding.message.setText("");
        ProgressDialog.DismissProgressDialog();
    }


    private void setAdapter() {
        FirebaseRef.getMessageRef()
                .child(FirebaseRef.getCurrentUserId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Chat model = dataSnapshot.getValue(Chat.class);
                            if (model.getReceiver_id().equals(FirebaseRef.getUserId()) && model.getSender_id().equals(userId) ||
                                    model.getReceiver_id().equals(userId) && model.getSender_id().equals(FirebaseRef.getCurrentUserId())) {
                                messageList.add(model);
                            }
                        }
                        messageAdapter.notifyDataSetChanged();


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    private void makingChatList() {
        DatabaseReference chatRef = FirebaseRef.getConversationRef()
                .child(FirebaseRef.getCurrentUserId())
                .child(userId);

        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference chatRef1 = FirebaseRef.getConversationRef()
                .child(userId)
                .child(FirebaseRef.getCurrentUserId());

        chatRef1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    chatRef1.child("id").setValue(FirebaseRef.getCurrentUserId());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}