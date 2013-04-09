package com.dunksoftware.seminoletix;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class ListActivity extends Activity {

	private Constants.GetTable mGetTable;
	private JSONObject[] GameObjects = null;

	GetGames games;
	String response = "ERROR!";

	private TextView EditDateText,
	EditOpponentText,
	EditSportText;
	
	TableLayout mainTable;

	final int MESSAGE = 200, 
			DETAILS_POPUP = 250;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		mainTable = (TableLayout)findViewById(R.id.UI_MainTableLayout);
		
		games = new GetGames();

		try {
			games.execute();
			response=games.get();
			
			// Show the pop-up box (for display testing)
			showDialog(MESSAGE);
			
			JSONArray gamesArray=new JSONArray(response);
			
			// Allocate space for all JSON objects embedded in the JSON array
			GameObjects = new JSONObject[gamesArray.length()];

			// Transfer each object in this JSONArray into its own object
			for(int i=0;i<gamesArray.length();i++)
				GameObjects[i] = gamesArray.getJSONObject(i);


			/*
			 * Value keys to get from each JSONObject:full(boolean), 
			 * availableDate, date, seats, seatsLeft, sport,
			 * teams:object{home, away}.
			 * 
			 * Notice how the date format in the JSONObject's format is
			 * 
			 * YYYY-MM-DD"T(time)"HH(in military time)-MM-SS.000Z
			 * This is not user friendly, so it will have to be formatted in
			 * this code. Break the Date string at T first. There is a function
			 * called "split" in the string class that does this task. Then
			 * break the date and maybe show an actual month in this formatting.
			 * Time "T" should be done with the same process, split.
			 */
			

			/*EditDateText.setText("availabledate");
			EditOpponentText.setText("");
			EditSportText.setText("sport");*/
			
			//Games = mGetTable.get();

			for(int i = 0; i < gamesArray.length(); i++) {
				TableRow gameTableRow = new TableRow(this);
				LinearLayout list = new LinearLayout(this);
				Button detailsButton = new Button(this);
				TextView[] info = new TextView[4];
				
				// set the list to a top down look
				list.setOrientation(LinearLayout.VERTICAL);
				detailsButton.setText("More Details");
				
				info[0] = new TextView(this);
				info[0].setText("Sport:\t\t" + GameObjects[i].getString("sport"));
				
				list.addView(info[0]);
				
				info[1] = new TextView(this);
				//Format the date so that it is appropriate
				String[] parsedDate;
				String dateTime = GameObjects[i].getString("availableDate");
				parsedDate = dateTime.split("T");
				String date = parsedDate[0];
				
				info[1].setText("Game Date:\t\t" + date);
				
				list.addView(info[1]);
				
				info[2] = new TextView(this);
				info[2].setText("Home:\t\t" + GameObjects[i].getJSONObject("teams")
													.getString("home").toUpperCase());
				
				list.addView(info[2]);
				
				info[3] = new TextView(this);
				info[3].setText("Opponent:\t\t" + GameObjects[i].getJSONObject("teams")
						.getString("away").toUpperCase());
				
				list.addView(info[3]);
				
				// add the button to display details for each game
				// might have to add tag to button
				list.addView(detailsButton);
				
				list.setPadding(0, 5, 0, 20);
				gameTableRow.addView(list);
				
				mainTable.addView(gameTableRow);
			}
		} catch(InterruptedException ex) {
			Log.w("List Activity - mGetTable.execute()", ex.getMessage());
		} catch(ExecutionException ex) {
			Log.w("List Activity - mGetTable.execute()", ex.getMessage());
			//} //catch (JSONException e) {
			// TODO Auto-generated catch block
			//	e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog.Builder builder;

		switch( id ) {

		case MESSAGE: {
			builder = new AlertDialog.
					Builder(this);

			builder.setCancelable(false).setTitle("Page Result").
			setMessage(response).setNeutralButton("Close", 
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});

			builder.create().show();
			break;
		}
		}
		return super.onCreateDialog(id);
	}

	public void showDetails(View v) {
		// check which details button called it
		// open the corresponding details window
	}

	class GetGames extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {

			// Create a new HttpClient and Post Header
			MyHttpClient client=new MyHttpClient(null);

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
