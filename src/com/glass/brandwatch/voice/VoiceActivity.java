package com.glass.brandwatch.voice;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.glass.brandwatch.R;
import com.glass.brandwatch.asynctask.RequestBrandDataTask;
import com.glass.brandwatch.utils.PropertiesManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class VoiceActivity extends Activity {
	
	private static final String TAG = "VoiceActivity";
	
	// For tap events
	private GestureDetector mGestureDetector;

	private TextView contentView = null;

	protected void onResume() {
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.voice_activity);
		contentView = (TextView) findViewById(R.id.voice_main_content);

		mGestureDetector = createGestureDetector(this);

		openVoicePrompt();
	}
	
	//Called once the user has completed the voice command
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 0 && resultCode == RESULT_OK) {
			List<String> results = data
					.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
			String spokenText = results.get(0);

			if (spokenText == null) {
				spokenText = "Please say a product name";
				contentView.setText(spokenText);
				return;
			}
			
			//Delegate server request, pass in the URL
			new RequestBrandDataTask().execute(
					PropertiesManager.getProperty("server_url"), spokenText);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// Setup the gesture handler
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return super.onGenericMotionEvent(event);
	}

	// Handle user gestures
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);

		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				//User entered application
				if (gesture == Gesture.TAP) {
					openVoicePrompt();
					return true;
				} else if (gesture == Gesture.SWIPE_DOWN) {
					Log.i(TAG, "Application exited because user swiped down");
				}
				return false;
			}
		});
		return gestureDetector;
	}

	//Start intent to show voice prompt
	private void openVoicePrompt() {
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		startActivityForResult(intent, 0);
	}
}