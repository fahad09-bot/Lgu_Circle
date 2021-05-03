package com.codesses.lgucircle.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.codesses.lgucircle.R;
import com.codesses.lgucircle.activity.EventsActivity;
import com.codesses.lgucircle.activity.IncubationActivity;
import com.codesses.lgucircle.activity.ServicesActivity;
import com.codesses.lgucircle.databinding.FragmentExtraBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ExtraFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExtraFragment extends Fragment {
    FragmentActivity context;
    private FragmentExtraBinding binding;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ExtraFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ExtraFragment newInstance(String param1, String param2) {
        ExtraFragment fragment = new ExtraFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_extra, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.services.setOnClickListener(v -> {
            startActivity(new Intent(context, ServicesActivity.class));

        });
        binding.incubation.setOnClickListener(v -> {
            startActivity(new Intent(context, IncubationActivity.class));

        });
        binding.events.setOnClickListener(v -> {
           startActivity(new Intent(context, EventsActivity.class));

        });
    }
}