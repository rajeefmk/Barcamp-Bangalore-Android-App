package com.bangalore.barcamp.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class BCBInternalVenueMapFragment extends BCBFragmentBaseClass {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View v = inflater.inflate(R.layout.internal_venue_map_layout, null);
		return v;
	}
	
	@Override
	public void onAttach(Activity activity) {
		Tracker t = ((BarcampBangalore) getActivity().getApplication())
				.getTracker();

		// Set screen name.
		t.setScreenName(this.getClass().getName());

		// Send a screen view.
		t.send(new HitBuilders.AppViewBuilder().build());		super.onAttach(activity);
	}
}
