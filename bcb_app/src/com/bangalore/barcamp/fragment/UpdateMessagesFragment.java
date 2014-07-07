package com.bangalore.barcamp.fragment;

import java.util.Collections;
import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bangalore.barcamp.MessagesListAdapter;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.data.BCBUpdatesMessage;
import com.bangalore.barcamp.database.MessagesDataSource;

public class UpdateMessagesFragment extends BCBFragmentBaseClass {
	private MessageLoadingTask task;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.slot_details_list, null);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		task = new MessageLoadingTask();
		task.execute();
	}

	class MessageLoadingTask extends
			AsyncTask<Void, Void, List<BCBUpdatesMessage>> {

		@Override
		protected List<BCBUpdatesMessage> doInBackground(Void... params) {
			MessagesDataSource ds = new MessagesDataSource(getActivity());
			ds.open();
			List<BCBUpdatesMessage> list = ds.getAllMessages();
			Collections.reverse(list);
			ds.close();
			return list;
		}

		@Override
		protected void onPostExecute(List<BCBUpdatesMessage> result) {
			super.onPostExecute(result);
			ListView listview = (ListView) getView().findViewById(
					R.id.listView1);
			MessagesListAdapter adapter = new MessagesListAdapter(
					getActivity(), R.layout.updates_list_item, result);
			listview.setAdapter(adapter);
		}

	}
}
