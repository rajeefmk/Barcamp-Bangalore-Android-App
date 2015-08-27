package com.bangalore.barcamp;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkStateReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		syncUpdatedSessionsData(context);
	}

	public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
		Reader reader = null;
		reader = new InputStreamReader(stream, "UTF-8");
		char[] buffer = new char[len];
		reader.read(buffer);
		return new String(buffer);
	}

	@SuppressLint("LongLogTag")
	public static void syncUpdatedSessionsData(Context context) {
		String userID = BCBSharedPrefUtils.getUserID(context);
		String userKey = BCBSharedPrefUtils.getUserKey(context);
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		boolean isConnected = activeNetInfo != null
				&& activeNetInfo.isConnected();
		if (isConnected) {
			String dataNotSent = BCBSharedPrefUtils
					.getAndClearDataNotSent(context);
			if (!TextUtils.isEmpty(dataNotSent)) {
				String[] dataArray = dataNotSent.split("/");
				for (String data : dataArray) {
					String[] sessionData = data.split(",");
					BufferedReader in = null;
					String urlString = String.format(
							SessionAttendingUpdateService.BASE_URL, userID,
							userKey, sessionData[0], sessionData[1]);
					try {
						URL url = new URL(urlString);
						HttpURLConnection conn = (HttpURLConnection) url.openConnection();
						conn.setReadTimeout(10000 /* milliseconds */);
						conn.setConnectTimeout(15000 /* milliseconds */);
						conn.setRequestMethod("GET");
						conn.setDoInput(true);
						// Starts the query
						conn.connect();
						int response = conn.getResponseCode();
//						Log.d(DEBUG_TAG, "The response is: " + response);
						in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

//						HttpClient client = new DefaultHttpClient();
//						HttpUriRequest request = new HttpGet(url);
//						HttpResponse response = client.execute(request);
//						in = new BufferedReader(new InputStreamReader(response
//								.getEntity().getContent()));
						StringBuffer sb = new StringBuffer("");
						String line = "";
						String NL = System.getProperty("line.separator");
						while ((line = in.readLine()) != null) {
							sb.append(line + NL);
						}
						in.close();
						String page = sb.toString();
						Log.e("SessionAttendingUpdateService", "id:"
								+ sessionData[0] + "  return:" + page);
						if (!page.equals("success\n")) {
							BCBSharedPrefUtils.setDataNotSent(context,
									sessionData[0], sessionData[1]);
						}

					} catch (Throwable e) {
						e.printStackTrace();
						BCBSharedPrefUtils.setDataNotSent(context,
								sessionData[0], sessionData[1]);
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

				}
			}
		}
	}

}
