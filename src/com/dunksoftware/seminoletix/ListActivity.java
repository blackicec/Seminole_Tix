package com.dunksoftware.seminoletix;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class ListActivity extends Activity {
	
	private Button bDetails;
	private Constants.GetTable mGetTable;
	private JSONObject[] Games = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
		mGetTable = new Constants.GetTable();
		mGetTable.execute(Constants.UsersAddress);
		try {
			Games = mGetTable.get();
			
			if(Games != null)
				for(int i = 0; i < Games.length; i++) {
					Log.w("List Activity", Games[i].getString("_id"));
				}
			else
				Log.w("List Activity", "Games is null");
			
			//do sh*t with the JSONObjects
			
		} catch(JSONException ex) {
			Log.w("List Activity - mGetTable.getString()", ex.getMessage());
		} catch(InterruptedException ex) {
			Log.w("List Activity - mGetTable.execute()", ex.getMessage());
		} catch(ExecutionException ex) {
			Log.w("List Activity - mGetTable.execute()", ex.getMessage());
		}	
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_list, menu);
		return true;
	}

	public void showDetails(View v) {
		// check which details button called it
		// open the corresponding details window
	}
}