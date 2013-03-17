package com.dunksoftware.seminoletix;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class SplashScreen extends Activity {

	private ImageView SeminoleFaceImage,
	SeminoleTitleImage;

	public static final int NO_CONNECTION_DIALOG = 80;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);

		if( !Online() ) {

			showDialog(NO_CONNECTION_DIALOG);
		}
		else {

			SeminoleFaceImage = (ImageView)findViewById(R.id.UI_SeminoleFaceImage);
			SeminoleTitleImage = (ImageView)findViewById(R.id.UI_SeminoletTitleImage);

			// Change the background color to white
			RelativeLayout rel = (RelativeLayout)findViewById(R.id.SplashMainLayout);
			rel.setBackgroundColor(Color.WHITE);

			final Handler timer = new Handler();

			final Timer scheduleTimer = new Timer();

			TimerTask ChangeTimer = new TimerTask() {
				int i = 255;

				@Override
				public void run() {
					timer.post(new Runnable() {

						@Override
						public void run() {
							/* When i reaches negative, the image's opacity will
							 * return to full. This prevents i from becoming negative 
							 */
							if( i <= 0)	i = 0;

							changeOpacity(i);
						}
					});

					if( i <= 0) {
						// clear all tasks on the scheduler
						scheduleTimer.cancel();

						// and ensure that images or completely transparent
						timer.post(new Runnable() {

							@Override
							public void run() {
								changeOpacity(0);

							}
						});

						startActivity(new Intent(getApplicationContext(), 
								LoginActivity.class));

						/* close this activity for good (remove it from the 
						 * bread crumb trail 
						 */
						finish();
					}
					else {
						i -= 10;	// now reduce the opacity
					}
				}
			};

			scheduleTimer.schedule(ChangeTimer, 2500, 100);
		}
	}

	@SuppressWarnings("deprecation")
	void changeOpacity( int opacity ) {
		SeminoleFaceImage.setAlpha(opacity);
		SeminoleTitleImage.setAlpha(opacity);
	}

	private boolean Online() {
		ConnectivityManager connectivityManager 
		= (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash_screen, menu);
		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id) {

		AlertDialog.Builder builder;

		switch( id ) {

		case NO_CONNECTION_DIALOG: {
			builder = new AlertDialog.
					Builder(this);

			builder.setCancelable(false).setTitle("Connection Error").
			setMessage(R.string.Error_NoConnection).setNeutralButton("Close", 
					new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					removeDialog(NO_CONNECTION_DIALOG);
					finish();
				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();

			break;
		}

		}
		return super.onCreateDialog(id);
	}
}
