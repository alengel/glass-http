package com.glass.brandwatch.asynctask;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.util.Log;

import com.glass.brandwatch.utils.HttpRequest;

public class RequestBrandDataTask extends AsyncTask<String, Void, Boolean> {

	private static final String TAG = "RequestBrandDataTask";
	private String url;
	private String query;

	//Called by execute() - initializes this class
	protected Boolean doInBackground(String... parameters) {
		url = parameters[0];
		query = parameters[1];

		return requestBrandData(url, query);
	}

	//Request data from the server
	protected Boolean requestBrandData(String url, String query) {
		Log.i(TAG, String.format("Requesting brand data for '%s'", query));
		
		// Setup the parameters
		List<NameValuePair> parameters = new ArrayList<NameValuePair>(1);
		parameters.add(new BasicNameValuePair("query", query));
		
		//Delegate the POST request to HttpRequest
		HttpResponse response = HttpRequest.doHttpPost(url, null, parameters);
		if(response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK)
			return true;
		
		return false;
	}

	//Called after each HTTP request
	protected void onPostExecute(Boolean success) {
		if (success) {
			Log.v(TAG, String.format("Request for '%s' succedeed", query));
		} else {
			Log.v(TAG, String.format("Request for '%s' failed", query));
		}
	}
}
