package com.example.sosgame1.controller;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.example.sosgame1.Board;
import com.example.sosgame1.Player;

//Class for game logic
public class LogicControl {
	
	private String one;
	private String two;
	private String three;
	private int totalCorrect = 0;
	private String input [][];
	
	private int boardRows =0;
	private int boardColumns = 0;
	private int control = 0;
	
	private boolean firstPlayer = true;  
	private boolean secondPlayer = false;
	private boolean giveTurnToFirst = false;
	private boolean giveTurnToSecond = false;	
	private int firstPlayerScore = 0;
	private int secondPlayerScore = 0;
	private List <CrossedCoordinate> crossedCoordinateList = new ArrayList<CrossedCoordinate>();
	public int currentPlayerColour = Player.COLOUR_BLUE;  //blue for player 1 and red for player 2
	private Board board = null;
	private int numEntered = 0;
	
	public LogicControl()
	{
		
	}  
	
	public LogicControl(Board board)
	{
		this.board = board;
	}
	
	//change
	public LogicControl(Board board,int i,int j)
	{
		this.board = board;
		boardRows = i;  
		boardColumns = j; 
		input = new String[boardRows][boardColumns];	
		
		if(i==5)
		{
			control=3;
		}
		
		if(i==7)
		{
			control = 5;
		}
		
		if(i==9)
		{
			control = 7;
		}
		
	}
	
	
	//TODO need to modify
	public void getPlayerNames()
	{
		
	}
	
	public void readCheck()
	{	 
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	 
	for(int i=0;i<25;i++)
	{
			  try
			  {
			  System.out.println("Please enter row value.");
			  int row = Integer.parseInt(br.readLine());
			  System.out.println("Please enter column value.");
			  int column= Integer.parseInt(br.readLine());
			  System.out.println("Please enter value.");
			  String answer= br.readLine();
			  getAndCheck(row,column,answer);
			  			  
			  }
			  catch(IOException e)
			  {
				  System.out.println(e);
			  }
		 }  
	   	  
	}	
	
	public void getAndCheck(int indexRow,int indexColumn,String inputValue)
	{
		boolean canEnter = true;
		if (canEnter == true)
		{
		input[indexRow][indexColumn] =inputValue;
		 numEntered++;
		 checkHorizontally();
		 checkVertically();
		 checkTopRight();
		 checkTopLeft();
		 checkBottomLeft();
		 checkBottomRight();
		 
		 //toggling boolean value
		 if(firstPlayer==true && giveTurnToFirst==false)
		 {
			 secondPlayer = true;
			 firstPlayer = false;
			 currentPlayerColour = Player.COLOUR_RED;
		 }
		 else if(secondPlayer == true && giveTurnToSecond ==false)
		 {
			 firstPlayer = true;
			 secondPlayer = false;
			 currentPlayerColour = Player.COLOUR_BLUE;
		 }
		 
		 giveTurnToFirst = false; //reset
		 giveTurnToSecond = false; //reset
		}
		 
		 	if(control==3)  // this is for 5*5 Board
			{
				if(numEntered==25)
				{
					
				}
			}
			
			if(control == 5) // this is for 7*7 Board
			{
				if(numEntered==49)
				{
					
				}
			}
			
			if(control == 7) // this is for 9*9 Board
			{
				if(numEntered==81)
				{
					
				}
			}
	}
	
	/**
	 * For checking horizontally. 
	 */
	public void checkHorizontally()
	{
		
		//total 25
		for(int i=0;i<boardColumns;i++)   //checking horizontally
		{
			//it will check 3 times for each row (for example (0,1,2),(1,2,3),(2,3,4))
			for(int j=0;j<control;j++)  
			{ 
				
				switch(j)
				{
				
				case(0):
					checkHorizontal(i,j,3);	
				break;
				
				case(1):
					checkHorizontal(i,j,4);	
				break;
				
				case(2):
					checkHorizontal(i,j,5);	
				break;
				
				case(3):
					checkHorizontal(i,j,6);	
				break;
				
				case(4):
					checkHorizontal(i,j,7);	
				break;
				
				case(5):
					checkHorizontal(i,j,8);	
				break;
				
				case(6):
					checkHorizontal(i,j,9);	
				break;
				
				}				
										
			}
		}		
		
	}
	

