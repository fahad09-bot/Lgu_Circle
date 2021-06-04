package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.EventAdapter;
import com.codesses.lgucircle.Interfaces.OnEventImageClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivityEventBinding;
import com.codesses.lgucircle.model.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EventAC extends AppCompatActivity {

    List<Event> eventsList;
    ActivityEventBinding binding;
    AppCompatActivity mContext;
    EventAdapter eventAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_event);
        getEvents();
        setAdapter();
        binding.btnBack.setOnClickListener(v -> onBackPressed());
    }

    private void setAdapter() {
        eventAdapter = new EventAdapter(mContext, eventsList, new OnEventImageClick() {
            @Override
            public void onImageClick(String path) {
                Intent intent = new Intent(mContext, ImageViewActivity.class);
                intent.putExtra(getString(R.string.intent_open_full_screen_image), path);
                startActivity(intent);
            }
        });

        binding.recyclerView.setAdapter(eventAdapter);
    }

    private void getEvents() {
        eventsList = new ArrayList<>();
        FirebaseRef
                .getEventRef()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren())
                        {
                            Event event = dataSnapshot.getValue(Event.class);
                            eventsList.add(event);
                        }

                        eventAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        Toast.makeText(mContext, "Error! " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}