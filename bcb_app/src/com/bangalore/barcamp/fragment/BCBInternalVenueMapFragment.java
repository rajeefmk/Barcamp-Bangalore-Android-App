package com.bangalore.barcamp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bangalore.barcamp.R;

public class BCBInternalVenueMapFragment extends BCBFragmentBaseClass {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.internal_venue_map_layout, null);
		return v;
	}
}
