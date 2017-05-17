// IPlayerService.aidl
package com.arutech.sargam;

import com.arutech.sargam.model.Song;
import com.arutech.sargam.data.store.ImmutablePreferenceStore;
import com.arutech.sargam.player.PlayerState;
import com.arutech.sargam.player.RemoteEqualizer;

interface IPlayerService {

    void stop();
    void skip();
    void previous();
    void togglePlay();
    void play();
    void pause();
    void setPreferences(in ImmutablePreferenceStore preferences);
    void setQueue(in List<Song> newQueue, int newPosition);
    void changeSong(int position);
    void editQueue(in List<Song> newQueue, int newPosition);
    void queueNext(in Song song);
    void queueNextList(in List<Song> songs);
    void queueLast(in Song song);
    void queueLastList(in List<Song> songs);
    void seekTo(int position);

    boolean isPlaying();
    Song getNowPlaying();
    List<Song> getQueue();
    int getQueuePosition();
    int getQueueSize();
    int getCurrentPosition();
    int getDuration();

    PlayerState getPlayerState();
    void restorePlayerState(in PlayerState state);

    int getMultiRepeatCount();
    void setMultiRepeatCount(int count);

    long getSleepTimerEndTime();
    void setSleepTimerEndTime(long timestampInMillis);

}
