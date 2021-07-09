package com.codesses.lgucircle.Fragments.Authority;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codesses.lgucircle.Adapters.IdeaAdapter;
import com.codesses.lgucircle.Interfaces.IdeaClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Singleton.VolleySingleton;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.FragmentIdeaStatusBinding;
import com.codesses.lgucircle.model.Idea;
import com.codesses.lgucircle.model.User;
import com.codesses.lgucircle.smtp.Smtp;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class IdeaStatusFragment extends Fragment {

    FragmentIdeaStatusBinding binding;
    FragmentActivity fragmentActivity;

    List<Idea> ideaList = new ArrayList<>();

    IdeaAdapter ideaAdapter;

    int status;
    User user;

    public IdeaStatusFragment(int position) {
        status = position;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIdeaStatusBinding.bind(inflater.inflate(R.layout.fragment_idea_status, container, false));
        initAdapter();
        getIdeas();
        return binding.getRoot();
    }

    private void initAdapter() {
        ideaAdapter = new IdeaAdapter(fragmentActivity, ideaList, new IdeaClick() {
            @Override
            public void onAccept(String id, int position) {
                showDialog(id, position);
            }

            @Override
            public void onReject(String id, int position) {
                getUserData(ideaList.get(position).getPitched_by());
                updateStatus(id, position, 2, 0);
            }
        });
        binding.ideaRecycler.setAdapter(ideaAdapter);
    }

    private void updateStatus(String id, int position, int status, int type) {
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);
        String i_id = ideaList.get(position).getI_id();
        FirebaseRef.getIdeaRef()
                .child(i_id)
                .updateChildren(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        if (status == 3) {
                            if (type == 2) {
                                sendAcceptEmail(id, fragmentActivity.getString(R.string.accept_message));
                                sendNotification(1);
                            } else if (type == 1) {
                                sendManualMail(id);
                                sendNotification(1);
                            }
                        } else if (status == 2) {
                            sendNotification(2);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(fragmentActivity, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void showDialog(String id, int position) {
        String message = "";
        message = "Are you sure you want to accept this idea?";

        new AlertDialog.Builder(fragmentActivity)
                .setTitle("Confirmation")
                .setCancelable(false)
                .setMessage(message)

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        showEmailDialog(id, position);
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .show();

    }

    private void showEmailDialog(String id, int position) {
        new AlertDialog.Builder(fragmentActivity)
                .setTitle("Confirmation")
                .setCancelable(false)
                .setMessage("Please select type of email you want to send.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(R.string.manual, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        getUserData(ideaList.get(position).getPitched_by());
                        updateStatus(id, position, 3, 1);
                        dialog.dismiss();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(R.string._default, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getUserData(ideaList.get(position).getPitched_by());
                        updateStatus(id, position, 3, 2);
                        dialog.dismiss();
                    }
                })
                .show();

    }

    private void getUserData(String id) {
        FirebaseRef.getUserRef()
                .child(id)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("IntentReset")
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        user = snapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void sendManualMail(String id) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[]{user.getEmail()});
        email.putExtra(Intent.EXTRA_SUBJECT, "Idea Accepted");
        email.setType("message/rfc822");
        startActivity(email);
    }

    private void sendAcceptEmail(String id, String message) {
        FirebaseRef.getUserRef()
                .child(id)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        Smtp smtp = new Smtp(fragmentActivity, user.getEmail(), "Idea Accepted", "Hi " + user.getFirst_name() + "\n" + message);
                        String message = "";
                        try {
                            message = smtp.execute().get();
                            Toast.makeText(fragmentActivity, message, Toast.LENGTH_SHORT).show();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void getIdeas() {
        FirebaseRef.getIdeaRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                ideaList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Idea idea = dataSnapshot.getValue(Idea.class);
                    if (idea.getStatus() == status)
                        ideaList.add(idea);
                }
                initAdapter();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendNotification(int type) {
        String nId = FirebaseRef.getNotificationRef().push().getKey();

        Map<String, Object> map = new HashMap<>();

        map.put("n_id", nId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("type", type);
        map.put("sent_to", user.getU_id());
        map.put("sent_by", FirebaseRef.getUserId());
        if (type == 1) {
            map.put("title", "Idea Accepted");
            map.put("message", "Congratulations your idea has been accepted");
        } else {
            map.put("title", "Idea Rejected");
            map.put("message", "Unfortunately your idea has been rejected");
        }
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
            if (user.getFcmToken() != null) {
                JSONObject mainObject = new JSONObject();
                JSONObject notificationObject = new JSONObject();
                JSONObject dataObject = new JSONObject();

                mainObject.put("to", "/token/" + user.getFcmToken());

//            Notification body
                notificationObject.put("title", "idea");
                notificationObject.put("body", fragmentActivity.getString(R.string.new_message));

//            Custom payload
//            dataObject.put("c_id", userId);
//            dataObject.put("user_image", user.getProfile_img());
//            dataObject.put("user_name", user.getFull_name());
                mainObject.put("notification", notificationObject);
                mainObject.put("data", dataObject);

                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST,
                        ApplicationUtils.FCM_URL,
                        mainObject,
                        response -> {

                            Log.d("FCM_RESPONSE", "sendPushNotification: " + response);
                            Toast.makeText(fragmentActivity, "Posted", Toast.LENGTH_SHORT).show();

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

                VolleySingleton.getInstance(fragmentActivity).addToRequestQueue(request);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}