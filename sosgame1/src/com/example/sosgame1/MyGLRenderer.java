// TODO: Credit http://www.learnopengles.com/ License? Apache
// TODO: Credit http://www.learnopengles.com/ License? Apache
package com.example.sosgame1;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;

/**
 * This class implements our custom renderer. Note that the GL10 parameter passed in is unused for OpenGL ES 2.0
 * renderers -- the static class GLES20 is used instead.
 */
public class MyGLRenderer implements GLSurfaceView.Renderer 
{	
	// TODO: Delete this?
	/** Used for debug logs. */
	private static final String TAG = "LessonFourRenderer";
	
	private final Context mActivityContext;
	
	/**
	 * Store the model matrix. This matrix is used to move models from object space (where each model can be thought
	 * of being located at the center of the universe) to world space.
	 */
	public float[] mModelMatrix = new float[16];

	/**
	 * Store the view matrix. This can be thought of as our camera. This matrix transforms world space to eye space;
	 * it positions things relative to our eye.
	 */
	public float[] mViewMatrix = new float[16];

	/** Store the projection matrix. This is used to project the scene onto a 2D viewport. */
	public float[] mProjectionMatrix = new float[16];
	
	/** Allocate storage for the final combined matrix. This will be passed into the shader program. */
	public float[] mMVPMatrix = new float[16];
	
	/** 
	 * Stores a copy of the model matrix specifically for the light position.
	 */
	public float[] mLightModelMatrix = new float[16];	
	
	/** Stores cube vertex coordinates. */
	public final FloatBuffer mCubePositions;
	
	/** Stores cube vertex colours. */
	public final FloatBuffer mCubeColors;
	
//	/** Stores line vertex colours. */
//	public final FloatBuffer lineColors;
	
	/** Stores cube vertex normals. */
	public final FloatBuffer mCubeNormals;
	
	/** Stores cube texture coordinates. */
	public final FloatBuffer mCubeTextureCoordinates;
	
//	/** Stores cell texture coordinates. */
//	public final FloatBuffer cellTextureCoordinates;
	
	/** This will be used to pass in the transformation matrix. */
	public int mMVPMatrixHandle;
	
	/** This will be used to pass in the modelview matrix. */
	public int mMVMatrixHandle;
	
	/** This will be used to pass in the light position. */
	public int mLightPosHandle;
	
	/** This will be used to pass in the texture. */
	public int mTextureUniformHandle;
	
	/** This will be used to pass in model position information. */
	public int mPositionHandle;
	
	/** This will be used to pass in model color information. */
	public int mColorHandle;
	
	/** This will be used to pass in model normal information. */
	public int mNormalHandle;
	
	/** This will be used to pass in model texture coordinate information. */
	public int mTextureCoordinateHandle;

	/** How many bytes per float. */
	public final int mBytesPerFloat = 4;	
	
	/** Size of the position data in elements. */
	public final int mPositionDataSize = 3;	
	
	/** Size of the color data in elements. */
	public final int mColorDataSize = 4;	
	
	/** Size of the normal data in elements. */
	public final int mNormalDataSize = 3;
	
	/** Size of the texture coordinate data in elements. */
	public final int mTextureCoordinateDataSize = 2;
	
	/** Used to hold a light centered on the origin in model space. We need a 4th coordinate so we can get translations to work when
	 *  we multiply this by our transformation matrices. */
	public final float[] mLightPosInModelSpace = new float[] {0.0f, 0.0f, 0.0f, 1.0f};
	
	/** Used to hold the current position of the light in world space (after transformation via model matrix). */
	public final float[] mLightPosInWorldSpace = new float[4];
	
	/** Used to hold the transformed position of the light in eye space (after transformation via modelview matrix) */
	public final float[] mLightPosInEyeSpace = new float[4];
	
	/** This is a handle to our cube shading program. */
	public int mProgramHandle;
		
	/** This is a handle to our light point program. */
	public int mPointProgramHandle;
	
