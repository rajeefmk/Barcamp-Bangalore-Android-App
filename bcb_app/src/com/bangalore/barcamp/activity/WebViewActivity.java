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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.internal.view.ActionBarPolicy;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.utils.BCBFragmentUtils;

public class WebViewActivity extends BCBFragmentActionbarActivity {
	private static final int SHOW_ERROR_DIALOG = 100;
	public static final String ENABLE_LOGIN = "enable_login";
	public static final String URL = "URLToShow";
	WebView webView;
	private ActionBarDrawerToggle mDrawerToggle;
	private int refreshMenuID;

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String newUrl = intent.getStringExtra(URL);
		String url = getIntent().getStringExtra(URL);
		if (!newUrl.equals(url)) {
			setIntent(intent);
			findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.loadUrl(newUrl);
			hideDrawer();
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
		} else if (item.getItemId() == refreshMenuID) {
			findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
			webView.setVisibility(View.GONE);
			webView.reload();
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
		mDrawerToggle = BCBFragmentUtils.setupActionBar(this, "BCB");
		webView = (WebView) findViewById(R.id.webView);
		WebSettings websettings = webView.getSettings();
		websettings.setJavaScriptEnabled(true);
		webView.setClickable(true);
		webView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				findViewById(R.id.linearLayout2).setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);
			}

			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				showDialog(SHOW_ERROR_DIALOG);
			}

			@Override
			public void onLoadResource(WebView view, String url) {
				if (url.contains("bcbapp://android")) {
					Intent newIntent = new Intent(WebViewActivity.this,
							MainFragmentActivity.class);
					newIntent.setData(Uri.parse(url));
					startActivity(newIntent);
					finish();
				}
				super.onLoadResource(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				if (url.equals(getIntent().getStringExtra(URL))) {
					return true;
				} else if (getIntent().getBooleanExtra(ENABLE_LOGIN, false)) {
					return false;
				}
				Log.e("Action", url);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(Uri.parse(url));
				startActivity(intent);
				return true;
			}

		});

		String url = getIntent().getStringExtra(URL);
		webView.loadUrl(url);

		// ActionBar actionbar = (ActionBar) findViewById(R.id.actionBar1);
		// actionbar.addAction(new Action() {
		//
		// @Override
		// public void performAction(View arg0) {
		// findViewById(R.id.linearLayout2).setVisibility(View.VISIBLE);
		// webView.setVisibility(View.GONE);
		// webView.reload();
		// }
		//
		// @Override
		// public int getDrawable() {
		// return R.drawable.refresh;
		// }
		// }, 0);

		webView.requestFocus(View.FOCUS_DOWN);

		BCBFragmentUtils.addNavigationActions(this);
		supportInvalidateOptionsMenu();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.refresh, menu);
		return true;
	}

	protected Dialog onCreateDialog(int id) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
		switch (id) {
		case SHOW_ERROR_DIALOG:
			alertDialog.setCancelable(false);
			alertDialog.setTitle(getString(R.string.error_title));
			alertDialog
					.setMessage(getString(R.string.connection_error_and_try_again));
			alertDialog.setButton(getString(R.string.ok),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dismissDialog(SHOW_ERROR_DIALOG);
							WebViewActivity.this.finish();
						}
					});
			break;
		}
		return alertDialog;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
}
