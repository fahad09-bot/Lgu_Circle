package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.ProfilePagerAdapter;
import com.codesses.lgucircle.Adapters.UserPostAdapter;
import com.codesses.lgucircle.Adapters.UserTabsAdapter;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.Services.ServicesChatAC;
import com.codesses.lgucircle.databinding.YourprofileBinding;
import com.codesses.lgucircle.model.Post;
import com.codesses.lgucircle.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class YourprofileActivity extends AppCompatActivity {

    //    TODO: Context
    AppCompatActivity mContext;

    YourprofileBinding binding;

    //  TODO: Variables
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = YourprofileBinding.bind(LayoutInflater.from(mContext).inflate(R.layout.yourprofile, null));
        setContentView(binding.getRoot());


//        TODO: Get Intent
//
        userId = getIntent().getStringExtra(Constants.USER_ID);
        if (userId.equals(FirebaseRef.getUserId()))
            binding.message.setVisibility(View.GONE);
        else
            binding.message.setVisibility(View.VISIBLE);

        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_newsfeed));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setIcon(R.drawable.ic_service));

        final ProfilePagerAdapter adapter = new ProfilePagerAdapter(
                getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                binding.tabLayout.getTabCount(), userId);

        binding.viewPager.setAdapter(adapter);

        binding.editProfile.setEnabled(false);
        binding.editProfile.setAlpha(0.5f);

        Log.d("UserId", userId);

        if (userId.equals(FirebaseRef.getUserId())) {
            binding.editProfile.setVisibility(View.VISIBLE);
        }

        binding.tabLayout.getTabAt(0).getIcon()
                .setColorFilter(
                        getResources().getColor(R.color.Green), PorterDuff.Mode.SRC_IN);

        //        TODO: Get User Data
        getUserData();

        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
                binding.tabLayout.getTabAt(tab.getPosition()).getIcon()
                        .setColorFilter(
                                getResources().getColor(R.color.Green), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                binding.tabLayout.getTabAt(tab.getPosition()).getIcon()
                        .setColorFilter(
                                getResources().getColor(R.color.blue_grey_300), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });


//        TODO: Click Listeners
        binding.backPress.setOnClickListener(this::backPress);
        binding.editProfile.setOnClickListener(this::openEditProfile);
        binding.message.setOnClickListener(this::startChat);

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
                        binding.bio.setText(user.getBio());


                        Picasso.get().load(user.getProfile_img()).into(binding.profileImg, new Callback() {
                            @Override
                            public void onSuccess() {
                                binding.editProfile.setEnabled(true);
                                binding.editProfile.setAlpha(1f);
                            }

                            @Override
                            public void onError(Exception e) {

                            }
                        });
                        binding.name.setText(user.getFirst_name() + " " + user.getLast_name());


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(mContext, "Alert!: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }

    private void startChat(View view) {
        Intent intent = new Intent(mContext, ServicesChatAC.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
    }


}


