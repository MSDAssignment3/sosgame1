package com.example.sosgame1;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;

public class Board {

	private MyGLRenderer renderer = null;
	private int sizeX = 5;
	private int sizeY = 5;
	private int centreX = 2;
	private int centreY = 2;
	public ArrayList<Cell> cells = new ArrayList<Cell>();
	public ArrayList<Tile> tiles = new ArrayList<Tile>();
	public ArrayList<Line> lines = new ArrayList<Line>();
	public ArrayList<Tile> tempTiles = new ArrayList<Tile>();
	
	public Board(MyGLRenderer renderer) {
		this.renderer = renderer;
		// foobar just testing
	}
	
	public Board(MyGLRenderer renderer, int sizeX, int sizeY) {
		this.renderer = renderer;
		reset(sizeX, sizeY);
	}
	
	public void reset(int sizeX, int sizeY) {
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		centreX = sizeX / 2;
		centreY = sizeY / 2;
		cells.clear();
		tiles.clear();
		lines.clear();
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				cells.add(new Cell(renderer, MyGLRenderer.textureOffsetCell,
						x - centreX, y - centreY));
			}
		}
	}

	public void addTile(int row, int column, int colour, char letter) {
		PointF p = boardToWorldXY(new Point(column, row));
		tiles.add(new Tile(renderer, colour, p.x, p.y, letter));
	}
	
	public Tile getTile(int row, int column) {
		// Convert the board coordinates to world coordinates
		PointF p = boardToWorldXY(new Point(column, row));
		// TODO: Maybe use Collections to search
		for (Tile tile: tiles) {
			if (tile.x == p.x && tile.y == p.y) {
				return tile;
			}
		}
		return null;
	}

	public void addLine(Point start, Point end, int colour) {
		PointF p1 = boardToWorldXY(new Point(start.x, start.y));
		PointF p2 = boardToWorldXY(new Point(end.x, end.y));
		Log.v("start", ""+start);
		Log.v("p1", ""+p1);
		Log.v("end", ""+end);
		Log.v("p2", ""+p2);
		lines.add(new Line(renderer, p1.x, p1.y, p2.x, p2.y, colour));
	}
	
	public void addLine(int row1, int column1, int row2, int column2,
			int colour) {
		addLine(new Point(column1, row1), new Point(column2, row2), colour);
	}
	
	public Point worldToBoardXY(Point p) {
		return new Point(p.x + centreX, centreY - p.y);
	}
	
	public PointF boardToWorldXY(Point p) {
		return new PointF(p.x - centreX, centreY - p.y);
	}
	
}
