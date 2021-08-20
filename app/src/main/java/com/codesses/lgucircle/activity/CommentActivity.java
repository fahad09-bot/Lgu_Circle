package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.codesses.lgucircle.Adapters.ReplyAdapter;
import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.Interfaces.OnReplyClick;
import com.codesses.lgucircle.R;

import com.codesses.lgucircle.Singleton.VolleySingleton;
import com.codesses.lgucircle.Utils.ApplicationUtils;
import com.codesses.lgucircle.Utils.CurrentDateAndTime;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.databinding.ActivityCommentBinding;
import com.codesses.lgucircle.databinding.CommentsItemLayoutBinding;
import com.codesses.lgucircle.model.Comment;
import com.codesses.lgucircle.model.OpinionReply;
import com.codesses.lgucircle.model.User;
import com.codesses.lgucircle.viewHolder.CommentViewHolder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    //   Context
    private AppCompatActivity mContext;

    //   View binding
    private ActivityCommentBinding binding;

    //    Variables
    private String postId = "",
            opinion = "",
            reply = "",
            selectedOpinionId = "",
    opinionId = "";

    //    Firebase paging adapter
    private DatabaseReference postIdRef;
    RecyclerView recyclerView;
    Adapter mAdapter;
    List<Comment> commentList = new ArrayList<>();
    User user;
    String postedById;
    private String fcmToken;
    private String opinionById;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_comment);

