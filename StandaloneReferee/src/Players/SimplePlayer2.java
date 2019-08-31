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
public class SimplePlayer2 extends Player
{
	public SimplePlayer2(String n, int t, int l)
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
public int minimaxAB(StateTree state, int alpha, int beta, int turn)
{
	// TO-FIX minimaxAB needs to be consistent for both turns.
    // Currently it is only one way.
	if (state.getLegalMoves().isEmpty())
	{
		// return the utility function
        return utility(state, turn, false);
	}

	// if turn == 1 do the MAX
	if (turn == 1)
	{
		int v = -100000; // set it -100000 instead of -infinity
		for (Move m : state.getLegalMoves())
		{
			// make a move from the legal Moves
			state.makeMove(m);
			// After making move, we will get a new state
			// and recursively check for minimax
			v = Math.max(v, minimaxAB(state, alpha, beta, 2));
			// prune the moves
			if (beta <= v)
			{
				return v;
			}
			// set the new alpha
			alpha = Math.max(alpha, v);
			// I think we do need to set a score for utility function somehow
			// TODO set the score
		}
		return v;
	}
    else
    {
        int v = +100000; // set it 100000 instead of infinity
        for (Move m: state.getLegalMoves())
        {
            // make a move from the legal Moves
            state.makeMove(m);
            // After making move, we will get a new state
            // and recursively check for minimax
            v = Math.min(v, minimaxAB(state, alpha, beta, 1));
            // prune the moves
            if (v <= alpha)
            {
                return v;
            }
            // set the new beta
            beta = Math.min(beta, v);
            // TODO set the score
        }
        return v;
    }
}

public int utility(StateTree state, int turn, boolean isOpponent)
{
    int[][] board = state.getBoardMatrix();

    int win = 0;
    int utilityVal = (!isOpponent) ? 1 : -1; // will return negative value for opponent moves
    // check horizontals
    for(int i=0; i<state.rows; i++)
    {
        for(int j=0; j<state.columns; j++)
        {
            if(board[i][j] == turn && win<state.winNumber)
            {
                win++;
            }
            else if(win == state.winNumber)
            {
                return utilityVal*100;
            }
            else {
                win = 0;
            }
        }
    }

    //check verticals
    for(int j=0;j<state.columns;j++)
    {
        for(int i=0;i<state.rows;i++)
        {
            if(board[i][j] == turn && win<state.winNumber)
            {
                win++;
            }
            else if(win == state.winNumber)
            {
                return utilityVal*100;
            }
            else {
                win = 0;
            }
        }
    }

    //check diagonals in both ways
    int count;
    // top-left to bottom-right (lower half)
    for(int rowStart = 0; rowStart < state.rows - state.winNumber; rowStart++){
        count = 0;
        int row, col;
        for( row = rowStart, col = 0; row < state.rows && col < state.columns; row++, col++ ){
            if(board[row][col] == turn){
                count++;
                if(count >= state.winNumber) return utilityVal*100;
            }
            else {
                count = 0;
            }
        }
    }

    // top-left to bottom-right (upper half)
    for(int colStart = 1; colStart < state.columns - state.winNumber; colStart++){
        count = 0;
        int row, col;
        for( row = 0, col = colStart; row < state.rows && col < state.columns; row++, col++ ){
            if(board[row][col] == turn){
                count++;
                if(count >= state.winNumber) return utilityVal*100;
            }
            else {
                count = 0;
            }
        }
    }

    // bottom-left to top-right (upper half)
    for(int rowStart = state.rows-1; rowStart >= 0 + state.winNumber-1; rowStart--){
        count = 0;
        int row, col;
        for( row = rowStart, col = 0; row >= 0 && col < state.columns; row--, col++ ){
            if(board[row][col] == turn){
                count++;
                if(count >= state.winNumber) return utilityVal*100;
            }
            else {
                count = 0;
            }
        }
    }

    // bottom-left to top-right (lower half)
    for(int colStart = 1; colStart < state.columns - state.winNumber; colStart++){
        count = 0;
        int row, col;
        for( row = state.rows-1, col = colStart; row > 0 && col < state.columns; row--, col++ ){
            if(board[row][col] == turn){
                count++;
                if(count >= state.winNumber) return utilityVal*100;
            }
            else {
                count = 0;
            }
        }
    }


//    for( int k = 0 ; k <= state.columns + state.rows - 2; k++ ) {
//        for( int j = 0 ; j <= k ; j++ ) {
//            int i = k - j;
//            if( i < state.rows && j < state.columns ) {
//                if(board[i][j] == turn && win<state.winNumber)
//                {
//                    win++;
//                }
//                else if(win == state.winNumber)
//                {
//                    return 100;
//                }
//                else {
//                    win = 0;
//                }
//            }
//        }
//    }
    return 0;
}
}

