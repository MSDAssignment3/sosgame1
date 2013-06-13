package com.example.sosgame1;

import java.util.ArrayList;
import android.graphics.Point;
import android.graphics.PointF;

public class Board {

	private GLRenderer renderer = null;
	public int sizeX = 5;
	public int sizeY = 5;
	private int centreX = 2;
	private int centreY = 2;
	public ArrayList<Cube> cells = new ArrayList<Cube>();
	public ArrayList<Cube> tiles = new ArrayList<Cube>();
	public ArrayList<Cube> lines = new ArrayList<Cube>();
	
	/** Holds the tiles displayed for user selection. */
	public ArrayList<Cube> tempTiles = new ArrayList<Cube>();
	
	/** Holds the cubes displayed during credits animation. */
	public ArrayList<Cube> creditsCubes = new ArrayList<Cube>();
	
	/** Simple constructor.
	 * @param renderer Reference to the renderer.
	 */
	public Board(GLRenderer renderer) {
		this.renderer = renderer;
	}
	
	/** Constructor taking board dimensions.
	 * @param renderer Reference to the renderer.
	 * @param sizeX Board x dimension = number of columns.
	 * @param sizeY Board y dimension = number of rows.
	 */
	public Board(GLRenderer renderer, int sizeX, int sizeY) {
		this.renderer = renderer;
		reset(sizeX, sizeY);
	}
	
	/** Reset the board.
	 * @param sizeX Board x dimension = number of columns.
	 * @param sizeY Board y dimension = number of rows.
	 */
	public void reset(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		centreX = sizeX / 2;
		centreY = sizeY / 2;
		synchronized (cells) {
			cells.clear();
		}
		synchronized (tiles) {
			tiles.clear();
		}
		synchronized (tempTiles) {
			tempTiles.clear();
		}
		synchronized (lines) {
			lines.clear();
		}
		synchronized (cells) {
			for (int x = 0; x < sizeX; x++) {
				for (int y = 0; y < sizeY; y++) {
					cells.add(new Cell(renderer, GLRenderer.textureOffsetCell,
							x - centreX, y - centreY));
				}
			}
		}
	}

	/** Add a tile.
	 * @param row Row index starting from zero.
	 * @param column Column index starting from zero
	 * @param colour Either Tile.COLOUR_RED or Tile.COLOUR_BLUE.
	 * @param letter Either 'S' or 'O'.
	 */
	public void addTile(int row, int column, int colour, char letter) {
		PointF p = boardToWorldXY(new Point(column, row));
		synchronized (tiles) {
			tiles.add(new Tile(renderer, colour, p.x, p.y, letter));
		}
	}
	
	/** Get a tile.
	 * @param row Row index starting from zero.
	 * @param column Column index starting from zero
	 * @return A Tile object.
	 */
	public Tile getTile(int row, int column) {
		// Convert the board coordinates to world coordinates
		PointF p = boardToWorldXY(new Point(column, row));
		// TODO: Maybe use Collections to search
		for (Cube tile: tiles) {
			if (tile.x == p.x && tile.y == p.y) {
				return (Tile) tile;
			}
		}
		return null;
	}

	/** Add a line.
	 * @param start Start Point with x = column and y = row.
	 * @param end End Point with x = column and y = row.
	 * @param colour Either Line.COLOUR_RED or Line.COLOUR_BLUE.
	 */
	public void addLine(Point start, Point end, int colour) {
		PointF p1 = boardToWorldXY(new Point(start.x, start.y));
		PointF p2 = boardToWorldXY(new Point(end.x, end.y));
		// Synchronise here in case the renderer is iterating across the lines.
		synchronized (lines) {
			lines.add(new Line(renderer, p1.x, p1.y, p2.x, p2.y, colour));
		}
	}
	
	/** Add a line.
	 * @param row1 Start row index.
	 * @param column1 Start column index.
	 * @param row2 End row index.
	 * @param column2 End column index.
	 * @param colour Either Line.COLOUR_RED or Line.COLOUR_BLUE.
	 */
	public void addLine(int row1, int column1, int row2, int column2,
			int colour) {
		addLine(new Point(column1, row1), new Point(column2, row2), colour);
	}
	
	/** Convert a PointF in the 3D world space to a Point in the board array.
	 * @param p A PointF with 3D world x, y coordinates.
	 * @return A Point with x = column and y = row.
	 */
	public Point worldToBoardXY(PointF p) {
		return new Point(Math.round(p.x) + centreX, centreY - Math.round(p.y));
	}
	
	/** Convert a Point in the board array to a PointF in the 3D world space.
	 * @param p A Point with x = column and y = row.
	 * @return A PointF with 3D world x, y coordinates.
	 */
	public PointF boardToWorldXY(Point p) {
		return new PointF(p.x - centreX, centreY - p.y);
	}
	
}
