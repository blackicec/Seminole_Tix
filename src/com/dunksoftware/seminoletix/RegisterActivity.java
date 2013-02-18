package com.dunksoftware.seminoletix;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class RegisterActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		
	}
	
	/***
	 * This function simply checks each EditText box to ensure that
	 * the element does not contain an empty string
	 * @return false -> if at least one box is empty;
	 * true -> is all box entries have some data
	 */
	private boolean verifyEntries() {
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}

}
