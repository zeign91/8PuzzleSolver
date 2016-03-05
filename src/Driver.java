import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Stack;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Homework 1
*CSCI 363
*/

public class Driver {
	
	static String userInput;
	static int numMoves = 0;
	static int fLimit = 0;
	static boolean goalFound = false;
	
	static int[] startStateEasy = {1, 3, 4, 		//Easy
								   8, 6, 2, 
								   7, 0, 5};
	
	static int[] startStateMed = {2, 8, 1, 			//Medium
								  0, 4, 3, 
								  7, 6, 5};
	
	static int[] startStateHard = {2, 8, 1, 		//Hard
								   4, 6, 3,
								   0, 7, 5};
	
	static int[] startStateWorst = {5, 6, 7, 		//Worst
									4, 0, 8, 
									3, 2, 1};
	
	public static void main (String []args)
	{		
		while (true)
		{
			System.out.print("Menu\n"
						  + "(1)New\n"
						  + "(2)Quit\n"
						  + "Enter: ");
			Scanner scan0 = new Scanner(System.in);
			userInput = scan0.nextLine();
			
			if (Integer.parseInt(userInput) == 1)
			{
				while(true)
				{
					int[] levelInput = null;
					int searchInput = -1;
				
					System.out.print("\nSelect puzzle difficulty: (1)easy, (2)medium, (3)hard, (4)worst\n"
							+ "Enter: ");
					
					Scanner scan1 = new Scanner(System.in);
					userInput = scan1.nextLine();
					
					if (Integer.parseInt(userInput) == 1) {
						levelInput = startStateEasy;
						System.out.println("\nSelected: Easy");
					}
					else if (Integer.parseInt(userInput) == 2) {
						levelInput = startStateMed;
						System.out.println("\nSelected: Medium");
					}
					else if (Integer.parseInt(userInput) == 3) {
						levelInput = startStateHard;
						System.out.println("\nSelected: Hard");
					}
					else if (Integer.parseInt(userInput) == 4) {
						levelInput = startStateWorst;
						System.out.println("\nSelected: Worst");
					}
					else
					{
						System.out.println("Invalid level input");
						System.exit(0);
					}
					
					System.out.print("\nSelect search algorithm:\n"
							+ "(1)A*\n"
							+ "(2)A* w/ Manhattan\n"
							+ "(3)IDA* w/ Manhattan\n"
							+ "(4)Depth-first Branch and Bound w/ Manhattan\n"
							+ "Enter: ");
					
					Scanner scan2 = new Scanner(System.in);
					userInput = scan2.nextLine();
					
					if (Integer.parseInt(userInput) == 1) {
						PuzzleBoard.Manhattan = false;
						aStar(levelInput);
					}
					else if (Integer.parseInt(userInput) == 2) {
						PuzzleBoard.Manhattan = true;
						aStar(levelInput);
					}
					else if (Integer.parseInt(userInput) == 3) {
						PuzzleBoard.Manhattan = true;
						idaStar(levelInput);
					}
					else if (Integer.parseInt(userInput) == 4) {
						PuzzleBoard.Manhattan = true;
						fLimit = Integer.MAX_VALUE; //infinity
						DFBnB(levelInput, fLimit);
					}
					else {
						System.out.println("Invalid search input");
						System.exit(0);
					}
					
					numMoves = 0;
					PuzzleBoard.nodeCount = 0;
					break;
				}	
			}
			
			else if (Integer.parseInt(userInput) == 2)
			{
				System.out.println("Exiting...");
				break;
			}
			else 
			{
				System.out.println("Invalid input");
				break;
			}
		}
		
	}
	
	/**
	 * A* implementation
	 * 
	 * @param startState
	 */
	public static void aStar(int[] startState)
	{
		PuzzleBoard start = new PuzzleBoard(startState, 0);
		//priority queue is used for open list to have the element with the lowest f value be the first element defined by the comparator
		PriorityQueue<PuzzleBoard> openList = new PriorityQueue<PuzzleBoard>();		//nodes to be evaluated
		LinkedList<PuzzleBoard> closedList = new LinkedList<PuzzleBoard>();			//nodes already evaluated
		
		final long startTime = System.currentTimeMillis();	//start execution timer
		openList.add(start);								//add the starting state to the open list
		
		while(openList.size() > 0)	//while open list is not empty
		{
			//Check if first element of open list is the same as goal state then show winning sequence
			PuzzleBoard x = openList.peek();	
			PuzzleBoard goal = new PuzzleBoard(PuzzleBoard.goalPuzzle, 0);
			if(x.mapEquals(goal))
            {
				final long endTime = System.currentTimeMillis();								//end execution timer
				System.out.println("\nExecution time: " + (endTime - startTime) + " ms");
				System.out.println("Expanded nodes: " + PuzzleBoard.getNodeCount());
				Stack<PuzzleBoard> toDisplay = reconstruct(x);									//reconstruct a stack of PuzzleBoard parents starting final state
				System.out.println("Optimal Sequence: " + numMoves + " moves\n");
				System.out.println(start.toString());
				print(toDisplay);
				return;
            }
			
			closedList.add(openList.poll());						//remove the first element from open list and add to closed list
			
            LinkedList <PuzzleBoard> neighbor = x.getChildren();	//get the children nodes of current PuzzleBoard
              
            while(neighbor.size() > 0)								//while there are still children/neighbors to consider
            {
            	PuzzleBoard y = neighbor.removeFirst();				//remove and return the first element of the children list

            	if(closedList.contains(y))							//if current child exist in closed list then continue loop
            		continue;

            	if(!closedList.contains(y))							//add current child to the open list
            		openList.add(y);
            }
		}
	}	
	
