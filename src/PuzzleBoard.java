import java.util.LinkedList;

/**
*@author Danny Lui
*CUNY ID#: 23200057
*Homework 1
*CSCI 363
*/

public class PuzzleBoard implements Comparable <Object> {
	
	public final static int[] goalPuzzle = {1, 2, 3, 
											8, 0, 4, 
											7, 6, 5};
	public static boolean Manhattan = false;
	public static int nodeCount = 0;	//total expanded states counter
	
	private int[] puzzle = new int[9];
	private int f = 0;
	private int g = 0;
	private int h = 0;
	PuzzleBoard parent = null;
	

	public PuzzleBoard (int[] initPuzzle, int initG)
	{
		puzzle = initPuzzle;
		g = initG;
		
		if (Manhattan == false)
			h = mismatchedTiles(puzzle);			//misplaced tiles heuristic
		else if (Manhattan == true)
			h = manhattanHeuristic(puzzle);		//manhattan heuristic
		
		f = g + h;
	}
	
	//accessor methods
	public int getF()	{	return f;	}
	public int getG()	{	return g;	}
	public int getH()	{	return h;	}
	public PuzzleBoard getParent()	{	return this.parent;	}
	public static int getNodeCount()	{ return nodeCount;	}
	
	//mutator methods
	public void setParent(PuzzleBoard input)	{	this.parent = input;	}
	
	/**
	 * This method calculates the h cost 
	 * or the number of misplaced tiles for 
	 * the current puzzle.
	 * 
	 * @param puzzle
	 * @return h
	 */
	public int mismatchedTiles(int[] puzzle)
	{
		int numMismatch = 0;
		for (int i = 0; i < 9; i++)
		{
			if(puzzle[i] != goalPuzzle[i] && puzzle[i] != 0)
				numMismatch++;
		}
		return numMismatch;
	}
	
	/**
	 * This method calculates the h cost by adding those tiles that are not in their goal
	 * positions the number of moves needed to get to their goal positions. For example
	 * 1 needs to move 2 tiles to it's goal position and 2 needs to move 3 tile to it's 
	 * goal position and so 2+3+... and so on to get the Manhattan Heuristic cost.
	 * 
	 * @param puzzle
	 * @return h
	 */
	
	public int manhattanHeuristic(int[] puzzle)	//UNCOMMENT TO USE MANHATTAN HEURISTIC and COMMENT MISPLACED TILE METHOD ABOVE
	{
		int tempH = 0;
	
		int[][] tempMatrix = {{puzzle[0], puzzle[1], puzzle[2]},
							  {puzzle[3], puzzle[4], puzzle[5]},
			                  {puzzle[6], puzzle[7], puzzle[8]}};
		
		int[][] goalMatrix = {{goalPuzzle[0], goalPuzzle[1], goalPuzzle[2]},
				  			  {goalPuzzle[3], goalPuzzle[4], goalPuzzle[5]},
				  			  {goalPuzzle[6], goalPuzzle[7], goalPuzzle[8]}};
		
		for (int i = 0; i < tempMatrix.length; i++)
		{
			for (int j = 0; j < tempMatrix.length; j++)
			{
				for (int k = 0; k < goalMatrix.length; k++)
				{
					for (int l = 0; l < goalMatrix.length; l++)
					{
						if (tempMatrix[i][j] == goalMatrix[k][l] && tempMatrix[i][j] != 0)
						{
							//System.out.println("tempMatrix: " + tempMatrix[i][j] + " row: " + i + " col: " + j);
							//System.out.println("goalMatrix: " + goalMatrix[k][l] + " row: " + k + " col: " + l);
							//System.out.println(tempMatrix[i][j] + " needs to move " + (Math.abs(i-k)+Math.abs(j-l)) + " tiles to reach goal position\n");
							tempH = tempH + (Math.abs(i-k)+Math.abs(j-l));
						}
					}
				}
			}
		}
		
		return tempH;
	}
	
