package com.codesses.lgucircle.activity.Services;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codesses.lgucircle.Adapters.MessageAdapter;
import com.codesses.lgucircle.Dialogs.MenuDialog;
import com.codesses.lgucircle.Dialogs.MsgImgUpDialog;
import com.codesses.lgucircle.Dialogs.ProgressDialog;
import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.Interfaces.OnImageClick;
import com.codesses.lgucircle.Interfaces.SendData;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Singleton.VolleySingleton;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.CheckEmptyFields;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.activity.ImageViewActivity;
import com.codesses.lgucircle.databinding.ActivityServicesChatBinding;
import com.codesses.lgucircle.model.Chat;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServicesChatAC extends AppCompatActivity implements OnImageClick {

    public static ActivityServicesChatBinding binding;
    AppCompatActivity mContext;
    MessageAdapter messageAdapter;
    String userId;
    User user, currentUser;
    String message;
    List<Chat> messageList = new ArrayList<>();
    Uri selectedImage;
    String imageUrl, fcmToken = "";
    int type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_services_chat);
        userId = getIntent().getStringExtra(Constants.USER_ID);
        binding.btnBack.setOnClickListener(v -> finish());
        getUserData();

        messageAdapter = new MessageAdapter(mContext, messageList, this);
        binding.chatRecycler.setAdapter(messageAdapter);
        binding.send.setOnClickListener(this::sendMessage);
        binding.attach.setOnClickListener(this::openDialogue);
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
//            @Override
//            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getBindingAdapterPosition();
//                deleteMessage(position);
//            }
//        });
//        itemTouchHelper.attachToRecyclerView(binding.chatRecycler);

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(mContext)
                        .setMessage("Delete " + Constants.selectedMessages + " messages?")
                        .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteMessages();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("CANCEL", null)
                        .create()
                        .show();
            }
        });
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
                fcmToken = user.getFcmToken();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        Gson gson = new Gson();
        currentUser = gson.fromJson(SharedPrefManager.getInstance(mContext).getSharedData(SharedPrefKey.USER), User.class);