	/** This is a handle to another program without texture. */
	public int noTexProgramHandle;
	
	/** This is a handle to the cube texture data. */
	public int cubeTextureDataHandle;
	
	/** This is a handle to the cell texture data. */
	public int cellTextureDataHandle;
	
//	/** Stores the tile objects. */
//	public ArrayList<Tile> tiles = new ArrayList<Tile>();
	
	/** Stores the board cell objects. */
	public ArrayList<Cell> cells = new ArrayList<Cell>();
	
	/** Viewport width. */
	public int width;
	
	/** Viewport height. */
	public int height;
	
	/** Near clipping plane used in frustum/projection matrix. */
	private final float near = 1;
	
	/** Far clipping plane used in frustum/projection matrix. */
	private final float far = 25;
	
	/** Eye/camera x coordinate used in view matrix. */
	public float eyeX = 0.0f;
	
	/** Eye/camera y coordinate used in view matrix. */
	public float eyeY = -1.0f;
	
	/** Eye/camera z coordinate used in view matrix. */
	public float eyeZ = 2.0f;	// 5x5
//	public float eyeZ = 3.5f;	// 7x7
//	public float eyeZ = 5.5f;	// 9x9
//	public float eyeZ = 7.5f;	// 11x11
//	public float eyeZ = 17.5f;	// 21x21
	
	/** Eye/camera x look coordinate used in view matrix. */
	public float lookX = 0.0f;
	
	/** Eye/camera y look coordinate used in view matrix. */
	public float lookY = 0.0f;

	/** Eye/camera z look coordinate used in view matrix. */
	public float lookZ = -5.0f;

	/** Eye/camera x up coordinate used in view matrix. */
	public float upX = 0.0f;
	
	/** Eye/camera y up coordinate used in view matrix. */
	public float upY = 1.0f;

	/** Eye/camera z up coordinate used in view matrix. */
	public float upZ = 0.0f;

	/** Min and max values. */
	// TODO: How to adjust these for different board sizes
	public float eyeXMin = -5;
	public float eyeXMax = 5;
	public float eyeYMin = -5;
	public float eyeYMax = 5;
	public float eyeZMin = 1; // This should not be zero
	public float eyeZMax = 21;
	
	// TODO: Are near and far values ok regarding depth buffer accuracy?
	
	/** This is used to set the tile z coordinate and also for the 
	 * ModelView calculation for touch to world coordinate calculations.*/
	public static final float tileZ = -3f;
	
	/** Scales the tile x dimensions. */
	public static final float tileScaleFactorX = 0.45f;
	
	/** Scales the tile y dimensions. */
	public static final float tileScaleFactorY = 0.45f;
	
	/** Scales the tile z dimensions. */
	public static final float tileScaleFactorZ = 0.125f;
	
	/** This is used to set the cell z coordinate and also for the 
	 * ModelView calculation for touch to world coordinate calculations.*/
	public static final float cellZ = tileZ - 2 * tileScaleFactorZ;
	
	/** Scales the cell x dimensions. */
	public static final float cellScaleFactorX = 0.5f;
	
	/** Scales the cell y dimensions. */
	public static final float cellScaleFactorY = 0.5f;
	
	/** Scales the cell z dimensions. */
	public static final float cellScaleFactorZ = 0.125f;
	
	public static final int textureOffsetTileBlue = 0;
	public static final int textureOffsetTileRed = 2 * 6 * 6;
	public static final int textureOffsetCell = 4 * 6 * 6;
	public static final int textureOffsetCredits = 6 * 6 * 6;
	
	public Board board = null;
	
