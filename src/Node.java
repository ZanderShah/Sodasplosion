
public class Node
{
	public int row, column;
	public Node previous;
	
	public Node (int row, int column, Node previous)
	{
		this.row = row;
		this.column = column;
		this.previous = previous;
	}
	
	public boolean equals (Object other)
	{
		if (! (other instanceof Node))
			return false;
		
		Node otherNode = (Node) other;
		
		return row == otherNode.row && column == otherNode.column;
	}
	
}
