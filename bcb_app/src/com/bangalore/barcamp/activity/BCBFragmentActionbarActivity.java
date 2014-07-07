package com.bangalore.barcamp.activity;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

public class BCBFragmentActionbarActivity extends ActionBarActivity {

	public static final int ADD_ACTIONBAR = 3;
	public static final int START_FRAGMENT = 4;
	public static final int FINISH_FRAGMENT = 5;
	public static final String FRAGMENT_TARGET = "FRAGMENT_TARGET";

	public Intent callForFunction(int id, Intent params) {
		if (id == FINISH_FRAGMENT) {
			FragmentManager frManager = getSupportFragmentManager();
			frManager.popBackStack();
		}
		return null;
	}

	public void hideDrawer() {

	}

}
