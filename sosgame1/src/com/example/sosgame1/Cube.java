/*
   Copyright 2011-2012 Learn OpenGL ES

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
/*
 * Portions of this class are derived from the code described at
 * http://www.learnopengles.com/android-lesson-four-introducing-basic-texturing/
 */
package com.example.sosgame1;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Cube {

	protected GLRenderer renderer;
	protected float x = 0;
	protected float y = 0;
	protected float z = 0;
	protected float rotationX = 0;
	protected float rotationY = 0;
	protected float rotationZ = 0;
	protected float scaleFactorX = 1;
	protected float scaleFactorY = 1;
	protected float scaleFactorZ = 1;
	protected int textureOffset = 0;
	protected int colourOffset = 0;

	protected Cube(GLRenderer renderer) {
		this.renderer = renderer;
	}
	
	protected Cube(GLRenderer renderer, int textureOffset) {
		this.renderer = renderer;
		this.textureOffset = textureOffset;
	}
	
	/** Constructor....
	 * @param renderer
	 * @param textureOffset
	 * @param x
	 * @param y
	 */
	protected Cube(GLRenderer renderer, int textureOffset, float x, float y) {
		this.renderer = renderer;
		this.textureOffset = textureOffset;
		this.x = x;
		this.y = y;
	}
	
	/**
	 * @param renderer
	 * @param textureOffset
	 * @param colourOffset
	 * @param x
	 * @param y
	 */
	protected Cube(GLRenderer renderer, int textureOffset, int colourOffset,
			float x, float y) {
		this.renderer = renderer;
		this.textureOffset = textureOffset;
		this.colourOffset = colourOffset;
		this.x = x;
		this.y = y;
	}

	/** Setter required for object animation of this property.
	 * @param angle Angle of rotation about Y axis.
	 */
	protected void setRotationY(float angle) {
		rotationY = angle;
	}
	
	/** Setter required for object animation of this property.
	 * @param angle Angle of rotation about X axis.
	 */
	protected void setRotationX(float angle) {
		rotationX = angle;
	}

	/** Setter required for object animation of this property.
	 * @param angle Angle of rotation about Z axis.
	 */
	protected void setRotationZ(float angle) {
		rotationZ = angle;
	}

	/** Setter required for object animation of this property.
	 * @param x X coordinate.
	 */
	public void setX(float x) {
		this.x = x;
	}

	/** Setter required for object animation of this property.
	 * @param y Y coordinate.
	 */
	public void setY(float y) {
		this.y = y;
	}	
	
	/** Setter required for object animation of this property.
	 * @param z Z coordinate.
	 */
	protected void setZ(float z) {
		this.z = z;
	}

	/** Draws a cube. Code (with textureOffset modification) from:<br>
	 * http://www.learnopengles.com/android-lesson-two-ambient-and-diffuse-lighting/
	 * @param ModelMatrix The model matrix which specifies translation, 
	 * rotation and scale.
	 */
	protected void draw(float[] ModelMatrix)
	{		
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
        
        // Pass in the texture coordinate information
        renderer.mCubeTextureCoordinates.position(textureOffset);
        GLES20.glVertexAttribPointer(renderer.mTextureCoordinateHandle, renderer.mTextureCoordinateDataSize, GLES20.GL_FLOAT, false, 
        		0, renderer.mCubeTextureCoordinates);
        
        GLES20.glEnableVertexAttribArray(renderer.mTextureCoordinateHandle);
        
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

	/** Overload used by Line subclass. */
	protected void draw() {
		// Implemented in Line class.
	}
	
}