	/**
	 * IDA* implementation
	 * 
	 * @param startState
	 */
	public static void idaStar(int[] startState)
	{
		PuzzleBoard start = new PuzzleBoard(startState, 0);
		fLimit = start.getF();
		//System.out.println("Start fLimit: " +fLimit);
		while (!goalFound) {
			DFBnB(startState, fLimit);
			//System.out.println("fLimit: " + fLimit);
		}
		goalFound = false;
	}
	
	/**
	 * Depth-First Branch and Bound implementation
	 * 
	 * @param startState
	 * @param minF
	 */
	public static void DFBnB(int[] startState, int minF)
	{
		PriorityQueue<PuzzleBoard> Q = new PriorityQueue<PuzzleBoard>();	
		LinkedList<PuzzleBoard> C = new LinkedList<PuzzleBoard>();
		
		PuzzleBoard start = new PuzzleBoard(startState, 0);
		PuzzleBoard goal = new PuzzleBoard(PuzzleBoard.goalPuzzle, 0);
		
		final long startTime = System.currentTimeMillis();	//start execution timer
		Q.add(start);
		int L = minF;

		while (Q.size() > 0) 
		{
			PuzzleBoard x = Q.peek();
			if(x.mapEquals(goal))
            {
				final long endTime = System.currentTimeMillis();								//end execution timer
				System.out.println("\nExecution time: " + (endTime - startTime) + " ms");
				System.out.println("Expanded nodes: " + PuzzleBoard.getNodeCount());
				Stack<PuzzleBoard> toDisplay = reconstruct(x);									//reconstruct a stack of PuzzleBoard parents starting final state
				System.out.println("Optimal Sequence: " + numMoves + " moves\n");
				System.out.println(start.toString());
				print(toDisplay);
				
				L = Math.min(x.getF(), L);														//for DFBnB
				System.out.println("F cost of goal: " +L);
				
				goalFound = true;																//for IDA* to exit while
				
				return;
            }
			
			LinkedList<PuzzleBoard> child_nodes = x.getChildren();
			PriorityQueue<PuzzleBoard> minFList = new PriorityQueue<PuzzleBoard>();				//Reason for this priority queue is to sort the successors by min f cost
			C.add(Q.poll());
					
			while (child_nodes.size() > 0) 
			{
				PuzzleBoard y = child_nodes.removeFirst();
				minFList.add(y);																//Add successors to priority queue
			}
			
			PuzzleBoard tempMinFPuzzle = minFList.peek();
			int counter = 0;																	//if no successors were added to the Q then minimum f needs to change
																								//if counter = 0 then no successors were added
			while (minFList.size() > 0)
			{
				PuzzleBoard z = minFList.poll();
				if (C.contains(z))
					continue;
				if (z.getF() > L)
					continue;
				if (!C.contains(z)) {
					Q.add(z);
					counter++;																	//if successors added then counter > 0
				}
			}
					
			if (counter == 0) 																	//change the f limit to the minimum f among the successors
				fLimit = tempMinFPuzzle.getF();

			
		}
	}
			
	
	
	/**
	 * This method is used to display the 
	 * toString representation of the puzzle 
	 * after popping it from the stack.
	 * 
	 * @param x
	 */
	public static void print(Stack<PuzzleBoard> x)
    {
		while(!x.isEmpty())
		{
			PuzzleBoard temp = x.pop();
			System.out.println(temp.toString());
		}
    }
	
	/**
	 * After finding the goal state, backtrack and cycle through
	 * the parents of the goal state and add them to the stack.
	 * 
	 * @param winner
	 * @return
	 */
	public static Stack<PuzzleBoard> reconstruct(PuzzleBoard winner)
	{
		Stack<PuzzleBoard> correctOutput = new Stack<PuzzleBoard>();
                
		while(winner.getParent() != null)
		{
			correctOutput.add(winner);
			winner = winner.getParent();
			numMoves++;	//optimal move counter
		}
		
		return correctOutput;
	}
			
}
