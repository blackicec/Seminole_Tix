package com.dunksoftware.seminoletix;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.os.AsyncTask;

public class UserControl {

	private static Object mLock = new Object();
	private static CookieStore mCookie = null;

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

			// TODO - check to see if Internet connection exists, if not return message. TAKE OUTSIDE
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

	public class Login extends AsyncTask<Void, Void, String> {

		private String mUserID,
		mPassword;

		private boolean mRemember_Me,
		mIsEmailAddress;

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
			DefaultHttpClient client = getNewHttpClient();
			HttpPost httppost = new HttpPost(Constants.LoginAddress);

			/*
			DefaultHttpClient mHttpClient = new DefaultHttpClient();
			BasicHttpContext mHttpContext = new BasicHttpContext();
			CookieStore mCookieStore      = new BasicCookieStore();        
			mHttpContext.setAttribute(ClientContext.COOKIE_STORE, mCookieStore);
			 */

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
				response = client.execute(httppost);

				responseMessage = EntityUtils.toString(response.getEntity());

				mCookie = client.getCookieStore();

				return mCookie.toString();
				//return responseMessage;
			}
			catch (IOException  ex) {
				// Connection was not established
				responseMessage = "Connection failed -> " + ex.getMessage();
			}			

			// return the status of the POST
			return responseMessage;
		}
	}

	public class MySSLSocketFactory extends SSLSocketFactory {
		SSLContext sslContext = SSLContext.getInstance("TLS");

		public MySSLSocketFactory(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
			super(truststore);

			TrustManager tm = new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
			};

			sslContext.init(null, new TrustManager[] { tm }, null);
		}

		@Override
		public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
			return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
		}

		@Override
		public Socket createSocket() throws IOException {
			return sslContext.getSocketFactory().createSocket();
		}
	}

	public DefaultHttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}
}
