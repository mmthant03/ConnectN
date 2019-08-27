package Utilities;
/**
 * This is the move class.
 * Your player will give move objects to the referee.
 * pop = true means you want to pop the bottom piece of the column specified
 * pop = false means you want to place a piece down the column specified
 * column index starts at 0, which corresponds to the leftmost column
 * 
 * @author Ethan Prihar
 *
 */

public class Move
{
	boolean pop;
	int column;

	public Move(boolean p, int c)
	{
		pop = p;
		column = c;
	}
	
	public boolean getPop() {
		return pop;
	}
	
	public int getColumn() {
		return column;
	}
}
