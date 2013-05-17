package com.example.sosgame1;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

public class Line {

	private final Context activityContext;
	private final MyGLRenderer renderer;
	
	public float x = 0;
	public float y = 0;
	public float z = 0;
	public int topFace = 1;
	public float xRotation = 0;
	public float yRotation = 0;
	public float zRotation = 0;

	public Line(Context context, MyGLRenderer renderer) {
		activityContext = context;
		this.renderer = renderer;
	}
	
	public void setYRotation(float angle) {
		yRotation = angle;
	}
	
	public void setXRotation(float angle) {
		xRotation = angle;
	}

	public void setZRotation(float angle) {
		zRotation = angle;
	}

	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * Draws a cube.
	 */			
	public void draw(float[] ModelMatrix)
	{		
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
        Matrix.multiplyMM(renderer.mMVPMatrix, 0, renderer.mViewMatrix, 0, ModelMatrix, 0);   
        
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
