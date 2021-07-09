package com.codesses.lgucircle.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.codesses.lgucircle.Adapters.AuthorityPagerAdapter;
import com.codesses.lgucircle.Adapters.ProfilePagerAdapter;
import com.codesses.lgucircle.Authentication.LoginActivity;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.Utils.FirebaseRef;
import com.codesses.lgucircle.databinding.ActivityAuthorityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import org.jetbrains.annotations.NotNull;

import java.util.Deque;
import java.util.Iterator;
import java.util.function.Consumer;

import static android.content.ContentValues.TAG;
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

        binding.logout.setOnClickListener(this::logout);
        subscribeToChannel();

//        setDestinationListener();
    }

    private void subscribeToChannel() {
        FirebaseMessaging.getInstance().subscribeToTopic("ideas")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void logout(View view) {
        FirebaseRef.getAuth().signOut();
        Intent intent = new Intent(mContext, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setDestinationListener() {
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull @NotNull NavController controller, @NonNull @NotNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId())
                {
                    case R.id.eventsNav:
                        binding.bottomNav.getMenu().findItem(R.id.eventsNav).setChecked(true);
                        break;
                    case R.id.ideasNav:
                        binding.bottomNav.getMenu().findItem(R.id.ideasNav).setChecked(true);
                        break;
                }
            }
        });
    }

    private void setBottomNavigationListener() {
        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                switch (item.getItemId())
                {
                    case R.id.eventsNav:
                        setFragment(R.id.eventsNav);
                        break;
                    case R.id.ideasNav:
                        setFragment(R.id.ideasNav);
                        break;
                    case R.id.notificationNav:
                        setFragment(R.id.notificationNav);
                        break;
                }
                return false;
            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void setFragment(int id) {


        navController.navigate(id, null, new NavOptions.Builder()
                .setPopUpTo(id,
                        true).build());

    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }


}