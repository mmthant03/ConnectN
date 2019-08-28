package Players;

import Utilities.Move;
import Utilities.StateTree;

import java.util.ArrayList;


/**
 * This is an example of how to make a player.
 * This player is extremely simple and does no tree building
 * but its good to test against at first.
 * 
 * @author Ethan Prihar
 *
 */
public class SimplePlayer1 extends Player
{
	public SimplePlayer1(String n, int t, int l)
	{
		super(n, t, l);
	}

	public Move getMove(StateTree state)
	{
		for(int j=0; j<state.columns; j++)
		{
			for(int i=0; i<state.rows; i++)
			{
				
				if(state.getBoardMatrix()[i][j] == 0)
				{
					return new Move(false, j);
				}
				
//				try{Thread.sleep(15000);}
//				catch(InterruptedException ex){Thread.currentThread().interrupt();}
				
//				if(this.turn == 1)
//					return new Move(false, 0);
//				if(this.turn == 2)
//					return new Move(false, 1);	
			}
			
//			if((this.turn == 1 && !state.pop1) || (this.turn == 2 && !state.pop2))
//			{
//				return new Move(true, 0);	
//			}
			
		}
		return new Move(false, 100);
	}

	// Myo Min Thant
	// minimax + alpha beta Pruning
	public int minimaxAB(StateTree state, int alpha, int beta, int turn) {
		// TODO terminal test and return utility
		if (state.getLegalMoves().isEmpty()) {
			// return the utility function
		}

		// if turn == 1 do the MAX
		if (turn == 1) {
			int v = -1000; // set it -1000 instead of -infinity
			for (Move m : state.getLegalMoves()) {
				// make a move from the legal Moves
				state.makeMove(m);
				// After making move, we will get a new child state
				// and recursively check for minimax
				v = Math.max(v, minimaxAB(state, alpha, beta, 2));
				// prune the moves
				if (beta <= v) {
					return v;
				}
				// set the new alpha
				alpha = Math.max(alpha, v);
				// I think we do need to set a score for utility function somehow
				// TODO set the score
			}
			return v;
		}
		else {
			int v = +1000; // set it 1000 instead of infinity
			for (Move m: state.getLegalMoves()) {
				// make a move from the legal Moves
				state.makeMove(m);
				// After making move, we will get a new child state
				// and recursively check for minimax
				v = Math.min(v, minimaxAB(state, alpha, beta, 1));
				// prune the moves
				if (v <= alpha) {
					return v;
				}
				// set the new beta
				beta = Math.min(beta, v);
				// TODO set the score
			}
			return v;
		}
	}


}