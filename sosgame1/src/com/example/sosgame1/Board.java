package com.example.sosgame1;

import java.util.ArrayList;

import android.graphics.Point;
import android.graphics.PointF;

public class Board {

	private MyGLRenderer renderer = null;
	private int sizeX = 5;
	private int sizeY = 5;
	private int centreX = 2;
	private int centreY = 2;
	public ArrayList<Cell> cells = new ArrayList<Cell>();
	public ArrayList<Tile> tiles = new ArrayList<Tile>();
	public ArrayList<Line> lines = new ArrayList<Line>();
	
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

	public Point worldToBoardXY(Point p) {
		return new Point(p.x + centreX, centreY - p.y);
	}
	
	public PointF boardToWorldXY(Point p) {
		return new PointF(p.x - centreX, centreY - p.y);
	}
	
}
