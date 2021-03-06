package com.arutech.sargam.adapter;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.data.store.MediaStoreUtil;
import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.data.store.PlaylistStore;

import javax.inject.Inject;

import io.reactivex.Observable;
import timber.log.Timber;

import static android.support.design.widget.Snackbar.LENGTH_SHORT;

public class LibraryEmptyState extends BasicEmptyState {

	private Activity mActivity;

	@Inject
	MusicStore mMusicStore;
	@Inject
	PlaylistStore mPlaylistStore;

	public LibraryEmptyState(Activity activity) {
		mActivity = activity;
		SargamApplication.getComponent(activity).inject(this);
	}

	public String getEmptyMessage() {
		return mActivity.getString(R.string.empty);
	}

	@Override
	public final String getMessage() {
		if (MediaStoreUtil.hasPermission(mActivity)) {
			return getEmptyMessage();
		} else {
			return mActivity.getString(R.string.empty_no_permission);
		}
	}

	public String getEmptyMessageDetail() {
		return mActivity.getString(R.string.empty_detail);
	}

	@Override
	public final String getDetail() {
		if (MediaStoreUtil.hasPermission(mActivity)) {
			return getEmptyMessageDetail();
		} else {
			return mActivity.getString(R.string.empty_no_permission_detail);
		}
	}

	public String getEmptyAction1Label() {
		return mActivity.getString(R.string.action_refresh);
	}

	@Override
	public final String getAction1Label() {
		if (MediaStoreUtil.hasPermission(mActivity)) {
			return getEmptyAction1Label();
		} else {
			return mActivity.getString(R.string.action_try_again);
		}
	}

	public String getEmptyAction2Label() {
		return super.getAction2Label();
	}

	@Override
	public final String getAction2Label() {
		if (MediaStoreUtil.hasPermission(mActivity)) {
			return getEmptyAction2Label();
		} else {
			return mActivity.getString(R.string.action_open_settings);
		}
	}

	@Override
	public void onAction1() {
		refreshLibraries();
	}

	private void refreshLibraries() {
		Observable<Boolean> musicStoreResult = mMusicStore.refresh();
		Observable<Boolean> playlistStoreResult = mPlaylistStore.refresh();

		Observable<Boolean> combinedResult = Observable.combineLatest(
				musicStoreResult, playlistStoreResult, (result1, result2) -> result1 && result2);

		combinedResult.take(1)
				.subscribe(successful -> {
					if (successful) {
						View container = mActivity.findViewById(R.id.library_page_list);
						Snackbar.make(container, R.string.confirm_refresh_library, LENGTH_SHORT)
								.show();
					}
				}, throwable -> {
					Timber.e(throwable, "Failed to refresh library");
				});
	}

	@Override
	public void onAction2() {
		if (!MediaStoreUtil.hasPermission(mActivity)) {
			MediaStoreUtil.requestPermission(mActivity)
					.subscribe(granted -> {
						refreshLibraries();
					});
		}
	}
}
