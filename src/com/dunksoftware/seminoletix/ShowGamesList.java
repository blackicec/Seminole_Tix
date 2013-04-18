package com.dunksoftware.seminoletix;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ShowGamesList extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_games_list);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_games_list, menu);
		return true;
	}

}
