package com.arutech.sargam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.adapter.BasicEmptyState;
import com.arutech.sargam.adapter.HeaderSection;
import com.arutech.sargam.adapter.HeterogeneousAdapter;
import com.arutech.sargam.adapter.web.TrackGroupGridSection;
import com.arutech.sargam.adapter.web.WSongSection;
import com.arutech.sargam.saavn.data.store.SaavnStore;
import com.arutech.sargam.view.BackgroundDecoration;
import com.arutech.sargam.view.DividerDecoration;

import java.util.Collections;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import timber.log.Timber;

public class WebSearchActivity extends BaseLibraryActivity implements SearchView.OnQueryTextListener {

	private static final String W_KEY_SAVED_QUERY = "WSearchActivity.LAST_QUERY";

	public static Intent newIntent(Context context, String query) {
		Intent intent = new Intent(context, WebSearchActivity.class);
		Bundle extras = new Bundle();
		extras.putString(W_KEY_SAVED_QUERY, query);
		intent.putExtras(extras);
		return intent;
	}

	@Inject
	SaavnStore mMusicStore;

	private SearchView searchView;
	private BehaviorSubject<String> mQueryObservable;

	private RecyclerView mRecyclerView;
	private HeterogeneousAdapter mAdapter;

	private TrackGroupGridSection mPlaylistSection;
	private WSongSection mSongSection;
	private TrackGroupGridSection mAlbumSection;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SargamApplication.getComponent(this).inject(this);

		String lastQuery;
		if (savedInstanceState != null) {
			lastQuery = savedInstanceState.getString(W_KEY_SAVED_QUERY);
		} else {
			lastQuery = "";
		}
		Timber.i("Searching for %s " + lastQuery);

		mQueryObservable = BehaviorSubject.createDefault(lastQuery);
		// Set up the RecyclerView's adapter
		mRecyclerView = (RecyclerView) findViewById(R.id.list);
		initAdapter();

		mQueryObservable
				.subscribeOn(Schedulers.io())
				.flatMap(query -> mMusicStore.searchForPlaylist(query))
				.compose(bindToLifecycle())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(playlists -> {
					Timber.i("Playlists size %d", playlists.size());
					mPlaylistSection.setData(playlists);
					mAdapter.notifyDataSetChanged();
				}, throwable -> {
					Timber.e(throwable, "Failed to search for playlists");
				});

		mQueryObservable
				.subscribeOn(Schedulers.io())
				.flatMap(query -> mMusicStore.searchForSongs(query))
				.compose(bindToLifecycle())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(songs -> {
					mSongSection.setData(songs);
					mAdapter.notifyDataSetChanged();
				}, throwable -> {
					Timber.e(throwable, "Failed to search for songs");
				});

		mQueryObservable
				.subscribeOn(Schedulers.io())
				.flatMap(query -> mMusicStore.searchForAlbums(query))
				.compose(bindToLifecycle())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(albums -> {
					Timber.i("Albums size %d", albums.size());
					mAlbumSection.setData(albums);
					mAdapter.notifyDataSetChanged();
				}, throwable -> {
					Timber.e(throwable, "Failed to search for albums");
				});


	}

	@Override
	protected int getContentLayoutResource() {
		return R.layout.activity_instance;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(W_KEY_SAVED_QUERY, mQueryObservable.getValue());
	}

	private void initAdapter() {
		mPlaylistSection = new TrackGroupGridSection(this, Collections.emptyList());
		mSongSection = new WSongSection(this, Collections.emptyList());
		mAlbumSection = new TrackGroupGridSection(this, Collections.emptyList());

		mAdapter = new HeterogeneousAdapter()
				.addSection(new HeaderSection(getString(R.string.header_playlists)))
				.addSection(mPlaylistSection)
				.addSection(new HeaderSection(getString(R.string.header_songs)))
				.addSection(mSongSection)
				.addSection(new HeaderSection(getString(R.string.header_albums)))
				.addSection(mAlbumSection);

		mAdapter.setEmptyState(new BasicEmptyState() {
			@Override
			public String getMessage() {
				String query = mQueryObservable.getValue();
				return (query == null || query.isEmpty())
						? ""
						: getString(R.string.empty_search);
			}
		});

		mRecyclerView.setAdapter(mAdapter);
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);
		layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
		mRecyclerView.setLayoutManager(layoutManager);
		mRecyclerView.addItemDecoration(new BackgroundDecoration());
		mRecyclerView.addItemDecoration(new DividerDecoration(this, R.id.empty_layout));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_search, menu);

		MenuItem searchItem = menu.findItem(R.id.menu_library_search);
		searchView = (SearchView) searchItem.getActionView();
		searchView.setOnQueryTextListener(this);
		searchView.setIconified(false);

		String query = mQueryObservable.getValue();
		if (query != null && !query.isEmpty()) {
			searchView.setQuery(query, true);
		} else {
			searchView.requestFocus();
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				navigateHome();
				return true;
			case R.id.menu_library_search:
				if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) onSearchRequested();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void navigateHome() {
		Intent mainActivity = new Intent(this, WebActivity.class);
		startActivity(mainActivity);
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		search(query);
		searchView.clearFocus();
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		search(newText);
		return true;
	}

	private void search(String query) {
		if (!mQueryObservable.getValue().equals(query)) {
			mQueryObservable.onNext(query);
		}
	}


}
