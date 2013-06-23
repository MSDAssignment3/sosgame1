package com.example.sosgame1;

/** This class is used to show tiles with the letter S or O on the game board.
 * @author David Moore
 */
public class Tile extends Cube {

    public char letter = 'S'; // Either "S" or "O"
    private Player player;
    
	/** Tile colour is an offset into an array of texture coordinates. */
    public static final int COLOUR_RED = GLRenderer.textureOffsetTileRed;

    /** Tile colour is an offset into an array of texture coordinates. */
    public static final int COLOUR_BLUE = GLRenderer.textureOffsetTileBlue;
    
	/** Constructor.
	 * @param renderer Reference to the renderer.
	 * @param colour Either Tile.COLOUR_RED or Tile.COLOUR_BLUE.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 */
	public Tile(GLRenderer renderer, int colour, float x, float y) {
		super(renderer, colour, x, y);
		init();
	}

	/** Constructor.
	 * @param renderer Reference to the renderer.
	 * @param colour Either Tile.COLOUR_RED or Tile.COLOUR_BLUE.
	 */
	public Tile(GLRenderer renderer, int colour) {
		super(renderer, colour);
		init();
	}

	/** Constructor.
	 * @param renderer Reference to the renderer.
	 * @param colour Either Tile.COLOUR_RED or Tile.COLOUR_BLUE.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @param letter Either 'S' or 'O'.
	 */
	public Tile(GLRenderer renderer, int colour, float x, float y,
			char letter) {
		super(renderer, colour, x, y);
		setLetter(letter);
		init();
	}
	
	/** Initialise. */
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
