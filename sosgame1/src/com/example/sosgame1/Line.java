// TODO: Credit http://www.learnopengles.com/ License? Apache
package com.example.sosgame1;

import android.opengl.GLES20;
import android.opengl.Matrix;

/** This class is used to show lines on the board when a player completes
 * the word "SOS".
 * @author David Moore
 */
public class Line extends Cube {

	/** Line start x coordinate. */
	public float startX;
	
	/** Line start y coordinate. */
	public float startY;

	/** Line end x coordinate. */
	public float endX;
	
	/** Line end y coordinate. */
	public float endY;
	
	/** X scale factor. */
	private float scaleFactorX = 1;

	/** Y scale factor. */
	private float scaleFactorY = 0.1f;

	/** Z scale factor. */
	private float scaleFactorZ = 0.1f;
	
	/** Line colour is an offset into an array of texture coordinates. */
	public static final int COLOUR_RED = 4 * 6 * 6;
	
	/** Line colour is an offset into an array of texture coordinates. */
	public static final int COLOUR_BLUE = 8 * 6 * 6;
	
	/** The model matrix is used to rotate/translate/scale the line in the 
	 * 3D world space. */
	private float[] modelMatrix = new float[16];

	/** Simple line constructor.
	 * @param renderer Reference to the renderer.
	 */
	public Line(GLRenderer renderer) {
		super(renderer);
	}
	
	/** Line constructor.
	 * @param renderer Reference to the renderer.
	 * @param startX Line start x coordinate.
	 * @param startY Line start y coordinate.
	 * @param endX Line end x coordinate.
	 * @param endY Line end y coordinate.
	 * @param colour Either Line.COLOUR_RED or Line.COLOUR_BLUE.
	 */
	public Line(GLRenderer renderer, float startX,
			float startY, float endX, float endY, int colour) {
		super(renderer);
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		z = GLRenderer.tileZ + 2 * scaleFactorY;
		colourOffset = colour;
	}
	
	/** Draws a line. */			
	public void draw()
	{		
		// Compute the rotation and scale factors
		if (startX == endX) {
			// Vertical line
			rotationZ = 90;
			scaleFactorX = 1;
			x = startX;
			y = startY + (endY - startY) / 2;
		} else if (startY == endY) {
			// Horizontal line
			rotationZ = 0;
			scaleFactorX = 1;
			x = startX + (endX - startX) / 2;
			y = startY;
		} else {
			// Diagonal line
			if (endY > startY && endX > startX
					|| endY < startY && endX < startX) {
				rotationZ = 45;
			} else {
				rotationZ = -45;
			}
			scaleFactorX = 1.414f;
			x = startX + (endX - startX) / 2;
			y = startY + (endY - startY) / 2;
		}

		Matrix.setIdentityM(modelMatrix, 0);
		Matrix.translateM(modelMatrix, 0, x, y, z);
		Matrix.rotateM(modelMatrix, 0, rotationZ, 0.0f, 0.0f, 1.0f);
		Matrix.scaleM(modelMatrix, 0, scaleFactorX, scaleFactorY, scaleFactorZ);
		
		// Pass in the position information
		renderer.mCubePositions.position(0);		
        GLES20.glVertexAttribPointer(renderer.mPositionHandle, renderer.mPositionDataSize, GLES20.GL_FLOAT, false,
        		0, renderer.mCubePositions);        
                
        GLES20.glEnableVertexAttribArray(renderer.mPositionHandle);        
        
        // Pass in the color information
        renderer.mCubeColors.position(colourOffset);
        GLES20.glVertexAttribPointer(renderer.mColorHandle, renderer.mColorDataSize, GLES20.GL_FLOAT, false,
        		0, renderer.mCubeColors);        
        
        GLES20.glEnableVertexAttribArray(renderer.mColorHandle);
        
        // Pass in the normal information
        renderer.mCubeNormals.position(0);
        GLES20.glVertexAttribPointer(renderer.mNormalHandle, renderer.mNormalDataSize, GLES20.GL_FLOAT, false, 
        		0, renderer.mCubeNormals);
        
        GLES20.glEnableVertexAttribArray(renderer.mNormalHandle);
        
		// This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(renderer.mMVPMatrix, 0, renderer.mViewMatrix, 0, modelMatrix, 0);   
        
        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(renderer.mMVMatrixHandle, 1, false, renderer.mMVPMatrix, 0);                
        
        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(renderer.mMVPMatrix, 0, renderer.mProjectionMatrix, 0, renderer.mMVPMatrix, 0);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(renderer.mMVPMatrixHandle, 1, false, renderer.mMVPMatrix, 0);
        
        // Pass in the light position in eye space.        
        GLES20.glUniform3f(renderer.mLightPosHandle, renderer.mLightPosInEyeSpace[0], renderer.mLightPosInEyeSpace[1], renderer.mLightPosInEyeSpace[2]);
        
        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);                               
	}	
	
}
