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
package nz.edu.unitec.sosgame1;

import java.io.IOException;
import java.util.List;
import nz.edu.unitec.sosgame1.R;
import nz.edu.unitec.sosgame1.controller.LogicControl;
import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.BounceInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;


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
    private Server server = null;
	private ClientThread client = null;
    private int boardColumns = 5; //default
    private int boardRows = 5;
    private Thread serverThread;
    private Thread clientThread;
    public static final int PLAY_SINGLE_DEVICE = 1;
    public static final int PLAY_SERVER = 2;
    public static final int PLAY_CLIENT = 3;
    public static final int PLAY_AI = 4;
    public int playMode = PLAY_SINGLE_DEVICE;
    private String lastIPAddress = "10.1.1.27";
    public AI theAI;
    public Thread AIThread;
    
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
				thePoint = ((Bundle) msg.obj).getString("Message");
				xy = thePoint.split(",");
				p = new PointF(Float.parseFloat(xy[0]),
						Float.parseFloat(xy[1]));
				if (myGLView != null) {
					myGLView.showTilesToChoose(p);
				}
				break;
			case Constant.CHOOSE_TILE:
				Log.v("Message", "arg1 = " + msg.arg1);
				thePoint = ((Bundle) msg.obj).getString("Message");
				xy = thePoint.split(",");
				p = new PointF(Float.parseFloat(xy[0]),
						Float.parseFloat(xy[1]));
				if (myGLView != null) {
					myGLView.chooseTile(p);
				}
				break;
			case Constant.BOARD_SIZE:
				if (alertDialog != null) {
					alertDialog.dismiss();
				}
				Log.v("Message", "arg1 = " + msg.arg1);
				if (myGLView == null) {
					viewToGame();
				}
				thePoint = ((Bundle) msg.obj).getString("Message");
				xy = thePoint.split(",");
				boardRows = Integer.parseInt(xy[0]);
				boardColumns = Integer.parseInt(xy[1]);
				myGLView.renderer.board.reset(boardColumns, boardRows);
				controller.setBoard(boardRows, boardColumns);
				break;
			case Constant.CLIENT_CONNECTED:
				if (alertDialog != null) {
					alertDialog.dismiss();
				}
				showWaitingForBoardSize();
				break;
			case Constant.SERVER_CONNECTED:
				if (alertDialog2 != null) {
					alertDialog2.dismiss();
				}
				break;
			case Constant.MESSAGE:
				break;
			case Constant.QUERY_BOARD_SIZE:
				if (server != null && boardRows > 0 && boardColumns > 0) {
					server.setMessage(Constant.BOARD_SIZE, boardRows + "," + boardColumns);
				}
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
		viewToSplash();
		
		//DB
		dataSource = new DataSource(this);
	    dataSource.open();
	    //get all Scores, to be diplayed
