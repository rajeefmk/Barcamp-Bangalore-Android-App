package com.bangalore.barcamp.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class AboutFragment extends BCBFragmentBaseClass {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.about_screen, null);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		((ImageView) getView().findViewById(R.id.imageView1))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent webIntent = new Intent(Intent.ACTION_VIEW);
						webIntent.setData(Uri
								.parse("https://barcampbangalore.org"));
						startActivity(webIntent);

					}
				});

		Tracker t = ((BarcampBangalore) getActivity().getApplication())
				.getTracker();

		// Set screen name.
		t.setScreenName(this.getClass().getName());

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
	}
}