	/**
	 * Initialize the model data.
	 */
	public MyGLRenderer(final Context activityContext)
	{	
		mActivityContext = activityContext;
		
		// Define points for a cube.		
		
		// X, Y, Z
		final float[] cubePositionData =
		{
				// In OpenGL counter-clockwise winding is default. This means that when we look at a triangle, 
				// if the points are counter-clockwise we are looking at the "front". If not we are looking at
				// the back. OpenGL has an optimization where all back-facing triangles are culled, since they
				// usually represent the backside of an object and aren't visible anyways.
				
				// Front face
				-1.0f, 1.0f, 1.0f,				
				-1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 
				-1.0f, -1.0f, 1.0f, 				
				1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, 1.0f,
				
				// Right face
				1.0f, 1.0f, 1.0f,				
				1.0f, -1.0f, 1.0f,
				1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, 1.0f,				
				1.0f, -1.0f, -1.0f,
				1.0f, 1.0f, -1.0f,
				
				// Back face
				1.0f, 1.0f, -1.0f,				
				1.0f, -1.0f, -1.0f,
				-1.0f, 1.0f, -1.0f,
				1.0f, -1.0f, -1.0f,				
				-1.0f, -1.0f, -1.0f,
				-1.0f, 1.0f, -1.0f,
				
				// Left face
				-1.0f, 1.0f, -1.0f,				
				-1.0f, -1.0f, -1.0f,
				-1.0f, 1.0f, 1.0f, 
				-1.0f, -1.0f, -1.0f,				
				-1.0f, -1.0f, 1.0f, 
				-1.0f, 1.0f, 1.0f, 
				
				// Top face
				-1.0f, 1.0f, -1.0f,				
				-1.0f, 1.0f, 1.0f, 
				1.0f, 1.0f, -1.0f, 
				-1.0f, 1.0f, 1.0f, 				
				1.0f, 1.0f, 1.0f, 
				1.0f, 1.0f, -1.0f,
				
				// Bottom face
				1.0f, -1.0f, -1.0f,				
				1.0f, -1.0f, 1.0f, 
				-1.0f, -1.0f, -1.0f,
				1.0f, -1.0f, 1.0f, 				
				-1.0f, -1.0f, 1.0f,
				-1.0f, -1.0f, -1.0f,
		};	
		
		// R, G, B, A
		final float[] cubeColorData =
		{				
				// Front face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				// Right face (red)
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				// Back face (white)
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,				
				1.0f, 1.0f, 1.0f, 1.0f,
				1.0f, 1.0f, 1.0f, 1.0f,
				
				// Left face (red)
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				// Top face (red)
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				
				// Bottom face (red)
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,				
				1.0f, 0.0f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
		
			// Red line colour data
				
				// Front face (red)
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				
				// Right face (red)
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				
				// Back face (red)
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				
				// Left face (red)
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				
				// Top face (red)
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				
				// Bottom face (red)
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,				
				1.0f, 0.0f, 0.0f, 0.9f,
				1.0f, 0.0f, 0.0f, 0.9f,
				
			// Blue line colour data
				
				// Front face (blue)
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				
				// Right face (blue)
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				
				// Back face (blue)
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				
				// Left face (blue)
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				
				// Top face (blue)
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				
				// Bottom face (blue)
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,				
				0.0f, 0.0f, 1.0f, 0.9f,
				0.0f, 0.0f, 1.0f, 0.9f,

		};
		
		// X, Y, Z
		// The normal is used in light calculations and is a vector which points
		// orthogonal to the plane of the surface. For a cube model, the normals
		// should be orthogonal to the points of each face.
		final float[] cubeNormalData =
		{												
				// Front face
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,				
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				
				// Right face 
				1.0f, 0.0f, 0.0f,				
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,				
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 0.0f,
				
				// Back face 
				0.0f, 0.0f, -1.0f,				
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,				
				0.0f, 0.0f, -1.0f,
				0.0f, 0.0f, -1.0f,
				
				// Left face 
				-1.0f, 0.0f, 0.0f,				
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,				
				-1.0f, 0.0f, 0.0f,
				-1.0f, 0.0f, 0.0f,
				
				// Top face 
				0.0f, 1.0f, 0.0f,			
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,				
				0.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,
				
				// Bottom face 
				0.0f, -1.0f, 0.0f,			
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f,				
				0.0f, -1.0f, 0.0f,
				0.0f, -1.0f, 0.0f
		};
		
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
				1.0f, 0.5f,

				// Textures for credits cubes
				
				// Front face
				0.5f, 0.0f,
				0.5f, 0.5f,
				1.0f, 0.0f,
				0.5f, 0.5f,
				1.0f, 0.5f,
				1.0f, 0.0f,				
				
				// Right face 
				0.5f, 0.5f, 				
				0.5f, 1.0f,
				1.0f, 0.5f,
				0.5f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.5f,	
				
				// Back face 
				0.0f, 0.5f, 				
				0.0f, 1.0f,
				0.5f, 0.5f,
				0.0f, 1.0f,
				0.5f, 1.0f,
				0.5f, 0.5f,	
				
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
		
		// Initialize the buffers.
		mCubePositions = ByteBuffer.allocateDirect(cubePositionData.length * mBytesPerFloat)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();							
		mCubePositions.put(cubePositionData).position(0);		
		
		mCubeColors = ByteBuffer.allocateDirect(cubeColorData.length * mBytesPerFloat)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();							
		mCubeColors.put(cubeColorData).position(0);
		
//		lineColors = ByteBuffer.allocateDirect(lineColorData.length * mBytesPerFloat)
//		        .order(ByteOrder.nativeOrder()).asFloatBuffer();							
//		lineColors.put(lineColorData).position(0);
				
		mCubeNormals = ByteBuffer.allocateDirect(cubeNormalData.length * mBytesPerFloat)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();							
		mCubeNormals.put(cubeNormalData).position(0);
		
		mCubeTextureCoordinates = ByteBuffer.allocateDirect(cubeTextureCoordinateData.length * mBytesPerFloat)
		.order(ByteOrder.nativeOrder()).asFloatBuffer();
		mCubeTextureCoordinates.put(cubeTextureCoordinateData).position(0);
		
//		cellTextureCoordinates = ByteBuffer.allocateDirect(cellTextureCoordinateData.length * mBytesPerFloat)
//		.order(ByteOrder.nativeOrder()).asFloatBuffer();
//		cellTextureCoordinates.put(cellTextureCoordinateData).position(0);
		
//		RenderingData.initData();
		
//		// Make some tiles
//        for (float x = -4; x < 5; x++) {
//        	for (float y = -4; y < 5; y++) {
//        		tiles.add(new Tile(this, textureOffsetTileBlue, x, y));
//        		if (y == 1) {
//        			tiles.get(tiles.size() - 1).setLetter('O');
//        		}
//        	}
//        }
		
//        // Make some cells
//        for (float x = -5; x < 6; x++) {
//        	for (float y = -5; y < 6; y++) {
//        		cells.add(new Cell(this, x, y));
//        	}
//        }

//        for (Cell cell: board.cells) {
//        	cells.add(cell);
//        }
        
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	/** Gets shader code from a text file.
	 * @param resId The resource ID of the text file.
	 * @return The shader code in a string.
	 */
	protected String getShader(int resId)
	{
		return RawResourceReader.readTextFileFromRawResource(mActivityContext, resId);
	}
	
	/** Calculate the view matrix. Called when the user adjusts zoom, pan, etc. 
	 */
	public void calculateViewMatrix() {
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, 
				lookX, lookY, lookZ, upX, upY, upZ);		
	}
	
	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) 
	{
		// Set the background clear color to black.
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		
		// Use culling to remove back faces.
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		
		// Enable depth testing
		GLES20.glEnable(GLES20.GL_DEPTH_TEST);
		
		// The below glEnable() call is a holdover from OpenGL ES 1, and is not needed in OpenGL ES 2.
		// Enable texture mapping
		// GLES20.glEnable(GLES20.GL_TEXTURE_2D);
			
		// Position the eye in front of the origin.
//		eyeX = 0.0f;
//		eyeY = -1.0f;
		// TODO: Code to change eyeZ when grid size changes
//		final float eyeZ = 4.0f; // For 5x5
//		eyeZ = 3.5f; // For 9x9

		// We are looking toward the distance
//		lookX = 0.0f;
//		lookY = 0.0f;
//		lookZ = -5.0f;

		// Set our up vector. This is where our head would be pointing were we holding the camera.
//		upX = 0.0f;
//		upY = 1.0f;
//		upZ = 0.0f;

		// Set the view matrix. This matrix can be said to represent the camera position.
		// NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination of a model and
		// view matrix. In OpenGL 2, we can keep track of these matrices separately if we choose.
		Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);		

		// The per_pixel_vertex_shader_tex_and_light and per_pixel_fragment_shader_tex_and_light
		// shaders do not pass in colour data. The per_pixel_vertex_shader and per_pixel_fragment_shader
		// do pass in colour data so the textures colours may be modified.
		final String vertexShader = getShader(R.raw.per_pixel_vertex_shader);   		
 		final String fragmentShader = getShader(R.raw.per_pixel_fragment_shader);			
		
		final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, vertexShader);		
		final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShader);		
		
		mProgramHandle = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle, 
				new String[] {"a_Position",  "a_Color", "a_Normal", "a_TexCoordinate"});								                                							       
        
		// TODO: Delete point stuff.
		// Define a simple shader program for our point.
