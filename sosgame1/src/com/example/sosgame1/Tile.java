package com.example.sosgame1;

public class Tile extends Cube {

    public char letter = 'S'; // Either "S" or "O"
    private Player player;
    
    public static final int COLOUR_RED = GLRenderer.textureOffsetTileRed;
    public static final int COLOUR_BLUE = GLRenderer.textureOffsetTileBlue;
    
	public Tile(GLRenderer renderer, int colour, float x, float y) {
		super(renderer, colour, x, y);
		init();
	}

	public Tile(GLRenderer renderer, int colour) {
		super(renderer, colour);
		init();
	}

	public Tile(GLRenderer renderer, int colour, float x, float y,
			char letter) {
		super(renderer, colour, x, y);
		setLetter(letter);
		init();
	}
	
	private void init() {
		scaleFactorX = GLRenderer.tileScaleFactorX;
		scaleFactorY = GLRenderer.tileScaleFactorY;
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
