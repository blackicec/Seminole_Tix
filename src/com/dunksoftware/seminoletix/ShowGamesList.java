package com.dunksoftware.seminoletix;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dunksoftware.seminoletix.ListActivity.AdditionDetailsListener;
import com.dunksoftware.seminoletix.UserControl.Logout;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
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

		// set up logout button
		((Button)findViewById(R.id.UI_LogoutBtn)).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						UserControl UC = new UserControl();
						UserControl.Logout logout = UC.new Logout();

						// begin the logout process
						logout.execute();

						// wait for the server's results
						try {
							JSONObject json = new JSONObject(logout.get());

							String message = json.getString("success");

							// check to see if user has successfully logged out
							if(message.equals("true")) {
								Toast.makeText(getApplicationContext(), 
										"You have been logged out.", Toast.LENGTH_LONG)
										.show();
							}
							else {
								Toast.makeText(getApplicationContext(), 
										"You are not logged in.", Toast.LENGTH_LONG)
										.show();
							}
							
							finish();
							
							startActivity(new Intent(getApplicationContext(),
									LoginActivity.class));

						} catch (JSONException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
					}
				});

		welcomeMsg.setText(ListActivity.userName);

		// get the list of current reserved games
		GetCurrentUserInfo userInfo = new GetCurrentUserInfo();
		userInfo.execute();

		try {
			String serverResponse = userInfo.get();

			mUserObject = new JSONObject(serverResponse);
			mGameIdTags = mUserObject.getJSONArray("tickets");

			// get full list of games to compare against
			String games = new GetListOfGames().execute().get();
			JSONArray gamesArray = new JSONArray(games);

			//Toast.makeText(this, games, Toast.LENGTH_LONG).show();

			//Toast.makeText(this, serverResponse, Toast.LENGTH_LONG).show();

			for(int i = 0; i < mGameIdTags.length(); i++) {
				for(int j = 0; j < gamesArray.length(); ++j)
					if(mGameIdTags.getJSONObject(i).getString("game_id")
							.equals(gamesArray.getJSONObject(j).getString("_id"))) {

						JSONObject currentObject = gamesArray.getJSONObject(j);

						TableRow gameTableRow = new TableRow(this);
						LinearLayout list = new LinearLayout(this);

						TextView[] info = new TextView[5];

						// set the list to a top down look
						list.setOrientation(LinearLayout.VERTICAL);


						info[0] = new TextView(this);
						info[0].setText("\t\tSport:\t\t" + currentObject.getString("sport"));

						list.addView(info[0]);

						info[1] = new TextView(this);

						//Format the date so that it is appropriate
						String dateTime = currentObject.getString("date");

						String date = FormatDate(dateTime);

						info[1].setText("\t\tGame Date:\t\t" + date);

						list.addView(info[1]);

						info[2] = new TextView(this);
						info[2].setText("\t\tHome Team:\t\t" + currentObject.getJSONObject("teams")
								.getString("home").toUpperCase());

						list.addView(info[2]);

						info[3] = new TextView(this);
						info[3].setText("\t\tAgainst:\t\t" + currentObject.getJSONObject("teams")
								.getString("away").toUpperCase());

						list.addView(info[3]);
						
						info[4] = new TextView(this);
						info[4].setText("\t\tTicket Confirmation #:\t\t" + 
								mGameIdTags.getJSONObject(i)
									.getString("confirmationId"));

						list.addView(info[4]);

						list.setPadding(0, 5, 0, 20);
						gameTableRow.addView(list);
						gameTableRow.setBackgroundResource(R.drawable.img_gloss_background);

						mainTable.addView(gameTableRow);
					}
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	String FormatDate(String Date) {
		String[] splits = Date.split("T");

		splits = splits[0].split("-");

		Date d = new Date(Integer.parseInt(splits[0]) - 1900, 
				Integer.parseInt(splits[1]), Integer.parseInt(splits[2])); 
		DateFormat newDate = DateFormat.getDateInstance(DateFormat.LONG); 
		newDate.format(d);

		return DateFormat.getDateInstance(DateFormat.LONG).format(d);
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

	class GetListOfGames extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// Create a new HttpClient and Post Header
			MyHttpClient client = new MyHttpClient(null);

			//sets cookie
			client.setCookieStore(UserControl.mCookie);

			// Prepare a request object
			HttpGet httpget = new HttpGet(Constants.GamesAddress); 

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
