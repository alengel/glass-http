package com.glass.brandwatch.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.util.Log;

public class PropertiesManager {

	private static final String TAG = "PropertiesManager";
	private static Properties properties = new Properties();

	public static void init(Context context, int resourceID) {
		Log.v(TAG, "Load properties file with id " + resourceID);
		
		try {
			InputStream rawResource = context.getResources().openRawResource(resourceID);
			properties.load(rawResource);
		} catch (NotFoundException e) {
			Log.e(TAG, "Did not find raw resource: " + e);
		} catch (IOException e) {
			Log.e(TAG, "Failed to open properties file");
		}
	}

	public static String getProperty(String property) {
		return properties.getProperty(property);
	}
}
