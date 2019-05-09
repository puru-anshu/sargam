package com.arutech.sargam.fragments;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceGroupAdapter;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.PreferenceViewHolder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.utils.Util;
import com.arutech.sargam.view.BackgroundDecoration;

import javax.inject.Inject;

public class PreferenceFragment extends PreferenceFragmentCompat
		implements View.OnLongClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

	private static final String DIRECTORY_FRAGMENT =
			"com.arutech.sargam.fragments.DirectoryListFragment";
	private static final String EQUALIZER_FRAGMENT =
			"com.arutech.sargam.fragments.EqualizerFragment";

	@Inject
	PreferenceStore mPrefStore;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SargamApplication.getComponent(this).inject(this);
	}

	@Override
	public void onCreatePreferences(Bundle bundle, String rootKey) {
		addPreferencesFromResource(R.xml.prefs);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		View view = super.onCreateView(inflater, container, savedInstanceState);

		setDivider(null);
		setDividerHeight(0);

		return view;
	}

	@Override
	public RecyclerView onCreateRecyclerView(LayoutInflater inflater, ViewGroup parent,
	                                         Bundle savedInstanceState) {
		RecyclerView view = super.onCreateRecyclerView(inflater, parent, savedInstanceState);

		int padding = (int) getResources().getDimension(R.dimen.global_padding);
		view.setPadding(padding, 0, padding, 0);

		view.addItemDecoration(new BackgroundDecoration(android.R.id.title));
		return view;
	}

	@Override
	protected RecyclerView.Adapter onCreateAdapter(PreferenceScreen preferenceScreen) {
		return new PreferenceGroupAdapter(preferenceScreen) {
			@Override
			public void onBindViewHolder(PreferenceViewHolder holder, int position) {
				super.onBindViewHolder(holder, position);

				// Override Equalizer preference attachment to add a long click listener
				// and to change the detail text at runtime
				String fragment = getItem(position).getFragment();
				if ("com.arutech.sargam.fragments.EqualizerFragment".equals(fragment)) {

					ViewGroup itemView = (ViewGroup) holder.itemView;
					TextView title = (TextView) itemView.findViewById(android.R.id.title);
					TextView detail = (TextView) itemView.findViewById(android.R.id.summary);

					boolean hasSystemEq = Util.getSystemEqIntent(getContext()) != null;

					if (hasSystemEq && Util.hasEqualizer()) {
						// If we have Jockey's Equalizer and another Equalizer
						itemView.setOnLongClickListener(PreferenceFragment.this);
						detail.setText(R.string.equalizer_more_options_detail);
						detail.setVisibility(View.VISIBLE);

					} else if (hasSystemEq && !Util.hasEqualizer()) {
						// If we don't have any equalizers
						detail.setText(R.string.equalizerUnsupported);
						detail.setVisibility(View.VISIBLE);
						itemView.setEnabled(false);
						title.setEnabled(false);
						detail.setEnabled(false);
					}
				}
			}
		};
	}

	@Override
	public void onResume() {
		super.onResume();
		getPreferenceManager().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);

		Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
		if (toolbar != null) {
			toolbar.setTitle(R.string.header_settings);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		getPreferenceManager().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public boolean onPreferenceTreeClick(Preference preference) {
		if (EQUALIZER_FRAGMENT.equals(preference.getFragment())) {
			Intent eqIntent = Util.getSystemEqIntent(getActivity());

			if (eqIntent != null && !mPrefStore.getEqualizerEnabled()) {
				// If the system has an equalizer implementation already in place, use it
				// to avoid weird problems and conflicts that can cause unexpected behavior

				// for example, on Motorola devices, attaching an Equalizer can cause the
				// MediaPlayer's volume to briefly become very loud -- even when the phone
				// is muted
				startActivity(eqIntent);
			} else if (Util.hasEqualizer()) {
				// If there isn't a global equalizer or the user has already enabled our
				// equalizer, navigate to the built-in implementation
				showEqualizerFragment();
			} else {
				Toast.makeText(getActivity(), R.string.equalizerUnsupported, Toast.LENGTH_LONG)
						.show();
			}
			return true;
		}
		return super.onPreferenceTreeClick(preference);
	}


	private void replaceFragment(Fragment next) {
		getFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
						R.anim.slide_in_left, R.anim.slide_out_right)
				.replace(R.id.pref_fragment_container, next)
				.addToBackStack(null)
				.commit();
	}

	private void showEqualizerFragment() {
		replaceFragment(new EqualizerFragment());
	}


	@Override
	public boolean onLongClick(View v) {
		if (Util.hasEqualizer()) {
			showEqualizerFragment();
		} else {
			Toast
					.makeText(
							getActivity(),
							R.string.equalizerUnsupported,
							Toast.LENGTH_LONG)
					.show();
		}
		return true;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (getString(R.string.pref_key_color_base).equals(key)
				|| getString(R.string.pref_key_color_accent).equals(key)
				|| getString(R.string.pref_key_color_primary).equals(key)) {
			getActivity().recreate();
		}
	}
}