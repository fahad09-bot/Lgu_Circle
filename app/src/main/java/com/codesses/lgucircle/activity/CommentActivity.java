package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.ReplyAdapter;
import com.codesses.lgucircle.Interfaces.OnReplyClick;
import com.codesses.lgucircle.R;

import com.codesses.lgucircle.Utils.CurrentDateAndTime;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivityCommentBinding;
import com.codesses.lgucircle.databinding.CommentsItemLayoutBinding;
import com.codesses.lgucircle.model.Comment;
import com.codesses.lgucircle.model.OpinionReply;
import com.codesses.lgucircle.viewHolder.CommentViewHolder;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.firebase.recyclerpagination.DatabasePagingOptions;
import com.shreyaspatil.firebase.recyclerpagination.FirebaseRecyclerPagingAdapter;
import com.shreyaspatil.firebase.recyclerpagination.LoadingState;


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
            selectedOpinionId = "";

    //    Firebase paging adapter
    private DatabaseReference postIdRef;
    private PagedList.Config config;
    private FirebaseRecyclerPagingAdapter<Comment, CommentViewHolder> mAdapter;
    private DatabasePagingOptions<Comment> options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_comment);

//        TODO: Getting intent & show full screen image
        postId = getIntent().getStringExtra(getString(R.string.post_id));

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

        binding.swipeRefreshLayout.setOnRefreshListener(() -> mAdapter.refresh());

        postIdRef = FirebaseRef.getCommentRef().child(postId);
        config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(5)
                .setPageSize(10)
                .build();

        options = new DatabasePagingOptions.Builder<Comment>()
                .setLifecycleOwner(this)
                .setQuery(postIdRef, config, Comment.class)
                .build();

        getOpinions();


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
                    binding.sendOpinion.setColorFilter(getResources().getColor(R.color.black));
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
                    binding.sendReply.setColorFilter(getResources().getColor(R.color.black));
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
        mAdapter.stopListening();
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
        mAdapter = new FirebaseRecyclerPagingAdapter<Comment, CommentViewHolder>(options) {
            RecyclerView.RecycledViewPool recycledViewPool = new RecyclerView.RecycledViewPool();

            @NonNull
            @Override
            public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                CommentsItemLayoutBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(mContext), R.layout.comments_item_layout, parent, false);
                return new CommentViewHolder(binding);
            }

            @Override
            protected void onBindViewHolder(@NonNull CommentViewHolder holder,
                                            int position,
                                            @NonNull Comment model) {
                holder.bind(mContext, model, onReplyClick);

                List<OpinionReply> repliesList = new ArrayList<>();
                postIdRef.child(model.getC_id()).child(mContext.getString(R.string.reply_lowercase))
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
            protected void onLoadingStateChanged(@NonNull LoadingState state) {
                switch (state) {
                    case LOADING_INITIAL:
                    case LOADING_MORE:
                        // Do your loading animation
                        binding.swipeRefreshLayout.setRefreshing(true);
                        break;

                    case LOADED:
                        // Stop Animation
                        binding.swipeRefreshLayout.setRefreshing(false);
                        break;

                    case FINISHED:
                        //Reached end of Data set
                        binding.swipeRefreshLayout.setRefreshing(false);
                        break;

                    case ERROR:
//                        retry();
                        break;
                }
            }

        };
        mAdapter.startListening();
        binding.recyclerView.setAdapter(mAdapter);
    }

    private void postOpinion(View view) {
        if (!TextUtils.isEmpty(opinion)) {
            String opinionId = FirebaseRef.getCommentRef().push().getKey();

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
                            getOpinionCount();
                        } else {
                            Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
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
            String replyId = FirebaseRef.getCommentRef().push().getKey();

            Map<String, Object> replyMap = new HashMap<>();
            replyMap.put("r_id", replyId);
            replyMap.put("replied_by", FirebaseRef.getUserId());
            replyMap.put("reply", reply);
            replyMap.put("date", CurrentDateAndTime.currentDate());
            replyMap.put("time", CurrentDateAndTime.currentTime());

            FirebaseRef.getCommentRef().child(postId).child(selectedOpinionId)
                    .child(getString(R.string.reply_lowercase)).child(replyId)
                    .updateChildren(replyMap)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            binding.replyBox.setText("");
                            Toast.makeText(mContext, "Reply: " + replyId, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "Warning: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }
    }

    private void getReplies(String oId, String postId, CommentViewHolder holder, RecyclerView.RecycledViewPool recycledViewPool) {


    }

}