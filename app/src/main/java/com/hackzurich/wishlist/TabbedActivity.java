package com.hackzurich.wishlist;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.TextView;

/**
 * Created by heat on 10/11/14.
 */
public class TabbedActivity extends FragmentActivity implements
        ActionBar.TabListener {

    public static final String ARG_USER_ID = "user_id";

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    private String userId;
    // Tab titles
    private String[] tabs = { "My Wishlist", "My Friends"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = getIntent().getStringExtra(ARG_USER_ID);

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // Adding Tabs
        for (String tab_name : tabs) {
            ActionBar.Tab tab = actionBar.newTab();
            actionBar.addTab(tab.setText(tab_name).setTabListener(this));
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

    }

    private class TabsPagerAdapter extends FragmentPagerAdapter {
        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0:
                    return MyWishesFragment.newInstance(1, userId);
                case 1:
                default:
                    return MyFriendsFragment.newInstance(2, userId);
            }
        }

        @Override
        public int getCount() {
            // get item count - equal to number of tabs
            return 2;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
