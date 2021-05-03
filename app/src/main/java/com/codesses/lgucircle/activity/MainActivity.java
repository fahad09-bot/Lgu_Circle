package com.codesses.lgucircle.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import android.widget.Toolbar;

import com.codesses.lgucircle.Adapters.UserTabsAdapter;
import com.codesses.lgucircle.R;
import com.google.android.material.tabs.TabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }


    @BindView(R.id.tab_layout)
    TabLayout Tab_Layout;
    @BindView(R.id.view_pager)
    ViewPager View_Pager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Tab_Layout.addTab(Tab_Layout.newTab().setText("Home"));
        Tab_Layout.addTab(Tab_Layout.newTab().setText("Search"));
        Tab_Layout.addTab(Tab_Layout.newTab().setText("Setting"));


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
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

            });
        }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.search_bar:
                Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.menu:
                Toast.makeText(MainActivity.this, "Menu", Toast.LENGTH_SHORT)
                        .show();
                break;
            default:
                break;
        }
        return true;
    }
}