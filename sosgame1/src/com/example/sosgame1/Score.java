package com.example.sosgame1;

/**
 * This is the individual final score of player.
 * Meaning it will be stored at the end of the game.
 * Properties are the same as the DB columns.
 * @author Bea
 *
 */
public class Score {
	
	private long scoreId;
	private String player;
	private int scoreValue;
	
	public long getScoreId() {
		return scoreId;
	}
	public void setScoreId(long scoreId) {
		this.scoreId = scoreId;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public int getScoreValue() {
		return scoreValue;
	}
	public void setScoreValue(int scoreValue) {
		this.scoreValue = scoreValue;
	}
	@Override
	public String toString() {
		String score = player + " " + scoreValue;
		return score;
	}

	
}