//	    List<Score> scores = dataSource.getAllScores(); //TEST LATER
	}

	private void viewToSplash() {
		if (myGLView != null) {
			mainView.removeView(myGLView);
			myGLView = null;
		}
		if (theAI != null) {
			theAI.running = false;
		}
		killClient();
		killServer();
		setContentView(R.layout.splash_page);
		((ImageButton) findViewById(R.id.btnPlay)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnPlayAI)).setOnClickListener(this);
		((ImageButton) findViewById(R.id.btnPlayMultiple)).setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void onClick(View v) {
		
		LayoutInflater inflater = getLayoutInflater();
		ImageButton buttonPlay;
		AnimatorSet btnPlayAniSet;
		
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
			buttonPlay = (ImageButton) findViewById(R.id.btnCredits);
			btnPlayAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate);
			btnPlayAniSet.setTarget(buttonPlay);
			btnPlayAniSet.addListener(new AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
					rollCredits = !rollCredits;
					if (rollCredits) {
						createCredits();
						myGLView.incrementAnimations();
					} else {
						deleteCredits();
						myGLView.decrementAnimations();
						myGLView.requestRender();
					}
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

		case R.id.btnPlay:
			// animate first
			buttonPlay = (ImageButton) findViewById(R.id.btnPlay);
			btnPlayAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate);
			btnPlayAniSet.setTarget(buttonPlay);
			btnPlayAniSet.addListener(new Animator.AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
			    	killClient();
			    	killServer();
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

		case R.id.btnPlayAI:
			playMode = PLAY_AI;
			buttonPlay = (ImageButton) findViewById(R.id.btnPlayAI);
			btnPlayAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate);
			btnPlayAniSet.setTarget(buttonPlay);
			btnPlayAniSet.addListener(new Animator.AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
			    	killClient();
			    	killServer();
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
			buttonPlay = (ImageButton) findViewById(R.id.btnPlayMultiple);
			btnPlayAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate);
			btnPlayAniSet.setTarget(buttonPlay);
			btnPlayAniSet.addListener(new AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
			    	//show a spinner
			    	if (server == null && client == null) {
			    		chooseServerClient();
			    	} else if (server != null) {
			    		chooseBoardSize();
			    	} else if (client != null) {
			    		showWaitingForBoardSize();
			    	}
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
			
		case R.id.btnBackToSettings:
			if (viewScores != null) {
				mainView.removeView(viewScores);
				viewScores = null;
			}
			break;
			
		case R.id.btnSettingsGame:
			ImageButton btnSettings = (ImageButton) findViewById(R.id.btnSettingsGame); //can't put before switch it will be null
			AnimatorSet btnSettingsAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate); //can't reuse
			btnSettingsAniSet.setTarget(btnSettings);
			btnSettingsAniSet.addListener(new AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
					viewToSettings(mainView);
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
			
		case R.id.btnScores:
			ImageButton buttonScores = (ImageButton) findViewById(R.id.btnScores); //can't put before switch it will be null
			AnimatorSet btnScoresAniSet = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.button_rotate); //can't reuse
			btnScoresAniSet.setTarget(buttonScores);
			btnScoresAniSet.addListener(new AnimatorListener() {
			    @Override 
			    public void onAnimationEnd(Animator animation) {
			    	if (viewScores == null) {
			    		viewToScore(mainView);
			    	} else {
			    		mainView.removeView(viewScores);
			    		viewScores = null;
			    	}
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
			btnScoresAniSet.start();
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
		myGLView = (GLESSurfaceView) findViewById(R.id.myGLSurfaceView1);
		myGLView.renderer.board.reset(boardRows,boardColumns);
		// Pass controller instance to the GLSurfaceView
		controller = new LogicControl(myGLView.renderer.board, boardRows, boardColumns, (MainActivity) this);
		myGLView.setController(controller);
		myGLView.setPlayMode(playMode);
		
		if (server != null) {
			myGLView.setServer(server);
			server.setBoard(boardRows, boardColumns);
		}
		else if(client != null) {
			myGLView.setClient(client);
		} else if (playMode == PLAY_AI) {
			// Start the AI
			theAI = new AI(myGLView.renderer.board, controller, myGLView);
			AIThread = new Thread(theAI, "AI");
			AIThread.start();
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
		((ImageButton) findViewById(R.id.btnScores)).setOnClickListener(this);
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

		try {
			ListView listView = (ListView) findViewById(R.id.listScores);
			List<Score> scores = dataSource.getAllScores();
			ArrayAdapter<Score> adapter = new ArrayAdapter<Score>(context, android.R.layout.simple_list_item_1, scores);

			adapter.notifyDataSetChanged();
			listView.setAdapter(adapter);
		} catch (Exception e) {
			Log.e("Datasource", "exception listing scores:", e);
		}

		((ImageButton) findViewById(R.id.btnBackToSettings)).setOnClickListener(this);
	}
	
	/**
	 * Updates the players' scores on the screen
	 */
	public void updateScore(int playerBlueScore, int playerRedScore){
		TextView textBlueScore = (TextView) findViewById(R.id.txtBlueScore);
		TextView textRedScore = (TextView) findViewById(R.id.txtRedScore);
		textBlueScore.setText(""+playerBlueScore);
		textRedScore.setText(""+playerRedScore);
	}
	
	/** Waits for animations to end without blocking the UI thread.
	 * @author David Moore
	 */
	private class waitForAnimEnd extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... params) {
			while (myGLView.animationInProgress) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			endGame2();
			super.onPostExecute(result);
		}
		
	}
	
	public void endGame() {
		new waitForAnimEnd().execute(null, null, null);
	}
	
	/**
	 * End game interface
	 * Gets name of winner
	 */
	public void endGame2() {
		AlertDialog.Builder alertEnd = new AlertDialog.Builder(context);
		String winner = "";
		int winnerScore = 0;
		TextView textBlueScore = (TextView) findViewById(R.id.txtBlueScore);
		TextView textRedScore = (TextView) findViewById(R.id.txtRedScore);
		boolean showEnterNameDialog = false;
		if(Integer.parseInt((String) textBlueScore.getText()) > Integer.parseInt((String) textRedScore.getText())) {
			winner = "BLUE";
			winnerScore = Integer.parseInt((String) textBlueScore.getText());
			if (playMode == PLAY_SINGLE_DEVICE || playMode == PLAY_SERVER || playMode == PLAY_AI) {
				showEnterNameDialog = true;
			}
		}
		else if(Integer.parseInt((String) textBlueScore.getText()) < Integer.parseInt((String) textRedScore.getText())) {
			winner = "RED";
			winnerScore = Integer.parseInt((String) textRedScore.getText());
			if (playMode == PLAY_SINGLE_DEVICE || playMode == PLAY_CLIENT || playMode == PLAY_AI) {
				showEnterNameDialog = true;
			}
		}
		else if(Integer.parseInt((String) textBlueScore.getText()) == Integer.parseInt((String) textRedScore.getText())) {
			winner = "BLUE & RED";
			winnerScore = Integer.parseInt((String) textBlueScore.getText());
			showEnterNameDialog = true;
		}
		if (showEnterNameDialog) {
			final int finalWinnerScore = winnerScore;
			final EditText txtWinner = new EditText(context);
			alertEnd.setTitle("Game ended! Player " + winner + " won! Enter winner's name:");
			alertEnd.setView(txtWinner);
			alertEnd.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String winnerName = txtWinner.getText().toString(); 
					Score score = new Score();
					score.setPlayer(winnerName);
					score.setScoreValue(finalWinnerScore);
					try {
						dataSource.addScore(score);
					} catch (Exception e) {
						Log.e("Datasource", "exception updating score:", e);
					}
					viewToSplash();
				}
			});
			alertEnd.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					viewToSplash();
				}
			});
			alertEnd.show();
		} else {
			showYouLost(winner);
			viewToSplash();
		}
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
            		String ip = Utils.getIPAddress(true);
            		AlertDialog.Builder alertIp = new AlertDialog.Builder(context);
            		alertIp.setTitle("This server's IP address");
            		alertIp.setMessage("["+ip+"]" + "\nInput this in the other device.");
            		alertIp.setPositiveButton("Got it, Play!", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int whichButton) {
            				server = new Server(handler);
            				serverThread = new Thread(server, "Server");
            				serverThread.start();
            				chooseBoardSize();
            				showWaitingForClient();
            				playMode = PLAY_SERVER;
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
            		txtIp.setText(lastIPAddress);
            		alertInputIp.setView(txtIp);
            		alertInputIp.setTitle("Enter IP address of Server device");
            		alertInputIp.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							String temp = txtIp.getText().toString();
							lastIPAddress = temp;
							showConnecting(temp);
							client = new ClientThread(temp, handler, (MainActivity) context);
							clientThread = new Thread(client, "Client");
							clientThread.start();
							playMode = PLAY_CLIENT;
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
            	if (server != null) {
            		server.setMessage(Constant.BOARD_SIZE, boardRows + "," + boardColumns);
            	}
            	viewToGame();
            }
		});
		AlertDialog alertDialog = alertBoardSize.create();
		alertDialog.show();
	}
	
	public void pointRed()
	{
		ImageView arrowRed = (ImageView) findViewById(R.id.imgPointerRed);
		ImageView arrowBlue = (ImageView) findViewById(R.id.imgPointerBlue);
		arrowBlue.setVisibility(View.INVISIBLE);
		arrowRed.setVisibility(View.VISIBLE);
		float endY = arrowRed.getY();
		ObjectAnimator anim = ObjectAnimator.ofFloat(arrowRed, "y", arrowRed.getY()+20f, endY);
		anim.setDuration(1000);
		anim.setInterpolator(new BounceInterpolator ());
		anim.start();
	}
	
	public void pointBlue()
	{
		ImageView arrowRed = (ImageView) findViewById(R.id.imgPointerRed);
		ImageView arrowBlue = (ImageView) findViewById(R.id.imgPointerBlue);
		arrowBlue.setVisibility(View.VISIBLE);
		arrowRed.setVisibility(View.INVISIBLE);
		float endY = arrowBlue.getY();
		ObjectAnimator anim = ObjectAnimator.ofFloat(arrowBlue, "y", arrowBlue.getY()+20f, endY);
		anim.setDuration(1000);
		anim.setInterpolator(new BounceInterpolator ());
		anim.start();
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
		if (theAI != null) {
			theAI.running = false;
		}
		killServer();
		killClient();
		if (dataSource != null) {
			dataSource.close();
		}
		super.onPause();
	}

	private void killClient() {
		if (client != null) {
			client.running = false;
			if (client.socket!= null && !client.socket.isClosed()) {
				try {
					// Stackoverflow mentions a bug in socket.close() which
					// is avoided by shutting down first.
					client.socket.shutdownInput();
				} catch (IOException e) {
					Log.d("Client socket", "failed to shutdownInput", e);
					e.printStackTrace();
				}
				try {
					client.socket.close();
				} catch (IOException e) {
					Log.d("Client socket", "failed to close", e);
					e.printStackTrace();
				}
			}
			client = null;
		}
	}

	private void killServer() {
		if (server != null) {
			server.running = false;
			if (server.socket != null && !server.socket.isClosed()) {
				Log.v("Server", "trying to close");
				try {
					// Stackoverflow mentions a bug in socket.close() which
					// is avoided by shutting down first.
					server.socket.shutdownInput();
				} catch (IOException e) {
					Log.d("Client socket", "failed to shutdownInput", e);
					e.printStackTrace();
				}
				try {
					server.socket.close();
				} catch (IOException e) {
					Log.d("Client socket", "failed to close", e);
					e.printStackTrace();
				}
			}
			server = null;
		}
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
		if (viewScores != null) {
			mainView.removeView(viewScores);
			viewScores = null;
		} else if (viewSettings != null) {
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
		} else if (myGLView != null) {
			viewToSplash();
		} else {
			super.onBackPressed();
		}
	}

	public AlertDialog alertDialog;
	
	private void showConnecting(String ipAddress) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Connecting to " + ipAddress + " ...");
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				killClient();
				dialog.dismiss();
			}
		});
		alertDialog = builder.create();
		alertDialog.setOnCancelListener(listener2);
		alertDialog.show();
	}
	
	private OnCancelListener listener2 = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			killClient();
			Log.v("Trying", "to kill client");
		}
	};
	
	private void showWaitingForBoardSize() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Connected. Waiting for board size ...");
		alertDialog = builder.create();
		alertDialog.show();
	}
	
	private AlertDialog alertDialog2;
	
	private OnCancelListener listener = new OnCancelListener() {
		@Override
		public void onCancel(DialogInterface dialog) {
			killServer();
			Log.v("Trying", "to kill server");
		}
	};
	
	private void showWaitingForClient() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Waiting for client to connect ...");
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				killServer();
				dialog.dismiss();
			}
		});
		alertDialog2 = builder.create();
		alertDialog2.setOnCancelListener(listener);
		alertDialog2.show();
	}

	private void showYouLost(String winner) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Player " + winner + " has won the game.\nClick OK to start a new game.");
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		alertDialog2 = builder.create();
		alertDialog2.show();
	}
	
}
