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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;


import android.os.AsyncTask;


public class UserControl {

	/***
	 * 
	 * @author blackice
	 *
	 */
	public static class RegisterUser extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {
			
			 // Create a new HttpClient and Post Header
		    HttpClient httpclient = new DefaultHttpClient();
		    HttpPost httppost = new HttpPost(Constants.UsersAddress);

		    try {
		        // Add your data
		        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        
		        nameValuePairs.add(new BasicNameValuePair("cardNum", "86"));
		        nameValuePairs.add(new BasicNameValuePair("pin", "1234"));
		        nameValuePairs.add(new BasicNameValuePair("email", "ed10@my.fsu.edu"));
		        nameValuePairs.add(new BasicNameValuePair("password", "1234"));
		        
		        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

		        // Execute HTTP Post Request
		        HttpResponse response = httpclient.execute(httppost);
				
		        // If the response does not enclose an entity, there is no need
				// to worry about connection release
		        HttpEntity entity = response.getEntity();

				if (entity != null) {
					// A Simple JSON Response Read
					InputStream instream = entity.getContent();
					
					try {
						JSONArray jsonArray = new JSONArray(Constants.convertStreamToString(instream));
						return jsonArray.toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
		        
		        return Integer.toString(response.getStatusLine().getStatusCode());
		        
		    } catch (ClientProtocolException e) {
		        // TODO Auto-generated catch block
		    } catch (IOException e) {
		        // TODO Auto-generated catch block
		    }
			return null;
		}
		
	}
	
}
