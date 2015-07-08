package com.monits.agilefant.cache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapLruCache extends LruCache<String, Bitmap> implements ImageCache {

	public static final int KB = 1024;
	public static final int DEFAULT_RATIO = 8;
	public static final int MEMORY_RATIO = DEFAULT_RATIO;

	/**
	 * Default constructor.
	 * Construct the object with a default chache size.
	 */
	public BitmapLruCache() {
		this(getDefaultLruCacheSize());
	}

	/**
	 * Constructor
	 * @param sizeInKiloBytes The size of the chache
	 */
	public BitmapLruCache(final int sizeInKiloBytes) {
		super(sizeInKiloBytes);
	}

	@Override
	protected int sizeOf(final String key, final Bitmap value) {
		return value.getRowBytes() * value.getHeight() / KB;
	}

	@Override
	public Bitmap getBitmap(final String url) {
		return get(url);
	}

	@Override
	public void putBitmap(final String url, final Bitmap bitmap) {
		put(url, bitmap);
	}

	private static int getDefaultLruCacheSize() {
		final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / KB);

		return maxMemory / MEMORY_RATIO;
	}
}