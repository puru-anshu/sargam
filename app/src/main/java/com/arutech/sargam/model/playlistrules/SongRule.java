package com.arutech.sargam.model.playlistrules;

import android.os.Parcel;
import android.os.Parcelable;

import com.arutech.sargam.data.store.MusicStore;
import com.arutech.sargam.data.store.PlayCountStore;
import com.arutech.sargam.data.store.PlaylistStore;
import com.arutech.sargam.model.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;


public class SongRule extends AutoPlaylistRule implements Parcelable {


	public static SongRule getLastAddedRule() {
		long time = System.currentTimeMillis() - 604800000;
		return new SongRule(DATE_ADDED, GREATER_THAN, String.valueOf(time));
	}

	public static SongRule getRecentlyPlayedRule() {
		long time = System.currentTimeMillis() - 604800000;
		return new SongRule(DATE_PLAYED, GREATER_THAN, String.valueOf(time));
	}
	public static SongRule getTopPlayedRule(int minCount) {
		return new SongRule(PLAY_COUNT, GREATER_THAN, String.valueOf(minCount));
	}
	protected SongRule(@Field int field, @Match int match, String value) {
		super(AutoPlaylistRule.SONG, field, match, value);
	}

	protected SongRule(Parcel in) {
		super(in);
	}

	@Override
	public Observable<List<Song>> applyFilter(PlaylistStore playlistStore, MusicStore musicStore,
	                                          PlayCountStore playCountStore) {
		return musicStore.getSongs()
				.observeOn(Schedulers.computation())
				.take(1)
				.map(library -> {
					List<Song> filtered = new ArrayList<>();
					for (Song song : library) {
						if (includeSong(song, playCountStore)) {
							filtered.add(song);
						}
					}
					return filtered;
				});
	}

	private boolean includeSong(Song song, PlayCountStore playCountStore) {
		switch (getField()) {
			case ID:
				return checkId(song.getSongId());
			case NAME:
				return checkString(song.getSongName());
			case PLAY_COUNT:
				return checkInt(playCountStore.getPlayCount(song));
			case SKIP_COUNT:
				return checkInt(playCountStore.getPlayCount(song));
			case YEAR:
				return checkInt(song.getYear());
			case DATE_ADDED:
				return checkInt(song.getDateAdded());
			case DATE_PLAYED:
				return checkInt(playCountStore.getPlayDate(song));
		}
		throw new IllegalArgumentException("Cannot compare against field " + getField());
	}

	public static final Creator<SongRule> CREATOR = new Creator<SongRule>() {
		@Override
		public SongRule createFromParcel(Parcel in) {
			return new SongRule(in);
		}

		@Override
		public SongRule[] newArray(int size) {
			return new SongRule[size];
		}
	};
}
