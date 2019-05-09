package com.arutech.sargam.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.fragments.web.TrackGroupFragment;
import com.arutech.sargam.fragments.web.WebSongFragment;
import com.arutech.sargam.player.PlayerController;
import com.arutech.sargam.saavn.data.store.SaavnStore;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class WebActivity extends BaseLibraryActivity {


	@Inject
	SaavnStore mMusicStore;
	@Inject
	PlayerController mPlayerController;

	private SwipeRefreshLayout mRefreshLayout;


	public static Intent newIntent(Context context) {
		return new Intent(context, WebActivity.class);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SargamApplication.getComponent(this).inject(this);
		onNewIntent(getIntent());
		initRefreshLayout();
		ViewPager pager = (ViewPager) findViewById(R.id.library_pager);

		AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.library_app_bar_layout);
		appBarLayout.addOnOffsetChangedListener((layout, verticalOffset) ->
				pager.setPadding(pager.getPaddingLeft(),
						pager.getPaddingTop(),
						pager.getPaddingRight(),
						layout.getTotalScrollRange() + verticalOffset));


		WebPagerAdapter adapter = new WebPagerAdapter(getSupportFragmentManager());
		pager.setAdapter(adapter);
		pager.addOnPageChangeListener(adapter);
		((TabLayout) findViewById(R.id.library_tabs)).setupWithViewPager(pager);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setHomeButtonEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
			getSupportActionBar().setTitle(R.string.header_web);
		}
	}

	@Override
	protected int getContentLayoutResource() {
		return R.layout.activity_web;
	}


	private void initRefreshLayout() {
		mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.library_refresh_layout);
		mRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT);

		mRefreshLayout.setEnabled(false);
		mMusicStore.isLoading().compose(bindToLifecycle())
				.observeOn(AndroidSchedulers.mainThread()).
				subscribe(
						refreshing -> {
							mRefreshLayout.setEnabled(refreshing);
							mRefreshLayout.setRefreshing(refreshing);
						}, throwable -> Timber.e(throwable, "Failed to update refresh indicator"));


	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_web, menu);


		return super.onCreateOptionsMenu(menu);

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_web_search:
				Intent intent = WebSearchActivity.newIntent(this, "");
				startActivity(intent);
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	@Override
	protected int getSnackbarContainerViewId() {
		return R.id.library_pager;
	}

	public static final int SEARCH_QUERY_THRESHOLD = 2;


	private class WebPagerAdapter extends FragmentPagerAdapter
			implements ViewPager.OnPageChangeListener {

		private TrackGroupFragment playlistFragment;
		private WebSongFragment songFragment;
		//private TrackGroupFragment artistFragment;
		private TrackGroupFragment albumFragment;


		WebPagerAdapter(FragmentManager fm) {
			super(fm);
		}


		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					if (songFragment == null) {
						songFragment = new WebSongFragment();
					}
					return songFragment;
				case 1:
					if (playlistFragment == null) {
						playlistFragment = TrackGroupFragment.getInstance("playlist");
					}
					return playlistFragment;

				case 2:
					if (albumFragment == null) {
						albumFragment = TrackGroupFragment.getInstance("album");
					}
					return albumFragment;

			}
			return new Fragment();
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return getResources().getString(R.string.header_top_songs);
				case 1:
					return getResources().getString(R.string.header_top_playlist);
				case 2:
					return getResources().getString(R.string.header_new_releases);
				default:
					return "Page " + position;
			}
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

		}

		@Override
		public void onPageSelected(int position) {
			// Hide the fab when outside of the Playlist fragment
		}

		@Override
		public void onPageScrollStateChanged(int state) {

		}
	}
}
