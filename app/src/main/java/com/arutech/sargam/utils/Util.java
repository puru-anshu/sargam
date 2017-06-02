package com.arutech.sargam.utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.audiofx.AudioEffect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Base64;

import com.arutech.sargam.R;
import com.arutech.sargam.model.Song;
import com.arutech.sargam.saavn.api.model.Track;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import timber.log.Timber;

import static android.content.Context.CONNECTIVITY_SERVICE;

public final class Util {

	/**
	 * This UUID corresponds to the UUID of an Equalizer Audio Effect. It has been copied from
	 * {@link AudioEffect#EFFECT_TYPE_EQUALIZER} for backwards compatibility since this field was
	 * added in API level 18.
	 */
	private static final UUID EQUALIZER_UUID;
	public static final String TRANSFORMATION = "DES/ECB/PKCS5Padding";

	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
			EQUALIZER_UUID = AudioEffect.EFFECT_TYPE_EQUALIZER;
		} else {
			EQUALIZER_UUID = UUID.fromString("0bed4300-ddd6-11db-8f34-0002a5d5c51b");
		}
	}

	/**
	 * This class is never instantiated
	 */
	private Util() {

	}

	/**
	 * Checks whether the device is in a state where we're able to access the internet. If the
	 * device is not connected to the internet, this will return {@code false}. If the device is
	 * only connected to a mobile network, this will return {@code allowMobileNetwork}. If the
	 * device is connected to an active WiFi network, this will return {@code true.}
	 *
	 * @param context            A context used to check the current network status
	 * @param allowMobileNetwork Whether or not the user allows the application to use mobile
	 *                           data. This is an internal implementation that is not enforced
	 *                           by the system, but is exposed to the user in our app's settings.
	 * @return Whether network calls should happen in the current connection state or not
	 */
	@SuppressWarnings("SimplifiableIfStatement")
	public static boolean canAccessInternet(Context context, boolean allowMobileNetwork) {
		ConnectivityManager connectivityManager;
		connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		if (info == null) {
			// No network connections are active
			return false;
		} else if (!info.isAvailable() || info.isRoaming()) {
			// The network isn't active, or is a roaming network
			return false;
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			// If it's a mobile network, return the user preference
			return allowMobileNetwork;
		} else {
			// The network is a wifi network
			return true;
		}
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean hasPermission(Context context, String permission) {
		return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
	}

	@TargetApi(Build.VERSION_CODES.M)
	public static boolean hasPermissions(Context context, String... permissions) {
		for (String permission : permissions) {
			if (!hasPermission(context, permission)) {
				return false;
			}
		}
		return true;
	}

	public static Intent getSystemEqIntent(Context c) {
		Intent systemEq = new Intent(AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
		systemEq.putExtra(AudioEffect.EXTRA_PACKAGE_NAME, c.getPackageName());

		ActivityInfo info = systemEq.resolveActivityInfo(c.getPackageManager(), 0);
		if (info != null && !info.name.startsWith("com.android.musicfx")) {
			return systemEq;
		} else {
			return null;
		}
	}

	/**
	 * Checks whether the current device is capable of instantiating and using an
	 * {@link android.media.audiofx.Equalizer}
	 *
	 * @return True if an Equalizer may be used at runtime
	 */
	public static boolean hasEqualizer() {
		for (AudioEffect.Descriptor effect : AudioEffect.queryEffects()) {
			if (EQUALIZER_UUID.equals(effect.type)) {
				return true;
			}
		}
		return false;
	}

	public static Bitmap fetchFullArt(Context context, Song song) {
		if (song == null || !song.isInLibrary()) {
			return null;
		}


		MediaMetadataRetriever retriever = new MediaMetadataRetriever();

		try {
			retriever.setDataSource(context, song.getLocation());
			byte[] stream = retriever.getEmbeddedPicture();
			if (stream != null) {
				return BitmapFactory.decodeByteArray(stream, 0, stream.length);
			}
		} catch (RuntimeException e) {
			Timber.e(e, "Failed to load full song artwork");
		} catch (OutOfMemoryError e) {
			Timber.e(e, "Unable to allocate space on the heap for full song artwork");
		}

		return null;
	}

	public static boolean isMarshmallow() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
	}

	public static boolean isLollipop() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
	}


	public static boolean isJellyBeanMR2() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2;
	}

	public static boolean isJellyBean() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
	}

	public static boolean isJellyBeanMR1() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	public static Uri getAlbumArtUri(long albumId) {
		return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
	}

	public static final String makeShortTimeString(final Context context, long secs) {
		long hours, mins;

		hours = secs / 3600;
		secs %= 3600;
		mins = secs / 60;
		secs %= 60;

		final String durationFormat = context.getResources().getString(
				hours == 0 ? R.string.durationformatshort : R.string.durationformatlong);
		return String.format(durationFormat, hours, mins, secs);
	}

	public static int getBlackWhiteColor(int color) {
		double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
		if (darkness >= 0.5) {
			return Color.WHITE;
		} else return Color.BLACK;
	}

	public static String getAlbumArtForFile(String filePath) {
		MediaMetadataRetriever mmr = new MediaMetadataRetriever();
		mmr.setDataSource(filePath);

		return mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
	}

	public static final String makeCombinedString(final Context context, final String first,
	                                              final String second) {
		final String formatter = context.getResources().getString(R.string.combine_two_strings);
		return String.format(formatter, first, second);
	}

	public static final String makeLabel(final Context context, final int pluralInt,
	                                     final int number) {
		return context.getResources().getQuantityString(pluralInt, number, number);
	}


	public static Bitmap fastblur(Bitmap sentBitmap, float scale, int radius) {

		int width = Math.round(sentBitmap.getWidth() * scale);
		int height = Math.round(sentBitmap.getHeight() * scale);
		sentBitmap = Bitmap.createScaledBitmap(sentBitmap, width, height, false);

		Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

		if (radius < 1) {
			return (null);
		}

		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dv[] = new int[256 * divsum];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {

				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		return (bitmap);
	}

	public static Uri decryptSaavn(String encryptedMediaUrl) {
		try {
			byte[] data = Base64.decode(encryptedMediaUrl, 0);
			Cipher cipher = Cipher.getInstance(TRANSFORMATION);
			cipher.init(2, new SecretKeySpec(getKeyString().getBytes(), "DES"));
			byte[] bytes = cipher.doFinal(data);
			return Uri.parse(new String(bytes));
		} catch (Exception e) {
			Timber.e(e, "Error in decoding songurl");
		}

		return Uri.EMPTY;
	}

	@NonNull
	private static String getKeyString() {
		return "38346591";
	}

	@android.support.annotation.NonNull
	public static List<Song> getSongsFromTracks(List<Track> trackList) {
		List<Song> songList = new ArrayList<Song>();

		int tNum = 0;
		for (Track track : trackList) {

			Song song = new Song.Builder()
					.setSongId(track.getId().hashCode())
					.setSongName(track.getTitle())
					.setAlbumName(track.getMoreInfo().getAlbum())
					.setAlbumId(Long.parseLong(track.getMoreInfo().getAlbumId()))
					.setArtWork(track.getImage()).setTrackNumber(tNum++)
					.setInLibrary(false)
					.setLocation(getLocation(track))
					.setArtistName(track.getMoreInfo().getArtistMap().toString())
					.build();
			songList.add(song);

		}
		return songList;
	}

	private static Uri getLocation(Track track) {
		String encryptedMediaUrl = track.getMoreInfo().getEncryptedMediaUrl();
		return decryptSaavn(encryptedMediaUrl);

	}

}
