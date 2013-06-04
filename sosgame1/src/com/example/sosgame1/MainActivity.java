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

import com.example.sosgame1.MyGLSurfaceView;
import com.example.sosgame1.controller.LogicControl;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

public class MainActivity extends Activity implements OnClickListener,
	SeekBar.OnSeekBarChangeListener {

    private MyGLSurfaceView myGLView;
    private RelativeLayout mainView;
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
		controller = new LogicControl();
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mainView = (RelativeLayout) findViewById(R.id.rlMain);
		((Button) findViewById(R.id.btnView)).setOnClickListener(this);
		((Button) findViewById(R.id.button2)).setOnClickListener(this);
		((Button) findViewById(R.id.btnCredits)).setOnClickListener(this);
		myGLView = (MyGLSurfaceView) findViewById(R.id.myGLSurfaceView1);
		// Pass controller instance to the GLSurfaceView
		myGLView.setController(controller);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnView:
			LayoutInflater inflater = getLayoutInflater();
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
		        	setting = toProgress(myGLView.mRenderer.eyeX, 
		        			myGLView.mRenderer.eyeXMin, 
		        			myGLView.mRenderer.eyeXMax);
		        	((SeekBar) findViewById(R.id.seekX)).setProgress(setting);
		        	setting = toProgress(myGLView.mRenderer.eyeY, 
		        			myGLView.mRenderer.eyeYMin, 
		        			myGLView.mRenderer.eyeYMax);
		        	((SeekBar) findViewById(R.id.seekY)).setProgress(setting);
		        	setting = toProgress(myGLView.mRenderer.eyeZ, 
		        			myGLView.mRenderer.eyeZMin, 
		        			myGLView.mRenderer.eyeZMax);
		        	((SeekBar) findViewById(R.id.seekZ)).setProgress(setting);
		        	setting = toProgress(myGLView.mRenderer.lookX, 
		        			myGLView.mRenderer.eyeXMin, 
		        			myGLView.mRenderer.eyeXMax);
		        	((SeekBar) findViewById(R.id.seekPanX)).setProgress(setting);
		        	setting = toProgress(myGLView.mRenderer.lookY, 
		        			myGLView.mRenderer.eyeYMin, 
		        			myGLView.mRenderer.eyeYMax);
		        	((SeekBar) findViewById(R.id.seekPanY)).setProgress(setting);
		        	xOffset = myGLView.mRenderer.eyeX - myGLView.mRenderer.lookX;
		        	yOffset = myGLView.mRenderer.eyeY - myGLView.mRenderer.lookY;
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
		}
	}

	private void createCredits() {
        // Credits
        Cube cube;
        float size = 0.75f;
        float posX = 1.5f;
        float posY = -1f;
        cube = new Cube(myGLView.mRenderer, MyGLRenderer.textureOffsetCredits, -posX, posY);
        cube.z = 0f;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 0;
        myGLView.mRenderer.board.creditsCubes.add(cube);
        cube = new Cube(myGLView.mRenderer, MyGLRenderer.textureOffsetCredits, posX, posY);
        cube.z = 0f;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 180;
        myGLView.mRenderer.board.creditsCubes.add(cube);
        cube = new Cube(myGLView.mRenderer, MyGLRenderer.textureOffsetCredits, 0, posY);
        cube.z = -posX;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 90;
        myGLView.mRenderer.board.creditsCubes.add(cube);
        cube = new Cube(myGLView.mRenderer, MyGLRenderer.textureOffsetCredits, 0, posY);
        cube.z = posX;
        cube.scaleFactorX = size;
        cube.scaleFactorY = size;
        cube.scaleFactorZ = size / 4;
        cube.rotationY = 270;
        myGLView.mRenderer.board.creditsCubes.add(cube);        
	}
	
	private void deleteCredits() {
		myGLView.mRenderer.board.creditsCubes.clear();
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
				myGLView.mRenderer.eyeX = fromProgress(progress, 
						myGLView.mRenderer.eyeXMin, myGLView.mRenderer.eyeXMax);
				xOffset = myGLView.mRenderer.eyeX - myGLView.mRenderer.lookX;
				Log.v("eyeX", ""+myGLView.mRenderer.eyeX);
			}
			break;
		case R.id.seekY:
			if (!isPanningY) {
				myGLView.mRenderer.eyeY = fromProgress(progress, 
						myGLView.mRenderer.eyeYMin, myGLView.mRenderer.eyeYMax);
				yOffset = myGLView.mRenderer.eyeY - myGLView.mRenderer.lookY;
				Log.v("eyeY", ""+myGLView.mRenderer.eyeY);
			}
			break;
		case R.id.seekZ:
			myGLView.mRenderer.eyeZ = fromProgress(progress, 
					myGLView.mRenderer.eyeZMin, myGLView.mRenderer.eyeZMax);
			Log.v("eyeZ", ""+myGLView.mRenderer.eyeZ);
			break;
		case R.id.seekPanX:
			isPanningX = true;
			myGLView.mRenderer.lookX = fromProgress(progress, 
					myGLView.mRenderer.eyeXMin, myGLView.mRenderer.eyeXMax);
			myGLView.mRenderer.eyeX = myGLView.mRenderer.lookX + xOffset;
        	setting = toProgress(myGLView.mRenderer.eyeX, 
        			myGLView.mRenderer.eyeXMin, 
        			myGLView.mRenderer.eyeXMax);
        	((SeekBar) findViewById(R.id.seekX)).setProgress(setting);
			Log.v("lookX", ""+myGLView.mRenderer.lookX);
			isPanningX = false;
			break;
		case R.id.seekPanY:
			isPanningY = true;
			myGLView.mRenderer.lookY = fromProgress(progress, 
					myGLView.mRenderer.eyeYMin, myGLView.mRenderer.eyeYMax);
			myGLView.mRenderer.eyeY = myGLView.mRenderer.lookY + yOffset;
        	setting = toProgress(myGLView.mRenderer.eyeY, 
        			myGLView.mRenderer.eyeYMin, 
        			myGLView.mRenderer.eyeYMax);
        	((SeekBar) findViewById(R.id.seekY)).setProgress(setting);
			Log.v("lookY", ""+myGLView.mRenderer.lookY);
			isPanningY = false;
			break;
		}
		myGLView.mRenderer.calculateViewMatrix();
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
