/********************************
 * Developed By: Codesses
 * Developer Name: Saad Iftikhar
 *
 */

package com.codesses.lgucircle.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;


import com.codesses.lgucircle.Adapters.UserPostAdapter;
import com.codesses.lgucircle.Interfaces.OnItemClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.PostUploadAVActivity;
import com.codesses.lgucircle.databinding.FragmentPostFeedBinding;
import com.codesses.lgucircle.model.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.LinkedList;


public class PostFeedFragment extends Fragment {

    //    Context
    private FragmentActivity mContext;

    //    Data binding
    private FragmentPostFeedBinding binding;

    //    ArrayList
    private LinkedList<Post> sportsFeedList = new LinkedList<>();
    private UserPostAdapter adapter;


    public PostFeedFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

//        getSportsFeed();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_post_feed, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getSportsFeed();

//        RecyclerView
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        binding.recyclerView.setLayoutManager(linearLayoutManager);


        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PostUploadAVActivity.class);
                startActivity(intent);
            }
        });

    }


    private void getSportsFeed() {

        FirebaseRef.getPostsRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            sportsFeedList.clear();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Post model = snapshot.getValue(Post.class);
                                sportsFeedList.addFirst(model);
                            }

                            adapter = new UserPostAdapter(mContext, sportsFeedList, new OnItemClick() {
                                @Override
                                public void onClick(String id) {

                                }

                                @Override
                                public void onMenuClick(View view, String id) {
                                    showMenu(view, id);
                                }
                            });
                            binding.recyclerView.setAdapter(adapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void showMenu(View v, String id) {
        PopupMenu popup = new PopupMenu(mContext, v);

        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.delete:
                        FirebaseRef
                                .getPostsRef()
                                .child(id)
                                .removeValue()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(mContext, "Successful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                        break;

                }
                return false;
            }
        });
        popup.inflate(R.menu.post_menu);
        popup.show();
    }

}