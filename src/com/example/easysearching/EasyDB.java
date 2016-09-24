package com.example.easysearching;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EasyDB {
	private static final int DATABASE_VERSION = 3;
	private static final String DATABASE_NAME = "easy.db";
	private static final String DATA = "data";

	public static final String ID = "_id";
	public static final String TYPE = "type";
	public static final String PATH = "path";
	public static final String NAME = "name";

	public static final String AUDIO = "audio";
	public static final String VIDEO = "video";
	public static final String IMAGE = "image";
	public static final String PROGRAM = "program";

	private final DatabaseHelper database;
	private final SQLiteDatabase sqLiteDatabase;
	private static EasyDB EasyDB;

	private EasyDB(Context context) {
		database = new DatabaseHelper(context);
		sqLiteDatabase = database.getWritableDatabase();
	}

	public static EasyDB getInstance(Context context) {
		if (EasyDB == null) {
			EasyDB = new EasyDB(context);
		}
		return EasyDB;
	}

	public void closeDataBase() {
		if (sqLiteDatabase != null)
			sqLiteDatabase.close();
		if (database != null)
			database.close();
	}

	public class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		// create database
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE " + DATA + "( " + ID
					+ "  INTEGER PRIMARY KEY, " + TYPE + " TEXT," + NAME
					+ " TEXT," + PATH + " TEXT UNIQUE)");

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + DATA);
			onCreate(db);
		}

	}

	// saves one data in the database

	public void saveData(Data data) {
		try {
			ContentValues contentValues = new ContentValues();
			contentValues.put(TYPE, data.getType());
			contentValues.put(PATH, data.getPath());
			contentValues.put(NAME, data.getName());
			sqLiteDatabase.insert(DATA, null, contentValues);
		} catch (SQLException e) {
		} catch (Exception e) {
		}
	}

	// saves all datas in the database

	public void saveDatas(List<File> datas) {
		for (int i = 0; i < datas.size(); i++) {
			File file = datas.get(i);
			Data data = new Data();
			data.setPath(file.getAbsolutePath());
			data.setName(file.getName());
			String name = file.getName().toLowerCase();
			if (name.endsWith(".apk")) {
				data.setType(PROGRAM);
			} else if (name.endsWith(".mp3") || name.endsWith(".amr")
					|| name.endsWith(".mid") || name.endsWith(".aac")
					|| name.endsWith(".wav")) {
				data.setType(AUDIO);
			} else if (name.endsWith(".mp4") || name.endsWith(".mkv")
					|| name.endsWith(".avi") || name.endsWith(".3gp")) {
				data.setType(VIDEO);
			} else if (name.endsWith(".jpg") || name.endsWith(".jpeg")
					|| name.endsWith(".png") || name.endsWith(".bmp")
					|| name.endsWith(".gif")) {
				data.setType(IMAGE);
			}
			saveData(data);
		}
	}

	// reads all data using cursor

	public ArrayList<Data> readData() {
		ArrayList<Data> datas = new ArrayList<Data>();
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from " + DATA, null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						Data data = new Data();
						data.setType(cur.getString(1));
						data.setName(cur.getString(2));
						data.setPath(cur.getString(3));
						datas.add(data);
					} while (cur.moveToNext());
				}
				if (!cur.isClosed()) {
					cur.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}

	// gets query from user input

	public ArrayList<Data> search(String key, String type) {
		ArrayList<Data> datas = new ArrayList<Data>();
		try {
			Cursor cur = sqLiteDatabase.rawQuery("select * from " + DATA
					+ " where type = '" + type + "' and name Like '%" + key
					+ "%'", null);
			if (cur != null) {
				if (cur.moveToFirst()) {
					do {
						Data data = new Data();
						data.setType(cur.getString(1));
						data.setName(cur.getString(2));
						data.setPath(cur.getString(3));
						datas.add(data);
					} while (cur.moveToNext());
				}
				if (!cur.isClosed()) {
					cur.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datas;
	}

	// will return true if the database has any data
	public boolean hasData() {
		try {
			int n = 0;
			Cursor cur = sqLiteDatabase.rawQuery(
					"select count(*) from " + DATA, null);
			if (cur != null)
				if (cur.moveToFirst())
					n = cur.getInt(0);
			if (!cur.isClosed()) {
				cur.close();
			}
			if (n > 0)
				return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
