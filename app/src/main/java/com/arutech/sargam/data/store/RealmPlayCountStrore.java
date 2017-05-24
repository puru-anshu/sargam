package com.arutech.sargam.data.store;

import android.content.Context;
import android.support.annotation.NonNull;

import com.arutech.sargam.db.SongCount;
import com.arutech.sargam.model.Song;

import io.reactivex.Observable;

import static com.arutech.sargam.db.SongCount.findById;


/**
 * Created by anshuman on 24/05/17.
 */

public class RealmPlayCountStrore implements PlayCountStore {

	private Context mContext;


	public RealmPlayCountStrore(Context mContext) {
		this.mContext = mContext;

	}

	@Override
	public Observable<Void> refresh() {
		return Observable.empty();
	}

	@Override
	public void save() {
	}

	@Override
	public int getPlayCount(Song song) {
		SongCount songCount = findById(song.getSongId());
		if (null != songCount) {
			return songCount.getPlays();
		}
		return 0;
	}

	@Override
	public int getSkipCount(Song song) {
		SongCount songCount = findById(song.getSongId());
		if (null != songCount) {
			return songCount.getSkips();
		}
		return 0;

	}

	@Override
	public long getPlayDate(Song song) {
		SongCount songCount = findById(song.getSongId());
		if (null != songCount) {
			return songCount.getPlayDate();
		}
		return 0;
	}

	@Override
	public void incrementPlayCount(Song song) {
		SongCount songCount = getSongCountObject(song);
		songCount.setPlays(songCount.getPlays() + 1);
		songCount.save();

	}

	@NonNull
	private SongCount getSongCountObject(Song song) {
		SongCount songCount = findById(song.getSongId());
		if (null == songCount)
			songCount = new SongCount(song.getSongId(), 0, 0, 0);
		return songCount;
	}


	@Override
	public void incrementSkipCount(Song song) {
		SongCount songCount = getSongCountObject(song);
		songCount.setSkips(songCount.getSkips() + 1);
		songCount.save();
	}

	@Override
	public void setPlayDateToNow(Song song) {
		SongCount songCount = getSongCountObject(song);
		songCount.setPlayDate(System.currentTimeMillis() / 1000);
		songCount.save();

	}

	@Override
	public void setPlayCount(Song song, int count) {
		SongCount songCount = getSongCountObject(song);
		songCount.setPlays(count);
		songCount.save();
	}

	@Override
	public void setSkipCount(Song song, int count) {
		SongCount songCount = getSongCountObject(song);
		songCount.setSkips(count);
		songCount.save();
	}

	@Override
	public void setPlayDate(Song song, long timeInUnixSeconds) {
		SongCount songCount = getSongCountObject(song);
		songCount.setPlayDate(timeInUnixSeconds);
		songCount.save();

	}


}
