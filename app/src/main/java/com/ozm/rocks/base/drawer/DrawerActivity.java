package com.ozm.rocks.base.drawer;

import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import com.ozm.R;
import com.ozm.rocks.base.mvp.BaseActivity;
import com.ozm.rocks.ui.view.OzomeToolbar;

public abstract class DrawerActivity extends BaseActivity{

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OzomeToolbar toolbar = (OzomeToolbar) findViewById(R.id.ozome_toolbar);
        toolbar.setLogoVisibility(false);
        toolbar.setDrawerIconVisibility(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_view);
//        mDrawerToggle = new ActionBarDrawerToggle(
//                this,                  /* host Activity */
//                mDrawerLayout,         /* DrawerLayout object */
//                R.drawable.ic_menu,  /* nav drawer icon to replace 'Up' caret */
//                R.string.drawer_open,  /* "open drawer" description */
//                R.string.drawer_close  /* "close drawer" description */
//        ) {
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
////                getActionBar().setTitle(mTitle);
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
////                getActionBar().setTitle(mDrawerTitle);
//            }
//        };

        // Set the drawer toggle as the DrawerListener
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

//    @Override
//    protected void onPostCreate(Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//        // Sync the toggle state after onRestoreInstanceState has occurred.
//        mDrawerToggle.syncState();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Pass the event to ActionBarDrawerToggle, if it returns
//        // true, then it has handled the app icon touch event
//        if (mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        // Handle your other action bar items...
//
//        return super.onOptionsItemSelected(item);
//    }

}
