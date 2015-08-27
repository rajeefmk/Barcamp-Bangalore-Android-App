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

package com.bangalore.barcamp;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.bangalore.barcamp.activity.BCBFragmentActionbarActivity;
import com.bangalore.barcamp.activity.MainFragmentActivity;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.fragment.SessionDetailsFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SessionAlarmIntentService extends IntentService {

	public static final String SESSION_ID = "SessionID";
	public final static String EXTRA_SESSION_POSITION = "session_position";
	public final static String EXTRA_SLOT_POS = "slotPosition";

	private static PowerManager.WakeLock sWakeLock;
	private static final Object LOCK = SessionAlarmIntentService.class;

	public SessionAlarmIntentService() {
		super("SessionAlarmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		synchronized (LOCK) {
			if (sWakeLock == null) {
				PowerManager pm = (PowerManager) this
						.getSystemService(Context.POWER_SERVICE);
				sWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
						"my_wakelock");
			}
		}
		sWakeLock.acquire();
		Log.e("SessionAlarmIntentService", " Service called ");
		boolean retVal = false;
		Context context = getApplicationContext(); // application Context
		if (((BarcampBangalore) context).getBarcampData() == null) {
			retVal = BCBUtils
					.updateContextWithBarcampData(getApplicationContext());
		} else {
			retVal = true;
		}
		if (retVal) {
			int icon = R.drawable.bcb_logo; // icon from resources
			CharSequence tickerText = "Session Alert"; // ticker-text
			long when = System.currentTimeMillis(); // notification time
			CharSequence contentTitle = "My notification"; // message title
			CharSequence contentText = "Hello World!"; // message text

			Intent notificationIntent = new Intent(this,
					MainFragmentActivity.class);
			notificationIntent.putExtra(
					BCBFragmentActionbarActivity.FRAGMENT_TARGET,
					SessionDetailsFragment.class);
			notificationIntent.putExtra(
					SessionDetailsFragment.EXTRA_SESSION_POSITION,
					intent.getIntExtra(EXTRA_SESSION_POSITION, 0));
			notificationIntent.putExtra(SessionDetailsFragment.EXTRA_SLOT_POS,
					intent.getIntExtra(EXTRA_SLOT_POS, 0));
			notificationIntent.putExtra(
					SessionDetailsFragment.EXTRA_SESSION_ID,
					intent.getStringExtra(SESSION_ID));

			String sessionID = null;
			BarcampData data = ((BarcampBangalore) context).getBarcampData();
			if (data != null) {
				int slotIndex = intent.getIntExtra(EXTRA_SLOT_POS, -1);
				int sessionPos = intent.getIntExtra(EXTRA_SESSION_POSITION, -1);
				sessionID = intent.getStringExtra(SESSION_ID);
				if (slotIndex == -1 || sessionPos == -1 || sessionID == null) {
					return;
				}
				Session session = data.slotsArray.get(slotIndex).sessionsArray
						.get(sessionPos);
				if (!session.id.equals(sessionID)) {
					// search session ID here
				}
				contentTitle = session.title;
				contentText = "By " + session.presenter + " @"
						+ session.location;
			}

			PendingIntent contentIntent = PendingIntent.getActivity(this,
					Integer.parseInt(sessionID), notificationIntent, 0);

			// the next two lines initialize the Notification, using the
			// configurations above
//			Notification notification = new Notification(icon, tickerText, when);
//			notification.flags |= Notification.FLAG_AUTO_CANCEL
//					| Notification.DEFAULT_SOUND
//					| Notification.FLAG_SHOW_LIGHTS;
//			notification.setLatestEventInfo(context, contentTitle, contentText,
//					contentIntent);
//
//			String ns = Context.NOTIFICATION_SERVICE;
//			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
//			mNotificationManager.notify(Integer.parseInt(sessionID),
//					notification);
			NotificationCompat.Builder mBuilder =
                    (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                                    .setSmallIcon(icon)
                                    .setTicker(tickerText)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .setContentText(contentText)
                                    .setContentTitle(contentTitle)
                                    .setContentIntent(contentIntent);
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // mId allows you to update the notification later on.
            mNotificationManager.notify(Integer.parseInt(sessionID), mBuilder.build());
			try {
				Uri notificationurl = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(
						getApplicationContext(), notificationurl);
				r.play();
			} catch (Exception e) {
			}

			Tracker t = ((BarcampBangalore) getApplication()).getTracker();

			// Set screen name.
			t.setScreenName(this.getClass().getName());

			// Send a screen view.
			t.send(new HitBuilders.AppViewBuilder().build());
		}
	}
}
