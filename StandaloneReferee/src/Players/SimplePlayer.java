package Players;

import Utilities.Move;
import Utilities.StateTree;


/**
 * This is an example of how to make a player.
 * This player is extremely simple and does no tree building
 * but its good to test against at first.
 * 
 * @author Ethan Prihar
 *
 */
public class SimplePlayer extends Player
{
	public SimplePlayer(String n, int t, int l)
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
}