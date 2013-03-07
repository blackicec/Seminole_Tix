package com.dunksoftware.seminoletix;

import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity {

	private EditText EditCardNumber,
	EditPIN,
	EditEmail,
	EditPassword,
	EditConfirmPass;

	private String CardNumber,
	PIN,
	Email,
	Password;

	private TextView ErrorMessage;

	// This variable handles user registration after form validation
	private UserControl.RegisterUser registerUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// Link all UI widgets to reference variables
		EditCardNumber = (EditText)findViewById(R.id.UI_EditCardNum);
		EditPIN = (EditText)findViewById(R.id.UI_EditPIN);
		EditEmail = (EditText)findViewById(R.id.UI_EditEmail);
		EditPassword = (EditText)findViewById(R.id.UI_EditPassword);
		EditConfirmPass = (EditText)findViewById(R.id.UI_EditConfirmPassword);

		// link message box to output error message
		ErrorMessage = (TextView)findViewById(R.id.UI_TextMessage);

		// Listener for submit button. Validates form data
		((Button)findViewById(R.id.UI_ButtonConfirm)).setOnClickListener(
				new OnClickListener() {

					boolean formOK = true;
					String errorMessage = "";
					Intent LoginIntent;

					@Override
					public void onClick(View arg0) {

						// reset the valid form flag
						formOK = true;

						if( verifyEntries() ) {

							// clear error message
							ErrorMessage.setText("");	

							// compare two passwords for equality
							String confirmPass = EditConfirmPass.getText().toString();
							if( !EditPassword.getText().toString().equals(confirmPass) ) {
								formOK = false;

								errorMessage = "Password entries do not match";

								EditPassword.setText("");
								EditConfirmPass.setText("");
							}

							// check email for format (contains '@', .com)
							String email = EditEmail.getText().toString();
							if( !email.contains("@")) {
								formOK = false;

								errorMessage = "Expected email format: \"user@server_name.com\""; 
							}

							// check card number length
							// Commented out for testing purposes only
							/*
							if(EditCardNumber.getText().length() != 
									Constants.CARD_NUMBER_LENGTH) {
								formOK = false;

								errorMessage = "Incorrect length on FSU Card Number";
							} */

							if( formOK ) {
								// capture data from the form
								CardNumber = EditCardNumber.getText().toString();
								PIN = EditPIN.getText().toString();
								Email = EditEmail.getText().toString();
								Password = EditPassword.getText().toString();

								// . . . then POST to site
								registerUser = new UserControl.RegisterUser(CardNumber, PIN, Email, Password);
								registerUser.execute();

								try {
									JSONObject JSONresponse = new JSONObject(registerUser.get());

									
									//JSONException: no value for messages
									//
									String successMsg = JSONresponse.get("messages").toString();
									//
									//
									
									ShowMessage(successMsg, Toast.LENGTH_LONG);

									//ShowMessage(JSONresponse.getString("success"), Toast.LENGTH_LONG);


									// Send the user back to the login page.
									if( JSONresponse.getString("success").equals("true")) {
										
										// Print out a success message to the user's UI
										ShowMessage(Constants.Success_msg, Toast.LENGTH_LONG);
										
										LoginIntent = new Intent( getApplicationContext(), 
												LoginActivity.class);
										startActivity(LoginIntent);

										/* Close the current activity, ensuring that this
										 *  SAME page cannot be reached via Back button, 
										 *  once a user has successfully registered. 
										 *  (Basically takes this page out of the "page history" )
										 */
										
										finish();
									}
									/* if sever returns false on registration, clear the CardNumber
									 * and PIN field
									 */
									else {
										// Print out a success message to the user's UI
										ShowMessage( JSONresponse.getString("message"), Toast.LENGTH_LONG);
										
										EditCardNumber.getText().clear();
										EditPIN.getText().clear();
									}
								} catch (InterruptedException e) {
									e.printStackTrace();
								} catch (ExecutionException e) {
									e.printStackTrace();
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							else {
								// Display current problem with the form
								ShowMessage(errorMessage, Toast.LENGTH_SHORT);
							}
						}
						else {
							ShowMessage("All fields are require a value.", 
									Toast.LENGTH_LONG);
						}
					}
				});

		// Listener for clear button. Simply clears out all of the form data
		((Button)findViewById(R.id.UI_ButtonClear)).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						EditCardNumber.setText("");
						EditPIN.setText("");
						EditEmail.setText("");
						EditPassword.setText("");
						EditConfirmPass.setText("");

						// return focus to the first edittext(CardNumber)
						EditCardNumber.requestFocus();

						// clear error message
						ErrorMessage.setText("");
					}
				});

	}

	/***
	 * This function simply checks each EditText box to ensure that
	 * the element does not contain an empty string
	 * @return false -> if at least one box is empty;
	 * true -> is all box entries have some data
	 */
	private boolean verifyEntries() {
		if(EditCardNumber.getText().length() <= 0)
			return false;
		if(EditPIN.getText().length() <= 0)
			return false;
		if(EditEmail.getText().length() <= 0)
			return false;
		if(EditPassword.getText().length() <= 0)
			return false;
		if(EditConfirmPass.getText().length() <= 0)
			return false;


		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_register, menu);
		return true;
	}

	void ShowMessage(String message, int length) {
		Toast.makeText(getApplicationContext(), message, length).show();
	}
}
