package nz.edu.unitec.sosgame1.controller;

//This class will store the crossed coordinate value of cells where line has been made
public class CrossedCoordinate {

	private int i1;
	private int j1;
	private int i2;
	private int j2;
	private int i3;
	private int j3;
	
	public CrossedCoordinate(){};
	
	public CrossedCoordinate(int i1,int j1,int i2,int j2,int i3,int j3)
	{
	   this.i1 = i1;
	   this.j1 = j1;
	   this.i2 = i2;
	   this.j2 = j2;
	   this.i3 = i3;
	   this.j3 = j3;		
	}
	
	
	public int getI1() {
		return i1;
	}

	public void setI1(int i1) {
		this.i1 = i1;
	}

	public int getJ1() {
		return j1;
	}

	public void setJ1(int j1) {
		this.j1 = j1;
	}

	public int getI2() {
		return i2;
	}

	public void setI2(int i2) {
		this.i2 = i2;
	}

	public int getJ2() {
		return j2;
	}

	public void setJ2(int j2) {
		this.j2 = j2;
	}

	public int getI3() {
		return i3;
	}

	public void setI3(int i3) {
		this.i3 = i3;
	}

	public int getJ3() {
		return j3;
	}

	public void setJ3(int j3) {
		this.j3 = j3;
	}
	
}
