package com.glass.brandwatch.picture;

import java.io.File;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;

import com.google.android.glass.media.CameraManager;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;

public class ActivateTakePictureActivity extends Activity {
	private static final int TAKE_PICTURE_REQUEST = 1;

	// For tap events
	private GestureDetector mGestureDetector;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// For gesture handling
		mGestureDetector = createGestureDetector(this);

		takePicture();
	}

	protected void onResume() {
		super.onResume();
	}

	// Start the intent to activate the camera
	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, TAKE_PICTURE_REQUEST);

		// Temporary commented out to avoid API calls
		Log.i("ActivateTakePictureActivity", "making request");
//		new Camfind().execute();
	}

	/**
	 * Called when image is taken and user accepted image Currently (July 31st,
	 * 2014) broken by
	 * https://code.google.com/p/google-glass-api/issues/detail?id=555
	 **/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// If image is taken and accepted by user, process it
		if (requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK) {
			String picturePath = data.getStringExtra(CameraManager.EXTRA_PICTURE_FILE_PATH);
			processImageWhenSaved(picturePath);
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	// Check if image is ready for further processing
	private void processImageWhenSaved(final String picturePath) {
		final File pictureFile = new File(picturePath);

		// When file is written, make image recognition request to Camfind API
		if (pictureFile.exists()) {
			new Camfind().execute(pictureFile);

			// When the image not ready, inform user by showing a progress bar
		} else {

			final File parentDirectory = pictureFile.getParentFile();
			FileObserver observer = new FileObserver(parentDirectory.getPath(),
					FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {

				private boolean isFileWritten;

				@Override
				public void onEvent(int event, String path) {
					if (!isFileWritten) {

						// For safety concerns, verify image is the one expected
						File affectedFile = new File(parentDirectory, path);
						isFileWritten = affectedFile.equals(pictureFile);

						if (isFileWritten) {
							stopWatching();

							// When file is ready, recursively call this
							// function to process
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									processImageWhenSaved(picturePath);
								}
							});
						}
					}
				}
			};
			observer.startWatching();
		}
	}

	// Setup the gesture handler
	public boolean onGenericMotionEvent(MotionEvent event) {
		if (mGestureDetector != null) {
			return mGestureDetector.onMotionEvent(event);
		}
		return super.onGenericMotionEvent(event);
	}

	// Activate camera on tap.
	private GestureDetector createGestureDetector(Context context) {
		GestureDetector gestureDetector = new GestureDetector(context);

		gestureDetector.setBaseListener(new GestureDetector.BaseListener() {

			@Override
			public boolean onGesture(Gesture gesture) {
				if (gesture == Gesture.TAP) {
					takePicture();
					return true;
				}
				return false;
			}
		});
		return gestureDetector;
	}
}
