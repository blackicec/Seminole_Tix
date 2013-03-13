package com.dunksoftware.seminoletix;
import java.io.IOException;
import java.net.CookieStore;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;


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

		@Override
		protected String doInBackground(Void... arg0) {

			/* First we want to check to make sure that the given PIN matches the
			 * Pun number inside of the database 
			 */

			// TODO - check to see if internet connection exists, if not return message. TAKE OUTSIDE
			String responseMessage = null;

			HttpResponse response = null;

			// Create a new HttpClient and Post Header
			HttpClient httpclient = new MyHttpClient(null);
			HttpPost httppost = new HttpPost(Constants.UsersAddress);

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

				responseMessage = EntityUtils.toString(response.getEntity());

				return responseMessage;
			}
			catch (IOException  ex) {
				// Connection was not established
				responseMessage = "Connection failed; " + ex.getMessage();
			}			

			// return the status of the POST
			return responseMessage;
		}
	}

	public static class Login extends AsyncTask<Void, Void, String> {
		
		private String mUserID,
			mPassword;
		private boolean mRemember_Me,
			mIsEmailAddress;
		
		private Object mLock = new Object();
		private CookieStore mCookie = null;
		
		public Login( String ID, String passW, boolean rememberMe ) {
			mUserID = ID;
			mPassword = passW;
			mRemember_Me = rememberMe;
			
			mIsEmailAddress = ID.contains("@") ? true : false;
		}

		@Override // email, cardNum, password, remember_me
		protected String doInBackground(Void... params) {
			
			// TODO - check to see if Internet connection exists, if not return message. TAKE OUTSIDE
						String responseMessage = null;

						HttpResponse response = null;

						// Create a new HttpClient and Post Header
						HttpClient httpclient = new MyHttpClient(null);
						HttpPost httppost = new HttpPost(Constants.LoginAddress);

						// Add your data
						List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);

						if( mIsEmailAddress )
							nameValuePairs.add(new BasicNameValuePair("email", mUserID));
						else
							nameValuePairs.add(new BasicNameValuePair("cardNum", mUserID));
						
						nameValuePairs.add(new BasicNameValuePair("password", mPassword));
						
						if( mRemember_Me )
							nameValuePairs.add(new BasicNameValuePair("remember_me", "TRUE"));

						try {
							httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

							// Execute HTTP Post Request
							response = httpclient.execute(httppost);

							responseMessage = EntityUtils.toString(response.getEntity());

							return responseMessage;
						}
						catch (IOException  ex) {
							// Connection was not established
							responseMessage = "Connection failed; " + ex.getMessage();
						}			

						// return the status of the POST
						return responseMessage;
			
		}
		
	}
}