//        TODO: Getting intent & show full screen image
        postId = getIntent().getStringExtra(getString(R.string.post_id));
        postedById = getIntent().getStringExtra(getString(R.string.intent_posted_by_id));
        Gson gson = new Gson();

        user = gson.fromJson(SharedPrefManager.getInstance(mContext).getSharedData(SharedPrefKey.USER), User.class);

        getOpinions();

        //        Get posted by user
        getPostedByUser();

        /****************
         * Click listener
         */
        binding.close.setOnClickListener(this::onBackPressed);
        binding.sendOpinion.setOnClickListener(this::postOpinion);
        binding.closeReplyBox.setOnClickListener(this::closeReplyBox);
        binding.sendReply.setOnClickListener(this::postReply);


        /****************
         * Recycler view
         */
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mContext));

        binding.swipeRefreshLayout.setOnRefreshListener(() -> mAdapter.notifyDataSetChanged());




        /**********************
         * Text watcher opinion
         */
        binding.opinionBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                opinion = s.toString();
                if (!TextUtils.isEmpty(opinion)) {
                    binding.sendOpinion.setColorFilter(getResources().getColor(R.color.Green));
                } else {
                    binding.sendOpinion.setColorFilter(getResources().getColor(R.color.Light_grey));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /**********************
         * Text watcher reply
         */
        binding.replyBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                reply = s.toString();
                if (!TextUtils.isEmpty(reply)) {
                    binding.sendReply.setColorFilter(getResources().getColor(R.color.Green));
                } else {
                    binding.sendReply.setColorFilter(getResources().getColor(R.color.Light_grey));

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void onBackPressed(View view) {
        onBackPressed();
    }

    private void closeReplyBox(View view) {
        binding.opinionBoxLayout.setVisibility(View.VISIBLE);
        binding.replyBoxLayout.setVisibility(View.GONE);
    }

    /******************
     * Manage Opinions
     */
    private void getOpinions() {
        postIdRef = FirebaseRef.getCommentRef().child(postId);
        postIdRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    commentList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        commentList.add(dataSnapshot.getValue(Comment.class));
                    }
                    setAdapter();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void postOpinion(View view) {
        if (!TextUtils.isEmpty(opinion)) {
            opinionId = FirebaseRef.getCommentRef().push().getKey();

            Map<String, Object> opinionMap = new HashMap<>();
            opinionMap.put("c_id", opinionId);
            opinionMap.put("commented_by", FirebaseRef.getUserId());
            opinionMap.put("comment", opinion);
            opinionMap.put("date", CurrentDateAndTime.currentDate());
            opinionMap.put("time", CurrentDateAndTime.currentTime());


            FirebaseRef.getCommentRef().child(postId).child(opinionId)
                    .updateChildren(opinionMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            binding.opinionBox.setText("");
                            sendNotification();
                            getOpinionCount();
                        } else {
                            Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void sendNotification() {
        String title = user.getFull_name();
        String nId = FirebaseRef.getNotificationRef().push().getKey();

        Map<String, Object> map = new HashMap<>();

        map.put("n_id", nId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("type", 1);
        map.put("post_id", postId);
        map.put("title", title);
        map.put("message",mContext.getString(R.string.label_leave_opinion_on_post));
        map.put("o_id", opinionId);
        map.put("sent_by", FirebaseRef.getUserId());
        map.put("sent_to", postedById);
        map.put("is_read", 0);

        assert nId != null;
        FirebaseRef.getNotificationRef().child(nId).updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        try {
                            sendPushNotification();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {
                        Log.e(ApplicationUtils.ERROR_TAG, task.getException().getMessage());
                        Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void getPostedByUser() {
        FirebaseRef.getUserRef().child(postedById)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            User postedByUser = dataSnapshot.getValue(User.class);

                            assert postedByUser != null;
                            fcmToken = postedByUser.getFcmToken();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {
                        Log.e(ApplicationUtils.ERROR_TAG, databaseError.getMessage());
                    }
                });
    }

    private void sendPushNotification() throws JSONException {
        JSONObject mainObject = new JSONObject();
        JSONObject notificationObject = new JSONObject();
        JSONObject dataObject = new JSONObject();


//            Main body
        mainObject.put("to", "/token/" + fcmToken);

//            Notification body
        notificationObject.put("title", user.getFull_name());
        notificationObject.put("body", mContext.getString(R.string.label_leave_opinion_on_post));
        notificationObject.put("click_action", "OPINION_ACTIVITY");

//            Custom payload
        dataObject.put(mContext.getString(R.string.intent_notification_type), mContext.getString(R.string.label_opinion));
        dataObject.put(mContext.getString(R.string.intent_post_id), postId);
        dataObject.put(mContext.getString(R.string.intent_posted_by_id), postedById);
        dataObject.put(mContext.getString(R.string.intent_opinion_id), opinionId);

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
    }


    private void sendReplyNotification() throws JSONException {
        JSONObject mainObject = new JSONObject();
        JSONObject notificationObject = new JSONObject();
        JSONObject dataObject = new JSONObject();


//            Main body
        mainObject.put("to", "/token/" + fcmToken);

//            Notification body
        notificationObject.put("title", user.getFull_name());
        notificationObject.put("body", mContext.getString(R.string.label_replied_to_opinion));
        notificationObject.put("click_action", "REPLY_ACTIVITY");


//            Custom payload
        dataObject.put(mContext.getString(R.string.intent_notification_type), mContext.getString(R.string.label_reply));
        dataObject.put(mContext.getString(R.string.intent_post_id), postId);
        dataObject.put(mContext.getString(R.string.intent_opinion_id), opinionId);
        dataObject.put(mContext.getString(R.string.intent_opinioned_by_id), opinionById);

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
    }

    private void getOpinionCount() {
        FirebaseRef.getCommentRef().child(postId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            postOpinionCount(dataSnapshot.getChildrenCount());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, "Warning: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void postOpinionCount(long count) {
        Map<String, Object> countMap = new HashMap<>();
        countMap.put("comment_count", count);

        FirebaseRef.getPostsRef().child(postId).updateChildren(countMap)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(mContext, "Opinion: " + count, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /******************
     * Manage reply
     */

    private void postReply(View view) {
        if (!TextUtils.isEmpty(reply)) {
            opinionById = FirebaseRef.getCommentRef().push().getKey();

            Map<String, Object> replyMap = new HashMap<>();
            replyMap.put("r_id", opinionById);
            replyMap.put("replied_by", FirebaseRef.getUserId());
            replyMap.put("reply", reply);
            replyMap.put("date", CurrentDateAndTime.currentDate());
            replyMap.put("time", CurrentDateAndTime.currentTime());

            FirebaseRef.getCommentRef().child(postId).child(selectedOpinionId)
                    .child(getString(R.string.reply_lowercase)).child(opinionById)
                    .updateChildren(replyMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            binding.replyBox.setText("");
                            sendNotificationForReply();
                            Toast.makeText(mContext, "Reply: " + opinionById, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void sendNotificationForReply() {
        String title = user.getFull_name();
        String message = mContext.getString(R.string.label_replied_to_opinion);

        String nId = FirebaseRef.getNotificationRef().push().getKey();

        Map<String, Object> map = new HashMap<>();

        map.put("n_id", nId);
        map.put("timestamp", System.currentTimeMillis());
        map.put("type", 1);
        map.put("post_id", postId);
        map.put("title", title);
        map.put("message", message);
        map.put("o_id", opinionId);
        map.put("sent_by", FirebaseRef.getUserId());
        map.put("sent_to", opinionById);
        map.put("is_read", 0);

        assert nId != null;
        FirebaseRef.getNotificationRef().child(nId).updateChildren(map)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.replyBox.setText("");
                        try {
                            sendReplyNotification();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.e(ApplicationUtils.ERROR_TAG, task.getException().getMessage());
                        Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setAdapter() {
        /**********************************
         * open reply box by click on reply
         */
        OnReplyClick onReplyClick = (reply, opinionId) -> {
            if (true) {
                binding.opinionBoxLayout.setVisibility(View.GONE);
                binding.replyBoxLayout.setVisibility(View.VISIBLE);
                selectedOpinionId = opinionId;

            }
        };


        /**********
         * Adapter
         */
        mAdapter = new RecyclerView.Adapter<CommentViewHolder>() {
            final RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                CommentsItemLayoutBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(mContext), R.layout.comments_item_layout, parent, false);
                return new CommentViewHolder(binding);
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull CommentViewHolder holder, int position) {

                holder.bind(mContext, commentList.get(position), onReplyClick);

                List<OpinionReply> repliesList = new ArrayList<>();
                postIdRef.child(commentList.get(position).getC_id()).child(mContext.getString(R.string.reply_lowercase))
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                repliesList.clear();
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        OpinionReply model = snapshot.getValue(OpinionReply.class);
                                        repliesList.add(model);

                                    }

                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, RecyclerView.VERTICAL, false);
                                    linearLayoutManager.setInitialPrefetchItemCount(repliesList.size());
                                    ReplyAdapter adapter = new ReplyAdapter(mContext, repliesList);
                                    holder.replyRecyclerView.setLayoutManager(linearLayoutManager);
                                    holder.replyRecyclerView.setAdapter(adapter);
                                    holder.replyRecyclerView.setRecycledViewPool(recycledViewPool);

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }

            @Override
            public int getItemCount() {
                return commentList.size();
            }


        };
        binding.recyclerView.setAdapter(mAdapter);
    }


}