package com.arutech.sargam.db;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

/**
 * Created by anshuman on 24/05/17.
 */
@Table(name = "SongCount")
public class SongCount extends Model {
	@Column(name = "songId", index = true, unique = true)
	private long songId;
	@Column(name = "numPlay")
	private int plays;
	@Column(name = "numSkip")
	private int skips;
	@Column(name = "lastPlayTime")
	private long playDate;

	public SongCount() {
		super();
	}

	public SongCount(long songId, int plays, int skips, long playDate) {
		super();
		this.songId = songId;
		this.plays = plays;
		this.skips = skips;
		this.playDate = playDate;
	}

	public int getPlays() {
		return plays;
	}

	public void setPlays(int plays) {
		this.plays = plays;
	}

	public int getSkips() {
		return skips;
	}

	public void setSkips(int skips) {
		this.skips = skips;
	}

	public long getPlayDate() {
		return playDate;
	}

	public void setPlayDate(long playDate) {
		this.playDate = playDate;
	}


	public static SongCount findById(long songId) {
		return new Select().from(SongCount.class).where("songId = ? ", songId).executeSingle();
	}
}
