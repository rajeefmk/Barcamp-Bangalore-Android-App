package com.bangalore.barcamp.utils;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.activity.BCBFragmentActionbarActivity;
import com.bangalore.barcamp.activity.ShareActivity;
import com.bangalore.barcamp.activity.UpdateMessagesActivity;
import com.bangalore.barcamp.activity.WebViewActivity;
import com.bangalore.barcamp.fragment.AboutFragment;
import com.bangalore.barcamp.fragment.BCBInternalVenueMapFragment;
import com.bangalore.barcamp.fragment.ScheduleFragment;

public class BCBFragmentUtils {

	protected static final int START_SCHEDULE = 100;
	protected static final int START_ABOUT = 101;
	protected static final int START_SETTINGS = 102;
	protected static final int START_SHARE = 103;
	protected static final int START_BCB12_TWEETS = 104;
	protected static final int START_BCB_UPDATES = 105;
	private static final String BCB_USER_SCHEDULE_URL = "http://barcampbangalore.org/bcb/wp-android_helper.php?action=getuserdata&userid=%s&userkey=%s";
	protected static final int START_INTERNAL_VENUE = 106;
	private static final String BCB_LOCATION_MAPS_URL = "https://www.google.co.in/maps?t=m&cid=0x9ed33f443055ef5f&z=17&iwloc=A";

	public static ActionBarDrawerToggle setupActionBar(
			final BCBFragmentActionbarActivity activity,
			final String drawerTitle) {
		DrawerLayout mDrawerLayout = (DrawerLayout) activity
				.findViewById(R.id.drawer_layout);
		final ActionBar actionbar = activity.getSupportActionBar();
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(
				activity, mDrawerLayout, R.drawable.abc_ic_go, R.string.ok,
				R.string.close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				actionbar.setTitle(drawerTitle);
				activity.supportInvalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				actionbar.setTitle(drawerTitle);
				activity.supportInvalidateOptionsMenu();
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()

			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);

		actionbar.setTitle("BCB");
		return mDrawerToggle;
	}

	public static void addNavigationActions(
			final BCBFragmentActionbarActivity homeActivity) {

		View view = homeActivity.findViewById(R.id.nav_agenda);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ScheduleFragment.class);
				FragmentManager frManager = homeActivity
						.getSupportFragmentManager();
				while (frManager.getBackStackEntryCount() > 0) {
					frManager.popBackStackImmediate();
				}
				FragmentTransaction ft = frManager.beginTransaction();
				ft.replace(R.id.content_frame, new ScheduleFragment());
				ft.commit();
				homeActivity.hideDrawer();

			}
		});

		view = homeActivity.findViewById(R.id.nav_about);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, AboutFragment.class);
				homeActivity.callForFunction(
						BCBFragmentActionbarActivity.START_FRAGMENT, intent);
			}
		});

		view = homeActivity.findViewById(R.id.nav_internal_venue_map);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity,
						BCBInternalVenueMapFragment.class);
				homeActivity.callForFunction(
						BCBFragmentActionbarActivity.START_FRAGMENT, intent);
			}
		});

		view = homeActivity.findViewById(R.id.nav_share);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ShareActivity.class);
				homeActivity.startActivity(intent);

			}
		});

		view = homeActivity.findViewById(R.id.nav_tweets);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(homeActivity, WebViewActivity.class);
				intent.putExtra(WebViewActivity.URL,
						"file:///android_asset/bcb11_updates.html");
				homeActivity.startActivityForResult(intent, START_BCB12_TWEETS);
			}
		});

		view = homeActivity.findViewById(R.id.nav_update);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity,
						UpdateMessagesActivity.class);
				homeActivity.startActivityForResult(intent, START_BCB_UPDATES);
			}
		});

		view = homeActivity.findViewById(R.id.nav_venue);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final PackageManager pm = homeActivity.getPackageManager();

				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse(BCB_LOCATION_MAPS_URL));
				final List<ResolveInfo> matches = pm.queryIntentActivities(
						intent, 0);
				for (ResolveInfo info : matches) {
					Log.e("MapPackage", info.loadLabel(pm) + " "
							+ info.activityInfo.packageName + " "
							+ info.activityInfo.name);
					if (info.activityInfo.name
							.equals("com.google.android.maps.MapsActivity")) {
						intent.setClassName("com.google.android.apps.maps",
								"com.google.android.maps.MapsActivity");
					}
				}

				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				homeActivity.startActivity(intent);
			}
		});

		view = homeActivity.findViewById(R.id.nav_BCB);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_VIEW, Uri
						.parse("http://barcampbangalore.org"));
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				homeActivity.startActivity(intent);
			}
		});

	}

}
