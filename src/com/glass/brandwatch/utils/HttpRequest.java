package com.glass.brandwatch.utils;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class HttpRequest {
	
	private static final String TAG = "HttpRequest";
	
	//Generic HTTP GET request
	static public HttpResponse doHttpGet(String url, Header[] headers) {
		Log.v(TAG, "Sending get request to " + url);

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		
		//Only set headers if required
		if(headers != null)
			get.setHeaders(headers);

		try {
			return client.execute(get);

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return null;
	}
	
	//Generic HTTP POST request
	static public HttpResponse doHttpPost(String url, Header[] headers, List<NameValuePair> parameters) {
		Log.v(TAG, "Sending post request to " + url);
		
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		
		//Only set headers if required
		if(headers != null)
			post.setHeaders(headers);

		try {
			// Make the post request with the passed parameters
			post.setEntity(new UrlEncodedFormEntity(parameters));

			// Handle the response
			return client.execute(post);

		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

		return null;
	}
}
