/*
 * Copyright (C) 2012 Saurabh Minni <http://100rabh.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bangalore.barcamp.activity;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.internal.view.ActionBarPolicy;
import android.view.MenuItem;
import android.widget.ListView;

import com.bangalore.barcamp.MessagesListAdapter;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BCBUpdatesMessage;
import com.bangalore.barcamp.database.MessagesDataSource;
import com.bangalore.barcamp.utils.BCBFragmentUtils;

public class UpdateMessagesActivity extends BCBFragmentActionbarActivity {

	public static final String FROM_NOTIFICATION = "fromNotification";
	public static String EXTRA_POS = "pos";
	MessageLoadingTask task;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.slot_details_list);
		mDrawerToggle = BCBFragmentUtils.setupActionBar(this, "BCB");

		// BCBUtils.addNavigationActions(this);
		if (getIntent().getBooleanExtra(FROM_NOTIFICATION, false)) {
		}

		BCBFragmentUtils.addNavigationActions(this);
		supportInvalidateOptionsMenu();

	}

	@Override
	protected void onResume() {
		super.onResume();
		task = new MessageLoadingTask();
		task.execute();
	}

	class MessageLoadingTask extends
			AsyncTask<Void, Void, List<BCBUpdatesMessage>> {

		@Override
		protected List<BCBUpdatesMessage> doInBackground(Void... params) {
			MessagesDataSource ds = new MessagesDataSource(
					UpdateMessagesActivity.this);
			ds.open();
			List<BCBUpdatesMessage> list = ds.getAllMessages();
			Collections.reverse(list);
			ds.close();
			return list;
		}

		@Override
		protected void onPostExecute(List<BCBUpdatesMessage> result) {
			super.onPostExecute(result);
			ListView listview = (ListView) findViewById(R.id.listView1);
			MessagesListAdapter adapter = new MessagesListAdapter(
					UpdateMessagesActivity.this, R.layout.updates_list_item,
					result);
			listview.setAdapter(adapter);
		}

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

}