//        final String pointVertexShader = getShader(R.raw.point_vertex_shader);        	       
//        final String pointFragmentShader = getShader(R.raw.point_fragment_shader);
//        
//        final int pointVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
//        final int pointFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
//        mPointProgramHandle = ShaderHelper.createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle, 
//        		new String[] {"a_Position"}); 
        
        // Define a shader program without texture.
        final String noTexVertexShader = getShader(R.raw.per_pixel_vertex_shader_no_tex);        	       
        final String noTexFragmentShader = getShader(R.raw.per_pixel_fragment_shader_no_tex);
        
        final int noTexVertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, noTexVertexShader);
        final int noTexFragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, noTexFragmentShader);
        noTexProgramHandle = ShaderHelper.createAndLinkProgram(noTexVertexShaderHandle, noTexFragmentShaderHandle, 
        		new String[] {"a_Position"}); 
        
        // Load the texture atlas
        cubeTextureDataHandle = TextureHelper.loadTexture(mActivityContext,
        		R.drawable.atlas2);
//        cellTextureDataHandle = TextureHelper.loadTexture(mActivityContext,
//        		R.drawable.board1);
	}	
	
	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) 
	{
		// Set the OpenGL viewport to the same size as the surface.
		GLES20.glViewport(0, 0, width, height);
		this.width = width;
		this.height = height;

		// Create a new perspective projection matrix. The height will stay the same
		// while the width will vary as per aspect ratio.
		float scaleFactor = 1;//2.2f;
		final float ratio = (float) width / height;
		Log.v("ratio", ""+ratio);
		final float left;// = -ratio;
		final float right;// = ratio;
		final float bottom;// = -1.0f * scaleFactor;
		final float top;// = 1.0f * scaleFactor;
		if (height > width) {
			left = -ratio * scaleFactor;
			right = ratio * scaleFactor;
			bottom = -1.0f * scaleFactor;
			top = 1.0f * scaleFactor;
//			eyeZ = (right - left) * (-tileZ) / (board.sizeX - (right - left));
//			Log.v("board.sizeX", "" + board.sizeX);
//			Log.v("eyeZ", "" + eyeZ);
		} else {
			left = -1 * scaleFactor;
			right = 1 * scaleFactor;
			bottom = -1.0f * scaleFactor / ratio;
			top = 1.0f * scaleFactor / ratio;
		}
		
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
//		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

//        int[] maxTextureSize = new int[1];
//        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
//        Log.v("maxTextureSize", "" + maxTextureSize[0]);
        
}	

	@Override
	public void onDrawFrame(GL10 glUnused) 
	{
		// Background colour
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		
		// Clear the screen and the depth buffer
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		
        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mProgramHandle);
        
        // Set program handles for cube drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix"); 
        mLightPosHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal"); 
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_TexCoordinate");
        
        // Set the active texture unit to texture unit 0.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        
        // Bind the texture to this unit.
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, cubeTextureDataHandle);
        
        // Tell the texture uniform sampler to use this texture in the shader by binding to texture unit 0.
        GLES20.glUniform1i(mTextureUniformHandle, 0);        
        
        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, tileZ);      
