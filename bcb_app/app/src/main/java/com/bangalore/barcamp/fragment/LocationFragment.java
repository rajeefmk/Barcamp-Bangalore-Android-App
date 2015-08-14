package com.bangalore.barcamp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class LocationFragment extends BCBFragmentBaseClass {
	public static final String LOCATION_EXTRA = "location_extra";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.location_screen, null);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View v = getView();
		String location = getIntent().getStringExtra(LOCATION_EXTRA)
				.toLowerCase();
		int mapID = getResources().getIdentifier(location, "drawable",
				getActivity().getPackageName());
		if (mapID > 0) {
			int id = R.drawable.asteroids;
			((ImageView) v.findViewById(R.id.locationImage))
					.setImageResource(mapID);
		}
		Tracker t = ((BarcampBangalore) getActivity().getApplication())
				.getTracker();

		// Set screen name.
		t.setScreenName(this.getClass().getName());

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());
	}
}
