package com.example.sosgame1;

public class Cell extends Cube {

	public Cell(GLRenderer renderer, int textureOffset, float x, float y) {
		super(renderer, textureOffset, x, y);
		scaleFactorX = GLRenderer.cellScaleFactorX;
		scaleFactorY = GLRenderer.cellScaleFactorY;
	}

	public Cell(GLRenderer renderer, int textureOffset) {
		super(renderer, textureOffset);
		scaleFactorX = GLRenderer.cellScaleFactorX;
		scaleFactorY = GLRenderer.cellScaleFactorY;
	}


}
