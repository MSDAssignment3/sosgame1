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
	
	//total will be 
	private String input [][] = new String[5][5];
	
	
	public LogicControl()
	{
		
	}
   
	public void sayHello()
	{
		System.out.println("Hello");
	}
	
	public void readAndCheck()
	{	 
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	 
	 for(int i=0;i<1;i++)
	 {
	  
		 for(int j=0;j<5;j++)
		 {
			  try
			  {
			  System.out.println("Row"+(i+1)+"Column"+(j+1)+"Please enter value.");
			  input[i][j] = br.readLine();			  
			  }
			  catch(IOException e)
			  {
				  System.out.println(e);
			  }
		 }
		
	   }
	   
	  check();
	}	
	
	
	public void readCheck()
	{	 
	  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));	 
	for(int i=0;i<3;i++)
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
		 check();
	}
	
	public void check()
	{
		int totalCorrect = 0;
		int count =0;
		String answer = "";
		String lastAnswer = "";
		
		//total 25
		for(int i=0;i<1;i++)   //checking horizontally
		{
			answer = "";
			for(int j=0;j<5;j++)  
			{ 
				if(input[i][j]!=null)
				{
					answer += input[i][j].toString();	
				}
				count++;
				if(count==3)  //check for every three cells
				{
					if(input[i][j]!=null)
					{
						lastAnswer = input[i][j].toString();	//get it for reuse
					}
					answer.trim();
					System.out.println(answer);
					if(answer.equalsIgnoreCase("SOS"))
					{		
						totalCorrect++;
						System.out.println("Total correct "+totalCorrect);
						count = 1; //reset counts	to 1					
						answer = "";
						answer +=lastAnswer;						
					}
					else
					{
						System.out.println("Incorrect");
					}
				}
										
			}
		}
			
//			//total 25
//			for(int h=0;h<5;h++)   //checking vertically
//			{
//				answer = "";
//				for(int v=0;v<5;v++)  
//				{ 
//					answer += input[v][h].toString();	
//					count++;
//					if(count==3)  //check for every three cells
//					{
//						lastAnswer = input[v][h].toString();	//get it for reuse
//						answer.trim();
//						System.out.println(answer);
//						if(answer.equalsIgnoreCase("SOS"))
//						{		
//							totalCorrect++;
//							System.out.println("Total correct "+totalCorrect);
//							count = 1; //reset counts	to 1					
//							answer = "";
//							answer +=lastAnswer;						
//						}
//						else
//						{
//							System.out.println("Incorrect");
//						}
//					}
//											
//				}		
//			
//		}
		
	
		
	}
}
