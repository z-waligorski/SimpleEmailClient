package com.z_waligorski.simpleemailclient.Adapters;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.Fragment;


import com.z_waligorski.simpleemailclient.Fragments.TabFragments.ComposeFragment;
import com.z_waligorski.simpleemailclient.Fragments.TabFragments.InboxFragment;

// Adapter for fragments in tabbed fragment
public class MyPagerAdapter extends FragmentPagerAdapter {

    CharSequence[] tabTitles;

    public MyPagerAdapter(FragmentManager fragmentMenager, CharSequence[] tabTitles) {
        super(fragmentMenager);
        this.tabTitles = tabTitles;
    }

    // Return fragment for given position
    @Override
    public Fragment getItem(int position) {
        if(position == 0) {
            InboxFragment inboxFragment = new InboxFragment();
            return inboxFragment;
        }
        else {
            ComposeFragment composeFragment = new ComposeFragment();
            return composeFragment;
        }
    }

    // Return number of tabs
    @Override
    public int getCount() {
        return tabTitles.length;
    }

    // Return title of given tab
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}
