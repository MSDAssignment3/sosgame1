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
/*
 * Portions of this class are derived from the sample code at
 * http://developer.android.com/training/graphics/opengl/index.html
 */
package com.example.sosgame1;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sosgame1.controller.LogicControl;

public class MainActivity extends Activity implements OnClickListener,
	SeekBar.OnSeekBarChangeListener {

	private Context context = this;
    private GLESSurfaceView myGLView = null;
    private RelativeLayout mainView;
    private RelativeLayout viewSplash;
    private View viewAdjustView = null;
    private View viewSettings = null;
    private float xOffset;
    private float yOffset;
    private boolean isPanningX = false;
    private boolean isPanningY = false;
    private LogicControl controller = null;
    private boolean rollCredits = false;
    private boolean adjustView = false;
    private DataSource dataSource;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_page);
		((ImageButton) findViewById(R.id.btnPlay)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnPlayMultiple)).setOnClickListener(this);
		viewSplash = (RelativeLayout) findViewById(R.id.rlSplash);
		
		//DB
		dataSource = new DataSource(this);
	    dataSource.open();
	    //get all Scores, to be diplayed
//	    List<Score> scores = dataSource.getAllScores(); //TEST LATER
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
			adjustView = !adjustView;
			if (adjustView) {
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
			} else {
				if (viewAdjustView != null) {
					mainView.removeView(viewAdjustView);
				}
			}
			break;
		case R.id.btnCredits:
			rollCredits = !rollCredits;
			if (rollCredits) {
				createCredits();
				myGLView.incrementAnimations();
			} else {
				deleteCredits();
				myGLView.decrementAnimations();
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
			    	viewToGame();
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

		case R.id.btnPlayMultiple:
			ImageButton btnPlayMultiple = (ImageButton) findViewById(R.id.btnPlayMultiple); //can't put before switch it will be null
			AnimatorSet btnMultipleAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate); //can't reuse
			btnMultipleAniSet.setTarget(btnPlayMultiple);
			btnMultipleAniSet.addListener(new AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
			    	//show a spinner
			    	chooseServerClient();
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
			btnMultipleAniSet.start();
			break;

		case R.id.btnBack:
			if (viewSettings != null) {
				if (rollCredits) {
					rollCredits = !rollCredits;
					deleteCredits();
					myGLView.decrementAnimations();
					myGLView.requestRender();
				}
				mainView.removeView(viewSettings);
			}
			break;
			
		case R.id.btnSettingsGame:
			viewToSettings(this.mainView);
			break;
			
		case R.id.testUpdateScore:
			updateScore();
			break;
			
		case R.id.testSaveScore:
			saveScore();
			break;
		}

	}
	
	/**
	 * Changes the contentView to the Game view itself
	 */
	private void viewToGame()
	{
    	setContentView(R.layout.activity_main);
		mainView = (RelativeLayout) findViewById(R.id.rlMain);
		((Button) findViewById(R.id.btnView)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnSettingsGame)).setOnClickListener(this);
		((Button) findViewById(R.id.testUpdateScore)).setOnClickListener(this);//REMOVE this when testing updateScore is not needed
		((Button) findViewById(R.id.testSaveScore)).setOnClickListener(this);//REMOVE this when testing saveScore is not needed
		myGLView = (GLESSurfaceView) findViewById(R.id.myGLSurfaceView1);
		myGLView.renderer.board.reset(7,7);
		// Pass controller instance to the GLSurfaceView
		controller = new LogicControl(myGLView.renderer.board, 7, 7);
		myGLView.setController(controller);
	}
	
	/**
	 * Adds Setting view on top of the current view
	 * @param view - the RelativeLayout where the click event occurred from
	 */
	private void viewToSettings(RelativeLayout view){
		LayoutInflater inflater = getLayoutInflater();
		viewSettings = inflater.inflate(R.layout.settings_page, null);
		if (viewSettings != null) {
			view.addView(viewSettings);
		}
		((ImageButton) findViewById(R.id.btnBack)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnCredits)).setOnClickListener(this);
	}
	
	/**
	 * THIS IS NOT YET COMPLETE
	 * Updates the Scores on the screen
	 */
	private void updateScore(){
		int dummy = 1; //change and remove later
		TextView textBlueScore = (TextView) findViewById(R.id.txtBlueScore);
		TextView textRedScore = (TextView) findViewById(R.id.txtRedScore);
		textBlueScore.setText(""+dummy);
		textRedScore.setText(""+dummy);
	}
	
	/**
	 * Save score to the DB
	 */
	private void saveScore(){
		TextView textBlueScore = (TextView) findViewById(R.id.txtBlueScore);
		Score score = dataSource.createScore( "TEST", Integer.parseInt((String) textBlueScore.getText()) );
	}
	

	/**
	 * Add alertDialogs for choosing server or client on multiplayer type of game
	 */
	private void chooseServerClient()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setTitle("Multiplayer, are you the...");
		alertDialogBuilder.setItems(R.array.multiplayer_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // The 'which' argument contains the index position of the selected item
            	if (which == 0) { //Server. /////Is there a better way? A constant or something?
            		//Show IP Adress /////PETER?
            		AlertDialog.Builder alertIp = new AlertDialog.Builder(context);
            		alertIp.setTitle("This server's IP address");
            		alertIp.setMessage("[ip adress]" + "Input this in the other device.");
            		alertIp.setPositiveButton("Got it, Play!", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
//            			  String value = txtIp.getText().toString();
            			  // Do something with value!
//            				 viewToGame();
            			}
            		});
            		alertIp.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            			  public void onClick(DialogInterface dialog, int whichButton) {
            			    // Canceled.
            			  }
            		});
            		alertIp.show();
            	}
            	else if (which == 1) { //Client
            		//Input IP address of Server
            		AlertDialog.Builder alertInputIp = new AlertDialog.Builder(context);
            		// Set an EditText view to get user input 
            		final EditText txtIp = new EditText(context);
            		alertInputIp.setView(txtIp);
            		alertInputIp.setTitle("Enter IP address of Server device");
            		alertInputIp.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
//            			  String value = txtIp.getText().toString();
            			  // Do something with value!
//            				 viewToGame();
            			}
            		});
            		alertInputIp.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            			  public void onClick(DialogInterface dialog, int whichButton) {
            			    // Canceled.
            			  }
            		});
            		alertInputIp.show();
            	}
            }
		});
		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	/** Create credits cubes */
	private void createCredits() {
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
	
	/** Delete credits cubes */
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

	@Override
	protected void onPause() {
		if (myGLView != null) {
			myGLView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (myGLView != null) {
			myGLView.onResume();
		}
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		if (viewSettings != null) {
			if (rollCredits) {
				rollCredits = !rollCredits;
				deleteCredits();
				myGLView.decrementAnimations();
				myGLView.requestRender();
			}
			mainView.removeView(viewSettings);
			viewSettings = null;
		} else if (viewAdjustView != null) {
			mainView.removeView(viewAdjustView);
			viewAdjustView = null;
			adjustView = !adjustView;
		} else {
			super.onBackPressed();
		}
	}

}
