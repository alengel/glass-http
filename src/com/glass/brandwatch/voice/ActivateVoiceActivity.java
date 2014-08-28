package com.glass.brandwatch.voice;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import com.glass.brandwatch.R;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class ActivateVoiceActivity extends Activity {

	// For tap events
	private GestureDetector mGestureDetector;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		// For gesture handling
		mGestureDetector = createGestureDetector(this);

		// Open the main voice activity showing the speech recognition screen
		openVoiceActivity();
	}

	// Start intent to handle voice command
	private void openVoiceActivity() {
		Intent intent = new Intent(this, VoiceActivity.class);
		startActivity(intent);
	}

	protected void onResume() {
		super.onResume();
	}

	// Setup the gesture handler
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return super.onGenericMotionEvent(event);
	}

	//Activate the voice activity on tap 
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);

		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					openVoiceActivity();
					return true;
				}
				return false;
			}
		});
		return gestureDetector;
	}
}
