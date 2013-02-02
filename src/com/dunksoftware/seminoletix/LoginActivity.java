package com.dunksoftware.seminoletix;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	EditText editUsername,
		editPassword;
	
	String mUserResponse,
		mPassResponse;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		editUsername = (EditText)findViewById(R.id.UI_EditFSUID);
		editPassword = (EditText)findViewById(R.id.UI_EditFSUPass);
		
		/* 
		 * Another dirty shortcut (One time use variable and
		 * One time use event handler
		 */
		((Button)findViewById(R.id.UI_CredSubmit)).setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean NameResult,
					PassResult, blah;
				
				mUserResponse = editUsername.getText().toString();
				mPassResponse = editPassword.getText().toString();
				
				NameResult = mUserResponse.equals("seminole");
				PassResult = mPassResponse.equals("dunk");
				
				
				if( NameResult && PassResult ) {
					((TextView)findViewById(R.id.UI_TextResult)).
						setText("Successful Login!!!!");
				}
				
				else {
					((TextView)findViewById(R.id.UI_TextResult)).
						setText("Failed to Log into the database");
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}

}
