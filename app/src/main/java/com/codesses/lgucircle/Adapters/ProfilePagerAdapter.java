package com.codesses.lgucircle.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.codesses.lgucircle.Fragments.Profile.UserPostFragment;
import com.codesses.lgucircle.Fragments.Profile.UserServiceFragment;

import org.jetbrains.annotations.NotNull;

public class ProfilePagerAdapter extends FragmentStatePagerAdapter {

    int totalTabs;
    String userId;

    public ProfilePagerAdapter(@NonNull @NotNull FragmentManager fm, int behavior, int totalTabs, String userId) {
        super(fm, behavior);
        this.totalTabs = totalTabs;
        this.userId = userId;
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                return new UserPostFragment(userId);

            case 1:
                return new UserServiceFragment(userId);

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
