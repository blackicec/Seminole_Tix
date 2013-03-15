package com.dunksoftware.seminoletix;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

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
	private UserControl.Logout Logout;


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

						if( ((CheckBox)findViewById(R.id.UI_CheckRememberMe)).isChecked()) {
							Login = mUserControl.new Login(mUserResponse, mPassResponse, true);

							ShowMessage("Is selected", Toast.LENGTH_SHORT);
						}
						else
							Login = mUserControl.new Login(mUserResponse, mPassResponse, false);

						Login.execute();

						//ShowMessage("Error occurred with login.", Toast.LENGTH_LONG );

						try {
							JSONObject JSONresponse = new JSONObject(Login.get());

							// Send the user back to the login page.
							if( JSONresponse.getString("success").equals("true")) {

								startActivity(new Intent(getApplicationContext(), 
										ListActivity.class));
								
								ShowMessage(JSONresponse.toString(), Toast.LENGTH_LONG);

								/* Close the current activity, ensuring that this
								 *  SAME page cannot be reached via Back button, 
								 *  once a user has successfully registered. 
								 *  (Basically takes this page out of the "page history" )
								 */

								//finish();
							}
							/* if sever returns false on registration, clear the CardNumber
							 * and PIN field
							 */
							else {
								// Print out a success message to the user's UI
								ShowMessage( JSONresponse.getString("message"), Toast.LENGTH_LONG);
								
								editUsername.getText().clear();
								editPassword.getText().clear();
							}
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

		// Event handler for the Register button
		mRegisterBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent nextActivityIntent = 
						new Intent(getApplicationContext(), RegisterActivity.class);

				startActivity(nextActivityIntent);
			}
		});

		// logout button test
		((Button)(findViewById(R.id.bUI_logoutBtn))).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Logout = mUserControl.new Logout();
				
				Logout.execute();
				
				try {
					JSONObject json = new JSONObject(Logout.get());
					ShowMessage(json.toString(), Toast.LENGTH_LONG);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
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
