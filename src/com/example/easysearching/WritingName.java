package com.example.easysearching;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class WritingName extends Activity implements OnQueryTextListener,
		OnItemClickListener {
	private ListView listView;
	private ListAdapter adapter;
	private int type;
	private ProgressDialog dialog;
	private Handler handler;
	private SearchView mSearchView;
	private EasyDB db;

	// initialize the writing name activity , add menu items

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_writingname);

		listView = (ListView) findViewById(R.id.list);
		type = getIntent().getIntExtra("position", -1);
		adapter = new ListAdapter(this, getType(type));

		listView.setAdapter(adapter);
		handler = new Handler();
		listView.setOnItemClickListener(this);
		search("");
		mSearchView = new SearchView(this);
		mSearchView.setQueryHint("Search Data");
		mSearchView.setOnQueryTextListener(this);
		LinearLayout linearLayout1 = (LinearLayout) mSearchView.getChildAt(0);
		LinearLayout linearLayout2 = (LinearLayout) linearLayout1.getChildAt(2);
		LinearLayout linearLayout3 = (LinearLayout) linearLayout2.getChildAt(1);
		AutoCompleteTextView autoComplete = (AutoCompleteTextView) linearLayout3
				.getChildAt(0);
		autoComplete.setTextColor(Color.WHITE);
	}

	// search is done in the chosen category, in case of 'all files' it is done
	// in the whole database

	private void search(final String key) {
		dialog = ProgressDialog.show(this, "Searching", "Searching for " + key);
		new Thread(new Runnable() {
			@Override
			public void run() {
				db = EasyDB.getInstance(WritingName.this);
				if (getType(type).isEmpty())
					adapter.setData(db.readData());
				else
					adapter.setData(db.search(key, getType(type)));
				handler.post(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
						dialog.dismiss();
					}
				});

			}
		}).start();
	}

	public String getType(int type) {
		String ty = "";
		switch (type) {
		case 0:
			ty = "";
			break;
		case 1:
			ty = EasyDB.IMAGE;
			break;
		case 2:
			ty = EasyDB.VIDEO;
			break;
		case 3:
			ty = EasyDB.AUDIO;
			break;
		case 4:
			ty = EasyDB.PROGRAM;
			break;
		default:
			break;
		}
		return ty;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		adapter.getFilter().filter(query);
		return true;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		adapter.getFilter().filter(newText);
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem item = menu.add("search");
		item.setIcon(android.R.drawable.ic_menu_search);
		item.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		item.setActionView(mSearchView);

		MenuItem refresh = menu.add("refresh");
		refresh.setIcon(R.drawable.refresh);
		refresh.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		return super.onCreateOptionsMenu(menu);
	}

	// opens the file

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		MediaPlayer mp = MediaPlayer.create(getApplicationContext(),
				R.raw.hardclick);
		mp.start();
		Intent intent = new Intent();
		intent.setAction(android.content.Intent.ACTION_VIEW);
		File file = adapter.getFile(arg2);
		if (type == 0) {
			int tempType = getTemptype(file.getName());
			intent.setDataAndType(Uri.fromFile(file), getTypeFor(tempType));
		} else
			intent.setDataAndType(Uri.fromFile(file), getTypeFor(type));
		startActivity(intent);
	}

	private int getTemptype(String name) {
		int type = 0;
		name = name.toLowerCase();
		if (name.endsWith(".apk")) {
			type = 4;
		} else if (name.endsWith(".mp3") || name.endsWith(".amr")
				|| name.endsWith(".mid") || name.endsWith(".aac")
				|| name.endsWith(".wav")) {
			type = 3;
		} else if (name.endsWith(".mp4") || name.endsWith(".mkv")
				|| name.endsWith(".avi") || name.endsWith(".3gp")) {
			type = 2;
		} else if (name.endsWith(".jpg") || name.endsWith(".jpeg")
				|| name.endsWith(".png") || name.endsWith(".bmp")
				|| name.endsWith(".gif")) {
			type = 1;
		}
		return type;
	}

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

	public String getTypeFor(int type) {
		String ty = "*";
		switch (type) {
		case 0:
			ty = "*/*";
			break;
		case 1:
			ty = "image/*";
			break;
		case 2:
			ty = "video/*";
			break;
		case 3:
			ty = "audio/*";
			break;
		case 4:
			ty = "application/vnd.android.package-archive";
			break;
		default:
			break;
		}
		return ty;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getIcon() != null) {
			MediaPlayer mp = MediaPlayer.create(getApplicationContext(),
					R.raw.refresh);
			mp.start();
			load();
		}
		return super.onOptionsItemSelected(item);
	}

	private void load() {
		dialog = ProgressDialog.show(this, "Refreshing...",
				"Data Loading.\nIt will take some time");
		new Thread(new Runnable() {
			@Override
			public void run() {
				db = EasyDB.getInstance(WritingName.this);
				ArrayList<File> datas;
				if (Environment.getExternalStorageDirectory() != null
						&& Environment.getExternalStorageDirectory().exists())
					datas = getListFiles(Environment
							.getExternalStorageDirectory());
				else
					datas = getListFiles(Environment.getRootDirectory());
				db.saveDatas(datas);
				handler.post(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
						search("");
						adapter.notifyDataSetChanged();
					}
				});
			}
		}).start();
	}

}
