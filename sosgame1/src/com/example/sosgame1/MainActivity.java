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

    private MyGLSurfaceView mGLView;
    private RelativeLayout mainView;
    private View slider = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity
//        mGLView = new MyGLSurfaceView(this);
//        RelativeLayout rl = new RelativeLayout(this);
//        Button btn = new Button(this);
//        TextView txt = new TextView(this);
//        txt.setText("foo");
//        rl.addView(mGLView);
//        rl.addView(btn);
//        rl.addView(txt);
//        setContentView(rl);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		mainView = (RelativeLayout) findViewById(R.id.rlMain);
		((Button) findViewById(R.id.btnView)).setOnClickListener(this);
		((Button) findViewById(R.id.button2)).setOnClickListener(this);
		mGLView = (MyGLSurfaceView) findViewById(R.id.myGLSurfaceView1);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btnView:
			LayoutInflater inflater = getLayoutInflater();
			slider = inflater.inflate(R.layout.view_adjust, null);
			if (slider != null) {
				mainView.addView(slider);
		        ((SeekBar) findViewById(R.id.seekX)).setOnSeekBarChangeListener(this);
		        ((SeekBar) findViewById(R.id.seekY)).setOnSeekBarChangeListener(this);
		        ((SeekBar) findViewById(R.id.seekZ)).setOnSeekBarChangeListener(this);
		        if (mGLView != null) {
		        	((SeekBar) findViewById(R.id.seekX))
		        		.setProgress((int) (mGLView.mRenderer.eyeX * 10 + 50));
		        	((SeekBar) findViewById(R.id.seekY))
		        		.setProgress((int) (mGLView.mRenderer.eyeY * 10 + 50));
					Log.v("eyeY init", ""+mGLView.mRenderer.eyeY);
		        }
			}
			break;
		case R.id.button2:
			if (slider != null) {
				mainView.removeView(slider);
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
			mGLView.mRenderer.eyeX = (progress - 50) / 10f;
			mGLView.mRenderer.calculateViewMatrix();
			Log.v("eyeX", ""+mGLView.mRenderer.eyeX);
			break;
		case R.id.seekY:
			mGLView.mRenderer.eyeY = (progress - 50) / 10f;
			mGLView.mRenderer.calculateViewMatrix();
			Log.v("eyeY", ""+mGLView.mRenderer.eyeY);
			break;
		}
		mGLView.requestRender();
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
