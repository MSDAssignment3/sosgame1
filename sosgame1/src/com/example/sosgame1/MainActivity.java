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

import java.io.IOException;
import java.util.List;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.sosgame1.controller.LogicControl;

public class MainActivity extends Activity implements OnClickListener,
	SeekBar.OnSeekBarChangeListener {

	private Context context = this;
    private GLESSurfaceView myGLView = null;
    private RelativeLayout mainView;
    private View viewAdjustView = null;
    private View viewSettings = null;
    private View viewScores = null;
    private float xOffset;
    private float yOffset;
    private boolean isPanningX = false;
    private boolean isPanningY = false;
    private LogicControl controller = null;
    private boolean rollCredits = false;
    private boolean adjustView = false;
    private DataSource dataSource;
    private Server server;
	private ClientThread client;
	private boolean sExist= false;
	private boolean cExist = false;
    private int boardColumns = 5; //default
    private int boardRows = 5;
    private Thread serverthread;
    private Thread clientThread;
    
    /** This message handler processes messages received from
     * a connected device when playing the game between two devices.
     */
    Handler handler = new Handler() {

    	@Override
		public void handleMessage(Message msg) {
    		String thePoint;
    		String[] xy;
    		PointF p;
			switch (msg.arg1) {
			case Constant.SHOW_TILES_TO_CHOOSE:
				Log.v("Message", "arg1 = " + msg.arg1);
				thePoint = ((Bundle) msg.obj).getString("PointF");
				xy = thePoint.split(",");
				p = new PointF(Float.parseFloat(xy[0]),
						Float.parseFloat(xy[1]));
				myGLView.showTilesToChoose(p);
				break;
			case Constant.CHOOSE_TILE:
				Log.v("Message", "arg1 = " + msg.arg1);
				thePoint = ((Bundle) msg.obj).getString("PointF");
				xy = thePoint.split(",");
				p = new PointF(Float.parseFloat(xy[0]),
						Float.parseFloat(xy[1]));
				myGLView.chooseTile(p);
				break;
			case Constant.BOARD_SIZE:
				Log.v("Message", "arg1 = " + msg.arg1);
				thePoint = ((Bundle) msg.obj).getString("Size");
				xy = thePoint.split(",");
				boardRows = Integer.parseInt(xy[0]);
				boardColumns = Integer.parseInt(xy[1]);
				myGLView.renderer.board.reset(boardColumns, boardRows);
				controller.setBoard(boardRows, boardColumns);
				break;
			default:
				Log.v("Message", "received");
				break;	
			}
			super.handleMessage(msg);
		}

    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.splash_page);
		((ImageButton) findViewById(R.id.btnPlay)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnPlayMultiple)).setOnClickListener(this);
		
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
					viewAdjustView = null;
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
			    	chooseBoardSize();
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
				viewSettings = null;
			}
			break;
			
		case R.id.btnSettingsGame:
			viewToSettings(this.mainView);
			break;
			
//		case R.id.testUpdateScore:
//			updateScore();
//			break;
			
