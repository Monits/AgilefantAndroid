/**
 * 
 */
package com.monits.agilefant;

import android.app.Application;

import com.androidquery.callback.BitmapAjaxCallback;

/**
 * @author gmuniz
 *
 */
public class AgilefantApplication extends Application {

	@Override
	public void onLowMemory() {
		BitmapAjaxCallback.clearCache();
	}
}
