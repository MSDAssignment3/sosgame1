package com.example.sosgame1;

import java.util.ArrayList;

public class Board {

	private MyGLRenderer renderer = null;
	private int sizeX = 5;
	private int sizeY = 5;
	private int centreX = 2;
	private int centreY = 2;
	public ArrayList<Cell> cells = new ArrayList<Cell>();
	
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
		cells.clear();
		for (int x = 0; x < sizeX; x++) {
			for (int y = 0; y < sizeY; y++) {
				cells.add(new Cell(renderer, x - centreX, y - centreY));
			}
		}
	}

}