	/**
	 * This method expands the current puzzle board into other possible
	 * states based on the position of the empty tile and where the empty 
	 * tile can move. In other words, the current puzzle board would be the 
	 * parent to the children that are expanded here. The 4 possible states
	 * are right, up, down, left (movements of the empty tile).
	 * 
	 * @return linked list of children
	 */
	public LinkedList<PuzzleBoard> getChildren()
    {
		LinkedList<PuzzleBoard> children = new LinkedList<PuzzleBoard>();	//create list of children nodes
		PuzzleBoard right, up, down, left;	//expand states when moving empty tile
		
		int pos = 0;
		int tempArray[] = new int[this.puzzle.length];
		
		while(this.puzzle[pos] != 0)	//find the position of empty tile [0->8]
		{
			pos++;
		}
		
		//horizontal movement of empty tile
		if(pos % 3 == 0)	//empty tile can move right for these positions [0,3,6]
		{
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos + 1];
			tempArray[pos + 1] = 0;
			
			right = new PuzzleBoard(tempArray, this.g + 1);
			right.setParent(this);
			children.add(right);

			nodeCount++;
		}
		
		else if(pos % 3 == 1)	//empty tile can move left and right for these positions [1,4,7]
		{
			//swap empty tile with right and expand/add a child
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos + 1];
			tempArray[pos + 1] = 0;
			
			right = new PuzzleBoard(tempArray, this.g + 1);
			right.setParent(this);
			children.add(right);
			
			nodeCount++;
			
			//swap empty tile with left and expand/add a child
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos - 1];
			tempArray[pos - 1] = 0;
                     
			left = new PuzzleBoard(tempArray, this.g + 1);
			left.setParent(this);
			children.add(left);
			
			nodeCount++;
		}
		
		else if(pos % 3 == 2)	//empty tile can move left for these positions [2,5,8]
		{
			//swap empty tile with left and expand/add a child
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos - 1];
			tempArray[pos - 1] = 0;
			
			left = new PuzzleBoard(tempArray, this.g + 1);
			left.setParent(this);
			children.add(left);
			
			nodeCount++;
		} 
		
		//vertical movement of empty tile
		if(pos / 3 == 0)	//empty tile can move down for these positions [0,1,2]
		{
			//swap empty tile with lower and expand/add a child
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos + 3];
			tempArray[pos + 3] = 0;
                    
			down = new PuzzleBoard(tempArray, this.g + 1);
			down.setParent(this);
			children.add(down);
			
			nodeCount++;
		}
		
		else if(pos / 3 == 1 )	//empty tile can move up and down for these positions [3,4,5] 
		{
			//swap empty tile with upper and expand/add a child
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos - 3];
			tempArray[pos - 3] = 0;
                    
			up = new PuzzleBoard(tempArray, this.g + 1);
			up.setParent(this);
			children.add(up);
			
			nodeCount++;
			
			//swap empty tile with lower and expand/add a child
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos + 3];
			tempArray[pos + 3] = 0;
                    
			down = new PuzzleBoard(tempArray, this.g + 1);
			down.setParent(this);
			children.add(down);
			
			nodeCount++;
		}
		
		else if (pos / 3 == 2 )	//empty tile can move up for these positions [6,7,8]
		{
			//swap empty tile with upper and expand/add a child
			tempArray = this.puzzle.clone();
			tempArray[pos] = tempArray[pos - 3];
			tempArray[pos - 3] = 0;
                    
			up = new PuzzleBoard(tempArray, this.g + 1);
			up.setParent(this);
			children.add(up);
			
			nodeCount++;
		}

		return children;
    }
	
	//string representation of puzzle
	public String toString()
    {
		String x = "";
		for(int i = 0; i < this.puzzle.length; i++)
		{
			x += puzzle[i] + " ";
			if((i + 1) % 3 == 0)
				x += "\n";
		}
		return x;
    }
    
	//for the open list priority queue where f values of the elements in the queue will be lowest from the head
	public int compareTo(Object input) 
    {
    	if (this.f < ((PuzzleBoard) input).getF())
    		return -1;
    	else if (this.f > ((PuzzleBoard) input).getF())
    		return 1;
    	return 0;
    }
    
    public boolean equals(PuzzleBoard test)
    {
    	if(this.f != test.getF())
    		return false;
    	for(int i = 0 ; i < this.puzzle.length; i++)
    	{
    		if(this.puzzle[i] != test.puzzle[i])
    			return false;
    	}
    	return true;
    }
   
    //Used to compare the current state to goal state
    public boolean mapEquals(PuzzleBoard test)
    {
    	for(int i = 0 ; i < this.puzzle.length; i++)
    	{
    		if(this.puzzle[i] != test.puzzle[i])
    			return false;
    	}
    	return true;
    }
}
