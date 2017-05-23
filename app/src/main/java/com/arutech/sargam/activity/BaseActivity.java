package com.arutech.sargam.activity;

import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.arutech.sargam.R;
import com.arutech.sargam.SargamApplication;
import com.arutech.sargam.data.annotations.AccentTheme;
import com.arutech.sargam.data.annotations.PrimaryTheme;
import com.arutech.sargam.data.store.MediaStoreUtil;
import com.arutech.sargam.data.store.PreferenceStore;
import com.arutech.sargam.data.store.ThemeStore;
import com.arutech.sargam.player.PlayerController;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import javax.inject.Inject;

import timber.log.Timber;

public abstract class BaseActivity extends RxAppCompatActivity {

	// Used when resuming the Activity to respond to a potential theme change
	@PrimaryTheme
	private int mPrimaryColor;
	@AccentTheme
	private int mAccentColor;

	@Inject
	PreferenceStore _mPreferenceStore;
	@Inject
	ThemeStore _mThemeStore;
	@Inject
	PlayerController _mPlayerController;

	/**
	 * @inheritDoc
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		SargamApplication.getComponent(this).injectBaseActivity(this);

		_mThemeStore.setTheme(this);
		mPrimaryColor = _mPreferenceStore.getPrimaryColor();
		mAccentColor = _mPreferenceStore.getAccentColor();

		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		if (_mPreferenceStore.showFirstStart()) {
			if (!MediaStoreUtil.hasPermission(this)) {
				MediaStoreUtil.requestPermission(this).subscribe(granted -> {
					if (granted) {
						showFirstRunDialog();
					}
				});
			}

		}

		_mPlayerController.getInfo()
				.compose(bindToLifecycle())
				.subscribe(this::showSnackbar, throwable -> {
					Timber.e(throwable, "Failed to show info message");
				});

		_mPlayerController.getError()
				.compose(bindToLifecycle())
				.subscribe(this::showSnackbar, throwable -> {
					Timber.e(throwable, "Failed to show error message");
				});
	}

	private void showFirstRunDialog() {
		View messageView = getLayoutInflater().inflate(R.layout.alert_pref, null);
		TextView message = (TextView) messageView.findViewById(R.id.pref_alert_content);
		CheckBox pref = (CheckBox) messageView.findViewById(R.id.pref_alert_option);

		message.setText(Html.fromHtml(getString(R.string.first_launch_detail)));
		message.setMovementMethod(LinkMovementMethod.getInstance());

		pref.setChecked(true);
		pref.setText(R.string.enable_additional_logging_detailed);

		new AlertDialog.Builder(this)
				.setTitle(R.string.first_launch_title)
				.setView(messageView)
				.setPositiveButton(R.string.action_agree,
						(dialog, which) -> {
							_mPreferenceStore.setAllowLogging(pref.isChecked());
							_mPreferenceStore.setShowFirstStart(false);
						})
				.setCancelable(false)
				.show();
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void setContentView(@LayoutRes int layoutResId) {
		super.setContentView(layoutResId);
		setupToolbar();
	}

	protected void setupToolbar() {
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		if (toolbar != null) {
			setSupportActionBar(toolbar);

			if (getSupportActionBar() != null) {
				getSupportActionBar().setDisplayHomeAsUpEnabled(true);
				getSupportActionBar().setHomeButtonEnabled(true);
				getSupportActionBar().setDisplayShowHomeEnabled(true);
			}
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void onResume() {
		super.onResume();

		// If the theme was changed since this Activity was created, or the automatic day/night
		// theme has changed state, recreate this activity
		_mThemeStore.setTheme(this);
		boolean primaryDiff = mPrimaryColor != _mPreferenceStore.getPrimaryColor();
		boolean accentDiff = mAccentColor != _mPreferenceStore.getAccentColor();

		if (primaryDiff || accentDiff) {
			recreate();
		}
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * @inheritDoc
	 */
	@Override
	public void onBackPressed() {
		Timber.v("onBackPressed");
		super.onBackPressed();
		finish();
	}

	@IdRes
	protected int getSnackbarContainerViewId() {
		return R.id.list;
	}

	protected void showSnackbar(String message) {
		View content = findViewById(getSnackbarContainerViewId());
		if (content == null) {
			content = findViewById(android.R.id.content);
		}
		Snackbar.make(content, message, Snackbar.LENGTH_LONG).show();
	}


}