//        FirebaseRef.getUserRef().child(FirebaseRef.getCurrentUserId()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                currentUser = snapshot.getValue(User.class);
//            }
//
//            @Override
//            public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//            }
//        });
    }

    private void sendMessage(View view) {
        makingChatList();
        ProgressDialog.ShowProgressDialog(mContext, R.string.sending, R.string.please_wait);
        if (CheckEmptyFields.isEditText(mContext, binding.message.getText().toString(), binding.message)) {
            String messageId = FirebaseRef.getMessageRef().push().getKey();
            message = binding.message.getText().toString().trim();
            HashMap<String, Object> message = new HashMap<>();
            message.put("m_id", messageId);
            message.put("sender_id", FirebaseRef.getCurrentUserId());
            message.put("receiver_id", userId);
            message.put("type", 0);
            message.put("message", this.message);
            message.put("timestamp", String.valueOf(System.currentTimeMillis()));

            FirebaseRef
                    .getMessageRef()
                    .child(FirebaseRef.getCurrentUserId())
                    .child(FirebaseRef.getUserId() + userId)
                    .child(messageId)
                    .updateChildren(message)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            FirebaseRef
                    .getMessageRef()
                    .child(userId)
                    .child(userId + FirebaseRef.getUserId())
                    .child(messageId)
                    .updateChildren(message)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                sendNotification(0);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            binding.message.setText("");
            ProgressDialog.DismissProgressDialog();
        }


    }

    private void sendNotification(int type) {
        String nId = FirebaseRef.getNotificationRef().push().getKey();

        Map<String, Object> map = new HashMap<>();

        map.put("n_id", nId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("type", 0);
        map.put("sent_by", FirebaseRef.getUserId());
        map.put("sent_to", userId);
        map.put("title", currentUser.getFull_name());
        if (type == 0 || type == 2)
            map.put("message", message);
        else
            map.put("message", "you have new message");
        map.put("is_read", 0);

        assert nId != null;
        FirebaseRef.getNotificationRef().child(nId).updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        sendPushNotification();
                    } else {
                        Log.e("error_tag", task.getException().getMessage());
                    }
                });

    }

    private void sendPushNotification() {

        try {
            JSONObject mainObject = new JSONObject();
            JSONObject notificationObject = new JSONObject();
            JSONObject dataObject = new JSONObject();
            mainObject.put("to", "/token/" + fcmToken);

//            Notification body
            notificationObject.put("title", "message");
            notificationObject.put("body", mContext.getString(R.string.new_message));

//            Custom payload
            dataObject.put("c_id", userId);
            dataObject.put("user_image", currentUser.getProfile_img());
            dataObject.put("user_name", currentUser.getFull_name());
            mainObject.put("notification", notificationObject);
            mainObject.put("data", dataObject);

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                    ApplicationUtils.FCM_URL,
                    mainObject,
                    response -> {

                        Log.d("FCM_RESPONSE", "sendPushNotification: " + response);
                        Toast.makeText(mContext, "Posted", Toast.LENGTH_SHORT).show();

                    },
                    error -> Log.d("FCM_RESPONSE", "Error " + error)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("content-type", "application/json");
                    headers.put("authorization", "key=AAAABefXTZo:APA91bFhvO8QxjODhBLgZSyqgnzIRYTOl02b2coksyna5790lvige4VHhKhdjD88dArcjHjgBbAfMl2oKNBKxJqTjLTT4aOqJRy-XFH70vftrxlBUXJU-H6hHWLYeGyLJK1GeoMpJjwB");
                    return headers;
                }
            };

            VolleySingleton.getInstance(mContext).addToRequestQueue(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void sendMessage() {
        String messageId = FirebaseRef.getMessageRef().push().getKey();
        makingChatList();
        HashMap<String, Object> msg = new HashMap<>();
        msg.put("m_id", messageId);
        msg.put("sender_id", FirebaseRef.getCurrentUserId());
        msg.put("receiver_id", userId);
        msg.put("messageImage", imageUrl);
        if (TextUtils.isEmpty(this.message)) {
            type = 1;
            msg.put("type", 1);
        } else {
            type = 2;
            msg.put("type", 2);
            msg.put("message", this.message.trim());
        }
        msg.put("timestamp", String.valueOf(System.currentTimeMillis()));

        FirebaseRef
                .getMessageRef()
                .child(FirebaseRef.getCurrentUserId())
                .child(FirebaseRef.getUserId() + userId)
                .child(messageId)
                .updateChildren(msg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        FirebaseRef
                .getMessageRef()
                .child(userId)
                .child(userId + FirebaseRef.getUserId())
                .child(messageId)
                .updateChildren(msg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            sendNotification(type);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
        binding.message.setText("");
        ProgressDialog.DismissProgressDialog();

    }


    private void setAdapter() {
        FirebaseRef.getMessageRef()
                .child(FirebaseRef.getCurrentUserId())
                .child(FirebaseRef.getCurrentUserId() + userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        messageList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            if (dataSnapshot.exists())
                                messageList.add(dataSnapshot.getValue(Chat.class));
                        }
                        if (!Constants.isMessagePicked) {
                            messageAdapter.notifyDataSetChanged();
                            binding.chatRecycler.scrollToPosition(messageList.size() - 1);
                        }


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
                    chatRef.child("c_id").setValue(userId);
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


    @Override
    public void onImageClick(String imageUrl) {
        Intent intent = new Intent(mContext, ImageViewActivity.class);
        intent.putExtra(getString(R.string.intent_open_full_screen_image), imageUrl);
        startActivity(intent);
    }

    @Override
    public void onLongClick() {
        binding.btnDelete.setVisibility(View.VISIBLE);
        binding.message.setEnabled(false);
        binding.attach.setEnabled(false);
        binding.send.setEnabled(false);
    }


    private void deleteMessages() {
        for (int i = 0; i < messageList.size(); i++) {
            if (messageList.get(i).isPicked()) {
                FirebaseRef
                        .getMessageRef()
                        .child(FirebaseRef.getCurrentUserId())
                        .child(FirebaseRef.getUserId() + userId)
                        .child(messageList.get(i).getM_id())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    dataSnapshot.getRef().removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
            }
        }
        binding.btnDelete.setVisibility(View.GONE);
        Constants.selectedMessages = -1;
        Constants.isMessagePicked = false;
        binding.message.setEnabled(true);
        binding.send.setEnabled(true);
        binding.attach.setEnabled(true);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Constants.isMessagePicked = false;
        Constants.selectedMessages = -1;
    }
}
