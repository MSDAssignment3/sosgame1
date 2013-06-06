package com.example.sosgame1;

public class Cell extends Cube {

	public Cell(MyGLRenderer renderer, int textureOffset, float x, float y) {
		super(renderer, textureOffset, x, y);
		scaleFactorX = MyGLRenderer.cellScaleFactorX;
		scaleFactorY = MyGLRenderer.cellScaleFactorY;
	}

	public Cell(MyGLRenderer renderer, int textureOffset) {
		super(renderer, textureOffset);
		scaleFactorX = MyGLRenderer.cellScaleFactorX;
		scaleFactorY = MyGLRenderer.cellScaleFactorY;
	}


}
