package com.arutech.sargam.data.store;

import android.media.audiofx.Equalizer;
import android.os.Parcel;
import android.os.Parcelable;

import timber.log.Timber;

public class ImmutablePreferenceStore implements ReadOnlyPreferenceStore, Parcelable {

	private final boolean mShowFirstStart;
	private final boolean mAllowLogging;
	private final boolean mUseMobileNetwork;
	private final boolean mOpenNowPlayingOnNewQueue;
	private final boolean mEnableNowPlayingGestures;
	private final int mDefaultPage;

	private final boolean mShuffled;
	private final int mRepeatMode;
	private final long mPreviousSleepTimerDurationMillis;
	private final int mEqualizerPresetId;
	private final boolean mEqualizerEnabled;
	private final String mEqualizerSettings;

	public ImmutablePreferenceStore(ReadOnlyPreferenceStore preferencesStore) {
		mShowFirstStart = preferencesStore.showFirstStart();
		mAllowLogging = preferencesStore.allowLogging();
		mUseMobileNetwork = preferencesStore.useMobileNetwork();
		mOpenNowPlayingOnNewQueue = preferencesStore.openNowPlayingOnNewQueue();
		mEnableNowPlayingGestures = preferencesStore.enableNowPlayingGestures();
		mDefaultPage = preferencesStore.getDefaultPage();

		mShuffled = preferencesStore.isShuffled();
		mPreviousSleepTimerDurationMillis = preferencesStore.getLastSleepTimerDuration();
		mRepeatMode = preferencesStore.getRepeatMode();
		mEqualizerPresetId = preferencesStore.getEqualizerPresetId();
		mEqualizerEnabled = preferencesStore.getEqualizerEnabled();

		Equalizer.Settings eqSettings = preferencesStore.getEqualizerSettings();
		if (eqSettings != null) {
			mEqualizerSettings = eqSettings.toString();
		} else {
			mEqualizerSettings = null;
		}
	}

	protected ImmutablePreferenceStore(Parcel in) {
		mShowFirstStart = in.readByte() != 0;
		mAllowLogging = in.readByte() != 0;
		mUseMobileNetwork = in.readByte() != 0;
		mOpenNowPlayingOnNewQueue = in.readByte() != 0;
		mEnableNowPlayingGestures = in.readByte() != 0;
		mDefaultPage = in.readInt();
		mShuffled = in.readByte() != 0;
		mPreviousSleepTimerDurationMillis = in.readLong();
		mRepeatMode = in.readInt();
		mEqualizerPresetId = in.readInt();
		mEqualizerEnabled = in.readByte() != 0;
		mEqualizerSettings = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeByte((byte) (mShowFirstStart ? 1 : 0));
		dest.writeByte((byte) (mAllowLogging ? 1 : 0));
		dest.writeByte((byte) (mUseMobileNetwork ? 1 : 0));
		dest.writeByte((byte) (mOpenNowPlayingOnNewQueue ? 1 : 0));
		dest.writeByte((byte) (mEnableNowPlayingGestures ? 1 : 0));
		dest.writeInt(mDefaultPage);
		dest.writeByte((byte) (mShuffled ? 1 : 0));
		dest.writeLong(mPreviousSleepTimerDurationMillis);
		dest.writeInt(mRepeatMode);
		dest.writeInt(mEqualizerPresetId);
		dest.writeByte((byte) (mEqualizerEnabled ? 1 : 0));
		dest.writeString(mEqualizerSettings);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Creator<ImmutablePreferenceStore> CREATOR = new Creator<ImmutablePreferenceStore>() {
		@Override
		public ImmutablePreferenceStore createFromParcel(Parcel in) {
			return new ImmutablePreferenceStore(in);
		}

		@Override
		public ImmutablePreferenceStore[] newArray(int size) {
			return new ImmutablePreferenceStore[size];
		}
	};

	@Override
	public boolean showFirstStart() {
		return mShowFirstStart;
	}

	@Override
	public boolean allowLogging() {
		return mAllowLogging;
	}

	@Override
	public boolean useMobileNetwork() {
		return mUseMobileNetwork;
	}

	@Override
	public boolean openNowPlayingOnNewQueue() {
		return mOpenNowPlayingOnNewQueue;
	}

	@Override
	public boolean enableNowPlayingGestures() {
		return mEnableNowPlayingGestures;
	}

	@Override
	public int getDefaultPage() {
		return mDefaultPage;
	}

	@Override
	public boolean isShuffled() {
		return mShuffled;
	}

	@Override
	public int getRepeatMode() {
		return mRepeatMode;
	}

	@Override
	public long getLastSleepTimerDuration() {
		return mPreviousSleepTimerDurationMillis;
	}

	@Override
	public int getEqualizerPresetId() {
		return mEqualizerPresetId;
	}

	@Override
	public boolean getEqualizerEnabled() {
		return mEqualizerEnabled;
	}

	@Override
	public Equalizer.Settings getEqualizerSettings() {
		if (mEqualizerSettings != null) {
			try {
				return new Equalizer.Settings(mEqualizerSettings);
			} catch (IllegalArgumentException exception) {
				Timber.e(exception, "getEqualizerSettings: failed to parse equalizer settings");
			}
		}
		return null;
	}

}
