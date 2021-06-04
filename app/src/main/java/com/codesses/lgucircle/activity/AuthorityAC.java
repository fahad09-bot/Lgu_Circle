package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.MenuItem;

import com.codesses.lgucircle.Adapters.AuthorityPagerAdapter;
import com.codesses.lgucircle.Adapters.ProfilePagerAdapter;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.databinding.ActivityAuthorityBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import org.jetbrains.annotations.NotNull;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class AuthorityAC extends AppCompatActivity {

    AppCompatActivity mContext;
    ActivityAuthorityBinding binding;

    //    Nav bar
    private NavController navController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        binding = DataBindingUtil.setContentView(mContext, R.layout.activity_authority);


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.bottomNav, navController);

        setBottomNavigationListener();
        setDestinationListener();
    }

    private void setDestinationListener() {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull @NotNull NavController controller, @NonNull @NotNull NavDestination destination, @Nullable @org.jetbrains.annotations.Nullable Bundle arguments) {
                switch (destination.getId())
                {
                    case R.id.eventsFragment:
                        binding.bottomNav.getMenu().findItem(R.id.eventsNav).setChecked(true);
                        break;
                    case R.id.ideasFragment:
                        binding.bottomNav.getMenu().findItem(R.id.ideasNav).setChecked(true);
                        break;
                }
            }
        });
    }

    private void setBottomNavigationListener() {
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.eventsNav:
                        navController.navigate(R.id.eventsFragment);
                        break;
                    case R.id.ideasNav:
                        navController.navigate(R.id.ideasFragment);
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }


}