package com.example.sosgame1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/** This class was supposed to avoid passing references to the renderer
 * around but it looks like it will take too long to get it working.
 * Will probably delete this class. */
public class RenderingData {

	/** Stores cube texture coordinates. */
	public static FloatBuffer mCubeTextureCoordinates;
	
	/** How many bytes per float. */
	public static int mBytesPerFloat = 4;	
	
	/** Size of the texture coordinate data in elements. */
	public static int mTextureCoordinateDataSize = 2;
	
	/** This will be used to pass in model texture coordinate information. */
	public static int mTextureCoordinateHandle;

	public static void initData() {
		// S, T (or X, Y)
		// Texture coordinate data.
		// Because images have a Y axis pointing downward (values increase as you move down the image) while
		// OpenGL has a Y axis pointing upward, we adjust for that here by flipping the Y axis.
		final float[] cubeTextureCoordinateData =
		{
				// Textures for blue tiles
				
				// Front face
				0.0f, 0.0f, 				
				0.0f, 0.25f,
				0.25f, 0.0f,
				0.0f, 0.25f,
				0.25f, 0.25f,
				0.25f, 0.0f,	
				
				// Right face 
				0.71f, 0.71f, 				
				0.71f, 1.0f,
				1.0f, 0.71f,
				0.71f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.71f,	
				
				// Back face 
				0.25f, 0.0f,
				0.25f, 0.25f,
				0.5f, 0.0f,
				0.25f, 0.25f,
				0.5f, 0.25f,
				0.5f, 0.0f,	
				
				// Left face 
				0.0f, 0.0f, 				
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				
				// Top face 
				0.0f, 0.0f, 				
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				
				// Bottom face 
				0.0f, 0.0f, 				
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,

				// Textures for red tiles
				
				// Front face
				0.0f, 0.25f, 				
				0.0f, 0.5f,
				0.25f, 0.25f,
				0.0f, 0.5f,
				0.25f, 0.5f,
				0.25f, 0.25f,	
				
				// Right face 
				0.71f, 0.71f, 				
				0.71f, 1.0f,
				1.0f, 0.71f,
				0.71f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.71f,	
				
				// Back face 
				0.25f, 0.25f,
				0.25f, 0.50f,
				0.50f, 0.25f,
				0.25f, 0.50f,
				0.50f, 0.50f,
				0.50f, 0.25f,	
				
				// Left face 
				0.0f, 0.0f, 				
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				
				// Top face 
				0.0f, 0.0f, 				
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				
				// Bottom face 
				0.0f, 0.0f, 				
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,
				0.0f, 0.0f,

				// Textures for board cells
				
				// Front face
				0.5f, 0.5f, 				
				0.5f, 1.0f,
				1.0f, 0.5f,
				0.5f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.5f,				
				
				// Right face 
				0.5f, 0.5f, 				
				0.5f, 1.0f,
				1.0f, 0.5f,
				0.5f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.5f,	
				
				// Back face 
				0.5f, 0.5f, 				
				0.5f, 1.0f,
				1.0f, 0.5f,
				0.5f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.5f,	
				
				// Left face 
				0.5f, 0.5f, 				
				0.5f, 1.0f,
				1.0f, 0.5f,
				0.5f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.5f,	
				
				// Top face 
				0.5f, 0.5f, 				
				0.5f, 1.0f,
				1.0f, 0.5f,
				0.5f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.5f,	
				
				// Bottom face 
				0.5f, 0.5f, 				
				0.5f, 1.0f,
				1.0f, 0.5f,
				0.5f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.5f
		};
		
		mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
		.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);

	}

}
