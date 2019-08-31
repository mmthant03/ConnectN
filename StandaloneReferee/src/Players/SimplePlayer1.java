package Players;

import Utilities.Move;
import Utilities.StateTree;
import Referee.Referee;
import Referee.RefereeBoard;

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
	    StateTree st = new RefereeBoard(state.rows, state.columns, state.winNumber, state.turn, state.pop1, state.pop2, state.parent);
		return minimaxAB(st, null, -100000, 100000, true).move;
	}

	// Myo Min Thant
	// minimax + alpha beta Pruning
	public OptimalMove minimaxAB(StateTree state, Move move, int alpha, int beta, boolean maxPlayer)
    {

        // terminal test
		if (state.getLegalMoves().isEmpty() || Referee.checkFull(state))
		{
			// return the utility function
            return new OptimalMove(move, Referee.checkConnect(state));
		}

		// if turn == 1 do the MAX
		if (maxPlayer)
		{
			int v = -100000; // set it -100000 instead of -infinity
			for (Move m : state.getLegalMoves())
			{
				// make a move from the legal Moves
				state.makeMove(m);
				// After making move, we will get a new state
				// and recursively check for minimax
                OptimalMove optimalMove = minimaxAB(state, m, alpha, beta, false);
                move = optimalMove.move;

				v = Math.max(v, optimalMove.utility);
                // set the new alpha
                alpha = Math.max(alpha, v);
				// prune the moves
				if (beta <= alpha)
				{
					return new OptimalMove(m, v);
				}
				// I think we do need to set a score for utility function somehow
				// TODO set the score
			}
			return new OptimalMove(move, v);
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
                OptimalMove optimalMove = minimaxAB(state, m, alpha, beta, true);
                move = optimalMove.move;

                v = Math.min(v, optimalMove.utility);
                // set the new beta
                beta = Math.min(beta, v);
                // prune the moves
                if (beta <= alpha)
                {
                    return new OptimalMove(m, v);
                }
                // TODO set the score
            }
            return new OptimalMove(move, v);
        }
	}

    //we might not need this one at all
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

        return 0;
    }


}