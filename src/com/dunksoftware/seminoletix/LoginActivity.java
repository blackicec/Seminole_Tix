package com.dunksoftware.seminoletix;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class LoginActivity extends Activity {

	public static final String PREFS_NAME = "TixSettingsFile";
	public static final String USER_NAME = "username";
	public static final String ERROR_STRING = "error";
	
	private JSONObject[] jsonObjects;
	
	private EditText editUsername,
		editPassword;
	
	private Button mRegisterBtn,
		mLoginBtn;
	
	private String mUserResponse,
		mPassResponse;
	
	private UserControl.GetUsers mGetUsers;
	
	private SharedPreferences mSettings;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		// Shared preferences still under construction
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		mSettings.getString(USER_NAME, ERROR_STRING);
		
		// link widgets to variables 
		editUsername = (EditText)findViewById(R.id.UI_EditFSUID);
		editPassword = (EditText)findViewById(R.id.UI_EditFSUPass);
		
		mRegisterBtn = (Button)findViewById(R.id.UI_registerBtn);
		mLoginBtn = (Button)findViewById(R.id.UI_signinBtn);
		
		mGetUsers = new UserControl.GetUsers();
		
		// set anonymous onclick listeners for registration and login buttons
		mLoginBtn.setOnClickListener(
				new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				boolean NameResult,
					PassResult;
				
				mUserResponse = editUsername.getText().toString();
				mPassResponse = editPassword.getText().toString();
				
				TextView message = (TextView)findViewById(R.id.UI_Message);		
				
				mGetUsers.execute();
				try {
					// Retrieve the array of JSON objects
					jsonObjects = mGetUsers.get();
					
					// attempt to validate entries
					boolean response = validate(jsonObjects, mUserResponse, "1234");
					
					if( response )
						message.setText("True");
					else
						message.setText("False");
					
					//registerUser = new UserControl.RegisterUser();
					//registerUser.execute();
					
					//message.setText(registerUser.get());
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		mRegisterBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent nextActivityIntent = 
						new Intent(getApplicationContext(), RegisterActivity.class);
				
				startActivity(nextActivityIntent);
			}
		});
	} // End of onCreate function
	
	private boolean validate( JSONObject[] jsonObjects, String id, String pin ) {
		
		for( int i = 0; i < jsonObjects.length; ++i ) {
			try {
				if(id.equals(jsonObjects[i].getString("_id")))
					return true;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return false;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		SharedPreferences.Editor editor = mSettings.edit();
		
		editor.putString(USER_NAME, mUserResponse);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_login, menu);
		return true;
	}
	
	/* This section below will be designed to validate login info */
}
