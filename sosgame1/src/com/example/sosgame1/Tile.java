package com.example.sosgame1;

public class Tile extends Cube {

    public char letter = 'S'; //either "S" or "O"
    private Player player;
    
	public Tile(MyGLRenderer renderer, float x, float y) {
		super(renderer, x, y);
		// TODO Auto-generated constructor stub
	}

	public Tile(MyGLRenderer renderer) {
		super(renderer);
		// TODO Auto-generated constructor stub
	}

	public char getLetter() {
        return letter;
    }

    public void setLetter(char letter) {
    	this.letter = letter;
    	if (letter == 'O') {
    		rotationY = 180;
    	} else {
    		rotationY = 0;
    	}
    }
    
    public Player getPlayer() {
            return player;
    }
    
    public void setPlayer(Player player) {
            this.player = player;
    }

}
