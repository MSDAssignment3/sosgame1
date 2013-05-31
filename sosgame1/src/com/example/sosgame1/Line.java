// TODO: Credit http://www.learnopengles.com/ License? Apache
package com.example.sosgame1;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

public class Line extends Cube {

	public float startX;
	public float startY;
	public float endX;
	public float endY;
	private float scaleFactorX = 1;
	private float scaleFactorY = 0.1f;
	public static final int COLOUR_RED = 0;
	public static final int COLOUR_BLUE = 180;
	
	private float[] modelMatrix = new float[16];

	public Line(MyGLRenderer renderer) {
		super(renderer);
	}
	
	public Line(MyGLRenderer renderer, float startX,
			float startY, float endX, float endY, int colour) {
		super(renderer);
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.rotationX = colour;
		z = renderer.tileZ + 0.2f;
	}
	
	// TODO: Constructor taking start and end points or centre and length?
	// TODO: Constructor as above plus front face?
	
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
		Matrix.rotateM(modelMatrix, 0, rotationX, 1.0f, 0.0f, 0.0f);
		Matrix.scaleM(modelMatrix, 0, scaleFactorX, scaleFactorY, 0.10f);
		
		// Pass in the position information
		renderer.mCubePositions.position(0);		
        GLES20.glVertexAttribPointer(renderer.mPositionHandle, renderer.mPositionDataSize, GLES20.GL_FLOAT, false,
        		0, renderer.mCubePositions);        
                
        GLES20.glEnableVertexAttribArray(renderer.mPositionHandle);        
        
        // Pass in the color information
        renderer.lineColors.position(0);
        GLES20.glVertexAttribPointer(renderer.mColorHandle, renderer.mColorDataSize, GLES20.GL_FLOAT, false,
        		0, renderer.lineColors);        
        
        GLES20.glEnableVertexAttribArray(renderer.mColorHandle);
        
        // Pass in the normal information
        renderer.mCubeNormals.position(0);
        GLES20.glVertexAttribPointer(renderer.mNormalHandle, renderer.mNormalDataSize, GLES20.GL_FLOAT, false, 
        		0, renderer.mCubeNormals);
        
        GLES20.glEnableVertexAttribArray(renderer.mNormalHandle);
        
//        // Pass in the texture coordinate information
//        renderer.mCubeTextureCoordinates.position(0);
//        GLES20.glVertexAttribPointer(renderer.mTextureCoordinateHandle, renderer.mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
//        		0, renderer.mCubeTextureCoordinates);
//        
//        GLES20.glEnableVertexAttribArray(renderer.mTextureCoordinateHandle);
        
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
