package com.example.sosgame1;


import com.example.sosgame1.controller.LogicControl;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.*;

public class GLESSurfaceView extends GLSurfaceView 
	implements OnTouchListener {

	public GLRenderer renderer;
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

	private Tile sTile;
	private Tile oTile;
	private Tile chosenTile;
	private PointF p = new PointF();
	
    public GLESSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
        init(context);
	}

    public GLESSurfaceView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        renderer = new GLRenderer(context);
        setRenderer(renderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
        setOnTouchListener(this);
        
        // Set up a scale detector to handle pinch zooming
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        scaleFactor = renderer.eyeZ;
        
        // Set up a gesture listener and detector to handle other gestures
        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(context, gestureListener);
        
        // Create the board
        renderer.setBoard(new Board(renderer, 5, 5));
        
    }
    
    public void setController(LogicControl controller) {
    	this.controller = controller;
    }
    
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
	        float tempScale = Math.max(scaleFactor, renderer.eyeZMin);
	        scaleFactor = Math.min(tempScale, renderer.eyeZMax);
	        renderer.eyeZ = scaleFactor;
	        Log.v("eyeZ", ""+renderer.eyeZ);
			renderer.calculateViewMatrix();
			requestRender();
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

        	switch (mode) {
        	case MODE_IDLE:
            	p = renderer.getWorldXY(x, y, 
            			GLRenderer.cellZ + GLRenderer.cellScaleFactorZ);
            	tappedCell = (Cell) renderer.getSelectedCube(p, renderer.board.cells);
        		
            	if (tappedCell != null) {
            		mode = MODE_WAIT_FOR_CHOICE;
            		sTile = new Tile(renderer,
            				Tile.COLOUR_BLUE, tappedCell.x - GLRenderer.tileScaleFactorX,
            				tappedCell.y, 'S');
            		sTile.z += GLRenderer.tileScaleFactorZ * 2;
            		oTile = new Tile(renderer,
            				Tile.COLOUR_BLUE, tappedCell.x + GLRenderer.tileScaleFactorX,
            				tappedCell.y, 'O');
            		oTile.z += GLRenderer.tileScaleFactorZ * 2;
        			// Safe way to communicate with renderer thread
        			queueEvent(new Runnable() {
						@Override
						public void run() {
							renderer.board.tempTiles.clear();
							renderer.board.tempTiles.add(sTile);
							renderer.board.tempTiles.add(oTile);
						}
					});
            		requestRender();
            	}
        		break;
        	case MODE_WAIT_FOR_CHOICE:
            	p = renderer.getWorldXY(x, y, 
            			GLRenderer.tileZ + GLRenderer.tileScaleFactorZ * 2);
            	chosenTile = (Tile) renderer.getSelectedCube(p,
            			renderer.board.tempTiles);

            	if (chosenTile != null) {
        			// Safe way to communicate with renderer thread
        			queueEvent(new Runnable() {
						@Override
						public void run() {
							renderer.board.tiles.add(chosenTile);
						}
					});
                	animationInProgress = true;
            		AnimatorSet animSet = new AnimatorSet();
            		anim = ObjectAnimator.ofFloat(chosenTile, "z",
            				chosenTile.z, 
            				chosenTile.z - GLRenderer.tileScaleFactorZ * 2);
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
        			// Safe way to communicate with renderer thread
        			queueEvent(new Runnable() {
						@Override
						public void run() {
							renderer.board.tempTiles.clear();
						}
					});
            		// Start continuous screen updates for duration of animation
            		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            		animSet.start();
                	
                	// Test calling Ar's method
                	if (Math.abs(chosenTile.x) < 3 && Math.abs(chosenTile.y) < 3) {
                		PointF pt = new PointF(chosenTile.x, chosenTile.y);
                		Point pt2 = renderer.board.worldToBoardXY(pt);
                		controller.getAndCheck(pt2.y, pt2.x, "" + chosenTile.letter);
                	}

            	} else {
        			// Safe way to communicate with renderer thread
        			queueEvent(new Runnable() {
						@Override
						public void run() {
							renderer.board.tempTiles.clear();
						}
					});
            		requestRender();
            	}
            	mode = MODE_IDLE;
        		break;
        	}
        	
        }
	}
	
	private void doPan(MotionEvent e1, MotionEvent e2,
			float distanceX, float distanceY) {
		float xOffset = renderer.eyeX - renderer.lookX;
		float yOffset = renderer.eyeY - renderer.lookY;
		// Sensitivity adjustment factor for different screen resolutions
		float factor = 10f / Math.min(renderer.width, renderer.height);
		renderer.eyeX += distanceX * factor;
		renderer.eyeY -= distanceY * factor;
		renderer.lookX = renderer.eyeX - xOffset;
		renderer.lookY = renderer.eyeY - yOffset;
		renderer.calculateViewMatrix();
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
        	case MotionEvent.ACTION_DOWN:
        		break;
        	case MotionEvent.ACTION_MOVE:
        		break;
        	}
        }
        return true;
    }

}