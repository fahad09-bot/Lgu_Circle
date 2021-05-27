package com.codesses.lgucircle.Dialogs;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import com.codesses.lgucircle.Adapters.ConversationAdapter;
import com.codesses.lgucircle.Adapters.UserAdapter;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.Constants;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.Services.ServicesChatAC;
import com.codesses.lgucircle.databinding.FragmentUserSearchDialogBinding;
import com.codesses.lgucircle.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class UserSearchDialog extends DialogFragment {

    FragmentUserSearchDialogBinding binding;
    FragmentActivity fragmentActivity;
    UserAdapter userAdapter;
    List<User> userList = new ArrayList<>();

    public UserSearchDialog() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
        setStyle(STYLE_NORMAL, R.style.FullScreenBottomSheetDialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentUserSearchDialogBinding.bind(inflater.inflate(R.layout.fragment_user_search_dialog, container, false));

        getUsers();
        return binding.getRoot();
    }

    private void getUsers() {
        userList.clear();
        FirebaseRef.getUserRef().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren())
                {
                    User user = dataSnapshot.getValue(User.class);
                    if (!user.getU_id().equals(FirebaseRef.getCurrentUserId()))
                        userList.add(user);
                }
                setAdapter();
                filterData();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void filterData() {
        binding.searchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List<User> filteredList = new ArrayList<>();
                for (User user : userList)
                {
                    if (user.getFirst_name().contains(s.toString().trim().toLowerCase()) || user.getLast_name().contains(s.toString().trim().toLowerCase()))
                    {
                        filteredList.add(user);
                    }
                }
                userAdapter.filterUsers(filteredList);
            }
        });
    }

    private void setAdapter() {
        userAdapter = new UserAdapter(userList, fragmentActivity,
                this::startChat);
        binding.userRecycler.setAdapter(userAdapter);
    }

    private void startChat(String userId) {
        Intent intent = new Intent(fragmentActivity, ServicesChatAC.class);
        intent.putExtra(Constants.USER_ID, userId);
        startActivity(intent);
        dismiss();
    }
}