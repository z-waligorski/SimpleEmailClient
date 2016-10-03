package com.z_waligorski.simpleemailclient.Activities;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.z_waligorski.simpleemailclient.Adapters.MenuAdapter;
import com.z_waligorski.simpleemailclient.Fragments.AboutFragment;
import com.z_waligorski.simpleemailclient.Fragments.SettingsFragment;
import com.z_waligorski.simpleemailclient.Fragments.TabbedFragment;
import com.z_waligorski.simpleemailclient.UI.LeftMenuItem;
import com.z_waligorski.simpleemailclient.R;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    //MainActivity provides NavigationDrawer with ListView containing menu items
    //and empty space that will be replaced by adequate fragment.

    private DrawerLayout drawerLayout;
    private ListView leftMenu;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<LeftMenuItem> menuList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftMenu = (ListView) findViewById(R.id.left_menu);

        // Create list of items for menu
        menuList = new ArrayList<LeftMenuItem>();
        menuList.add(new LeftMenuItem("Home", R.drawable.home));
        menuList.add(new LeftMenuItem("Settings", R.drawable.settings));
        menuList.add(new LeftMenuItem("About", R.drawable.about));

        // Set adapter for menu
        leftMenu.setAdapter(new MenuAdapter(this, R.layout.single_menu_item, menuList));

        // OnClickListener for menu
        leftMenu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectFragment(position);
                drawerLayout.closeDrawer(leftMenu);
            }
        });

        // Setup ActionBarDrawerToogle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.menu_opened, R.string.menu_closed) {

            @Override
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                invalidateOptionsMenu();
                super.onDrawerClosed(drawerView);
            }
        };

        // Enable displaying drawerToggle on ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Listener for drawer events
        drawerLayout.addDrawerListener(drawerToggle);

        // Load as default TabbedFragmentActivity
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, new TabbedFragment()).commit();
    }

    public void selectFragment(int position) {
        Fragment fragment;
        FragmentManager fManager = getSupportFragmentManager();

        if (position == 0){
            fragment = new TabbedFragment();
            fManager.beginTransaction().replace(R.id.main_content, fragment).commit();
        }else if (position == 1){
            fragment = new SettingsFragment();
            fManager.beginTransaction().replace(R.id.main_content, fragment).commit();
        } else {
            fragment = new AboutFragment();
            fManager.beginTransaction().replace(R.id.main_content, fragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item))
            return true;

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        drawerToggle.onConfigurationChanged(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        // Synchronize DrawerToggle with DrawerLAyout
        drawerToggle.syncState();
        super.onPostCreate(savedInstanceState);
    }

    // If currently displayed fragment is not TabbedFragment pressing back button should display TabbedFragment
    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.main_content);
        if(!(fragment instanceof TabbedFragment)) {
            fragmentManager.beginTransaction().replace(R.id.main_content, new TabbedFragment()).commit();
        } else {
            super.onBackPressed();
        }
    }
}
