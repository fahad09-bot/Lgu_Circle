package com.codesses.lgucircle.Fragments.Authority;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.codesses.lgucircle.Adapters.AuthorityPagerAdapter;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.FragmentIdeasBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import org.jetbrains.annotations.NotNull;


public class IdeasFragment extends Fragment {


    FragmentIdeasBinding binding;
    FragmentActivity fragmentActivity;
    public static final String[] filters = {"Pending", "Rejected", "Accepted"};



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentIdeasBinding.bind(inflater.inflate(R.layout.fragment_ideas, container, false));

        setUpViewPager();
        return binding.getRoot();
    }

    private void setUpViewPager() {

        final AuthorityPagerAdapter adapter = new AuthorityPagerAdapter(
                fragmentActivity);

        binding.viewPager.setAdapter(adapter);


        new TabLayoutMediator(
                binding.tabLayout,
                binding.viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull @NotNull TabLayout.Tab tab, int position) {
                        tab.setText(filters[position]);
                    }
                })
                .attach();
    }

}