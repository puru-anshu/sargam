package com.arutech.sargam.data.store;


import com.arutech.sargam.model.Song;

import io.reactivex.Observable;


public interface PlayCountStore {

    Observable<Void> refresh();
    void save();

    int getPlayCount(Song song);
    int getSkipCount(Song song);
    long getPlayDate(Song song);

    void incrementPlayCount(Song song);
    void incrementSkipCount(Song song);
    void setPlayDateToNow(Song song);

    void setPlayCount(Song song, int count);
    void setSkipCount(Song song, int count);
    void setPlayDate(Song song, long timeInUnixSeconds);

}
