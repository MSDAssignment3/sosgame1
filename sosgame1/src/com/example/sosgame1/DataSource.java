package com.example.sosgame1;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Serves as DAO
 * It maintains the database connection and supports adding new scores and fetching all scores.
 * @author Bea
 * Created through the tutorials from:
 * http://www.vogella.com/articles/AndroidSQLite/article.html
 * http://androiddevelopmentworld.blogspot.co.nz/2013/04/android-sqlite-tutorial.html
 */
public class DataSource {
	// Database fields
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;
	private String[] allColumns = { DatabaseHelper.COLUMN_ID,
			DatabaseHelper.COLUMN_PLAYER, DatabaseHelper.COLUMN_VALUE };
	
	public DataSource(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
	    dbHelper.close();
	}
	
	public Score createScore(String player, int score) {
	    ContentValues values = new ContentValues();
	    values.put(DatabaseHelper.COLUMN_PLAYER, player);
	    values.put(DatabaseHelper.COLUMN_VALUE, score);
	    long insertId = database.insert(DatabaseHelper.TABLE_SCORE, null,
	        values);
	    Cursor cursor = database.query(DatabaseHelper.TABLE_SCORE,
	        allColumns, DatabaseHelper.COLUMN_ID + " = " + insertId, null,
	        null, null, null);
	    cursor.moveToFirst();
	    Score newScore = cursorToScore(cursor);
	    cursor.close();
	    return newScore;
	}
	
	 public void deleteScore(Score score) {
		 long id = score.getScoreId();
		 System.out.println("Score deleted with id: " + id);
		 database.delete(DatabaseHelper.TABLE_SCORE, DatabaseHelper.COLUMN_ID
				 + " = " + id, null);
	}

	public List<Score> getAllScores() {
		List<Score> scores = new ArrayList<Score>();

		String orderBy = DatabaseHelper.COLUMN_VALUE + " DESC";
		Cursor cursor = database.query(DatabaseHelper.TABLE_SCORE,
        allColumns, null, null, null, null, orderBy);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Score score = cursorToScore(cursor);
			scores.add(score);
			cursor.moveToNext();
		}
		cursor.close();
		return scores;
	}
	
	private Score cursorToScore(Cursor cursor) {
		Score score = new Score();
		score.setScoreId(cursor.getLong(0));
		score.setPlayer(cursor.getString(1));
		score.setScoreValue(cursor.getInt(2));
		return score;
	}

	public void addScore(Score score) {
		String selection = DatabaseHelper.COLUMN_PLAYER + "=?";
		String[] selectionArgs = new String[]{score.getPlayer()};
		Cursor cursor = database.query(DatabaseHelper.TABLE_SCORE, allColumns, 
				selection, selectionArgs, null, null, null, null);
		int newScore;
		ContentValues values = new ContentValues();
		cursor.moveToFirst();
		if (!cursor.isAfterLast()) {
			newScore = cursor.getInt(2) + score.getScoreValue();
			cursor.close();
			values.put(DatabaseHelper.COLUMN_PLAYER, score.getPlayer());
			values.put(DatabaseHelper.COLUMN_VALUE, newScore);
			String whereClause = DatabaseHelper.COLUMN_PLAYER + "=?";
			String[] whereArgs = new String[]{score.getPlayer()};
			database.update(DatabaseHelper.TABLE_SCORE, values, whereClause,
					whereArgs);
		} else {
			cursor.close();
			newScore =  + score.getScoreValue();
			values.put(DatabaseHelper.COLUMN_PLAYER, score.getPlayer());
			values.put(DatabaseHelper.COLUMN_VALUE, newScore);
			database.insert(DatabaseHelper.TABLE_SCORE, null, values);
		}
	}
	
}
