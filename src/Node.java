/**
 * Keeps track of point on the grid in order to generate
 * paths 
 * 
 * (Idea suggested by Henry Bullingham and I used wikipedia's
 * page on node graph architecture for help)
 *
 * @author Alexander Shah
 * @version Jun 13, 2015
 */
public class Node
{
	public int row, col;
	public Node previous;
	
	/**
	 * Constructor for a new node
	 * 
	 * @param row the given row of the node
	 * @param col the given column of the node
	 * @param previous the Node created before this one
	 */
	public Node (int row, int col, Node previous)
	{
		this.row = row;
		this.col = col;
		this.previous = previous;
	}
	
	/**
	 * 
	 */
	public boolean equals (Object other)
	{
		if (!(other instanceof Node))
		{
			return false;
		}
		
		Node otherNode = (Node) other;
		
		return row == otherNode.row && col == otherNode.col;
	}
	
}
