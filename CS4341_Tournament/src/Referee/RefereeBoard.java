package Referee;

import Utilities.StateTree;

/**
 * This is a simple state that just keeps track of the board and whose
 * turn it is. The referee uses this board to keep track of things
 * and check if anybody has won.
 * 
 * @author Ethan Prihar
 *
 */

public class RefereeBoard extends StateTree
{
	public RefereeBoard(int r, int c, int w, int t, boolean p1, boolean p2, StateTree p)
	{
		super(r, c, w, t, p1, p2, p);
	}
}
