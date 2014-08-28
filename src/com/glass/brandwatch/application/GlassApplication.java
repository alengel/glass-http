package com.glass.brandwatch.application;

import android.app.Application;

import com.glass.brandwatch.R;
import com.glass.brandwatch.utils.PropertiesManager;

public class GlassApplication extends Application {
	@Override
	public void onCreate() {
		super.onCreate();
		
		//Initialize the configuration file once, so that it's properties are accessible
		PropertiesManager.init(getApplicationContext(), R.raw.config);
	}
}