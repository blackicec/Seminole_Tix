package com.dunksoftware.seminoletix;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dunksoftware.seminoletix.ListActivity.AdditionDetailsListener;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ShowGamesList extends Activity {
	
	JSONObject mUserObject;
	JSONArray mGameIdTags;
	
	private TableLayout mainTable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_games_list);
		
		TextView welcomeMsg = (TextView)findViewById(R.id.UI_GreetingText);
		mainTable = (TableLayout)findViewById(R.id.UI_MainTableLayout);
		
		welcomeMsg.setText(ListActivity.userName);
		
		// get the list of current reserved games
		GetCurrentUserInfo userInfo = new GetCurrentUserInfo();
		userInfo.execute();
		
		try {
			String serverResponse = userInfo.get();
			
			mUserObject = new JSONObject(serverResponse);
			mGameIdTags = mUserObject.getJSONArray("tickets");
			
			Toast.makeText(this, serverResponse, Toast.LENGTH_LONG).show();

			for(int i = 0; i < mGameIdTags.length(); i++) {
				TableRow gameTableRow = new TableRow(this);
				LinearLayout list = new LinearLayout(this);

				TextView[] info = new TextView[4];

				// set the list to a top down look
				list.setOrientation(LinearLayout.VERTICAL);


				info[0] = new TextView(this);
				list.addView(info[0]);

				info[1] = new TextView(this);
				list.addView(info[1]);

				info[2] = new TextView(this);
				list.addView(info[2]);

				info[3] = new TextView(this);
				list.addView(info[3]);

				list.setPadding(0, 5, 0, 20);
				gameTableRow.addView(list);
				gameTableRow.setBackgroundResource(R.drawable.img_gloss_background);

				mainTable.addView(gameTableRow);
			}
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	class GetCurrentUserInfo extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			// Create a new HttpClient and Post Header
			MyHttpClient client = new MyHttpClient(null);

			//sets cookie
			client.setCookieStore(UserControl.mCookie);
			
			// Prepare a request object
			HttpGet httpget = new HttpGet(Constants.CurrentUserAddress); 

			// Execute the request
			HttpResponse response=null;

			// return string
			String returnString = null;

			try {
				// Open the web page.
				response = client.execute(httpget);
				returnString = EntityUtils.toString(response.getEntity());

			}
			catch (IOException  ex) {
				// Connection was not established
				returnString = "Connection failed; " + ex.getMessage();
			}
			return returnString;
		}
	}

}
