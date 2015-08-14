package com.bangalore.barcamp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.bangalore.barcamp.activity.BCBFragmentActionbarActivity;

public class BCBFragmentBaseClass extends Fragment {
	private static final String SAVED_INTENT = "savedIntent";
	private Intent intent;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(SAVED_INTENT)) {
				intent = savedInstanceState.getParcelable(SAVED_INTENT);
			}
		}
		super.onActivityCreated(savedInstanceState);
	}

	public Intent getIntent() {
		return intent;
	}

	public void setIntent(Intent intent) {
		this.intent = intent;
	}

	public void finish() {
		BCBFragmentActionbarActivity activity = (BCBFragmentActionbarActivity) getActivity();
		activity.callForFunction(BCBFragmentActionbarActivity.FINISH_FRAGMENT,
				null);
	}

	public Intent callForFunction(int id, Intent params) {
		return null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(SAVED_INTENT, intent);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(SAVED_INTENT)) {
				intent = savedInstanceState.getParcelable(SAVED_INTENT);
			}
		}
	}
}
