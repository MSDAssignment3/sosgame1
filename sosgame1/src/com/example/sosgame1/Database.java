package com.example.sosgame1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "sos.db";
	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_SCORE = "score";
	private static final String COLUMN_ID = "score_id";
	private static final String COLUMN_PLAYER = "player";
	private static final String COLUMN_VALUE = "score_value";
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
	  + TABLE_SCORE + "(" + COLUMN_ID
	  + " integer primary key autoincrement, " + COLUMN_PLAYER
	  + " text not null, " + COLUMN_VALUE + "text not null);";
	
	public Database(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(Database.class.getName(), "Upgrading database from version " + oldVersion + " to "
		    + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
	    onCreate(db);
		
	}

}
