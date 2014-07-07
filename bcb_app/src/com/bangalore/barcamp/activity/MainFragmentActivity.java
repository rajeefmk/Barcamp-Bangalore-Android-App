package com.bangalore.barcamp.activity;

import static com.bangalore.barcamp.gcm.CommonUtilities.SENDER_ID;

import java.lang.reflect.Constructor;
import java.util.List;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.internal.view.ActionBarPolicy;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.SessionAttendingUpdateService;
import com.bangalore.barcamp.data.BCBUpdatesMessage;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.BarcampUserScheduleData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;
import com.bangalore.barcamp.database.MessagesDataSource;
import com.bangalore.barcamp.fragment.BCBFragmentBaseClass;
import com.bangalore.barcamp.fragment.ScheduleFragment;
import com.bangalore.barcamp.fragment.SlotDetailsFragment;
import com.bangalore.barcamp.gcm.GCMUtils;
import com.bangalore.barcamp.utils.BCBFragmentUtils;

public class MainFragmentActivity extends BCBFragmentActionbarActivity {
	private FetchScheduleAsyncTask task = null;
	private CharSequence mTitle;
	private CharSequence mDrawerTitle;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private Menu menu;
	public final static int CALL_REFRESH = 100;
	public static final int DISMISS_PROGRESS_DIALOG = 101;
	public static final int CALL_SLOT_DETAILS = 102;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		ActionBarPolicy.get(this).showsOverflowMenuButton();
		super.onCreate(savedInstanceState);
		ActionBarPolicy.get(this).showsOverflowMenuButton();
		setContentView(R.layout.fragment);
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.content_frame, new ScheduleFragment()).commit();
		}
		mDrawerToggle = BCBFragmentUtils.setupActionBar(this, "BCB");

		if (!GCMUtils.isRegistered(this)) {
			Intent registrationIntent = new Intent(
					"com.google.android.c2dm.intent.REGISTER");
			// sets the app name in the intent
			registrationIntent.putExtra("app",
					PendingIntent.getBroadcast(this, 0, new Intent(), 0));
			registrationIntent.putExtra("sender", SENDER_ID);
			startService(registrationIntent);
		}

		MessagesDataSource ds = new MessagesDataSource(getApplicationContext());
		ds.open();
		List<BCBUpdatesMessage> list = ds.getAllMessages();
		ds.close();
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
		// supportInvalidateOptionsMenu();
	}

	class FetchScheduleAsyncTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			syncUpdatedSessionsData(getApplicationContext());

			BCBUtils.syncUserScheduleData(getApplicationContext());
			if (BCBUtils.updateContextWithBarcampData(getApplicationContext())) {
				BarcampData sessionsData = ((BarcampBangalore) getApplicationContext())
						.getBarcampData();
				List<BarcampUserScheduleData> schedule = ((BarcampBangalore) getApplicationContext())
						.getUserSchedule();
				if (schedule != null && sessionsData != null) {
					if (sessionsData.slotsArray != null
							&& !sessionsData.slotsArray.isEmpty()) {
						for (Slot slot : sessionsData.slotsArray) {
							if (slot.sessionsArray != null
									&& !slot.sessionsArray.isEmpty()) {
								for (Session session : slot.sessionsArray) {
									boolean foundSessionInSchedule = false;
									for (BarcampUserScheduleData data : schedule) {
										if (data.id.equals(session.id)) {
											foundSessionInSchedule = true;
											break;
										}
									}

									if (foundSessionInSchedule
											&& BCBSharedPrefUtils
													.getAlarmSettingsForID(
															MainFragmentActivity.this,
															session.id) != BCBSharedPrefUtils.ALARM_SET) {
										BCBUtils.setAlarmForSession(
												MainFragmentActivity.this,
												slot, session,
												sessionsData.slotsArray
														.indexOf(slot),
												slot.sessionsArray
														.indexOf(session));
									} else if (!foundSessionInSchedule
											&& BCBSharedPrefUtils
													.getAlarmSettingsForID(
															MainFragmentActivity.this,
															session.id) == BCBSharedPrefUtils.ALARM_SET) {
										BCBUtils.removeSessionFromSchedule(
												MainFragmentActivity.this,
												session.id,
												sessionsData.slotsArray
														.indexOf(slot),
												slot.sessionsArray
														.indexOf(session));
									}

								}
							}
						}
					}
				}
				return true;
			}
			return false;
		}

		private void syncUpdatedSessionsData(Context context) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivityManager
					.getActiveNetworkInfo();
			boolean isConnected = activeNetInfo != null
					&& activeNetInfo.isConnected();
			if (isConnected) {
				String dataNotSent = BCBSharedPrefUtils
						.getAndClearDataNotSent(context);
				if (!TextUtils.isEmpty(dataNotSent)) {
					String[] dataArray = dataNotSent.split("/");
					for (String data : dataArray) {
						String[] sessionData = data.split(",");
						Intent newIntent = new Intent(context,
								SessionAttendingUpdateService.class);
						newIntent.putExtra(
								SessionAttendingUpdateService.SESSION_ID,
								sessionData[0]);
						newIntent.putExtra(
								SessionAttendingUpdateService.IS_ATTENDING,
								sessionData[1]);
						context.startService(newIntent);
					}
				}
			}

		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!isCancelled()) {
				FragmentManager frManager = getSupportFragmentManager();
				MyAlertDialogFragment alertFragment = (MyAlertDialogFragment) frManager
						.findFragmentByTag("dialog");
				if (alertFragment != null && alertFragment.isVisible()) {
					alertFragment.dismiss();
				}
				BarcampData data = ((BarcampBangalore) getApplicationContext())
						.getBarcampData();
				if (data != null) {
					Fragment fragment = getSupportFragmentManager()
							.findFragmentById(R.id.content_frame);
					if (fragment instanceof ScheduleFragment) {
						ScheduleFragment schFragment = (ScheduleFragment) fragment;
						schFragment.callForFunction(
								ScheduleFragment.CALL_REFRESH_DATA, null);
					}

				}
				if (!result) {
					// failure
					// showDialog(SHOW_ERROR_DIALOG);
					// getView().findViewById(R.id.progressBar1).setVisibility(
					// View.GONE);
				} else {

				}
			}
			task = null;
		}
	}

	public static class MyAlertDialogFragment extends DialogFragment {

		ProgressDialog progressDialog;

		public static MyAlertDialogFragment newInstance(int title) {
			MyAlertDialogFragment frag = new MyAlertDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			progressDialog = new ProgressDialog(getActivity());
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(true);
			return progressDialog;

		}

		@Override
		public void dismiss() {
			// TODO Auto-generated method stub
			progressDialog.dismiss();
			super.dismiss();
			this.dismissAllowingStateLoss();
		}
	}

	@Override
	public Intent callForFunction(int id, Intent params) {
		if (id == CALL_REFRESH) {

			FragmentManager frManager = getSupportFragmentManager();
			MyAlertDialogFragment fragment = MyAlertDialogFragment
					.newInstance(0);
			fragment.setCancelable(false);
			fragment.show(getSupportFragmentManager(), "dialog");
			task = new FetchScheduleAsyncTask();
			task.execute();

			return null;
		} else if (id == DISMISS_PROGRESS_DIALOG) {
			FragmentManager frManager = getSupportFragmentManager();
			MyAlertDialogFragment fragment = (MyAlertDialogFragment) frManager
					.findFragmentByTag("dialog");
			if (fragment != null) {
				fragment.dismiss();
			}
			return null;
		} else if (id == CALL_SLOT_DETAILS) {
			FragmentManager frManager = getSupportFragmentManager();

			SlotDetailsFragment fragment = new SlotDetailsFragment();
			fragment.setIntent(params);
			frManager.beginTransaction().replace(R.id.content_frame, fragment)
					.addToBackStack("slot").commit();
			// supportInvalidateOptionsMenu();
		} else if (id == START_FRAGMENT) {
			try {
				FragmentManager frManager = getSupportFragmentManager();
				Class<?> clazz = Class.forName(params.getComponent()
						.getClassName());
				Constructor<?> ctor = clazz.getConstructor();
				BCBFragmentBaseClass fragment = (BCBFragmentBaseClass) ctor
						.newInstance(new Object[] {});
				fragment.setIntent(params);
				frManager.beginTransaction()
						.replace(R.id.content_frame, fragment)
						.addToBackStack("session").commit();
				supportInvalidateOptionsMenu();
			} catch (Exception e) {
				Log.e("Error", "Error creating fragment : " + e.getMessage());
			}

		}

		else if (id == ADD_ACTIONBAR) {
			// MenuItem menuItem = menu.getItem(0);
			// menuItem.setIntent(params);
			// menuItem.setIcon(R.drawable.share_icon);
			// menuItem.setEnabled(true);
			// supportInvalidateOptionsMenu();

		}
		return super.callForFunction(id, params);
	}

}
