package nz.edu.unitec.sosgame1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import nz.edu.unitec.sosgame1.controller.LogicControl;
import android.graphics.Point;
import android.os.Handler;
import android.util.Log;

/** The AI class is used when playing against the machine.
 * The class has to run in it's own thread because it needs to wait
 * for things (animation, logic) to finish in other threads and it
 * cannot wait in those other threads without locking up the game.
 * @author David Moore
 *
 */
public class AI implements Runnable {

	private Board board;
	private LogicControl controller;
	private GLESSurfaceView surfaceView;
	private int[] offsetsX = new int[]{0, 0, 1, 2, 1, 2,  1,  2,  0,  0, -1, -2, -1, -2, -1, -2};
	private int[] offsetsY = new int[]{1, 2, 1, 2, 0, 0, -1, -2, -1, -2, -1, -2,  0,  0,  1,  2};
	private int[] offsetsOX = new int[]{ 0, 0, -1, 1, -1, 1, -1,  1};
	private int[] offsetsOY = new int[]{-1, 1,  0, 0, -1, 1,  1, -1};
	private Random random = new Random();
	public volatile boolean running = true;
	private Handler handler = new Handler();
	public volatile boolean waitForGo = false;
	
	public class PointAndLetter {
		Point p = new Point();
		char letter;
	}
	
	public AI(Board board, LogicControl controller, GLESSurfaceView surfaceView) {
		this.board = board;
		this.controller = controller;
		this.surfaceView = surfaceView;
	}
	
	@Override
	public void run() {
		while (running) {
			while (controller.currentPlayerColour == Player.COLOUR_RED && !waitForGo && running) {
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				final PointAndLetter p1 = makeMove();
				if (p1.p.x >= 0) {
					board.addTile(p1.p.y, p1.p.x, Tile.COLOUR_RED, p1.letter);
					surfaceView.requestRender();
				}
				synchronized (this) {
					waitForGo = true;
				}
				handler.post(new Runnable() {
					@Override
					public void run() {
						synchronized (this) {
							controller.getAndCheck(p1.p.y, p1.p.x, "" + p1.letter);
							waitForGo = false;
						}
					}
				});
				// Wait for animations to end
				while (surfaceView.animationInProgress) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public PointAndLetter makeMove() {
		PointAndLetter p = new PointAndLetter();
		p.p.x = -1;
		boolean found = false;
		for (int row = 0; row < board.sizeY; row++) {
			for (int col = 0; col < board.sizeX; col++) {
				if (controller.input[row][col] == null) {
					// Check for cases where S makes a line
					for (int index = 0; index < offsetsX.length; index += 2) {
						int x1 = col + offsetsX[index];
						int x2 = col + offsetsX[index + 1];
						int y1 = row + offsetsY[index];
						int y2 = row + offsetsY[index + 1];
						if (x1 >= 0 && x1 < board.sizeX && x2 >= 0 && x2 < board.sizeX &&
								y1 >= 0 && y1 < board.sizeY && y2 >= 0 && y2 < board.sizeY) {
							String foo = controller.input[y1][x1] + controller.input[y2][x2];
							if (foo.equals("OS")) {
								Log.v("AI", "found");
								p.p.x = col;
								p.p.y = row;
								p.letter = 'S';
								found = true;
							}
						}
						if (found) {
							break;
						}
					}
					if (!found) {
						// Check for cases where O makes a line
						for (int index = 0; index < offsetsOX.length; index += 2) {
							int x1 = col + offsetsOX[index];
							int x2 = col + offsetsOX[index + 1];
							int y1 = row + offsetsOY[index];
							int y2 = row + offsetsOY[index + 1];
							if (x1 >= 0 && x1 < board.sizeX && x2 >= 0 && x2 < board.sizeX &&
									y1 >= 0 && y1 < board.sizeY && y2 >= 0 && y2 < board.sizeY) {
								String foo = controller.input[y1][x1] + controller.input[y2][x2];
								if (foo.equals("SS")) {
									Log.v("AI", "found");
									p.p.x = col;
									p.p.y = row;
									p.letter = 'O';
									found = true;
								}
							}
							if (found) {
								break;
							}
						}
					}
				}
				if (found) {
					break;
				}
			}
			if (found) {
				break;
			}
		}
		// Find an empty cell if we couldn't make a line
		if (!found) {
			ArrayList<Integer> cells = new ArrayList<Integer>();
			for (int i = 0; i < board.sizeX * board.sizeY; i++) {
				cells.add(Integer.valueOf(i));
			}
			Collections.shuffle(cells);
			for (Integer cell: cells) {
				int row = cell / board.sizeX;
				int col = cell % board.sizeX;
				p.p.x = col;
				p.p.y = row;
				if (controller.input[p.p.y][p.p.x] == null) {
					Log.v("AI", "placed tile at " + p.p.x + "," + p.p.y);
					found = true;
					if (random.nextInt(20) > 10) {
						p.letter = 'S';
					} else {
						p.letter = 'O';
					}
					break;
				}
			}
		}
		return p;
	}

}
