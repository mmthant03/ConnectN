package Players;

import Utilities.Move;
import Utilities.StateTree;

/**
 * This class is what you will extend when you make your player class.
 * Each player needs to have a getMove function which the referee calls.
 * The getMove function is probably where you want to do your min-maxing
 * and alpha-beta pruning, with helper functions and such.
 * 
 * @author Ethan Prihar
 *
 */

public abstract class Player
{
	String name; // the name of your player
	int turn; // the number corresponding to your turn (1 or 2)
	int timeLimit; // the amount of time (in seconds) you have to make a move
	int N; // the amount of pieces to connect in order to win
	
	public Player(String n, int t, int l)
	{
		name = n;
		turn = t;
		timeLimit = l;
	}
	
	public void setN(int n) {
		N = n;
	}
	
	public String getName() {
		return name;
	}
	
	
	// This is the method the referee will call when it wants a move from your player
	public abstract Move getMove(StateTree state);
}
