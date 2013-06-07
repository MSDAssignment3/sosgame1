package com.example.sosgame1;


import com.example.sosgame1.controller.LogicControl;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.*;

public class MyGLSurfaceView extends GLSurfaceView 
	implements OnTouchListener {

	public MyGLRenderer mRenderer;
	private ObjectAnimator anim;
	private ObjectAnimator anim2;
	private boolean animationInProgress = false;
    
	/** Handles pinch gestures for zooming. */
	private ScaleGestureDetector scaleDetector;
	
	/** Scale factor returned by the scale detector. */
	private float scaleFactor;

	/** Handles gestures other than pinch zooming. */
	private SimpleOnGestureListener gestureListener;
	
	private GestureDetector gestureDetector;
	
    private LogicControl controller = null;
    
    private static final int MODE_IDLE = 0;
    private static final int MODE_WAIT_FOR_CHOICE = 1;
    private int mode = MODE_IDLE;
    
    private Cell tappedCell = null;

    public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context);
	}

    public MyGLSurfaceView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
        setOnTouchListener(this);
        
        // Set up a scale detector to handle pinch zooming
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        scaleFactor = mRenderer.eyeZ;
        
        // Set up a gesture listener and detector to handle other gestures
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(context, gestureListener);
        
        // Create the board
        mRenderer.setBoard(new Board(mRenderer, 5, 5));
        
        // Add some tiles
//        for (int column = 1; column < 4; column++) {
//        	for (int row = 0; row < 5; row++) {
//        		mRenderer.board.addTile(row, column, Tile.COLOUR_BLUE, 'S');
//        	}
//        }

