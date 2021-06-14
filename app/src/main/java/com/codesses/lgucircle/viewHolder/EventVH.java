package com.codesses.lgucircle.viewHolder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnEventImageClick;
import com.codesses.lgucircle.databinding.EventsItemLayoutBinding;
import com.codesses.lgucircle.model.Event;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        binding.eventName.setText(event.getName());
        binding.eventInfo.setText(event.getInfo());
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        String month = monthFormat.format(Date.parse(event.getDate()));
        String day = new SimpleDateFormat("dd").format(Date.parse(event.getDate()));
        String dayTime = new SimpleDateFormat("EE hh:mm aa").format(Date.parse(event.getDate()));
        binding.month.setText(month);
        binding.day.setText(day);

        binding.dayTime.setText(dayTime);

        binding.department.setText(event.getDepartment());

        binding.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEventImageClick.onImageClick(event.getImage());
            }
        });

    }
}
