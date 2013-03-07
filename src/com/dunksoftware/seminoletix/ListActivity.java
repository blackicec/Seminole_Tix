package com.dunksoftware.seminoletix;

import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;


import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ListActivity extends Activity {
	
	private Constants.GetTable mGetTable;
	private JSONObject[] Games = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		mGetTable = new Constants.GetTable();
		mGetTable.execute(Constants.GamesAddress);
		try {
			Games = mGetTable.get();
			
			if(Games == null)
				Log.w("List Activity", "Games is null");
			
			// pulling of information works fine
			// Games[index].getString("FIELD_NAME")
			
			
			class GetGames extends AsyncTask<Void, Void, JSONObject[]> {

				@Override
				protected JSONObject[] doInBackground(Void... params) {

					// create an array of json objects that will be returned
					JSONObject[] jsonObjects = null;

					// Create the httpclient
					HttpClient httpclient = new DefaultHttpClient();

					// Prepare a request object
					HttpGet httpget = new HttpGet(Constants.GamesAddress); 

					// Execute the request
					HttpResponse response;

					// return string
					String returnString = null;

					try {

						// Open the web page.
						response = httpclient.execute(httpget);

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
							returnString = "Unable to load page - " + response.getStatusLine();
						}
					}
					catch (IOException  ex) {
						// Connection was not established
						returnString = "Connection failed; " + ex.getMessage();
					}
					catch (JSONException ex){
						// JSON errors
						returnString = "JSON failed; " + ex.getMessage();
					}

					return jsonObjects;
				}
			}

			
			
			
			
			for(int i = 0; i < Games.length; i++) {
				Log.w("Games", Games[i].getString("_id"));
				Log.w("Games", Games[i].getString("availableDate"));
				Log.w("Games", Games[i].getString("full"));
				Log.w("Games", Games[i].getString("seats"));
				Log.w("Games", Games[i].getString("seatsLeft"));
				Log.w("Games", Games[i].getString("sport"));
				Log.w("Games", Games[i].getString("teams[]"));
			}
			
		} catch(JSONException ex) {
			Log.w("List Activity - mGetTable.getString()", ex.getMessage());
		} catch(InterruptedException ex) {
			Log.w("List Activity - mGetTable.execute()", ex.getMessage());
		} catch(ExecutionException ex) {
			Log.w("List Activity - mGetTable.execute()", ex.getMessage());
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}

	public void showDetails(View v) {
		// check which details button called it
		// open the corresponding details window
	}
}
