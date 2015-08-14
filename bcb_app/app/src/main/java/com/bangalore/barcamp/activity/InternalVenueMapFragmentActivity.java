package com.bangalore.barcamp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.internal.view.ActionBarPolicy;
import android.view.MenuItem;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.utils.BCBFragmentUtils;

public class InternalVenueMapFragmentActivity extends
		BCBFragmentActionbarActivity {
	public static final String SHARE_STRING = "Share String";
	private ActionBarDrawerToggle mDrawerToggle;

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.internal_venue_map_layout);
		mDrawerToggle = BCBFragmentUtils.setupActionBar(this, "Venue");

		// BCBUtils.createActionBarOnActivity(this);
		// BCBUtils.addNavigationActions(this);

		BCBFragmentUtils.addNavigationActions(this);
		supportInvalidateOptionsMenu();
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
