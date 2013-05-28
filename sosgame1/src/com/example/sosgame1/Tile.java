package com.example.sosgame1;

public class Tile extends Cube {

    public char letter = 'S'; //either "S" or "O"
    private Player player;
    
    public static final int COLOUR_RED = MyGLRenderer.textureOffsetTileRed;
    public static final int COLOUR_BLUE = MyGLRenderer.textureOffsetTileBlue;
    
	public Tile(MyGLRenderer renderer, int colour, float x, float y) {
		super(renderer, colour, x, y);
		// TODO Auto-generated constructor stub
	}

	public Tile(MyGLRenderer renderer, int colour) {
		super(renderer, colour);
		// TODO Auto-generated constructor stub
	}

	public Tile(MyGLRenderer renderer, int colour, float x, float y,
			char letter) {
		super(renderer, colour, x, y);
		setLetter(letter);
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
