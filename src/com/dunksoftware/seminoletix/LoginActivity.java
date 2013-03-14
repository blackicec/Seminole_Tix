package com.dunksoftware.seminoletix;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	public static final String PREFS_NAME = "TixSettingsFile";
	public static final String USER_NAME = "username";
	public static final String ERROR_STRING = "error";

	private EditText editUsername,
	editPassword;

	private Button mRegisterBtn,
	mLoginBtn;

	private String mUserResponse,
	mPassResponse;

	private SharedPreferences mSettings;

	private UserControl mUserControl;
	private UserControl.Login Login;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Shared preferences still under construction
		mSettings = getSharedPreferences(PREFS_NAME, 0);
		mSettings.getString(USER_NAME, ERROR_STRING);
		
		mUserControl = new UserControl();

		// link widgets to variables 
		editUsername = (EditText)findViewById(R.id.UI_EditFSUID);
		editPassword = (EditText)findViewById(R.id.UI_EditFSUPass);

		mRegisterBtn = (Button)findViewById(R.id.UI_registerBtn);
		mLoginBtn = (Button)findViewById(R.id.UI_signinBtn);

		// set anonymous onclick listeners for registration and login buttons
		mLoginBtn.setOnClickListener(
				new OnClickListener() {

					// email, cardNum, password, remember_me
					@Override
					public void onClick(View v) {

						mUserResponse = editUsername.getText().toString();
						mPassResponse = editPassword.getText().toString();

						if( (boolean)findViewById(R.id.UI_CheckRememberMe).isSelected())
							Login = mUserControl.new Login(mUserResponse, mPassResponse, true);
						else
							Login = mUserControl.new Login(mUserResponse, mPassResponse, false);

						Login.execute();

						String result = "Error Occurred";
						try {
							result = Login.get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						ShowMessage( result, Toast.LENGTH_LONG );
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

	void ShowMessage(String message, int length) {
		Toast.makeText(getApplicationContext(), message, length).show();
	}
}