//        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
               
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);                        
        
        if (board != null) {
        	// Draw the tiles.
        	for (Cube tile: board.tiles) {
        		Matrix.setIdentityM(mModelMatrix, 0);
        		Matrix.translateM(mModelMatrix, 0, tile.x, tile.y, tileZ + tile.z);
        		Matrix.rotateM(mModelMatrix, 0, tile.rotationY, 0.0f, 1.0f, 0.0f);
        		Matrix.rotateM(mModelMatrix, 0, tile.rotationZ, 0.0f, 0.0f, 1.0f);
        		Matrix.scaleM(mModelMatrix, 0, tileScaleFactorX, tileScaleFactorY,
        				tileScaleFactorZ);
        		tile.draw(mModelMatrix);
        	}
        
        	// Draw the temporary tiles.
        	for (Cube tile: board.tempTiles) {
        		Matrix.setIdentityM(mModelMatrix, 0);
        		Matrix.translateM(mModelMatrix, 0, tile.x, tile.y, tileZ + tile.z);
        		Matrix.rotateM(mModelMatrix, 0, tile.rotationY, 0.0f, 1.0f, 0.0f);
        		Matrix.rotateM(mModelMatrix, 0, tile.rotationZ, 0.0f, 0.0f, 1.0f);
        		Matrix.scaleM(mModelMatrix, 0, tileScaleFactorX, tileScaleFactorY,
        				tileScaleFactorZ);
        		tile.draw(mModelMatrix);
        	}
        
        	// Draw the cells.
        	for (Cube cell: board.cells) {
        		Matrix.setIdentityM(mModelMatrix, 0);
        		Matrix.translateM(mModelMatrix, 0, cell.x, cell.y, cellZ);
        		Matrix.rotateM(mModelMatrix, 0, cell.rotationY, 0.0f, 1.0f, 0.0f);
        		Matrix.rotateM(mModelMatrix, 0, cell.rotationZ, 0.0f, 0.0f, 1.0f);
        		Matrix.scaleM(mModelMatrix, 0, cellScaleFactorX, cellScaleFactorY,
        				cellScaleFactorZ);
        		cell.draw(mModelMatrix);
        	}
        	
        	// Draw credits cubes
        	long period = 10000L;
            long time = SystemClock.elapsedRealtime() % period;
            float angle = (360f / period) * ((int) time);
            float dz = (float) Math.sin(((float)Math.PI * 4 / period) * ((int) time));
        	for (Cube credit: board.creditsCubes) {
        		Matrix.setIdentityM(mModelMatrix, 0);
//        		if (eyeZ - 1.5f < credit.scaleFactorZ - credit.z - dz) {
//        			dz = -credit.scaleFactorZ - credit.z - 1.5f;
//        		}
        		Matrix.translateM(mModelMatrix, 0, 0, 0, eyeZ - 4);
        		Matrix.translateM(mModelMatrix, 0, 0, dz / 2, dz / 4);
        		Matrix.rotateM(mModelMatrix, 0, angle, 0.0f, 1.0f, 0.0f);
        		Matrix.translateM(mModelMatrix, 0, credit.x, credit.y, credit.z);
        		Matrix.rotateM(mModelMatrix, 0, -angle, 0.0f, 1.0f, 0.0f);
//        		Matrix.rotateM(mModelMatrix, 0, credit.rotationY, 0.0f, 1.0f, 0.0f);
        		Matrix.rotateM(mModelMatrix, 0, credit.rotationZ, 0.0f, 0.0f, 1.0f);
        		Matrix.scaleM(mModelMatrix, 0, credit.scaleFactorX,
        				credit.scaleFactorY, credit.scaleFactorZ);
        		Matrix.rotateM(mModelMatrix, 0, credit.rotationY, 0.0f, 1.0f, 0.0f);
        		credit.draw(mModelMatrix);
        	}
        }
        
        // Change the shader program to one that does not use textures
        GLES20.glUseProgram(noTexProgramHandle);
        // Enable alpha blending
        GLES20.glEnable(GLES20.GL_BLEND);
