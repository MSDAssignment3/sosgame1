package com.example.sosgame1;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.view.View.*;

public class MyGLSurfaceView extends GLSurfaceView implements OnTouchListener {

//	private final MyGLRenderer mRenderer;
	// Can't set a final variable outside of the constructor (I think) 
	public MyGLRenderer mRenderer;
	private ObjectAnimator anim;
	private boolean animationInProgress = false;
    /** Handles pinch gestures for zooming. */
	private ScaleGestureDetector scaleDetector;
	private float scaleFactor;
	private float minScale;
	private float maxScale;

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
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        scaleFactor = mRenderer.eyeZ;
    }
    
    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

	// Based on:
	// http://android-developers.blogspot.co.nz/2010/06/making-sense-of-multitouch.html
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

    @Override
    public boolean onTouch(View v, MotionEvent e) {
    	scaleDetector.onTouchEvent(e);
        float x = e.getX();
        float y = e.getY();
        
        if (!animationInProgress && !scaleDetector.isInProgress()) {
        	switch (e.getAction()) {
        	case MotionEvent.ACTION_DOWN:
        		PointF p = mRenderer.getWorldXY(x, y, 
        				mRenderer.cubeZ + mRenderer.cubeZScaleFactor);
        		Cube foo = mRenderer.getSelectedCube(p);
        		
        		if (foo != null) {
        			animationInProgress = true;
        			setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        			AnimatorSet animSet = new AnimatorSet();
        			ObjectAnimator zAnim = ObjectAnimator.ofFloat(foo, "z",
        					0, 1, 1, 0);
        			zAnim.setDuration(1000);
        			float start;
        			float end;
        			if (foo.yRotation == 0) {
        				start = 0;
        				end = 180;
        			} else {
        				start = 180;
        				end = 0;
        			}
        			anim = ObjectAnimator.ofFloat(foo, "yRotation", start, start,
        					end, end);
        			anim.setDuration(1000);
        			anim.addListener(new AnimatorListenerAdapter() {
        				public void onAnimationEnd(Animator animation) {
        					setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        					animationInProgress = false;
        				}
        			});
        			animSet.playTogether(zAnim, anim);
        			animSet.start();
        		}
        		break;
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
    
//	@Override
//	public boolean onTouch(View v, MotionEvent event) {
//		// TODO Auto-generated method stub
//		return false;
//	}

}