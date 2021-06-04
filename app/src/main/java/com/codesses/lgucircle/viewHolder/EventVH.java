package com.codesses.lgucircle.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnEventImageClick;
import com.codesses.lgucircle.databinding.EventsItemLayoutBinding;
import com.codesses.lgucircle.model.Event;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class EventVH extends RecyclerView.ViewHolder {

    EventsItemLayoutBinding binding;


    public EventVH(@NonNull @NotNull EventsItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(Event event, OnEventImageClick onEventImageClick)
    {
        if (event.getType() == 0)
            binding.image.setVisibility(View.GONE);
        else if (event.getType() == 1) {
            binding.image.setVisibility(View.VISIBLE);
            Picasso.get().load(event.getImage()).resize(500, 500).into(binding.image);
        }

        binding.eventInfo.setText(event.getInfo());

        binding.dateTime.setText(event.getDate());

        binding.dep.setText(event.getDepartment());

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventImageClick.onImageClick(event.getImage());
            }
        });

    }
}
