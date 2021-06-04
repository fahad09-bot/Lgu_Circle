package com.codesses.lgucircle.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codesses.lgucircle.Fragments.Authority.EventsFragment;
import com.codesses.lgucircle.Fragments.Authority.IdeaStatusFragment;
import com.codesses.lgucircle.Fragments.Authority.IdeasFragment;

import org.jetbrains.annotations.NotNull;

public class AuthorityPagerAdapter extends FragmentStateAdapter {


    public AuthorityPagerAdapter(@NonNull @NotNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @NotNull
    @Override
    public Fragment createFragment(int position) {
        return new IdeaStatusFragment(position + 1);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