	/**
	 * For checking vertically. 
	 */
	public void checkVertically()
	{			
		for(int j=0;j<boardRows;j++)   //checking vertically
		{
			//it will check 3 times for each column (for example (0,1,2),(1,2,3),(2,3,4))
			for(int i=0;i<control;i++)  
			{ 
				
				switch(i)
				{
				
				case(0):
					checkVertical(i,j,3);
				break;
				
				case(1):
					checkVertical(i,j,4);
				break;
				
				case(2):
					checkVertical(i,j,5);
				break;
				
				case(3):
					checkVertical(i,j,6);
				break;
				
				case(4):
					checkVertical(i,j,7);
				break;
				
				case(5):
					checkVertical(i,j,8);
				break;
				
				case(6):
					checkVertical(i,j,9);
				break;
			
				}				
										
			}
		}
				
	}
	
	/**
	 * Checking from top right
	 */
	public void checkTopRight()
	{
		//j value will start from existing columns -2 (array start from 0 so -3)
		//int i=0,j=2;
		int i=0,j=boardColumns - 3;
		int columns = 2; // will increment till it is  equal to columns -1 
		int count =0;
		int columnControl = boardColumns -1;
		String answer = "";	
		
		while(j!=-1)
		{						
				// k=j and k is also used as i
				for(int k=0;k<=columns;k++)
				{
					if(input[k][j+k]!=null)
					{
						answer += input[k][j+k].toString();	
					}
					count++;
					if(count==3)
					{
						answer.trim();						
						if(answer.equalsIgnoreCase("SOS"))
						{	
							//get coordinates
							int temI1 = k-2;
							int temJ1 = (j+k)-2;
							int temI2 =k-1;
							int temJ2 = (j+k)-1;
							int temI3 = k;
							int temJ3 = j+k;
							boolean hasStored = checkIfStored(temI1,temJ1,temI2,temJ2,temI3,temJ3);
							
							if(hasStored==false)
							{														
								totalCorrect++;
								changePlayerTurn();	
								System.out.println(answer+"Top Right");
								System.out.println("Total correct "+totalCorrect);	
								board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
								//store coordinates							
								storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);

							}
						}
						
						if(k<columns)//if there are more cells to check then start from previous cell
						{							
							k=k-2;
						}
						
						//reset
						answer = "";
						count = 0;
					}
	
				}
			
			
			//reset
			answer="";
			count=0;
			if(columns<columnControl)
			{
				columns++;
			}
			j--;  //start from one less column			
			
		}
			
	}
	
	
	/**
	 * Checking from top right
	 */
	public void checkTopLeft()
	{
		//j value will start from 2
		int j=2;
		int columns = 2; // will increment till it is  equal to columns -1 
		int count =0;
		String answer = "";	
		int columnControl = boardColumns -1;
		
		while(j<boardColumns)
		{						
				// k=j 
				for(int k=columns,i=0;k>=0;k--,i++)
				{
					if(input[i][k]!=null)
					{
						answer += input[i][k].toString();	
					}
					count++;
					if(count==3)
					{
						answer.trim();						
						if(answer.equalsIgnoreCase("SOS"))
						{	
							//get coordinates
							int temI1 = i-2;
							int temJ1 = k+2;
							int temI2 = i-1;
							int temJ2 = k+1;
							int temI3 = i;
							int temJ3 = k;
							boolean hasStored = checkIfStored(temI1,temJ1,temI2,temJ2,temI3,temJ3);
							
							if(hasStored==false)
							{
								totalCorrect++;
								changePlayerTurn();	
								board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
								System.out.println(answer+"Top Left");
								System.out.println("Total correct "+totalCorrect);		
								
								//store coordinates							
								storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);

							}
						}						
						
						if(k>0)//if there are more cells to check then start from previous cell
						{							
							k=k+2;
							i=i-2;
						}
						
						//reset
						answer = "";
						count = 0;
					}
	
				}
			
			
			//reset
			answer="";
			count=0;
			if(columns<columnControl)
			{
				columns++;
			}
			j++;  //start from another column			
			
		}
	}
	
	
	/**
	 * Checking from Bottom left
	 */
	public void checkBottomLeft()
	{
		//j value will start from zero
		int columns = boardColumns - 2; //start from existing columns - 1 (array start from zero so -2) 
		int count =0;
		String answer = "";	
		int newI = 1;
		while(columns>=2)          
		{						
				// j 
				for(int i=0,j=0;j<=columns;j++,i++)
				{
					if(input[i+newI][j]!=null)
					{
						answer += input[i+newI][j].toString();	
					}
					count++;					
					if(count==3)
					{
						answer.trim();						
						if(answer.equalsIgnoreCase("SOS"))
						{		
							//get coordinates
							int temI1 = (i+newI)-2;
							int temJ1 = j-2;
							int temI2 = (i+newI)-1;
							int temJ2 = j-1;
							int temI3 = (i+newI);
							int temJ3 = j;
							boolean hasStored = checkIfStored(temI1,temJ1,temI2,temJ2,temI3,temJ3);
							if(hasStored==false)
							{							
								totalCorrect++;
								changePlayerTurn();		
								board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
								System.out.println(answer+"Bottom Left");
								System.out.println("Total correct "+totalCorrect);		
								
								//store coordinates							
								storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);

							}
						}
						
						//System.out.println(i+","+columns+j);
						if(j<columns)//if there are more cells to check then start from previous cell
						{							
							j=j-2;
							i=i-2;							
						}
						
						//reset
						answer = "";
						count = 0;
					}
	
				}
			
			
			//reset
			answer="";
			count=0;
			columns--; //start from another column	 	
			newI++;   //i will start from 2
			
		}
	}
	
	
	/**
	 * Checking from Bottom Right
	 */
	public void checkBottomRight()
	{
		//j value will start from existing columns value -1		
		int forJ = boardColumns - 1;
		int columns =  boardColumns - 2; ; //start from existing columns - 1 (array start from zero so -2) 
		int count =0;
		String answer = "";	
		int newI = 1;
		
		while(columns>=2)          //j >= than existing columns -2 (array start from zero so -3)
		{						
				// j 
				for(int i=0,j=forJ;i<=columns;j--,i++)
				{
					if(input[i+newI][j]!=null)
					{
						answer += input[i+newI][j].toString();							
					}
					count++;
					if(count==3)
					{
						answer.trim();						
						if(answer.equalsIgnoreCase("SOS"))
						{		
							//get coordinates
							int temI1 = (i+newI)-2;
							int temJ1 = j+2;
							int temI2 = (i+newI)-1;
							int temJ2 = j+1;
							int temI3 = i+newI;
							int temJ3 = j;
							
							boolean hasStored = checkIfStored(temI1,temJ1,temI2,temJ2,temI3,temJ3);
							if(hasStored==false)
							{
								totalCorrect++;
								changePlayerTurn();		
								board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
								System.out.println(answer+"Bottom Right");
								System.out.println("Total correct "+totalCorrect);	
								
								//store coordinates							
								storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);

							}
						}
						
						//System.out.println(i+","+columns+j);
						if(i<columns)//if there are more cells to check then start from previous cell
						{							
							j=j+2;
							i=i-2;
						}
						
						//reset
						answer = "";
						count = 0;
					}
	
				}
			
			
			//reset
			answer="";
			count=0;
			columns--; //start from another column	 	
			newI++;   //i will start from 2
			
		}
	}
	
	/**
	 * Check if line has been made on these coordinates
	 * @param i1
	 * @param j1
	 * @param i2
	 * @param j2
	 * @param i3
	 * @param j3
	 * @return
	 */
	public boolean checkIfStored(int i1,int j1,int i2,int j2,int i3,int j3)
	{
		boolean hasStored = false;
		int listSize = 0;
		while(listSize<crossedCoordinateList.size())
		{
			CrossedCoordinate crossed = new CrossedCoordinate();
			crossed=crossedCoordinateList.get(listSize);
			
			if(i1==crossed.getI1()&&j1==crossed.getJ1())
			{				
				if( i2==crossed.getI2()&&j2==crossed.getJ2())
				{					
					if( i3==crossed.getI3()&&j3==crossed.getJ3())
					{						
						hasStored = true;
						break;
					}
				}
			}	
			listSize++;
			
		}
		return hasStored;
	}
	
	/**
	 * Toggle player turn and allocate mark
	 */
	public void changePlayerTurn()
	{
		if(firstPlayer==true)
		{
			firstPlayerScore++;
			board.playerOne.setScore(firstPlayerScore);
			giveTurnToFirst =true;
			System.out.println("FirstPlayer"+firstPlayerScore);
		}
		else if(secondPlayer==true)
		{
			secondPlayerScore++;
			board.playerTwo.setScore(secondPlayerScore);
			giveTurnToSecond = true;
			System.out.println("SecondPlayer"+secondPlayerScore);
		}
	}
	
	/**
	 * Store the coordinates where line is currently made
	 */
	public void storeCoordinates(int i1,int j1,int i2,int j2,int i3,int j3)
	{
		CrossedCoordinate cro = new CrossedCoordinate(i1,j1,i2,j2,i3,j3);
		crossedCoordinateList.add(cro);			
	}
	
	public void checkHorizontal(int i,int j,int counter)
	{
		int count =0;
		String answer = "";
		for(int k=j;k<counter;k++)
		{
		if(input[i][k]!=null)
		{
			answer += input[i][k].toString();	
		}
		count++;
		if(count==3)  //check for every three cells
		{					
			answer.trim();						
			if(answer.equalsIgnoreCase("SOS"))
			{		
				//get coordinates
				int temI1 = i;
				int temJ1 = k-2;
				int temI2 = i;
				int temJ2 = k-1;
				int temI3 = i;
				int temJ3 = k;
				boolean hasStored = checkIfStored(temI1,temJ1,temI2,temJ2,temI3,temJ3);						
				
				if(hasStored==false)
				{
					totalCorrect++;
					changePlayerTurn();							
					board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
					System.out.println(answer+" Horizontally");
					System.out.println("Total correct "+totalCorrect);									
					
					//store coordinates							
					storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);								
				}
			}		
			
			count = 0; //reset counts	to 0					
			answer = "";
		}
	  }
	
	}

	
	public void checkVertical(int i,int j,int counter)
	{
		int count =0;
		String answer = "";
		for(int k=i;k<counter;k++)
		{
		if(input[k][j]!=null)
		{
			answer += input[k][j].toString();	
		}
		count++;
		if(count==3)  //check for every three cells
		{					
			answer.trim();						
			if(answer.equalsIgnoreCase("SOS"))
			{		
				//get coordinates
				int temI1 = k-2;
				int temJ1 = j;
				int temI2 = k-1;
				int temJ2 = j;
				int temI3 = k;
				int temJ3 = j;
				boolean hasStored = checkIfStored(temI1,temJ1,temI2,temJ2,temI3,temJ3);
				
				if(hasStored==false)
				{
					totalCorrect++;
					changePlayerTurn();
					board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
					System.out.println(answer+" Vertically");
					System.out.println("Total correct "+totalCorrect);		
					
					//store coordinates							
					storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);

				}
			}
					
			
			count = 0; //reset counts	to 0					
			answer = "";
		}
	  }
	}
	
	
}

