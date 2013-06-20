/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// TODO Is this all that is required for the Apache license? Change copyright?
// Words about what changes were made?

package com.example.sosgame1;

import com.example.sosgame1.GLESSurfaceView;
import com.example.sosgame1.controller.LogicControl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity implements OnClickListener,
	SeekBar.OnSeekBarChangeListener {

    private GLESSurfaceView myGLView;
    private RelativeLayout mainView;
    private RelativeLayout viewSplash;
    private View viewAdjustView = null;
    private float xOffset;
    private float yOffset;
    private boolean isPanningX = false;
    private boolean isPanningY = false;
    private LogicControl controller = null;
    private boolean rollCredits = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Create an instance of the logic controller
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_page);
		((ImageButton) findViewById(R.id.btnPlay)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnSettings)).setOnClickListener(this);
		viewSplash = (RelativeLayout) findViewById(R.id.rlSplash);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		
		LayoutInflater inflater = getLayoutInflater();
		
		switch (v.getId()) {
		case R.id.btnView:
			viewAdjustView = inflater.inflate(R.layout.view_adjust, null);
			if (viewAdjustView != null) {
				mainView.addView(viewAdjustView);
		        ((SeekBar) findViewById(R.id.seekX)).setOnSeekBarChangeListener(this);
		        ((SeekBar) findViewById(R.id.seekY)).setOnSeekBarChangeListener(this);
		        ((SeekBar) findViewById(R.id.seekZ)).setOnSeekBarChangeListener(this);
		        ((SeekBar) findViewById(R.id.seekPanX)).setOnSeekBarChangeListener(this);
		        ((SeekBar) findViewById(R.id.seekPanY)).setOnSeekBarChangeListener(this);
		        if (myGLView != null) {
		        	int setting;
		        	setting = toProgress(myGLView.renderer.eyeX, 
		        			myGLView.renderer.eyeXMin, 
		        			myGLView.renderer.eyeXMax);
		        	((SeekBar) findViewById(R.id.seekX)).setProgress(setting);
		        	setting = toProgress(myGLView.renderer.eyeY, 
		        			myGLView.renderer.eyeYMin, 
		        			myGLView.renderer.eyeYMax);
		        	((SeekBar) findViewById(R.id.seekY)).setProgress(setting);
		        	setting = toProgress(myGLView.renderer.eyeZ, 
		        			myGLView.renderer.eyeZMin, 
		        			myGLView.renderer.eyeZMax);
		        	((SeekBar) findViewById(R.id.seekZ)).setProgress(setting);
		        	setting = toProgress(myGLView.renderer.lookX, 
		        			myGLView.renderer.eyeXMin, 
		        			myGLView.renderer.eyeXMax);
		        	((SeekBar) findViewById(R.id.seekPanX)).setProgress(setting);
		        	setting = toProgress(myGLView.renderer.lookY, 
		        			myGLView.renderer.eyeYMin, 
		        			myGLView.renderer.eyeYMax);
		        	((SeekBar) findViewById(R.id.seekPanY)).setProgress(setting);
		        	xOffset = myGLView.renderer.eyeX - myGLView.renderer.lookX;
		        	yOffset = myGLView.renderer.eyeY - myGLView.renderer.lookY;
		        }
			}
			break;
		case R.id.button2:
			if (viewAdjustView != null) {
				mainView.removeView(viewAdjustView);
			}
			break;
		case R.id.btnCredits:
			rollCredits = !rollCredits;
			if (rollCredits) {
				createCredits();
				myGLView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
			} else {
				deleteCredits();
				myGLView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
				myGLView.requestRender();
			}
			break;
		case R.id.btnPlay:
			// animate first
			ImageButton buttonPlay = (ImageButton) findViewById(R.id.btnPlay); //can't put before switch it will be null
			AnimatorSet btnPlayAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate); //can't reuse
			btnPlayAniSet.setTarget(buttonPlay);
			btnPlayAniSet.addListener(new Animator.AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
			    	viewToSplash();
					        
			    }

				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
					
				}

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
					
				}
			});
			btnPlayAniSet.start();
			break;  

		case R.id.btnSettings:
			ImageButton buttonSettings = (ImageButton) findViewById(R.id.btnSettings); //can't put before switch it will be null
			AnimatorSet btnSettingsAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate); //can't reuse
			btnSettingsAniSet.setTarget(buttonSettings);
			btnSettingsAniSet.addListener(new AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
			    	viewToSettings();       
			    }
				@Override
				public void onAnimationCancel(Animator animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationRepeat(Animator animation) {
					// TODO Auto-generated method stub
				}

				@Override
				public void onAnimationStart(Animator animation) {
					// TODO Auto-generated method stub
				}
			});
			btnSettingsAniSet.start();
			break;

		case R.id.btnBack:
			if (viewAdjustView != null) {
				viewSplash.removeView(viewAdjustView);
			}
			break;
		}
		
	}
	
	/**
	 * Changes the contentView to the Game view itself
	 */
	private void viewToSplash()
	{
    	setContentView(R.layout.activity_main);
		mainView = (RelativeLayout) findViewById(R.id.rlMain);
		((Button) findViewById(R.id.btnView)).setOnClickListener(this);
		((Button) findViewById(R.id.button2)).setOnClickListener(this);
		((Button) findViewById(R.id.btnCredits)).setOnClickListener(this);
		myGLView = (GLESSurfaceView) findViewById(R.id.myGLSurfaceView1);
		// Pass controller instance to the GLSurfaceView
		controller = new LogicControl(myGLView.renderer.board);
		myGLView.setController(controller);
	}
	
	/**
	 * Adds Setting view on top of the current view
	 */
	private void viewToSettings()
	{
		LayoutInflater inflater = getLayoutInflater();
		viewAdjustView = inflater.inflate(R.layout.settings_page, null);
		if (viewAdjustView != null) {
			viewSplash.addView(viewAdjustView);
		}
		((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(this);
	}

	private void createCredits() {
        // Credits
        Cube cube;
        float size = 0.75f;
        float posX = 1.5f;
        float posY = 0.0f;
        cube = new Cube(myGLView.renderer, GLRenderer.textureOffsetCredits, -posX, posY);
        cube.z = 0f;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 0;
        synchronized (myGLView.renderer.board.creditsCubes) {
        	myGLView.renderer.board.creditsCubes.add(cube);
		}
        cube = new Cube(myGLView.renderer, GLRenderer.textureOffsetCredits, posX, posY);
        cube.z = 0f;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 180;
        synchronized (myGLView.renderer.board.creditsCubes) {
        	myGLView.renderer.board.creditsCubes.add(cube);
		}
        cube = new Cube(myGLView.renderer, GLRenderer.textureOffsetTileBlue, 0, posY);
        cube.z = -posX;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 0;
        synchronized (myGLView.renderer.board.creditsCubes) {
        	myGLView.renderer.board.creditsCubes.add(cube);
		}
        cube = new Cube(myGLView.renderer, GLRenderer.textureOffsetTileRed, 0, posY);
        cube.z = posX;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 180;
        synchronized (myGLView.renderer.board.creditsCubes) {
        	myGLView.renderer.board.creditsCubes.add(cube);
		}
	}
	
	private void deleteCredits() {
        synchronized (myGLView.renderer.board.creditsCubes) {
        	myGLView.renderer.board.creditsCubes.clear();
        }
	}
	
	/** Scale a value to the range 0 to 100 given a min (scaled to zero) and 
	 * a max value (scaled to 100). Used with progress bars with default 0 to
	 * 100 range.
	 * @param value The value to be scaled.
	 * @param min Scale range minimum.
	 * @param max Scale range maximum.
	 * @return The value scaled to between zero and 100.
	 */
	private int toProgress(float value, float min, float max) {
		return (int) ((value - min) / (max - min) * 100f);
	}
	
	/** Scale a value between 0 and 100 to a value between min and max.
	 * @param progress Value between 0 and 100.
	 * @param min Scale range minimum.
	 * @param max Scale range maximum.
	 * @return The progress value scaled between min and max.
	 */
	private float fromProgress(int progress, float min, float max) {
		return min + progress / 100f * (max - min);
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// Convert progress value to view parameters
		int setting;
		switch (seekBar.getId()) {
		case R.id.seekX:
			if (!isPanningX) {
				myGLView.renderer.eyeX = fromProgress(progress, 
						myGLView.renderer.eyeXMin, myGLView.renderer.eyeXMax);
				xOffset = myGLView.renderer.eyeX - myGLView.renderer.lookX;
				Log.v("eyeX", ""+myGLView.renderer.eyeX);
			}
			break;
		case R.id.seekY:
			if (!isPanningY) {
				myGLView.renderer.eyeY = fromProgress(progress, 
						myGLView.renderer.eyeYMin, myGLView.renderer.eyeYMax);
				yOffset = myGLView.renderer.eyeY - myGLView.renderer.lookY;
				Log.v("eyeY", ""+myGLView.renderer.eyeY);
			}
			break;
		case R.id.seekZ:
			myGLView.renderer.eyeZ = fromProgress(progress, 
					myGLView.renderer.eyeZMin, myGLView.renderer.eyeZMax);
			Log.v("eyeZ", ""+myGLView.renderer.eyeZ);
			break;
		case R.id.seekPanX:
			isPanningX = true;
			myGLView.renderer.lookX = fromProgress(progress, 
					myGLView.renderer.eyeXMin, myGLView.renderer.eyeXMax);
			myGLView.renderer.eyeX = myGLView.renderer.lookX + xOffset;
        	setting = toProgress(myGLView.renderer.eyeX, 
        			myGLView.renderer.eyeXMin, 
        			myGLView.renderer.eyeXMax);
        	((SeekBar) findViewById(R.id.seekX)).setProgress(setting);
			Log.v("lookX", ""+myGLView.renderer.lookX);
			isPanningX = false;
			break;
		case R.id.seekPanY:
			isPanningY = true;
			myGLView.renderer.lookY = fromProgress(progress, 
					myGLView.renderer.eyeYMin, myGLView.renderer.eyeYMax);
			myGLView.renderer.eyeY = myGLView.renderer.lookY + yOffset;
        	setting = toProgress(myGLView.renderer.eyeY, 
        			myGLView.renderer.eyeYMin, 
        			myGLView.renderer.eyeYMax);
        	((SeekBar) findViewById(R.id.seekY)).setProgress(setting);
			Log.v("lookY", ""+myGLView.renderer.lookY);
			isPanningY = false;
			break;
		}
		myGLView.renderer.calculateViewMatrix();
		myGLView.requestRender();
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
