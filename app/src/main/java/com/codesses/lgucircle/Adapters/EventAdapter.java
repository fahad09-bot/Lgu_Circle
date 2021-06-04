package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.OnEventImageClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.EventsItemLayoutBinding;
import com.codesses.lgucircle.model.Event;
import com.codesses.lgucircle.viewHolder.EventVH;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventVH> {

    Context mContext;
    List<Event> eventList;
    OnEventImageClick onEventImageClick;

    public EventAdapter(Context mContext, List<Event> eventList, OnEventImageClick onEventImageClick) {
        this.mContext = mContext;
        this.eventList = eventList;
        this.onEventImageClick = onEventImageClick;
    }

    @NonNull
    @NotNull
    @Override
    public EventVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        EventsItemLayoutBinding binding = EventsItemLayoutBinding.bind(LayoutInflater.from(mContext).inflate(R.layout.events_item_layout, parent, false));

        return new EventVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull EventVH holder, int position) {
        holder.onBind(eventList.get(position), onEventImageClick);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}
