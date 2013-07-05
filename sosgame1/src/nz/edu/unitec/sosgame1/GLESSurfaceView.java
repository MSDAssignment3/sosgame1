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


import nz.edu.unitec.sosgame1.controller.LogicControl;
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
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.*;

public class GLESSurfaceView extends GLSurfaceView 
	implements OnTouchListener {

	public GLRenderer renderer;
	private ObjectAnimator anim;
	private ObjectAnimator anim2;
	public boolean animationInProgress = false;
    
	/** Handles pinch gestures for zooming. */
	private ScaleGestureDetector scaleDetector;
	
	/** Scale factor returned by the scale detector. */
	private float scaleFactor;

	/** Handles gestures other than pinch zooming. */
	private SimpleOnGestureListener gestureListener;
	
	private GestureDetector gestureDetector;
	
    private LogicControl controller = null;
    private Server server = null;
    private ClientThread client = null;
    
    private static final int MODE_IDLE = 0;
    private static final int MODE_WAIT_FOR_CHOICE = 1;
	private static final int MODE_NOT_YOUR_TURN = 2;
    private int mode = MODE_IDLE;
    
    private Cell tappedCell = null;

	private Tile sTile;
	private Tile oTile;
	private Tile chosenTile;
	private PointF p = new PointF();
	private int animationCounter = 0;
	
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
        renderer.setBoard(new Board(renderer, this, 5, 5));
    }
    
    public void setController(LogicControl controller) {
    	this.controller = controller;
    }
    
    public void setServer(Server aServer) {
    	server = aServer;
    }
    
    public void setClient(ClientThread aClient) {
    	client = aClient;
    }
    
    private class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			if ((server == null && client == null) || 
					(server != null && !server.running) || 
					(client != null && !client.running)) {
				// Single device play
				doSingleTap(e);
			} else {
				// Two device play
				if (server != null && server.running &&
						controller.currentPlayerColour == Player.COLOUR_BLUE) {
					doSingleTap(e);
				}
				if (client != null && client.running &&
						controller.currentPlayerColour == Player.COLOUR_RED) {
					doSingleTap(e);
				}
			}
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
				p = renderer.getWorldXY(x, y, GLRenderer.cellZ
						+ GLRenderer.cellScaleFactorZ);
				Tile tile = (Tile) renderer.getSelectedCube(p, renderer.board.tiles);
				if (tile == null) {
					showTilesToChoose(p);
					if (server != null && client == null) {
						server.setMessage(Constant.SHOW_TILES_TO_CHOOSE, p.x + ","+p.y);
					} else if (server == null && client != null) {
						client.setMessage(Constant.SHOW_TILES_TO_CHOOSE, p.x + ","+p.y);
					}
				} else {
					float z = tile.z;
					anim = ObjectAnimator.ofFloat(tile, "z", z, z - 0.2f, z);
					anim.setDuration(300);
					anim.addListener(new AnimatorListenerAdapter() {
						public void onAnimationEnd(Animator animation) {
							// Stop continuous screen updates to save battery
							decrementAnimations();
							requestRender();
						}
					});
					// Start continuous screen updates for duration of animation
					incrementAnimations();
					anim.start();
				}
				break;
			case MODE_WAIT_FOR_CHOICE:
				p = renderer.getWorldXY(x, y, GLRenderer.tileZ
						+ GLRenderer.tileScaleFactorZ * 2);
				chooseTile(p);
				if (server != null && client == null) {
					server.setMessage(Constant.CHOOSE_TILE, p.x + ","+p.y);
				} else if (server == null && client != null) {
					client.setMessage(Constant.CHOOSE_TILE, p.x + ","+p.y);
				}
				break;
			case MODE_NOT_YOUR_TURN:
				// TODO Do we need to do anything here?
				break;
			}
		}
	}

	/** The player has either tapped on one of the two tiles or has tapped
	 * elsewhere. If a tile was tapped then add the tile to the tiles
	 * collection and animate. Otherwise clear the temporary tiles.
	 * @param p The x, y coordinates in the 3D world space.
	 */
	public void chooseTile(PointF p) {
		chosenTile = (Tile) renderer.getSelectedCube(p, renderer.board.tempTiles);

		if (chosenTile != null) {
			synchronized (renderer.board.tiles) {
				renderer.board.tiles.add(chosenTile);
			}
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
					decrementAnimations();
					requestRender();
				}
			});
			animSet.playTogether(anim, anim2);
			synchronized (renderer.board.tempTiles) {
				renderer.board.tempTiles.clear();
			}
			// Start continuous screen updates for duration of animation
			incrementAnimations();
			animSet.start();
			
			// Call the logic controller
			PointF pt = new PointF(chosenTile.x, chosenTile.y);
			Point pt2 = renderer.board.worldToBoardXY(pt);
			controller.getAndCheck(pt2.y, pt2.x, "" + chosenTile.letter);
			
//			while (controller.currentPlayerColour == Player.COLOUR_RED) {
//				AI.PointAndLetter p1 = theAI.makeMove();
//				if (p1.p.x >= 0) {
//					renderer.board.addTile(p1.p.y, p1.p.x, Tile.COLOUR_RED, p1.letter);
//				}
//				controller.getAndCheck(p1.p.y, p1.p.x, "" + p1.letter);
//			}

		} else {
			// Clean up if player touches anywhere not on the two tiles
			synchronized (renderer.board.tempTiles) {
				renderer.board.tempTiles.clear();
			}
			requestRender();
		}
		mode = MODE_IDLE;
	}

	/** Show two tiles for the player to choose from.
	 * @param p The x, y coordinates in the 3D world space.
	 */
	public void showTilesToChoose(PointF p) {
		tappedCell = (Cell) renderer.getSelectedCube(p, renderer.board.cells);
		
		if (tappedCell != null) {
			mode = MODE_WAIT_FOR_CHOICE;
			sTile = new Tile(renderer,
					Board.playerColourToTileColour(controller.currentPlayerColour),
					tappedCell.x - GLRenderer.tileScaleFactorX,
					tappedCell.y, 'S');
			sTile.z += GLRenderer.tileScaleFactorZ * 2;
			oTile = new Tile(renderer,
					Board.playerColourToTileColour(controller.currentPlayerColour),
					tappedCell.x + GLRenderer.tileScaleFactorX,
					tappedCell.y, 'O');
			oTile.z += GLRenderer.tileScaleFactorZ * 2;
			synchronized (renderer.board.tempTiles) {
				renderer.board.tempTiles.clear();
				renderer.board.tempTiles.add(sTile);
				renderer.board.tempTiles.add(oTile);
			}
			requestRender();
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

    /** Called when a 3D animation is started. If the number of animations is
     * one then start continuous rendering.
     */
    public synchronized void incrementAnimations() {
    	animationCounter++;
    	if (animationCounter == 1) {
    		setRenderMode(RENDERMODE_CONTINUOUSLY);
    		animationInProgress = true;
    	}
    }
    
    /** Called when a 3D animation ends. If the number of animations is
     * zero then stop continuous rendering.
     */
    public synchronized void decrementAnimations() {
    	animationCounter--;
    	if (animationCounter == 0) {
    		setRenderMode(RENDERMODE_WHEN_DIRTY);
    		animationInProgress = false;
    	}
    }

}
