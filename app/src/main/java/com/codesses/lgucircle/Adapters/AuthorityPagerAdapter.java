package com.codesses.lgucircle.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codesses.lgucircle.Fragments.Authority.IdeaStatusFragment;

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
