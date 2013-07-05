package nz.edu.unitec.sosgame1;

public class Cell extends Cube {

	public Cell(GLRenderer renderer, int textureOffset, float x, float y) {
		super(renderer, textureOffset, x, y);
		init();
	}

	public Cell(GLRenderer renderer, int textureOffset) {
		super(renderer, textureOffset);
		init();
	}

	private void init() {
		scaleFactorX = GLRenderer.cellScaleFactorX;
		scaleFactorY = GLRenderer.cellScaleFactorY;
		z = GLRenderer.cellZ;
	}

}