//		case R.id.testSaveScore:
//			saveScore();
//			break;
			
		case R.id.testListScore:
			viewToScore(this.mainView);
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
		((Button) findViewById(R.id.testListScore)).setOnClickListener(this);//REMOVE this when testing saveScore is not needed
		myGLView = (GLESSurfaceView) findViewById(R.id.myGLSurfaceView1);
		myGLView.renderer.board.reset(boardRows,boardColumns);
		// Pass controller instance to the GLSurfaceView
		controller = new LogicControl(myGLView.renderer.board, boardRows, boardColumns, (MainActivity)this);
		myGLView.setController(controller);
		
		if (sExist) {
			myGLView.setServer(server);
		}
		else if(cExist) {
			myGLView.setClient(client);
		}
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
	 * Show score list
	 */
	private void viewToScore(RelativeLayout view)
	{
		LayoutInflater inflater = getLayoutInflater();
		viewScores = inflater.inflate(R.layout.scores_page, null);
		if (viewScores != null) {
			view.addView(viewScores);
		}
		ListView listView = (ListView) findViewById(R.id.listScores);
		List<Score> scores = dataSource.getAllScores();
		ArrayAdapter<Score> adapter = new ArrayAdapter<Score>(context, android.R.layout.simple_list_item_1, scores);

		adapter.notifyDataSetChanged();

		try
		{
		listView.setAdapter(adapter); //TODO: Something inside gives NullPointerException
		}
		catch(Exception e)
		{
		}
	}
	
	/**
	 * Updates the players' scores on the screen
	 */
	public void updateScore(int playerBlueScore, int playerRedScore){
		TextView textBlueScore = (TextView) findViewById(R.id.txtBlueScore);
		TextView textRedScore = (TextView) findViewById(R.id.txtRedScore);
		textBlueScore.setText(""+playerBlueScore);
		textRedScore.setText(""+playerRedScore);
		Score score = new Score();
		score.setPlayer("Player Blue");
		score.setScoreValue(playerBlueScore);
		dataSource.addScore(score);
	}
	
	/**
	 * Save score to the DB
	 */
	private void saveScore(String playerName, int playerScore){
		Score score = dataSource.createScore( playerName, playerScore);
	}
	
	/**
	 * End game interface
	 * Gets name of winner
	 */
	public void endGame() {
		AlertDialog.Builder alertEnd = new AlertDialog.Builder(context);
		String winner = "";
		int winnerScore = 0;
		TextView textBlueScore = (TextView) findViewById(R.id.txtBlueScore);
		TextView textRedScore = (TextView) findViewById(R.id.txtRedScore);
		if(Integer.parseInt((String) textBlueScore.getText()) > Integer.parseInt((String) textRedScore.getText())) {
			winner = "BLUE";
			winnerScore = Integer.parseInt((String) textBlueScore.getText());
		}
		else if(Integer.parseInt((String) textBlueScore.getText()) < Integer.parseInt((String) textRedScore.getText())) {
			winner = "RED";
			winnerScore = Integer.parseInt((String) textRedScore.getText());
		}
		else if(Integer.parseInt((String) textBlueScore.getText()) == Integer.parseInt((String) textRedScore.getText())) {
			winner = "BLUE & RED";
			winnerScore = Integer.parseInt((String) textBlueScore.getText());
		}
		final int finalWinnerScore = winnerScore;
		final EditText txtWinner = new EditText(context);
		alertEnd.setTitle("Game ended! Player " + winner + " won! Enter winner's name:");
		alertEnd.setView(txtWinner);
		alertEnd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String winnerName = txtWinner.getText().toString(); 
				saveScore(winnerName, finalWinnerScore);
			}
		});
		alertEnd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled.
			  }
		});
		alertEnd.show();
		//TODO:go back to menu or show highscores
	}

	/**
	 * Add alertDialogs for choosing server or client on multiplayer type of game
	 */
	private void chooseServerClient()
	{
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		alertDialogBuilder.setTitle("Multiplayer, are you the...");
		alertDialogBuilder.setItems(R.array.multiplayer_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            // The 'which' argument contains the index position of the selected item
            	if (which == 0) { 
            		server = new Server(handler);
            		serverthread = new Thread(server);
            		serverthread.start();
            		String ip = Utils.getIPAddress(true);
            		AlertDialog.Builder alertIp = new AlertDialog.Builder(context);
            		alertIp.setTitle("This server's IP address");
            		alertIp.setMessage("["+ip+"]" + "\nInput this in the other device.");
            		alertIp.setPositiveButton("Got it, Play!", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
            				sExist = true;
            				chooseBoardSize();
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
							String temp = txtIp.getText().toString(); 
							client = new ClientThread(temp, handler);
							clientThread = new Thread(client);
							clientThread.start();
							cExist = true;
							viewToGame();
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
	
	/**
	 * Interface that makes the user choose a board size
	 */
	private void chooseBoardSize()
	{
		AlertDialog.Builder alertBoardSize = new AlertDialog.Builder(context);
		alertBoardSize.setTitle("Choose board size");
		alertBoardSize.setItems(R.array.board_size_array, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            	if (which == 0) { //5x5
            		boardRows = 5;
            		boardColumns = 5;
            	}
            	else if (which == 1) { //7x7
            		boardRows = 7;
            		boardColumns = 7;
            	}
            	else if (which == 2) { //9x9
            		boardRows = 9;
            		boardColumns = 9;
            	}
            	if (sExist) {
            		server.setBoard(boardRows, boardColumns);
            	}
            	viewToGame();
            }
		});
		AlertDialog alertDialog = alertBoardSize.create();
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
		// TODO Properly terminate the server/client threads
		if (server != null) {
			server.running = false;
			server.setMessage(Constant.EXIT, "");
			try {
				server.client.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (client != null) {
			client.running = false;
			client.setMessage(Constant.EXIT, "");
			try {
				client.socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
