package com.example.sosgame1;

public class Cell extends Cube {

	public Cell(MyGLRenderer renderer, int textureOffset, float x, float y) {
		super(renderer, textureOffset, x, y);
		scaleFactorX = MyGLRenderer.cellXScaleFactor;
		scaleFactorY = MyGLRenderer.cellYScaleFactor;
	}

	public Cell(MyGLRenderer renderer, int textureOffset) {
		super(renderer, textureOffset);
		scaleFactorX = MyGLRenderer.cellXScaleFactor;
		scaleFactorY = MyGLRenderer.cellYScaleFactor;
	}


}
