package com.bangalore.barcamp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.bangalore.barcamp.R;
import com.bangalore.barcamp.SlotItemsListAdapter;
import com.bangalore.barcamp.activity.BCBFragmentActionbarActivity;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Slot;

public class SlotDetailsFragment extends BCBFragmentBaseClass {
	public static String EXTRA_POS = "pos";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.slot_details_list, null);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		BarcampData data = ((BarcampBangalore) getActivity()
				.getApplicationContext()).getBarcampData();

		if (data == null) {
			finish();
			return;
		}

		int pos = getIntent().getIntExtra(EXTRA_POS, 0);

		if (data.slotsArray.size() < pos) {
			finish();
			return;
		}
		Slot slot = data.slotsArray.get(pos);
		if (slot.sessionsArray == null || slot.sessionsArray.isEmpty()) {
			(view.findViewById(R.id.listView1)).setVisibility(View.GONE);
			(view.findViewById(R.id.no_slot_text_view))
					.setVisibility(View.VISIBLE);
			return;
		}

		ListView listview = (ListView) view.findViewById(R.id.listView1);
		SlotItemsListAdapter adapter = new SlotItemsListAdapter(getActivity(),
				R.layout.session_list_item, slot.sessionsArray);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int pos,
					long id) {
				Intent intent = new Intent(getActivity(),
						SessionDetailsFragment.class);
				intent.putExtra(SessionDetailsFragment.EXTRA_SESSION_POSITION,
						pos);
				intent.putExtra(SessionDetailsFragment.EXTRA_SLOT_POS,
						getIntent().getIntExtra(EXTRA_POS, 0));
				BCBFragmentActionbarActivity activity = (BCBFragmentActionbarActivity) getActivity();
				activity.callForFunction(
						BCBFragmentActionbarActivity.START_FRAGMENT, intent);
			}
		});
	}
}
