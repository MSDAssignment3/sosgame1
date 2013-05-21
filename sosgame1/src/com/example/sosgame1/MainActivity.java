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
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener,
	SeekBar.OnSeekBarChangeListener{

    private MyGLSurfaceView myGLView;
    private RelativeLayout mainView;
    private View viewAdjustView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
//        myGLView = new MyGLSurfaceView(this);
//        RelativeLayout rl = new RelativeLayout(this);
//        Button btn = new Button(this);
//        TextView txt = new TextView(this);
//        txt.setText("foo");
//        rl.addView(myGLView);
//        rl.addView(btn);
//        rl.addView(txt);
//        setContentView(rl);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mainView = (RelativeLayout) findViewById(R.id.rlMain);
		((Button) findViewById(R.id.btnView)).setOnClickListener(this);
		((Button) findViewById(R.id.button2)).setOnClickListener(this);
		myGLView = (MyGLSurfaceView) findViewById(R.id.myGLSurfaceView1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
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
		        if (myGLView != null) {
		        	int setting = (int) ((myGLView.mRenderer.eyeX - myGLView.mRenderer.eyeXMin)
		        			/ (myGLView.mRenderer.eyeXMax - myGLView.mRenderer.eyeXMin)
		        			* 100f);
		        	((SeekBar) findViewById(R.id.seekX)).setProgress(setting);
		        	setting = (int) ((myGLView.mRenderer.eyeY - myGLView.mRenderer.eyeYMin) 
		        			/ (myGLView.mRenderer.eyeYMax - myGLView.mRenderer.eyeYMin)
		        			* 100f);
		        	((SeekBar) findViewById(R.id.seekY)).setProgress(setting);
		        	setting = (int) ((myGLView.mRenderer.eyeZ  - myGLView.mRenderer.eyeZMin)
		        			/ (myGLView.mRenderer.eyeZMax - myGLView.mRenderer.eyeZMin)
		        			* 100);
		        	((SeekBar) findViewById(R.id.seekZ)).setProgress(setting);
//					Log.v("eyeY init", ""+myGLView.mRenderer.eyeY);
		        }
			}
			break;
		case R.id.button2:
			if (viewAdjustView != null) {
				mainView.removeView(viewAdjustView);
			}
			break;
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// Convert progress value to view parameters
		switch (seekBar.getId()) {
		case R.id.seekX:
			myGLView.mRenderer.eyeX = myGLView.mRenderer.eyeXMin + progress / 100f 
				* (myGLView.mRenderer.eyeXMax - myGLView.mRenderer.eyeXMin);
			myGLView.mRenderer.calculateViewMatrix();
			Log.v("eyeX", ""+myGLView.mRenderer.eyeX);
			break;
		case R.id.seekY:
			myGLView.mRenderer.eyeY = myGLView.mRenderer.eyeYMin + progress / 100f 
				* (myGLView.mRenderer.eyeYMax - myGLView.mRenderer.eyeYMin);
			myGLView.mRenderer.calculateViewMatrix();
			Log.v("eyeY", ""+myGLView.mRenderer.eyeY);
			break;
		case R.id.seekZ:
			myGLView.mRenderer.eyeZ = myGLView.mRenderer.eyeZMin + progress / 100f 
				* (myGLView.mRenderer.eyeZMax - myGLView.mRenderer.eyeZMin);
			myGLView.mRenderer.calculateViewMatrix();
			Log.v("eyeZ", ""+myGLView.mRenderer.eyeZ);
			break;
		}
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
