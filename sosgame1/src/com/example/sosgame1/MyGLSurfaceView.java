package com.example.sosgame1;


import com.example.sosgame1.controller.LogicControl;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.view.View.*;

public class MyGLSurfaceView extends GLSurfaceView 
	implements OnTouchListener {

	public MyGLRenderer mRenderer;
	private ObjectAnimator anim;
	private boolean animationInProgress = false;
    
	/** Handles pinch gestures for zooming. */
	private ScaleGestureDetector scaleDetector;
	
	/** Scale factor returned by the scale detector. */
	private float scaleFactor;

	/** Handles gestures other than pinch zooming. */
	private SimpleOnGestureListener gestureListener;
	
	private GestureDetector gestureDetector;
	private float minScale;
	private float maxScale;
	
    private LogicControl controller = null;

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
        
        mRenderer.setBoard(new Board(mRenderer, 9, 9));
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

		@Override
		public void onLongPress(MotionEvent e) {
			doSingleTap(e);
			super.onLongPress(e);
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
        			mRenderer.cubeZ + mRenderer.cubeZScaleFactor);
        	Tile foo = mRenderer.getSelectedTile(p);

        	if (foo != null) {
            	
            	// Test calling Ar's method
            	if (Math.abs(foo.x) < 3 && Math.abs(foo.y) < 3) {
            		controller.getAndCheck((int) foo.y + 2, (int) foo.x + 2, 
            				"" + foo.letter);
            	}

            	animationInProgress = true;
        		// Start continuous screen updates for duration of animation
        		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        		AnimatorSet animSet = new AnimatorSet();
        		ObjectAnimator zAnim = ObjectAnimator.ofFloat(foo, "z",
        				0, 1, 1, 0);
        		// Slightly shorter than rotation to make sure z gets set to zero
        		zAnim.setDuration(950);
        		float start;
        		float end;
        		if (foo.rotationY == 0) {
        			start = 0;
        			end = 180;
        		} else {
        			start = 180;
        			end = 0;
        		}
        		anim = ObjectAnimator.ofFloat(foo, "rotationY", start, start,
        				end, end);
        		anim.setDuration(1000);
        		anim.addListener(new AnimatorListenerAdapter() {
        			public void onAnimationEnd(Animator animation) {
        				// Stop continuous screen updates to save battery
        				setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        				animationInProgress = false;
        			}
        		});
        		animSet.playTogether(zAnim, anim);
        		animSet.start();
        	}
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