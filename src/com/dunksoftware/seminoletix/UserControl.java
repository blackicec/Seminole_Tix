package com.dunksoftware.seminoletix;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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


public class UserControl {

	/***
	 * 
	 * @author blackice
	 *
	 */
	public static class RegisterUser extends AsyncTask<Void, Void, String> {
		private String CardNumber,
		PIN,
		Email,
		Password;

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
		}

		@Override
		protected String doInBackground(Void... arg0) {
			String returnValue = "NOOP";

			JSONObject[] jsonObjects;

			HttpResponse response = null;

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(Constants.UsersAddress);


			returnValue += " -> Made it to try, ";
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

			nameValuePairs.add(new BasicNameValuePair("cardNum", CardNumber));
			nameValuePairs.add(new BasicNameValuePair("pin", PIN));
			nameValuePairs.add(new BasicNameValuePair("email", Email));
			nameValuePairs.add(new BasicNameValuePair("password", Password));
			
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				response = httpclient.execute(httppost);	

				if(response.getStatusLine().getStatusCode() == 200){
					// Connection was established. Get the content. 

					HttpEntity entity = response.getEntity();
					// If the response does not enclose an entity, there is no need
					// to worry about connection release

					if (entity != null) {
						// A Simple JSON Response Read
						InputStream instream = entity.getContent();

						JSONArray jsonArray;
						
						try {
							jsonArray = new JSONArray(Constants.convertStreamToString(instream));

							// allocate space for the object array
							jsonObjects = new JSONObject[jsonArray.length()];
							for( int i = 0; i < jsonArray.length(); ++i) {
								jsonObjects[i] = new JSONObject(jsonArray.optString(i));
							}
							returnValue = "Good";
							return returnValue;
						}

						catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						// Close the stream.
						instream.close();
					}
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

				/*if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					returnValue += " Entity not NULL";
					try {
						returnValue += " Made it to try 2";
						JSONArray jsonArray = new JSONArray(Constants.convertStreamToString(instream));
						returnValue += "At return 1";

						return jsonArray.toString();
						//returnValue = jsonArray.toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}*/

				return returnValue;

			}
			catch (IOException  ex) {
				// Connection was not established
				returnValue = "Connection failed; " + ex.getMessage();
			}
			
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
