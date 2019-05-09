package com.arutech.sargam.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.MediaDescriptionCompat;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.KeyEvent;

import com.arutech.sargam.R;


/**
 * Helper APIs for constructing MediaStyle notifications
 *
 * Modified from https://gist.github.com/ianhanniballake/47617ec3488e0257325c
 * @author Ian Lake
 */
public class MediaStyleHelper {
	private static final String CHANNEL_ID = "media_playback_channel";

	@RequiresApi(Build.VERSION_CODES.O)
	public static void createChannel(Context context) {
		NotificationManager
				mNotificationManager =
				(NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
		// The id of the channel.
		String id = CHANNEL_ID;
		// The user-visible name of the channel.
		CharSequence name = "Media playback";
		// The user-visible description of the channel.
		String description = "Media playback controls";
		int importance = NotificationManager.IMPORTANCE_LOW;
		NotificationChannel mChannel = new NotificationChannel(id, name, importance);
		// Configure the notification channel.
		mChannel.setDescription(description);
		mChannel.setShowBadge(false);
		mChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
		mNotificationManager.createNotificationChannel(mChannel);
	}

    /**
     * Build a notification using the information from the given media session.
     * @param context Context used to construct the notification.
     * @param mediaSession Media session to get information.
     * @return A pre-built notification with information from the given media session.
     */
    public static NotificationCompat.Builder from(Context context,
                                                  MediaSessionCompat mediaSession) {
//	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//		    MediaStyleHelper.createChannel(context);
//	    }

        MediaControllerCompat controller = mediaSession.getController();
        MediaMetadataCompat mediaMetadata = controller.getMetadata();
        MediaDescriptionCompat description = mediaMetadata.getDescription();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID);

        builder
                .setContentTitle(description.getTitle())
                .setContentText(description.getSubtitle())
                .setSubText(description.getDescription())
                .setContentIntent(controller.getSessionActivity())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setWhen(0)
                .setShowWhen(false);

        if (description.getIconBitmap() == null) {
            builder.setLargeIcon(
                    BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        } else {
            builder.setLargeIcon(description.getIconBitmap());
        }

        return builder;
    }

    /**
     * Create a {@link PendingIntent} appropriate for a MediaStyle notification's action. Assumes
     * you are using a media button receiver.
     * @param context Context used to construct the pending intent.
     * @param mediaKeyEvent KeyEvent code to send to your media button receiver.
     * @return An appropriate pending intent for sending a media button to your media button
     *      receiver.
     */
    public static PendingIntent getActionIntent(Context context, int mediaKeyEvent) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setPackage(context.getPackageName());
        intent.putExtra(Intent.EXTRA_KEY_EVENT,
                new KeyEvent(KeyEvent.ACTION_DOWN, mediaKeyEvent));
        return PendingIntent.getBroadcast(context, mediaKeyEvent, intent, 0);
    }
}