package com.codesses.lgucircle.Adapters;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.codesses.lgucircle.Fragments.EventsFragment;
import com.codesses.lgucircle.Fragments.NotificationFragment;
import com.codesses.lgucircle.Fragments.PostFeedFragment;
import com.codesses.lgucircle.Fragments.SettingFragment;

public class UserTabsAdapter extends FragmentPagerAdapter {
    Context context;
    int totalTabs;

    public UserTabsAdapter(Context context, @NonNull FragmentManager fm, int behavior, int totalTabs) {
        super(fm, behavior);
        this.context = context;
        this.totalTabs = totalTabs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new PostFeedFragment();
            case 1:
                return new EventsFragment();
            case 2:
                return new NotificationFragment();
            case 3:
                return new SettingFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
