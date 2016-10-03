package com.z_waligorski.simpleemailclient.Fragments;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.z_waligorski.simpleemailclient.Adapters.MyPagerAdapter;
import com.z_waligorski.simpleemailclient.R;


public class TabbedFragment extends Fragment {

    private ViewPager viewPager;
    private MyPagerAdapter adapter;
    private TabLayout tabLayout;
    private CharSequence[] tabTitles = {"Inbox", "Compose"};

    public TabbedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_tabbed, container, false);

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);

        adapter = new MyPagerAdapter(getChildFragmentManager(), tabTitles);

        // Setup adapter that provides views for viewPager
        viewPager.setAdapter(adapter);

        // Link tabLayout with viewPager.
        tabLayout.setupWithViewPager(viewPager);

        return view;
    }
}
