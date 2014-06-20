package com.bangalore.barcamp.utils;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.activity.AboutActivity;
import com.bangalore.barcamp.activity.BCBFragmentActionbarActivity;
import com.bangalore.barcamp.activity.InternalVenueMapActivity;
import com.bangalore.barcamp.activity.ScheduleActivity;
import com.bangalore.barcamp.activity.SettingsActivity;
import com.bangalore.barcamp.activity.ShareActivity;
import com.bangalore.barcamp.activity.UpdateMessagesActivity;
import com.bangalore.barcamp.activity.WebViewActivity;

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
			BCBFragmentActionbarActivity activity, final String drawerTitle) {
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
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				actionbar.setTitle(drawerTitle);
				// invalidateOptionsMenu(); // creates call to
				// onPrepareOptionsMenu()

			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		actionbar.setDisplayHomeAsUpEnabled(true);
		actionbar.setHomeButtonEnabled(true);

		actionbar.setTitle("Barcamp Bangalore Monsoon 2014");
		return mDrawerToggle;
	}

	public static void addNavigationActions(
			final BCBFragmentActionbarActivity homeActivity) {

		View view = homeActivity.findViewById(R.id.nav_agenda);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ScheduleActivity.class);
				homeActivity.startActivityForResult(intent, START_SCHEDULE);
			}
		});

		view = homeActivity.findViewById(R.id.nav_about);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, AboutActivity.class);
				homeActivity.startActivityForResult(intent, START_ABOUT);
			}
		});

		view = homeActivity.findViewById(R.id.nav_internal_venue_map);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity,
						InternalVenueMapActivity.class);
				homeActivity.startActivityForResult(intent,
						START_INTERNAL_VENUE);
			}
		});

		view = homeActivity.findViewById(R.id.nav_settings);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, SettingsActivity.class);
				homeActivity.startActivityForResult(intent, START_SETTINGS);
			}
		});
		view.setVisibility(View.GONE);

		view = homeActivity.findViewById(R.id.nav_share);
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(homeActivity, ShareActivity.class);
				homeActivity.startActivityForResult(intent, START_SHARE);
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
