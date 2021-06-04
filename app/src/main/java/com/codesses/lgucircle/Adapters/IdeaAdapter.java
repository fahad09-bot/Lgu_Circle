package com.codesses.lgucircle.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.codesses.lgucircle.Interfaces.IdeaClick;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.IdeasItemLayoutBinding;
import com.codesses.lgucircle.model.Idea;
import com.codesses.lgucircle.viewHolder.IdeaVH;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaVH> {

    Context mContext;
    List<Idea> ideaList;
    IdeaClick ideaClick;

    public IdeaAdapter(Context mContext, List<Idea> ideaList, IdeaClick ideaClick) {
        this.mContext = mContext;
        this.ideaList = ideaList;
        this.ideaClick = ideaClick;
    }

    @NonNull
    @NotNull
    @Override
    public IdeaVH onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        IdeasItemLayoutBinding binding = IdeasItemLayoutBinding.bind(LayoutInflater.from(mContext).inflate(R.layout.ideas_item_layout, parent, false));
        return new IdeaVH(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull IdeaVH holder, int position) {
        holder.onBind(ideaList.get(position), ideaClick);
    }

    @Override
    public int getItemCount() {
        return ideaList.size();
    }
}
