package com.dunksoftware.seminoletix;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class RegisterActivity extends Activity {

	private EditText EditCardNumber,
		EditPIN,
		EditEmail,
		EditPassword,
		EditConfirmPass;
	
	private TextView ErrorMessage;
	
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
			
			@Override
			public void onClick(View arg0) {
				if( verifyEntries() ) {
					// clear error message
					ErrorMessage.setText("");
					
					String confirmPass = EditConfirmPass.getText().toString();
					// compare two passwords for equality
					if( !EditPassword.getText().toString().equals(confirmPass) ) {
						formOK = false;
						
						ErrorMessage.setText("Password entries do not match");
						
						EditPassword.setText("");
						EditConfirmPass.setText("");
					}
					
					String email = EditEmail.getText().toString();
					// check email for format (contains '@', .com)
					if( !email.contains("@") || !email.endsWith(".com")) {
						formOK = false;
						
						ErrorMessage.setText("Expected email format: \"user@server_name.com\"");
					}
					
					// check card number length
					if(EditCardNumber.getText().length() != 
							Constants.CARD_NUMBER_LENGTH) {
						formOK = false;
						
						ErrorMessage.setText("Incorrect length on FSU Card Number");
					}
					
					if( formOK ) {
						// then POST to site
					}
				}
				else {
					ErrorMessage.setText("Error: All fields are required to have a value.");
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

}
