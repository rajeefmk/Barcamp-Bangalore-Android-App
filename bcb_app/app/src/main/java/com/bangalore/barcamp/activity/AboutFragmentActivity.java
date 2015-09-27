package com.bangalore.barcamp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.internal.view.ActionBarPolicy;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.bangalore.barcamp.BCBConsts;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.utils.BCBFragmentUtils;

public class AboutFragmentActivity extends BCBFragmentActionbarActivity {
    public static final String SHARE_STRING = "Share String";
    private ActionBarDrawerToggle mDrawerToggle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_screen);
        mDrawerToggle = BCBFragmentUtils.setupActionBar(this, BCBConsts.BARCAMP_BANGALORE);

        // BCBUtils.createActionBarOnActivity(this);
        // BCBUtils.addNavigationActions(this);

        BCBFragmentUtils.addNavigationActions(this);
        supportInvalidateOptionsMenu();
        ((ImageView) findViewById(R.id.imageView1))
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent webIntent = new Intent(Intent.ACTION_VIEW);
                        webIntent.setData(Uri
                                .parse("https://barcampbangalore.org"));
                        startActivity(webIntent);

                    }
                });
    }

    @Override
    public void hideDrawer() {
        super.hideDrawer();
        DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawers();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttachFragment(android.app.Fragment fragment) {
        super.onAttachFragment(fragment);
        ActionBarPolicy.get(this).showsOverflowMenuButton();
        supportInvalidateOptionsMenu();
    }

    @Override
    public Intent callForFunction(int id, Intent params) {
        if (id == START_FRAGMENT) {
            startActivity(params);

        }
        return super.callForFunction(id, params);
    }
}
