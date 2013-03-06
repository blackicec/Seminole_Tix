package com.dunksoftware.seminoletix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

public class Constants {
	
	public static String UsersAddress = "https://chi.erdaniels.com/users/";
	public static String GamesAddress = "https://chi.erdaniels.com/games/";
	
	public static String Success_msg = "Successful Registration";
	public static String IncorrectPIN_msg = "The provided information does " +
			"not match our records.";
	
	public static int CARD_NUMBER_LENGTH = 12;
	
	public static class GetTable extends AsyncTask<String, Void, JSONObject[]> {
		
		@Override
		protected JSONObject[] doInBackground(String... params) {
			// create an array of json objects that will be returned
			JSONObject[] jsonObjects = null;
			// Create the httpclient
			//HttpClient httpclient = new DefaultHttpClient();
			HttpClient httpclient = new MyHttpClient(null);
			// set timeout to 10 seconds
			final HttpParams httpparams = httpclient.getParams();
			HttpConnectionParams.setConnectionTimeout(httpparams, 10000);
			HttpConnectionParams.setSoTimeout(httpparams, 10000);
			// Prepare a request object
			Log.w("Constants - GetTable.execute()", params[0]);
			HttpGet httpget = new HttpGet(params[0]); 
			// Execute the request
			HttpResponse response;

			String errorString = null;
	
			try {
				
				Log.w("", "About to execute()");
				response = httpclient.execute(httpget);
				Log.w("", "Client was executed()");
	
				if(response.getStatusLine().getStatusCode() == 200){
					// Connection was established. Get the content. 
	
					HttpEntity entity = response.getEntity();
					// If the response does not enclose an entity, there is no need
					// to worry about connection release
	
					if (entity != null) {
						// A Simple JSON Response Read
						InputStream instream = entity.getContent();
	
						JSONArray jsonArray = new JSONArray(Constants.convertStreamToString(instream));
	
						// allocate space for the object array
						jsonObjects = new JSONObject[jsonArray.length()];
						for( int i = 0; i < jsonArray.length(); ++i) {
							jsonObjects[i] = new JSONObject(jsonArray.optString(i));
						}
	
						// Close the stream.
						instream.close();
					}
				}
				else {
					// code here for a response other than 200.  A response 200 means the webpage was ok
					// Other codes include 404 - not found, 301 - redirect etc...
					// Display the response line.
					errorString = "Unable to load page - " + response.getStatusLine();
				}
			} catch (IOException  ex) {
				// Connection was not established
				errorString = "Connection failed: " + ex.getMessage();
			} catch (JSONException ex) {
				// JSON errors
				errorString = "JSON failed: " + ex.getMessage();
			} finally { 
				if(errorString != null)
					Log.w("GetTable", errorString); 
			}
			
			return jsonObjects;
		}
	}
	
	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}
}
