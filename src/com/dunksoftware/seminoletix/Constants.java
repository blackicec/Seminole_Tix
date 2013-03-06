package com.dunksoftware.seminoletix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class Constants {
	
	public static String UsersAddress = "http://chi.erdaniels.com/users/";
	public static String GamesAddress = "http://chi.erdaniels.com/games/";
	
	public static String Success_msg = "Successful Registration";
	
	public static int CARD_NUMBER_LENGTH = 12;
	
	public static class GetTable extends AsyncTask<String, Void, JSONObject[]> {
		
		@Override
		protected JSONObject[] doInBackground(String... params) {
			// create an array of json objects that will be returned
			JSONObject[] jsonObjects = null;
			// Create the httpclient
			HttpClient httpclient = new DefaultHttpClient();
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
/*
	private HttpClient sslClient(HttpClient client) {
	    try {
	        X509TrustManager tm = new X509TrustManager() { 
	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	        };
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLSocketFactory ssf = new MySSLSocketFactory(ctx);
	        ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
	        ClientConnectionManager ccm = client.getConnectionManager();
	        SchemeRegistry sr = ccm.getSchemeRegistry();
	        sr.register(new Scheme("https", ssf, 443));
	        return new DefaultHttpClient(ccm, client.getParams());
	    } catch (Exception ex) {
	        return null;
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

	     public MySSLSocketFactory(SSLContext context) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
	        super(null);
	        sslContext = context;
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
	*/
}

