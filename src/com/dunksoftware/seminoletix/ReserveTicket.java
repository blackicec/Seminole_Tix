package com.dunksoftware.seminoletix;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.util.EntityUtils;
import android.os.AsyncTask;
import android.util.Log;


// usage:
//
// ReserveTicket reserveticket = new ReserveTicket();
// reserveticket.execute("id of the game you want to reserve ticket for");
// String DidItWork = reserveticket.get();

public class ReserveTicket extends AsyncTask<String, Void, String> {

	@Override
	protected String doInBackground(String... params) {
	
		HttpClient httpclient = new MyHttpClient(null);
		
		// set the cookie
		((AbstractHttpClient) httpclient).setCookieStore(UserControl.mCookie);
		
		Log.w("ReserveTicket.execute()", params[0]);
		
		try {
			HttpPost httppost = new HttpPost(Constants.ReservedGamesAddress
					+ params[0]);
			
			HttpResponse response = httpclient.execute(httppost);
			
			String responseMessage = EntityUtils.toString(response.getEntity());
			Log.w("ReserveTicket.execute()", responseMessage);
			
			return responseMessage;
		} catch (UnsupportedEncodingException e) {
			Log.w("ReserveTicket.execute() error", e.getMessage());
		} catch (ClientProtocolException e) {
			Log.w("ReserveTicket.execute() error", e.getMessage());
		} catch (IOException e) {
			Log.w("ReserveTicket.execute() error", e.getMessage());
		}		
		
		return null;
	}
}
