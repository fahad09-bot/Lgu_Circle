package com.codesses.lgucircle.Fragments.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.UserPostAdapter;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.FragmentUserPostBinding;
import com.codesses.lgucircle.model.Post;
import com.codesses.lgucircle.model.Service;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;


public class UserPostFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    FragmentUserPostBinding binding;

    FragmentActivity mContext;


    LinkedList<Post> postList = new LinkedList<>();

    UserPostAdapter userPostAdapter;

    String userId;

    public UserPostFragment(String userId) {
        // Required empty public constructor
        this.userId = userId;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        // Inflate the layout for this fragment
        binding = FragmentUserPostBinding.bind(inflater.inflate(R.layout.fragment_user_post, container, false));

        getProfileData();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                deletePost(position);
            }
        });
        if (userId.equals(FirebaseRef.getUserId())) {
            itemTouchHelper.attachToRecyclerView(binding.postsRecycler);
        }
        return binding.getRoot();
    }

    private void deletePost(int position) {
        FirebaseRef
                .getPostsRef()
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Post post = dataSnapshot.getValue(Post.class);
                            assert post != null;
                            if (post.getP_id().equals(postList.get(position).getP_id()) && post.getPosted_by().equals(FirebaseRef.getUserId())) {
                                dataSnapshot.getRef().removeValue();
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Log.e("OnCancelled", error.getMessage());
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
                            binding.postsRecycler.setAdapter(userPostAdapter);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }
}