//        GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
//        GLES20.glBlendFunc(GLES20.GL_DST_ALPHA, GLES20.GL_ZERO);

        // Reset the shader program handles for line drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(noTexProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(noTexProgramHandle, "u_MVMatrix"); 
        mLightPosHandle = GLES20.glGetUniformLocation(noTexProgramHandle, "u_LightPos");
//        mTextureUniformHandle = GLES20.glGetUniformLocation(noTexProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(noTexProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(noTexProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(noTexProgramHandle, "a_Normal"); 

        // Draw the lines
        if (board != null) {
        	for (Cube line: board.lines) {
        		line.draw();
        	}
        }
        
//        for (float x = 1; x < 3; x++) {
//        	Line line;
//        	line = new Line(this, x, x, x + 2, x, Line.COLOUR_RED);
//        	line.z = tileZ + 0.2f;
//        	line.draw();
//        	line = new Line(this, x, x, x, x + 2, Line.COLOUR_BLUE);
//        	line.z = tileZ + 0.2f;
//        	line.draw();
//        	line = new Line(this, x, x, x + 2, x + 2, Line.COLOUR_RED);
//        	line.z = tileZ + 0.2f;
//        	line.draw();
//        	line = new Line(this, -x, -x, -x - 2, -x - 2, Line.COLOUR_BLUE);
//        	line.z = tileZ + 0.2f;
//        	line.draw();
//        	line = new Line(this, x, x, x + 2, x - 2, Line.COLOUR_RED);
//        	line.z = tileZ + 0.2f;
//        	line.draw();
//        	line = new Line(this, x, x, x - 2, x + 2, Line.COLOUR_BLUE);
//        	line.z = tileZ + 0.2f;
//        	line.draw();
//        }
        
		GLES20.glDisable(GLES20.GL_BLEND);
	}				
	
	/** Calculate world coordinates from touch coordinates. gluUnProject code 
	 * adapted (with one major change to interpolation code) from:<br>
	 * http://gamedev.stackexchange.com/questions/26292/opengl-es-2-0-gluunproject
	 * @param x Touch x.
	 * @param y Touch y.
	 * @param knownZ The known z value for the 3D object clicked on.
	 * @return A PointF object with x and y coordinates in the 3D world space.
	 */
	public PointF getWorldXY(float x, float y, float knownZ) {
		float[] nearPos = new float[4];
		float[] farPos = new float[4];
		float[] modelViewMatrix = new float[16];
		modelViewMatrix = getModelViewMatrix();
		int[] viewPortMatrix = new int[]{0, 0, width, height};
		boolean unprojNear = (GLU.gluUnProject(x, height - y, 0, 
				modelViewMatrix, 0, mProjectionMatrix, 0, 
				viewPortMatrix, 0, nearPos, 0) == GLES20.GL_TRUE);
		boolean unprojFar = (GLU.gluUnProject(x, height - y, 1, 
				modelViewMatrix, 0, mProjectionMatrix, 0, 
				viewPortMatrix, 0, farPos, 0) == GLES20.GL_TRUE);
		float unprojectedX = 0;
		float unprojectedY = 0;
		if (unprojNear && unprojFar) {
			// To convert the transformed 4D vector to 3D, you must divide
			// it by the W component
			nearPos = convertTo3d(nearPos);
			farPos = convertTo3d(farPos);
			Log.v("nearPos", ""+nearPos[0]+", "+nearPos[1]+", "+nearPos[2]+", "+nearPos[3]);
			Log.v("farPos", ""+farPos[0]+", "+farPos[1]+", "+farPos[2]+", "+farPos[3]);

			// The following interpolation code from the original example on
			// gamedev.stackexchange.com is commented out because it is
			// incorrect, at least in our application.
//			// Use the near and far instead of the assumed camera position.
//			float perspectiveNear = near;
//			float perspectiveFar = far;
//			unprojectedX = (((farPos[0] - nearPos[0]) 
//					/ (perspectiveFar - perspectiveNear)) * nearPos[2]) 
//					+ nearPos[0];
//			unprojectedY = (((farPos[1] - nearPos[1]) 
//					/ (perspectiveFar - perspectiveNear)) * nearPos[2])  
//					+ nearPos[1];

			// This is the correct way to get the final x, y values.
			// After we get two 3D points from gluUnProject we need to
			// interpolate along the z axis to get the correct x and y
			// coordinates using the known z value for the 3D object we clicked
			// on.
			float u = (knownZ - nearPos[2]) / (farPos[2] - nearPos[2]);
			unprojectedX = nearPos[0] + u * (farPos[0] - nearPos[0]);
			unprojectedY = nearPos[1] + u * (farPos[1] - nearPos[1]);
			
			Log.v("unprojectedXY", ""+(unprojectedX)+", "+(unprojectedY));
		}
		return new PointF(unprojectedX, unprojectedY);
		// TODO: Handling cases where gluUnProject returns GL_FALSE?
	}
	
	/** Get the ModelView matrix needed for gluUnProject calculations.
	 * @return The model matrix multiplied by the view matrix.
	 */
	private float[] getModelViewMatrix() {
		float[] modelMatrix = new float[16];
		float[] resultMatrix = new float[16];
		// We don't really need the modelViewMatrix unless the world space,
		// not just objects within that space, has been transformed.
		Matrix.setIdentityM(modelMatrix, 0);
        Matrix.multiplyMM(resultMatrix, 0, mViewMatrix, 0, modelMatrix, 0);   
		return resultMatrix;
	}
	
    /** Convert a 4D (x, y, z, w) vector to a 3D (x, y, z) vector.<br>
     * From: http://gamedev.stackexchange.com/questions/26292/opengl-es-2-0-gluunproject
     * @param vector The 4D vector
     * @return A 3D vector
     */
    private float[] convertTo3d(float[] vector) {
        float[] result = new float[4];
        for (int index = 0; index < vector.length; index++) {
            result[index] = vector[index] / vector[3];
        }
        return result;
    }
    
    /** Takes a PointF holding world x, y coordinates and searches for a cube
     * which covers those coordinates. Since the Cell, Tile and Line classes are
     * subclasses of the Cube class this method can search for any of these
     * objects and the Cube returned by the method can be cast to the desired
     * type. For example:<br>Tile tile = (Tile) getSelectedCube(p, tiles);
     * @param p The coordinates in the world space.
     * @param cubes An ArrayList containing references to Cube objects.
     * @return A Cube object.
     */
    public Cube getSelectedCube(PointF p, ArrayList<Cube> cubes) {
    	float dx;
    	float dy;
		for (Cube cube: cubes) {
			dx = cube.scaleFactorX;
			dy = cube.scaleFactorY;
			if (p.x >= cube.x - dx && p.x <= cube.x + dx
					&& p.y >= cube.y - dy && p.y <= cube.y + dy) {
				return cube;
			}
		}
		return null;
    }
    
}
