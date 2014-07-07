package com.bangalore.barcamp.fragment;

import java.util.List;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.R;

public class ShareFragment extends BCBFragmentBaseClass {
	public static final String SHARE_STRING = "Share String";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.share_screen, null);
		return v;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View v = getView();
		((EditText) v.findViewById(R.id.editText1))
				.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {

					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
						((TextView) getView().findViewById(
								R.id.charsLeftTextView)).setText("Chars left: "
								+ (140 - s.length() - 7));
					}
				});
		if (getIntent().hasExtra(SHARE_STRING)) {
			((EditText) v.findViewById(R.id.editText1)).setText(getIntent()
					.getStringExtra(SHARE_STRING));
		}
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		final PackageManager pm = getActivity().getPackageManager();
		final Spinner spinner = (Spinner) v.findViewById(R.id.shareTypeSpinner);
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
				getActivity(), android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		final List<ResolveInfo> matches = pm.queryIntentActivities(intent, 0);
		String selectedItem = BCBSharedPrefUtils.getShareSettings(getActivity()
				.getApplicationContext());
		int selectedPos = -1;
		for (ResolveInfo info : matches) {
			adapter.add(info.loadLabel(pm));
			if (selectedItem.equals(info.loadLabel(pm))) {
				selectedPos = matches.indexOf(info);
			}
		}
		spinner.setAdapter(adapter);

		if (selectedPos != -1) {
			spinner.setSelected(true);
			spinner.setSelection(selectedPos);
		}
		((TextView) v.findViewById(R.id.charsLeftTextView))
				.setText("Chars left: 140");
		((Button) v.findViewById(R.id.button1))
				.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(Intent.ACTION_SEND);
						intent.setType("text/plain");
						int appSelectedPos = spinner.getSelectedItemPosition();
						ResolveInfo info = matches.get(appSelectedPos);
						intent.setClassName(info.activityInfo.packageName,
								info.activityInfo.name);

						BCBSharedPrefUtils.setShareSettings(getActivity()
								.getApplicationContext(), (String) info
								.loadLabel(pm));
						intent.putExtra(
								Intent.EXTRA_TEXT,
								((EditText) getActivity().findViewById(
										R.id.editText1)).getText().toString()
										+ " #barcampblr");
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

						startActivity(intent);
						finish();

					}
				});

	}
}
