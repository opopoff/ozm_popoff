package com.ozm.fun.base.drawable;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.ozm.R;
import com.ozm.fun.data.social.SocialActivity;

public abstract class DrawableActivity extends SocialActivity {

    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;
    private DrawerLayout drawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = (Toolbar) findViewById(R.id.ozome_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);

        drawer = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        // Drawer object Assigned to the view
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                // code here will execute once the drawer is opened
                // (As I dont want anything happened whe drawer is
                // open I am not going to put anything here)
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                // Code here will execute once drawer is closed
            }
        };
        // Drawer Toggle Object Made
        drawer.setDrawerListener(toggle);
        // Drawer Listener set to the Drawer toggle
        toggle.syncState();
    }
}
