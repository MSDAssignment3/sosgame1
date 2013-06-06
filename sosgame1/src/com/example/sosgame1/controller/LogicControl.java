package com.example.sosgame1.controller;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

//Class for game logic
public class LogicControl {
	
	private String one;
	private String two;
	private String three;
	private int totalCorrect = 0;
	//total will be 
	private String input [][] = new String[5][5];	
	
	public LogicControl()
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
		 checkHorizontally();
		 checkVertically();
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
							totalCorrect++;
							System.out.println(answer+" Horizontally");
							System.out.println("Total correct "+totalCorrect);											
						}
						else
						{
							//System.out.println("Incorrect0");
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
							totalCorrect++;
							System.out.println(answer+" Horizontally");
							System.out.println("Total correct "+totalCorrect);										
						}
						else
						{
							//System.out.println("Incorrect1");
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
							totalCorrect++;
							System.out.println(answer+" Horizontally");
							System.out.println("Total correct "+totalCorrect);										
						}
						else
						{
							//System.out.println("Incorrect2");
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
							totalCorrect++;
							System.out.println(answer+" Vertically");
							System.out.println("Total correct "+totalCorrect);											
						}
						else
						{
							//System.out.println("Incorrect0");
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
							totalCorrect++;
							System.out.println(answer+" Vertically");
							System.out.println("Total correct "+totalCorrect);										
						}
						else
						{
							//System.out.println("Incorrect1");
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
							totalCorrect++;
							System.out.println(answer+" Vertically");
							System.out.println("Total correct "+totalCorrect);										
						}
						else
						{
							//System.out.println("Incorrect2");
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
	
	
}

