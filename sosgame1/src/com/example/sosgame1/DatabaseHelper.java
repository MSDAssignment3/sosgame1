package com.example.sosgame1;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Creates the Database.
 * @author Bea
 * Created through the tutorials from:
 * http://www.vogella.com/articles/AndroidSQLite/article.html
 * http://androiddevelopmentworld.blogspot.co.nz/2013/04/android-sqlite-tutorial.html
 */
public class DatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "sos.db";
	private static final int DATABASE_VERSION = 2;
	
	public  static final String TABLE_SCORE = "score";
	public  static final String COLUMN_ID = "score_id";
	public  static final String COLUMN_PLAYER = "player";
	public  static final String COLUMN_VALUE = "score_value";
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
	  + TABLE_SCORE + "(" + COLUMN_ID
	  + " integer primary key autoincrement, " + COLUMN_PLAYER
	  + " text not null, " + COLUMN_VALUE + "integer not null);";
	//SQLite datatype, INTEGER, can store up to 8-bytes. LONG datatypes are 8-byte
	
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(DatabaseHelper.class.getName(), "Upgrading database from version " + oldVersion + " to "
		    + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCORE);
	    onCreate(db);
	}

}
