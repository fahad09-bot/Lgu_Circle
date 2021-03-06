package com.codesses.lgucircle.Dialogs;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import com.codesses.lgucircle.activity.Services.ConversationAC;
import com.codesses.lgucircle.activity.Services.ServicesChatAC;
import com.codesses.lgucircle.activity.YourprofileActivity;
import com.codesses.lgucircle.databinding.FragmentUserSearchDialogBinding;
import com.codesses.lgucircle.model.User;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class UserSearchDialog extends DialogFragment {

    FragmentUserSearchDialogBinding binding;
    FragmentActivity fragmentActivity;
    UserAdapter userAdapter;
    List<User> userList = new ArrayList<>();
    String activityName;
    Intent intent;

    public UserSearchDialog(String activityName) {
        this.activityName = activityName;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
        setStyle(STYLE_NORMAL, R.style.MyEventBottomSheet);
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
                    if (!user.getU_id().equals(FirebaseRef.getCurrentUserId()) && !user.getType().equals("authority"))
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
                    if (user.getFull_name().trim().toLowerCase().contains(s.toString().trim().toLowerCase()))
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
                this::activityStart);
        binding.userRecycler.setAdapter(userAdapter);
    }

    private void activityStart(String userId) {

        if (TextUtils.equals(activityName, "ConversationAC"))
            intent = new Intent( fragmentActivity, ServicesChatAC.class);
        else if (TextUtils.equals(activityName, "MainActivity")) {
            intent = new Intent(fragmentActivity, YourprofileActivity.class);
        }

        intent.putExtra(Constants.USER_ID, userId);
        fragmentActivity.startActivity(intent);

    }
}