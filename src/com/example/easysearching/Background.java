package com.example.easysearching;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

public class Background extends Activity {
	ImageView image;

	// INITIALIZES ACTIVITY

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background);

		MediaPlayer mp = MediaPlayer.create(getApplicationContext(),
				R.raw.background);
		mp.start();

		final Context context = this;

		Thread timer = new Thread() {
			@Override
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					Intent openListView = new Intent(context,
							ListViewPage.class);
					startActivity(openListView);
				}
			}

		};

		timer.start();
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}

}
