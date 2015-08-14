package com.bangalore.barcamp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class LoginActivity extends Activity {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);

		((Button) findViewById(R.id.loginButton))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(LoginActivity.this,
								WebViewActivity.class);
						intent.putExtra(WebViewActivity.ENABLE_LOGIN, true);
						intent.putExtra(
								WebViewActivity.URL,
                                    "http://barcampbangalore.org/bcb/wp-login_android.php?redirect_to=http%3A%2F%2Fbarcampbangalore.org%2Fbcb%2Fwp-android_helper.php%3Faction%3Dauth");
						// Intent intent = new Intent(
						// Intent.ACTION_VIEW,
						// Uri.parse("http://barcampbangalore.org/bcb/wp-login.php?redirect_to=http%3A%2F%2Fbarcampbangalore.org%2Fbcb%2Fwp-android_helper.php%3Faction%3Dauth"));
						// intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);

						Tracker t = ((BarcampBangalore) getApplication())
								.getTracker();

						// Send a screen view.
						t.send(new HitBuilders.EventBuilder()
								.setCategory("ui_action").setAction("button")
								.setLabel("LoginAttempted").build());
					}
				});
		TextView textView = (TextView) findViewById(R.id.skipLoginButton);
		// SpannableString content = new
		// SpannableString(getResources().getString(
		// R.string.do_not_login));
		// content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
		// textView.setText(content);

		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Tracker t = ((BarcampBangalore) getApplication()).getTracker();

				// Send a screen view.
				t.send(new HitBuilders.EventBuilder().setCategory("ui_action")
						.setAction("button").setLabel("LoginSkipped").build());
				finish();
			}
		});
	}
}
