package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.UserPostAdapter;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.model.Post;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class YourprofileActivity extends AppCompatActivity {

    //    TODO: Context
    AppCompatActivity mContext;

    //    TODO: Widgets

    @BindView(R.id.back_press)
    ImageView Back_Press;
    @BindView(R.id.edit_profile)
    TextView Edit;
    @BindView(R.id.profile_img)
    ImageView Profile_Img;
    @BindView(R.id.name)
    TextView Name;
    @BindView(R.id.game)
    TextView Game;
    @BindView(R.id.recycler_view)
    RecyclerView Recycler_View;

    //  TODO: Variables
    String userId;

    LinkedList<Post> postList;

    UserPostAdapter userPostAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yourprofile);

        mContext = this;
        ButterKnife.bind(this);
        postList = new LinkedList<>();


//        TODO: Get Intent
//
        userId = getIntent().getStringExtra("User_Id");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        Recycler_View.setLayoutManager(linearLayoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext, linearLayoutManager.getOrientation());
        Recycler_View.addItemDecoration(dividerItemDecoration);

        Log.d("UserId", userId);

        if (userId.equals(FirebaseRef.getUserId())) {
            Edit.setVisibility(View.VISIBLE);
        }

        //        TODO: Get User Data
        getUserData();
//        TODO: Get Profile Data
        getProfileData();


//        TODO: Click Listeners
        Back_Press.setOnClickListener(this::backPress);
        Edit.setOnClickListener(this::openEditProfile);
    }

    private void openEditProfile(View view) {

        startActivity(new Intent(mContext, EditProfileActivity.class));

    }

    private void backPress(View view) {
        onBackPressed();
    }

    private void getUserData() {
        FirebaseRef.getUserRef()
                .child(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);


                        Picasso.get().load(user.getProfile_img()).into(Profile_Img);
                        Name.setText(user.getFirst_name() + " " + user.getLast_name());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, "Alert!: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void getProfileData() {
        FirebaseRef.getPostsRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            postList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Post model = snapshot.getValue(Post.class);
                                if (model.getPosted_by().equals(userId)) {
                                    postList.addFirst(model);
                                }
                            }
                            Log.e("POSTSUSER", String.valueOf(postList.size()));

                            userPostAdapter = new UserPostAdapter(mContext, postList);
                            Recycler_View.setAdapter(userPostAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}


