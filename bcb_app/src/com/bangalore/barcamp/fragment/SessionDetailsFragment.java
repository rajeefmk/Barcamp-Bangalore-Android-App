package com.bangalore.barcamp.fragment;

import java.net.MalformedURLException;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.bangalore.barcamp.BCBSharedPrefUtils;
import com.bangalore.barcamp.BCBUtils;
import com.bangalore.barcamp.R;
import com.bangalore.barcamp.SessionAttendingUpdateService;
import com.bangalore.barcamp.activity.BCBFragmentActionbarActivity;
import com.bangalore.barcamp.activity.ShareActivity;
import com.bangalore.barcamp.data.BarcampBangalore;
import com.bangalore.barcamp.data.BarcampData;
import com.bangalore.barcamp.data.Session;
import com.bangalore.barcamp.data.Slot;
import com.bangalore.barcamp.widgets.CircularImageView;

public class SessionDetailsFragment extends BCBFragmentBaseClass {

	public final static String EXTRA_SESSION_POSITION = "session_position";
	public final static String EXTRA_SLOT_POS = "slotPosition";
	public static final String EXTRA_SESSION_ID = "sessionID";
	private static final int SHOW_ERROR_DIALOG = 100;
	private MenuItem mShareItem;
	private Session session;
	private ShareActionProvider mShareActionProvider;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// setHasOptionsMenu(true);
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.session_details, null);
		return view;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// View share = MenuItemCompat.getActionView(mShareItem);
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.session_details, menu);
		mShareItem = menu.findItem(R.id.share_session);
		mShareActionProvider = (ShareActionProvider) MenuItemCompat
				.getActionProvider(mShareItem);
		mShareActionProvider.setShareIntent(getDefaultIntent());

		Log.e("OptionsMenu", "options menu created");

	}

	private Intent getDefaultIntent() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		if (session != null) {
			intent.putExtra(Intent.EXTRA_TEXT, "I am attending session "
					+ session.title + " by " + session.presenter + " @"
					+ session.location + " between " + session.time);
		}
		return intent;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		Log.e("OptionsMenu", "onPrepareOptionsMenu called");
		if (mShareItem != null) {
			mShareItem.setVisible(true);
		} else {
			Log.e("OptionsMenu", "options menu not found");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.share_session) {
			Intent intent = new Intent(getActivity(), ShareActivity.class);
			intent.putExtra(ShareActivity.SHARE_STRING,
					"I am attending session " + session.title + " by "
							+ session.presenter + " @" + session.location
							+ " between " + session.time);
			startActivity(intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
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
		final Slot slot = data.slotsArray.get(getIntent().getIntExtra(
				EXTRA_SLOT_POS, 0));
		session = slot.sessionsArray.get(getIntent().getIntExtra(
				EXTRA_SESSION_POSITION, 0));
		String id = getIntent().getStringExtra(EXTRA_SESSION_ID);
		if (id != null && !id.equals(session.id)) {
			ShowErrorDialogFragment.newInstance(R.string.error_title).show(
					getFragmentManager(), "dialog");
			finish();
		}

		((TextView) view.findViewById(R.id.title)).setText(session.title);
		((TextView) view.findViewById(R.id.time)).setText(session.time);
		((TextView) view.findViewById(R.id.location)).setText(session.location);
		((TextView) view.findViewById(R.id.presenter)).setText("By "
				+ session.presenter);
		((TextView) view.findViewById(R.id.description)).setText(Html
				.fromHtml(session.description));
		((TextView) view.findViewById(R.id.description))
				.setMovementMethod(LinkMovementMethod.getInstance());
		try {
			((CircularImageView) view.findViewById(R.id.authorImage))
					.setImageURL(new URL(session.photo));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox1);
		checkBox.setChecked(BCBSharedPrefUtils.getAlarmSettingsForID(
				getActivity(), session.id) == BCBSharedPrefUtils.ALARM_SET);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					BCBUtils.setAlarmForSession(getActivity()
							.getApplicationContext(), slot, session,
							getIntent().getIntExtra(EXTRA_SLOT_POS, 0),
							getIntent().getIntExtra(EXTRA_SESSION_POSITION, 0));
				} else {
					BCBUtils.removeSessionFromSchedule(getActivity()
							.getApplicationContext(), session.id, getIntent()
							.getIntExtra(EXTRA_SLOT_POS, 0), getIntent()
							.getIntExtra(EXTRA_SESSION_POSITION, 0));
				}
				Intent newIntent = new Intent(getActivity()
						.getApplicationContext(),
						SessionAttendingUpdateService.class);
				newIntent.putExtra(SessionAttendingUpdateService.SESSION_ID,
						session.id);
				newIntent.putExtra(SessionAttendingUpdateService.IS_ATTENDING,
						isChecked ? "true" : "false");
				getActivity().startService(newIntent);
			}
		});

		view.findViewById(R.id.location_layout).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity()
								.getApplicationContext(),
								LocationFragment.class);
						intent.putExtra(LocationFragment.LOCATION_EXTRA,
								session.location);
						BCBFragmentActionbarActivity activity = (BCBFragmentActionbarActivity) getActivity();
						activity.callForFunction(
								BCBFragmentActionbarActivity.START_FRAGMENT,
								intent);

					}
				});

		// Intent intent = new Intent(getActivity(), ShareActivity.class);
		// intent.putExtra(ShareActivity.SHARE_STRING, "I am attending session "
		// + session.title + " by " + session.presenter + " @"
		// + session.location + " between " + session.time);
		// IntentAction shareAction = new IntentAction(getActivity(), intent,
		// R.drawable.share_icon);
		// BCBFragmentActionbarActivity activity =
		// (BCBFragmentActionbarActivity) getActivity();
		// activity.callForFunction(BCBFragmentActionbarActivity.ADD_ACTIONBAR,
		// intent);
		setHasOptionsMenu(true);
		// getActivity().supportInvalidateOptionsMenu();
		if (mShareActionProvider != null) {
			mShareActionProvider.setShareIntent(getDefaultIntent());
		}
	}

	public static class ShowErrorDialogFragment extends DialogFragment {

		public static ShowErrorDialogFragment newInstance(int title) {
			ShowErrorDialogFragment frag = new ShowErrorDialogFragment();
			Bundle args = new Bundle();
			args.putInt("title", title);
			frag.setArguments(args);
			return frag;
		}

		private AlertDialog alertDialog;

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			alertDialog = new AlertDialog.Builder(getActivity())
					.setTitle("Error!!!")
					.setMessage(
							"Error!!! Session got changed. Please check schedule again.")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.dismiss();

								}
							}).create();
			return alertDialog;
		}

	}

}
