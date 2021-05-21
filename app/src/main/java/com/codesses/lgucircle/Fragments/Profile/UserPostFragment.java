package com.codesses.lgucircle.Fragments.Profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codesses.lgucircle.Adapters.UserPostAdapter;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.FragmentUserPostBinding;
import com.codesses.lgucircle.model.Post;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserPostFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
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

    public UserPostFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserPostFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserPostFragment newInstance(String param1, String param2) {
        UserPostFragment fragment = new UserPostFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
        return binding.getRoot();
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
                                if (model.getPosted_by().equals(FirebaseRef.getUserId())) {
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