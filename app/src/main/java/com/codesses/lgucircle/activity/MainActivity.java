package com.codesses.lgucircle.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.codesses.lgucircle.Adapters.UserTabsAdapter;
import com.codesses.lgucircle.Dialogs.UserSearchDialog;
import com.codesses.lgucircle.R;
import com.codesses.lgucircle.activity.Services.ConversationAC;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.FirebaseMessaging;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;
import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {


    @BindView(R.id.tab_layout)
    TabLayout Tab_Layout;
    @BindView(R.id.view_pager)
    ViewPager View_Pager;
    @BindView(R.id.search)
    ImageView Search_Image;
    @BindView(R.id.message)
    ImageView Message_Image;

    AppCompatActivity mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        subscribeToChannel();
        Tab_Layout.addTab(Tab_Layout.newTab().setIcon(R.drawable.ic_newsfeed));
        Tab_Layout.addTab(Tab_Layout.newTab().setIcon(R.drawable.ic_event));
        Tab_Layout.addTab(Tab_Layout.newTab().setIcon(R.drawable.ic_notification));
        Tab_Layout.addTab(Tab_Layout.newTab().setIcon(R.drawable.ic_setting));

        Tab_Layout.getTabAt(0).getIcon()
                .setColorFilter(
                        getResources().getColor(R.color.Green), PorterDuff.Mode.SRC_IN);

        final UserTabsAdapter adapter = new UserTabsAdapter(this,
                getSupportFragmentManager(),
                BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,
                Tab_Layout.getTabCount());


        View_Pager.setAdapter(adapter);
        View_Pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(Tab_Layout));
        Tab_Layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View_Pager.setCurrentItem(tab.getPosition());
                Tab_Layout.getTabAt(tab.getPosition()).getIcon()
                        .setColorFilter(
                                getResources().getColor(R.color.Green), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Tab_Layout.getTabAt(tab.getPosition()).getIcon()
                        .setColorFilter(
                                getResources().getColor(R.color.steel), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });

        Message_Image.setOnClickListener(this::startConversation);
        Search_Image.setOnClickListener(this::showSearchDialog);
    }

    private void showSearchDialog(View view) {
        UserSearchDialog userSearchDialog = new UserSearchDialog("MainActivity");
        userSearchDialog.show(getSupportFragmentManager(), "user search");
    }


    private void startConversation(View view) {
        Intent intent = new Intent(MainActivity.this, ConversationAC.class);
        startActivity(intent);
    }


    private void subscribeToChannel() {
        FirebaseMessaging.getInstance().subscribeToTopic("events")
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
}