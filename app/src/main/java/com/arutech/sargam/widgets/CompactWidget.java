package com.arutech.sargam.widgets;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.RemoteViews;

import com.arutech.sargam.R;
import com.arutech.sargam.activity.MainActivity;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.utils.MediaStyleHelper;

import io.reactivex.Observable;
import timber.log.Timber;

import static android.view.KeyEvent.KEYCODE_MEDIA_NEXT;
import static android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
import static android.view.KeyEvent.KEYCODE_MEDIA_PREVIOUS;

public class CompactWidget extends BaseWidget {

    @Override
    protected void onUpdate(Context context) {
        Observable.just(createBaseView(context))
                .flatMap(views -> mPlayerController.getNowPlaying().take(1)
                        .map(song -> setSong(context, views, song)))
                .flatMap(views -> mPlayerController.isPlaying().take(1)
                        .map(isPlaying -> setPlaying(views, isPlaying)))
                .subscribe(views -> updateAllInstances(context, views),
                        throwable -> Timber.e(throwable, "Failed to update widget"));
    }

    private RemoteViews createBaseView(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_compact);

        Intent launcherIntent = MainActivity.newNowPlayingIntent(context);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launcherIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_compact_container, pendingIntent);

        views.setOnClickPendingIntent(R.id.widget_next,
                MediaStyleHelper.getActionIntent(context, KEYCODE_MEDIA_NEXT));

        views.setOnClickPendingIntent(R.id.widget_play_pause,
                MediaStyleHelper.getActionIntent(context, KEYCODE_MEDIA_PLAY_PAUSE));

        views.setOnClickPendingIntent(R.id.widget_previous,
                MediaStyleHelper.getActionIntent(context, KEYCODE_MEDIA_PREVIOUS));

        @ColorInt int buttonColor = ContextCompat.getColor(context, R.color.widget_button);

        views.setInt(R.id.widget_next, "setColorFilter", buttonColor);
        views.setInt(R.id.widget_play_pause, "setColorFilter", buttonColor);
        views.setInt(R.id.widget_previous, "setColorFilter", buttonColor);

        return views;
    }

    private static RemoteViews setSong(Context context, RemoteViews views, @Nullable Song song) {
        if (song == null) {
            String defaultSongName = context.getString(R.string.nothing_playing);

            views.setTextViewText(R.id.widget_compact_title, defaultSongName);
            views.setTextViewText(R.id.widget_compact_detail, "");
        } else {
            views.setTextViewText(R.id.widget_compact_title, song.getSongName());
            views.setTextViewText(R.id.widget_compact_detail,
                    song.getArtistName() + " – " + song.getAlbumName());
        }

        return views;
    }

    private static RemoteViews setPlaying(RemoteViews views, boolean isPlaying) {
        views.setImageViewResource(R.id.widget_play_pause,
                (isPlaying)
                        ? R.drawable.ic_pause_36dp
                        : R.drawable.ic_play_arrow_36dp);

        return views;
    }

}