package com.codesses.lgucircle.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.EventAdapter;
import com.codesses.lgucircle.Dialogs.EventsUpload;
import com.codesses.lgucircle.Interfaces.OnEventImageClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.activity.ImageViewActivity;
import com.codesses.lgucircle.databinding.FragmentEventsBinding;
import com.codesses.lgucircle.model.Event;
import com.codesses.lgucircle.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventsFragment extends Fragment {

    FragmentEventsBinding binding;
    FragmentActivity fragmentActivity;

    List<Event> eventsList = new ArrayList<>();

    EventAdapter eventAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentActivity = getActivity();
        binding = FragmentEventsBinding.bind(inflater.inflate(R.layout.fragment_events, container, false));

        binding.addEvent.setOnClickListener(this::createEvent);
        checkUserType();
        getEvents();
        setAdapter();
        return binding.getRoot();
    }

    private void setAdapter() {

        eventAdapter = new EventAdapter(fragmentActivity, eventsList, new OnEventImageClick() {
            @Override
            public void onImageClick(String path) {
                Intent intent = new Intent(fragmentActivity, ImageViewActivity.class);
                intent.putExtra(getString(R.string.intent_open_full_screen_image), path);
                startActivity(intent);
            }
        });

        binding.recyclerView.setAdapter(eventAdapter);
    }

    private void createEvent(View view) {
        EventsUpload eventsUpload = new EventsUpload();
        eventsUpload.show(fragmentActivity.getSupportFragmentManager(), "Event Upload");
    }

    private void getEvents() {
        FirebaseRef
                .getEventRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        eventsList.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Event event = dataSnapshot.getValue(Event.class);
                            eventsList.add(event);
                        }

                        eventAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(fragmentActivity, "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUserType() {
        FirebaseRef
                .getUserRef()
                .child(FirebaseRef.getCurrentUserId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        User model = snapshot.getValue(User.class);
                        if (model.getType().toLowerCase().equals("authority"))
                            binding.addEvent.setVisibility(View.VISIBLE);
                        else
                            binding.addEvent.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(fragmentActivity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}