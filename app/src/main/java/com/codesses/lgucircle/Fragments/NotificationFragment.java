package com.codesses.lgucircle.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.codesses.lgucircle.Enums.SharedPrefKey;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.Utils.SharedPrefManager;
import com.codesses.lgucircle.databinding.FragmentNotificationBinding;
import com.codesses.lgucircle.model.Notification;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationFragment extends Fragment {
    FragmentActivity context;
    private FragmentNotificationBinding binding;
    List<Notification> notificationList = new ArrayList<>();
    //    Adapter
    private NotificationsAdapter adapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
        initAdapter();
        getNotifications();
        return binding.getRoot();
    }

    private void initAdapter() {
        adapter = new NotificationsAdapter(notificationList, context);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(context));
        binding.recyclerView.setAdapter(adapter);
    }

    private void getNotifications() {

        FirebaseRef.getNotificationRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            notificationList.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                Notification model = dataSnapshot.getValue(Notification.class);
                                Gson gson = new Gson();
                                User user = gson.fromJson(SharedPrefManager.getInstance(context).getSharedData(SharedPrefKey.USER), User.class);
                                if (user.getType().equals("authority")) {
                                    if (model.getType() == 3) {
                                        notificationList.add(model);
                                    }
                                } else if (user.getType().equals("user")) {
                                    if (model.getType() == 0 || model.getType() == 1 || model.getType() == 2) {
                                        if (model.getSent_to().equals(FirebaseRef.getUserId())) {
                                            notificationList.add(model);
                                        }
                                    } else if (model.getType() == 4) {
                                        notificationList.add(model);

                                    }
                                }

                            }
                            adapter.notifyDataSetChanged();

//                    Sort array in descending order according to date
                            Collections.sort(notificationList, Collections.reverseOrder());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

}