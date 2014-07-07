package com.bangalore.barcamp.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.bangalore.barcamp.activity.BCBFragmentActionbarActivity;

public class BCBFragmentBaseClass extends Fragment {
	private Intent intent;

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

}
