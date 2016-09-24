package com.example.easysearching;

import java.io.File;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class ListViewPage extends ListActivity {
	private ProgressDialog dialog;
	private Handler handler;
	private EasyDB db;
	MediaPlayer mp;

	// calls the load() to load the whole database

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String[] searchItem = new String[] { "All Files", "Images", "Videos",
				"Musics", "Apps" };
		handler = new Handler();
		load();
		setListAdapter(new SearchArrayAdapter(this, searchItem));
	}

	// creates menu inflater to add options menu "about us" and "about app"

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.my_options_menu, menu);
		return true;
	}

	// opens the alertdailog after clicking "about app " option.
	private void openOptionsAboutAppDialog() {
		new AlertDialog.Builder(this)
				.setTitle(R.string.aboutApp_title)
				.setMessage(R.string.aboutApp_message)
				.setPositiveButton(R.string.aboutApp_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// MediaPlayer mp = MediaPlayer.create(
								// getApplicationContext(),
								// R.raw.hardclick);
								mp.start();
								// TODO Auto-generated method stub

							}
						}).show();
	}

	// opens the alertdailog after clicking "about us " option.
	private void openOptionsAboutUsDialog() {

		new AlertDialog.Builder(this)
				.setTitle(R.string.aboutUs_title)
				.setMessage(R.string.aboutUs_message)
				.setPositiveButton(R.string.aboutUs_ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub

								mp.start();
							}
						}).show();
	}

	// Event handler for options menu

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// MediaPlayer mp = MediaPlayer.create(getApplicationContext(),
		// R.raw.hardclick);
		mp = MediaPlayer.create(getApplicationContext(), R.raw.hardclick);

		mp.start();
		switch (item.getItemId()) {
		case R.id.aboutUs:
			openOptionsAboutUsDialog();
			return true;
		case R.id.aboutApp:
			openOptionsAboutAppDialog();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// loads the whole database

	private void load() {
		dialog = ProgressDialog.show(this, "Loading...",
				"Data Loading.\nIt will take some time");
		new Thread(new Runnable() {
			@Override
			public void run() {
				db = EasyDB.getInstance(ListViewPage.this);
				if (!db.hasData()) {
					ArrayList<File> datas;
					if (Environment.getExternalStorageDirectory() != null
							&& Environment.getExternalStorageDirectory()
									.exists())
						datas = getListFiles(Environment
								.getExternalStorageDirectory());
					else
						datas = getListFiles(Environment.getRootDirectory());
					db.saveDatas(datas);

				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
					}
				});
			}
		}).start();
	}

	// add all files in a file arraylist

	private ArrayList<File> getListFiles(File parentDir) {
		try {
			ArrayList<File> inFiles = new ArrayList<File>();
			File[] files = parentDir.listFiles();
			for (File file : files) {
				if (file.isDirectory()) {
					inFiles.addAll(getListFiles(file));
				} else {
					inFiles.add(file);
				}
			}
			return inFiles;
		} catch (Exception e) {
			return new ArrayList<File>();
		}

	}

	// select the category

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		mp = MediaPlayer.create(getApplicationContext(), R.raw.hardclick);
		mp.start();
		final Context context = this;
		Intent intent = new Intent(context, WritingName.class);
		intent.putExtra("position", position);
		startActivity(intent);
	}

}
