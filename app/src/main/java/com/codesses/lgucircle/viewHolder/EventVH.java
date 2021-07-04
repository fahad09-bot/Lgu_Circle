package com.codesses.lgucircle.viewHolder;

import android.view.View;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnEventImageClick;
import com.codesses.lgucircle.databinding.EventsItemLayoutBinding;
import com.codesses.lgucircle.model.Event;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EventVH extends RecyclerView.ViewHolder {

    EventsItemLayoutBinding binding;


    public EventVH(@NonNull @NotNull EventsItemLayoutBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void onBind(Event event, OnEventImageClick onEventImageClick) {
        if (event.getType() == 0)
            binding.image.setVisibility(View.GONE);
        else if (event.getType() == 1) {
            binding.image.setVisibility(View.VISIBLE);
            Picasso.get().load(event.getImage()).resize(500, 500).into(binding.image);
        }
        binding.eventName.setText(event.getName());
        binding.eventInfo.setText(event.getInfo());
        String monthPattern = "MMMM", dayPattern = "dd", dayTimePattern = "EE hh:mm aa";
        SimpleDateFormat monthFormat = new SimpleDateFormat(monthPattern);

        String month = null;
        String day = null;
        String dayTime = null;
        String eventDate = event.getDate();

        month = monthFormat.format(Date.parse(eventDate));
        day = new SimpleDateFormat(dayPattern).format(Date.parse(eventDate));
        dayTime = new SimpleDateFormat(dayTimePattern).format(Date.parse(eventDate));
        String shortMonth = "";
        switch (month.toLowerCase()) {
            case "january":
                shortMonth = "jan";
                break;

            case "february":
                shortMonth = "feb";
                break;

            case "march":
                shortMonth = "mar";
                break;

            case "April":
                shortMonth = "apr";
                break;

            case "may":
                shortMonth = "may";
                break;

            case "june":
                shortMonth = "jun";
                break;


            case "july":
                shortMonth = "jul";
                break;

            case "august":
                shortMonth = "aug";
                break;

            case "september":
                shortMonth = "sep";
                break;


            case "october":
                shortMonth = "oct";
                break;

            case "november":
                shortMonth = "nov";
                break;

            case "december":
                shortMonth = "dec";
                break;


        }
        binding.month.setText(shortMonth);
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
