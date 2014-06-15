package com.bangalore.barcamp.activity;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.SessionAttendingUpdateService;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.BarcampUserScheduleData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;

public class MainFragmentActivity extends ActionBarActivity {
	private FetchScheduleAsyncTask task = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment);
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
				BarcampData data = ((BarcampBangalore) getApplicationContext())
						.getBarcampData();
				if (data != null) {
					// updateViews(data);
				}
				if (!result) {
					// failure
					// showDialog(SHOW_ERROR_DIALOG);
					// getView().findViewById(R.id.progressBar1).setVisibility(
					// View.GONE);
				}
			}
			task = null;
		}

	}

}
