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
	//total will be 
	private String input [][] = new String[25][25];	
	
	private boolean firstPlayer = false;  
	private boolean secondPlayer = true;
	private boolean giveTurnToFirst = false;
	private boolean giveTurnToSecond = false;	
	private int firstPlayerScore = 0;
	private int secondPlayerScore = 0;
	private List <CrossedCoordinate> crossedCoordinateList = new ArrayList<CrossedCoordinate>();
	public int currentPlayerColour = Player.COLOUR_BLUE;  //blue for player 1 and red for player 2
	//public int secondPlayerColour = Player.COLOUR_RED;
	private Board board = null;
	
	public LogicControl()
	{
		
	}  
	
	public LogicControl(Board board)
	{
		this.board = board;
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
		 input[indexRow][indexColumn] =inputValue;
		 
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
		 
		 checkHorizontally();
		 checkVertically();
		 checkTopRight();
		 checkTopLeft();
		 checkBottomLeft();
		 checkBottomRight();
	}
	
	/**
	 * For checking horizontally. 
	 */
	public void checkHorizontally()
	{
		int count =0;
		String answer = "";
		String lastAnswer = "";
		
		//total 25
		for(int i=0;i<5;i++)   //checking horizontally
		{
			answer = "";
			//it will check 3 times for each row (for example (0,1,2),(1,2,3),(2,3,4))
			for(int j=0;j<3;j++)  
			{ 
				
				switch(j)
				{
				
				case(0):
					for(int k=0;k<3;k++)
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
				break;
				
				case(1):
					for(int k=1;k<4;k++)
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
								System.out.println(answer+" Horizontally");
								System.out.println("Total correct "+totalCorrect);										
								board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
								//store coordinates							
								storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);
							}							
							
						}
						count = 0; //reset counts	to 0					
						answer = "";	
					}
				  }
				break;
				
				case(2):
					for(int k=2;k<5;k++)
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
							
							System.out.println(hasStored+"case2");
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
		int count =0;
		String answer = "";
		String lastAnswer = "";		
		
		for(int j=0;j<5;j++)   //checking vertically
		{
			answer = "";
			//it will check 3 times for each column (for example (0,1,2),(1,2,3),(2,3,4))
			for(int i=0;i<3;i++)  
			{ 
				
				switch(i)
				{
				
				case(0):
					for(int k=0;k<3;k++)
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
				break;
				
				case(1):
					for(int k=1;k<4;k++)
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
				break;
				
				case(2):
					for(int k=2;k<5;k++)
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
								System.out.println(answer+" Vertically");
								System.out.println("Total correct "+totalCorrect);			
								board.addLine(temI1, temJ1, temI3, temJ3 , currentPlayerColour);
								//store coordinates							
								storeCoordinates(temI1,temJ1,temI2,temJ2,temI3,temJ3);
							}
						}
						
						count = 0; //reset counts	to 0					
						answer = "";	
					}
				  }
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
		//j value will start from existing columns -2 (array start from 0)
		int i=0,j=2;
		int columns = 2; //start from 2 and will increment till it is  equal to 4 
		int count =0;
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
							//System.out.println(k+","+columns+"less than columns");
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
			if(columns<4)
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
		//j value will start from existing columns -2 (array start from 0)
		int j=2;
		int columns = 2; //start from 2 and will increment till it is  equal to 4 
		int count =0;
		String answer = "";	
		
		while(j<5)
		{						
				// k=j 
				for(int k=columns,i=0;k>=0;k--,i++)
				{
					if(input[i][k]!=null)
					{
						answer += input[i][k].toString();	
						//System.out.println(answer+i+","+k);	
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
						
						//System.out.println(i+","+columns+k);
						if(k>0)//if there are more cells to check then start from previous cell
						{
							//System.out.println(i+","+columns+k+"greater than 0");
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
			if(columns<4)
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
		int columns = 3; //start from existing columns - 1 (array start from zero) 
		int count =0;
		String answer = "";	
		int newI = 1;
		while(columns>=2)      //j >= than existing columns -2 (array start from zero)
		{						
				// j 
				for(int i=0,j=0;j<=columns;j++,i++)
				{
					if(input[i+newI][j]!=null)
					{
						answer += input[i+newI][j].toString();	
						//System.out.println(answer+i+","+k);	
					}
					count++;
					//System.out.println(i+","+columns+j);
					//System.out.println(count);
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
							//System.out.println(i+","+columns+j+"less than columns");
							j=j-2;
							i=i-2;
							//System.out.println(i+","+columns+j+"less than columns");
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
		//j value will start from existing columns value
		//int j = 4;
		int columns = 3; //start from existing columns - 1 (array start from zero) 
		int count =0;
		String answer = "";	
		int newI = 1;
		
		while(columns>=2)      //j >= than existing columns -2 (array start from zero)
		{						
				// j 
				for(int i=0,j=4;i<=columns;j--,i++)
				{
					if(input[i+newI][j]!=null)
					{
						answer += input[i+newI][j].toString();	
						//System.out.println(answer+i+","+k);	
					}
					count++;
					//System.out.println(i+","+columns+j);
					//System.out.println(count);
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
							//System.out.println(i+","+columns+j+"less than columns");
							j=j+2;
							i=i-2;
							//System.out.println(i+","+columns+j+"less than columns");
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
				System.out.println(crossed.getI1()+""+crossed.getJ1());
				if( i2==crossed.getI2()&&j2==crossed.getJ2())
				{
					System.out.println(crossed.getI2()+""+crossed.getJ2());
					if( i3==crossed.getI3()&&j3==crossed.getJ3())
					{
						System.out.println(crossed.getI3()+""+crossed.getJ3());
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
	
}

