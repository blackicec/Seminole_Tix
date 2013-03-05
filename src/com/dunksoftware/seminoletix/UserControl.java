package com.dunksoftware.seminoletix;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.support.v4.content.AsyncTaskLoader;


public class UserControl {

	/***
	 * @author blackice
	 */
	public static class RegisterUser extends AsyncTask<Void, Void, String> {

		private AsyncTask<String, Void, JSONObject[]> asyncAccounts;

		private String CardNumber,
		PIN,
		Email,
		Password;
		
		boolean credentialsOK;

		/***
		 * 
		 * @param card -> FSU card number to be registered
		 * @param pin -> required FSU pin
		 * @param email -> FSU outlook email address
		 * @param password -> User's password of choice
		 */
		public RegisterUser(String card, String pin, String email,
				String password) {

			CardNumber = card;
			PIN = pin;
			Email = email;
			Password = password;
			
			// first assume that given credentials are correct
			credentialsOK = true;
		}
		
		public void register() {
			asyncAccounts = new Constants.GetTable();
			asyncAccounts.execute( Constants.UsersAddress );

			// holds the return value for the list of users returned from GetTable()
			JSONObject[] JSONusers;

			try {
				JSONusers = asyncAccounts.get();

				for(int i = 0; i < JSONusers.length; ++i) {
					try {
						if(JSONusers[i].get("cardNum").equals(CardNumber)) {
							if( !JSONusers[i].get("pin").equals(PIN) ) {
								credentialsOK = false;
							}
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				// The POST will execute iff the credentialsOK is still set to TRUE
				this.execute();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(Void... arg0) {

			/* First we want to check to make sure that the given PIN matches the
			 * Pun number inside of the database 
			 */

			//TODO - check to see if internet connection exists, if not return message.
			String returnValue = "Successful Registration";

			HttpResponse response = null;

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.UsersAddress);

			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("cardNum", CardNumber)); 
			nameValuePairs.add(new BasicNameValuePair("pin", PIN));
			nameValuePairs.add(new BasicNameValuePair("email", Email));
			nameValuePairs.add(new BasicNameValuePair("password", Password));

			if( credentialsOK ) {
				try { //  will be change later (set flag in if statement, handle messages outside
					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

					// Execute HTTP Post Request
					response = httpclient.execute(httppost);	

					if(response.getStatusLine().getStatusCode() == 200){
						// Connection was established. Get the content. 

						HttpEntity entity = response.getEntity();
						// If the response does not enclose an entity, there is no need
						// to worry about connection release

					}
					else {
						// code here for a response other than 200.  A response 200 means the webpage was ok
						// Other codes include 404 - not found, 301 - redirect etc...
						// Display the response line.
						returnValue = "Unable to load page - " + "Code: " + 
								Integer.toString(response.getStatusLine().getStatusCode()) +
								response.getStatusLine();
						return returnValue;
					}

					return returnValue;

				}
				catch (IOException  ex) {
					// Connection was not established
					returnValue = "Connection failed; " + ex.getMessage();
				}
			}
			else
				returnValue = Constants.IncorrectPIN_msg;

			// return the status of the POST
			return returnValue;
		}
	}

	/***
	 * comment (null return check)
	 * @author blackice
	 *
	 */
	public static class GetUsers extends AsyncTask<Void, Void, JSONObject[]> {

		@Override
		protected JSONObject[] doInBackground(Void... params) {

			// create an array of json objects that will be returned
			JSONObject[] jsonObjects = null;

			// Create the httpclient
			HttpClient httpclient = new DefaultHttpClient();

			// Prepare a request object
			HttpGet httpget = new HttpGet(Constants.UsersAddress); 

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

}
