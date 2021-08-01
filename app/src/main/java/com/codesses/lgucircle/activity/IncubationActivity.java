package com.codesses.lgucircle.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Singleton.VolleySingleton;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.databinding.IncubationBinding;
import com.codesses.lgucircle.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class IncubationActivity extends AppCompatActivity {
    AppCompatActivity mContext;

    IncubationBinding binding;
    User user;
    String pitch;

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
        Gson gson = new Gson();
        user = gson.fromJson(SharedPrefManager.getInstance(mContext).getSharedData(SharedPrefKey.USER), User.class);
        binding.iName.setText(user.getFirst_name() + " " + user.getLast_name());
        binding.iEmail.setText(user.getEmail());
        binding.iPhoneNo.setText(user.getPhone());
        binding.department.setText(user.getDepartment());
        binding.rollNo.setText(user.getRoll_no());
//        FirebaseRef.getUserRef()
//                .child(FirebaseRef.getUserId())
//                .addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user = dataSnapshot.getValue(User.class);
//
//                        
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//                        Toast.makeText(mContext, "Alert!: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });

    }


    private void registerIdea(View view) {
        pitch = binding.pitch.getText().toString();

        if (!TextUtils.isEmpty(pitch)) {
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
        } else {
            Toast.makeText(mContext, "Please fill the field", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendDataToFirebase() {

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
                        sendNotification();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void sendNotification() {
        String nId = FirebaseRef.getNotificationRef().push().getKey();

        Map<String, Object> map = new HashMap<>();

        map.put("n_id", nId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("type", 3);
        map.put("sent_by", FirebaseRef.getUserId());
        map.put("title", user.getFull_name());
        map.put("message", pitch);
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
            mainObject.put("to", "/topics/" + "ideas");

//            Notification body
            notificationObject.put("title", "idea");
            notificationObject.put("body", mContext.getString(R.string.new_message));

//            Custom payload
//            dataObject.put("c_id", userId);
            dataObject.put("user_image", user.getProfile_img());
            dataObject.put("user_name", user.getFull_name());
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

}
