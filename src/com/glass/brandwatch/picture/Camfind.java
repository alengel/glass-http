package com.glass.brandwatch.picture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
//import org.apache.http.entity.mime.MultipartEntity;
//import org.apache.http.entity.mime.content.ContentBody;
//import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.glass.brandwatch.utils.HttpRequest;
import com.glass.brandwatch.utils.PropertiesManager;

public class Camfind extends AsyncTask<File, Void, String> {
	private static final String TAG = "Camfind";

	// Called from execute() - initializes this class
	protected String doInBackground(File... image) {
		String token = getImageToken(image);
		return getImageName(token);
	}

	//Get image token from the Camfind API from the uploaded image
	private String getImageToken(File[] image) {

		String token = "";
		String url = PropertiesManager.getProperty("image_request_url");
		String testImageUrl = "http://upload.wikimedia.org/wikipedia/en/2/2d/Mashape_logo.png";

		//Set up parameters for POST request
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(2);
		parameters.add(new BasicNameValuePair("image_request[locale]", "en_US"));
		parameters.add(new BasicNameValuePair("image_request[remote_image_url]", testImageUrl));

		//Make the POST request
		HttpResponse response = HttpRequest.doHttpPost(url, getHeaders(), parameters);

		try {
			//Get token from the JSON response
			JSONObject jObject = new JSONObject(EntityUtils.toString(response.getEntity()));
			token = jObject.getString("token");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		// Image upload broken in Camfind API since July 28th, 2014.
		// http://www.mashape.com/imagesearcher/camfind/support/45
		// MultipartEntity mpEntity = new MultipartEntity();
		// ContentBody imageFile = new FileBody(image, "image/jpeg");
		// mpEntity.addPart("image_request[image]", imageFile);

		return token;
	}

	//Use image token from POST request to get the name
	private String getImageName(String token) {

		String name = null;

		Boolean completed = false;
		int attempts = 5;

		//Only attempt 5 requests to reduce API calls while the response is not complete
		while (attempts != 0 && completed == false) {
			HttpResponse response = makeGetRequest(token);

			//If the response is not null, parse the JSON response
			if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				try {
					String responseString = EntityUtils.toString(response.getEntity());
					Log.v(TAG, "Requesting image get response " + responseString);

					JSONObject jObject = new JSONObject(responseString);
					
					//If the status is completed, get the name from the JSON response
					if (jObject.getString("status").equals("completed")) {
						completed = true;
						name = jObject.getString("name");
					}

				} catch (Exception e) {
					Log.e(TAG, e.getMessage());
				}
			}

			//If the status is incomplete, sleep for 5 seconds to reduce API calls
			if (completed == false) {
				attempts--;
				SystemClock.sleep(5000);
			}
		}
		return name;
	}

	//Get the headers for the Camfind API
	private Header[] getHeaders() {
		String key = PropertiesManager.getProperty("mashape_key");
		return new Header[] { new BasicHeader("X-Mashape-Key", key) };
	}

	//Delegate the GET request to the HttpRequest, passing the URL
	private HttpResponse makeGetRequest(String token) {

		Log.v(TAG, "Requesting image get response with token " + token);

		// Set up the get request variables
		String url = PropertiesManager.getProperty("image_response_url") + token;

		return HttpRequest.doHttpGet(url, getHeaders());
	}

	//Called after each HTTP request
	protected void onPostExecute(String productName) {
		if (productName == null) {
			Log.v(TAG, "Could not find product name");
		} else {
			Log.v(TAG, "Found product name " + productName);
		}
	}
}