//        // For screenshot
//        mRenderer.board.addTile(2, 2, Tile.COLOUR_BLUE, 'S');
//        mRenderer.board.addTile(3, 3, Tile.COLOUR_RED, 'O');
//        mRenderer.board.addTile(4, 4, Tile.COLOUR_BLUE, 'S');
//        mRenderer.board.addLine(new Point(4,4), new Point(2,2), Line.COLOUR_RED);
//        mRenderer.board.addTile(2, 0, Tile.COLOUR_BLUE, 'S');
//        mRenderer.board.addTile(2, 1, Tile.COLOUR_RED, 'O');
//        mRenderer.board.addLine(new Point(0,2), new Point(2,2), Line.COLOUR_BLUE);
//        mRenderer.board.addTile(3, 1, Tile.COLOUR_BLUE, 'S');
//        mRenderer.board.addTile(0, 5, Tile.COLOUR_RED, 'O');
//        mRenderer.board.addTile(1, 1, Tile.COLOUR_BLUE, 'S');
//        mRenderer.board.addLine(new Point(1,1), new Point(1,3), Line.COLOUR_RED);
//        mRenderer.board.addTile(3, 5, Tile.COLOUR_RED, 'O');
//        mRenderer.board.addTile(2, 6, Tile.COLOUR_BLUE, 'S');
//        mRenderer.board.addLine(4, 4, 2, 6, Line.COLOUR_BLUE);
        
    }
    
    public void setController(LogicControl controller) {
    	this.controller = controller;
    }
    
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    private class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			doSingleTap(e);
			return super.onSingleTapConfirmed(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			doPan(e1, e2, distanceX, distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

    }
    
	/** Handle pinch zoom. Based on:<br>
	 * http://android-developers.blogspot.co.nz/2010/06/making-sense-of-multitouch.html
	 */
	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
		@Override
		public boolean onScale(ScaleGestureDetector detector) {
	        scaleFactor /= detector.getScaleFactor();
	        float tempScale = Math.max(scaleFactor, mRenderer.eyeZMin);
	        scaleFactor = Math.min(tempScale, mRenderer.eyeZMax);
	        mRenderer.eyeZ = scaleFactor;
	        Log.v("eyeZ", ""+mRenderer.eyeZ);
			mRenderer.calculateViewMatrix();
	        return true;
	    }
	}

	/** Handle single tap.
	 * @param e MotionEvent
	 */
	private void doSingleTap(MotionEvent e) {
        float x = e.getX();
        float y = e.getY();
        
        if (!animationInProgress && !scaleDetector.isInProgress()) {
        	PointF p = mRenderer.getWorldXY(x, y, 
        			MyGLRenderer.tileZ + MyGLRenderer.tileScaleFactorZ);
        	Tile foo = (Tile) mRenderer.getSelectedCube(p, mRenderer.board.tiles);

        	switch (mode) {
        	case MODE_IDLE:
            	PointF p2 = mRenderer.getWorldXY(x, y, 
            			MyGLRenderer.cellZ + MyGLRenderer.cellScaleFactorZ);
            	tappedCell = (Cell) mRenderer.getSelectedCube(p2, mRenderer.board.cells);
        		
            	if (tappedCell != null) {
            		mode = MODE_WAIT_FOR_CHOICE;
            		mRenderer.board.tempTiles.clear();
            		Tile sTile = new Tile(mRenderer,
            				Tile.COLOUR_BLUE, tappedCell.x - MyGLRenderer.tileScaleFactorX,
            				tappedCell.y, 'S');
            		sTile.z += MyGLRenderer.tileScaleFactorZ * 2;
            		mRenderer.board.tempTiles.add(sTile);
            		Tile oTile = new Tile(mRenderer,
            				Tile.COLOUR_BLUE, tappedCell.x + MyGLRenderer.tileScaleFactorX,
            				tappedCell.y, 'O');
            		oTile.z += MyGLRenderer.tileScaleFactorZ * 2;
            		mRenderer.board.tempTiles.add(oTile);
            		requestRender();
            	}
        		break;
        	case MODE_WAIT_FOR_CHOICE:
            	PointF p3 = mRenderer.getWorldXY(x, y, 
            			MyGLRenderer.tileZ + MyGLRenderer.tileScaleFactorZ * 2);
            	Tile chosenTile = (Tile) mRenderer.getSelectedCube(p3,
            			mRenderer.board.tempTiles);

            	if (chosenTile != null) {
            		mRenderer.board.tiles.add(chosenTile);
                	animationInProgress = true;
            		AnimatorSet animSet = new AnimatorSet();
            		anim = ObjectAnimator.ofFloat(chosenTile, "z",
            				chosenTile.z, 
            				chosenTile.z - MyGLRenderer.tileScaleFactorZ * 2);
            		anim.setDuration(300);
            		anim2 = ObjectAnimator.ofFloat(chosenTile, "x",
            				chosenTile.x, tappedCell.x);
            		anim2.setDuration(300);
            		anim.addListener(new AnimatorListenerAdapter() {
            			public void onAnimationEnd(Animator animation) {
            				// Stop continuous screen updates to save battery
            				setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            				animationInProgress = false;
            				requestRender();
            			}
            		});
            		animSet.playTogether(anim, anim2);
            		mRenderer.board.tempTiles.clear();
            		// Start continuous screen updates for duration of animation
            		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            		animSet.start();
                	
                	// Test calling Ar's method
                	if (Math.abs(chosenTile.x) < 3 && Math.abs(chosenTile.y) < 3) {
                		PointF pt = new PointF(chosenTile.x, chosenTile.y);
                		Point pt2 = mRenderer.board.worldToBoardXY(pt);
                		controller.getAndCheck(pt2.y, pt2.x, "" + chosenTile.letter);
                	}

            	} else {
            		mRenderer.board.tempTiles.clear();
            		requestRender();
            	}
            	mode = MODE_IDLE;
        		break;
        	}
        	
//        	if (foo != null) {
//            	animationInProgress = true;
//        		// Start continuous screen updates for duration of animation
//        		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        		AnimatorSet animSet = new AnimatorSet();
//        		ObjectAnimator zAnim = ObjectAnimator.ofFloat(foo, "z",
//        				0, 1, 1, 0);
//        		zAnim.setDuration(1000);
//        		float start;
//        		float end;
//        		if (foo.rotationY == 0) {
//        			start = 0;
//        			end = 180;
//        		} else {
//        			start = 180;
//        			end = 0;
//        		}
//        		anim = ObjectAnimator.ofFloat(foo, "rotationY", start, start,
//        				end, end);
//        		anim.setDuration(1000);
//        		anim.addListener(new AnimatorListenerAdapter() {
//        			public void onAnimationEnd(Animator animation) {
//        				// Stop continuous screen updates to save battery
//        				setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        				animationInProgress = false;
//        				requestRender();
//        			}
//        		});
//        		animSet.playTogether(zAnim, anim);
//        		animSet.start();
//        	}
        }
	}
	
	private void doPan(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {
		float xOffset = mRenderer.eyeX - mRenderer.lookX;
		float yOffset = mRenderer.eyeY - mRenderer.lookY;
		// Sensitivity adjustment factor for different screen resolutions
		float factor = 10f / Math.min(mRenderer.width, mRenderer.height);
		mRenderer.eyeX += distanceX * factor;
		mRenderer.eyeY -= distanceY * factor;
		mRenderer.lookX = mRenderer.eyeX - xOffset;
		mRenderer.lookY = mRenderer.eyeY - yOffset;
		mRenderer.calculateViewMatrix();
		requestRender();
	}
	
    public boolean onTouch(View v, MotionEvent e) {
    	// Call the scale gesture detector to handle pinch zooming
    	scaleDetector.onTouchEvent(e);
    	// Call the simple gesture detector to handle other gestures
    	gestureDetector.onTouchEvent(e);
        float x = e.getX();
        float y = e.getY();
        
        if (!animationInProgress && !scaleDetector.isInProgress()) {
        	switch (e.getAction()) {
//        	case MotionEvent.ACTION_DOWN:
//        		PointF p = mRenderer.getWorldXY(x, y, 
//        				mRenderer.cubeZ + mRenderer.cubeZScaleFactor);
//        		Cube foo = mRenderer.getSelectedCube(p);
//        		
//        		if (foo != null) {
//        			animationInProgress = true;
//        			setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
//        			AnimatorSet animSet = new AnimatorSet();
//        			ObjectAnimator zAnim = ObjectAnimator.ofFloat(foo, "z",
//        					0, 1, 1, 0);
//        			zAnim.setDuration(1000);
//        			float start;
//        			float end;
//        			if (foo.rotationY == 0) {
//        				start = 0;
//        				end = 180;
//        			} else {
//        				start = 180;
//        				end = 0;
//        			}
//        			anim = ObjectAnimator.ofFloat(foo, "rotationY", start, start,
//        					end, end);
//        			anim.setDuration(1000);
//        			anim.addListener(new AnimatorListenerAdapter() {
//        				public void onAnimationEnd(Animator animation) {
//        					setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
//        					animationInProgress = false;
//        				}
//        			});
//        			animSet.playTogether(zAnim, anim);
//        			animSet.start();
//        		}
//        		break;
        	case MotionEvent.ACTION_MOVE:
        		if (!scaleDetector.isInProgress()) {
        			float dx = x - mPreviousX;
        			float dy = y - mPreviousY;

        			// reverse direction of rotation above the mid-line
        			if (y > getHeight() / 2) {
        				dx = dx * -1 ;
        			}

        			// reverse direction of rotation to left of the mid-line
        			if (x < getWidth() / 2) {
        				dy = dy * -1 ;
        			}

        			// mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
        		}
        		break;
        	}
        	requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;
        requestRender();
        return true;
    }

}