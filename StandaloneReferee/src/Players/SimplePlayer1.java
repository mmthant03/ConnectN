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
	    st.setBoardMatrix(state.getBoardMatrix());
	    OptimalMove m = minimaxAB(st, null, 14, -100000, 100000, true);
	    return m.move;
	}

	// Myo Min Thant
	// minimax + alpha beta Pruning
	public OptimalMove minimaxAB(StateTree state, Move move, int depth, int alpha, int beta, boolean maxPlayer)
    {


        // terminal test
		if (state.getLegalMoves().isEmpty() || depth == 0 || Referee.checkFull(state) || Referee.checkForWinner(state)!=0)
		{
            // return the utility function
            int e = evaluation(state);
            return new OptimalMove(move, e);
		}

		// if turn == 1 do the MAX
		if (maxPlayer)
		{
			int v = -100000; // set it -100000 instead of -infinity
			for (Move m : state.getLegalMoves())
			{
				// make a move from the legal Moves
				StateTree child = state.makeChild();
                child.makeMove(m);
				// After making move, we will get a new state
				// and recursively check for minimax

                OptimalMove optimalMove = minimaxAB(child, m, depth-1, alpha, beta, false);
                move = (v >= optimalMove.utility) ? move : optimalMove.move;

				v = Math.max(v, optimalMove.utility);
                // set the new alpha
                alpha = Math.max(alpha, v);
				// prune the moves
				if (beta <= alpha)
				{
				    break;
					//return new OptimalMove(move, v);
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
                StateTree child = state.makeChild();
                child.makeMove(m);
                // After making move, we will get a new state
                // and recursively check for minimax
                OptimalMove optimalMove = minimaxAB(child, m, depth-1, alpha, beta, true);
                move = (v <= optimalMove.utility) ? move : optimalMove.move;

                v = Math.min(v, optimalMove.utility);
                // set the new beta
                beta = Math.min(beta, v);
                // prune the moves
                if (beta <= alpha)
                {
                    break;
                    //return new OptimalMove(move, v);
                }
                // TODO set the score
            }
            return new OptimalMove(move, v);
        }
	}

    //we might not need this one at all
	public int evaluation(StateTree state)
    {
        int turn = this.turn;
        int[][] board = state.getBoardMatrix();
        int horizontalWin = 0;
        int verticalWin = 0;
        int diagonalOneWin = 0;
        int diagonalTwoWin = 0;
        int[] turnCount = new int[3];
        int oppTurn = 3-turn;
        // check horizontals
        // this checks for how many 1s and 2s are in each rows
        // for each row, the more pieces of a player there is, he is more likely to win
        // but if he
        for(int i=0; i<state.rows; i++)
        {
            for(int j=0; j<state.columns; j++)
            {
                if(board[i][j] == turn) {
                    if (j > 0 && board[i][j - 1] == 0 && turnCount[0] > 0) {
                        turnCount[turn] = turnCount[turn] + turnCount[0];
                        turnCount[turn]++;
                        turnCount[0] = 0;

                    }
                    else {
                        turnCount[turn]++;
                    }
                }
                else if(board[i][j] == oppTurn) {
                    if (j > 0 && board[i][j - 1] == 0 && turnCount[0] > 0) {
                        turnCount[turn] = turnCount[turn] + turnCount[0];
                        turnCount[oppTurn]++;
                        turnCount[0] = 0;
                    }
                    else {
                        turnCount[oppTurn]++;
                    }
                }
                else if(board[i][j] == 0){
                    if(j > 0 && board[i][j-1] == turn) {
                        turnCount[turn]++;
                    }
                    else if(j > 0 && board[i][j-1] == oppTurn) {
                        turnCount[oppTurn]++;
                    }
                    else{
                        turnCount[0]++;
                    }
                }
            }
        }
        turnCount[turn] = (state.winNumber>turnCount[turn]) ? 0 : turnCount[turn];
        turnCount[oppTurn] = (state.winNumber>turnCount[oppTurn]) ? 0 : turnCount[oppTurn];
        horizontalWin = turnCount[turn]-turnCount[oppTurn];
        turnCount[turn] = 0;
        turnCount[oppTurn] = 0;

        //check verticals
        for(int j=0;j<state.columns;j++)
        {
            for(int i=0;i<state.rows;i++) {
                if (board[i][j] == turn) {
                    if (i < state.rows - 1 && board[i + 1][j] != oppTurn) {
                        turnCount[turn]++;
                    } else if (i < state.rows - 1 && board[i + 1][j] == oppTurn) {
                        turnCount[turn] = (state.winNumber > turnCount[turn]) ? 0 : turnCount[turn];
                    } else {
                        turnCount[turn]++;
                    }
                }
                else if (board[i][j] == oppTurn) {
                    if (i < state.rows - 1 && board[i + 1][j] != turn) {
                        turnCount[oppTurn]++;
                    } else if (i < state.rows - 1 && board[i + 1][j] == turn) {
                        turnCount[oppTurn] = (state.winNumber > turnCount[oppTurn]) ? 0 : turnCount[oppTurn];
                    } else {
                        turnCount[oppTurn]++;
                    }
                }
                else if (board[i][j] == 0) {
                    if(i > 0 && board[i-1][j] == turn) {
                        turnCount[turn]++;
                    }
                    else if(i > 0 && board[i-1][j] == oppTurn){
                        turnCount[oppTurn]++;
                    }
                }
            }
        }
        verticalWin = turnCount[turn]-turnCount[oppTurn];
        turnCount[turn] = 0;
        turnCount[oppTurn] = 0;

        //check diagonals in both ways
        // top-left to bottom-right (lower half)
        for(int rowStart = 0; rowStart < state.rows - state.winNumber; rowStart++){
            int row, col;
            for( row = rowStart, col = 0; row < state.rows && col < state.columns; row++, col++ ){
                if(board[row][col] == turn){
                    turnCount[turn]++;
                }
                else if(board[row][col] == oppTurn){
                    turnCount[oppTurn]++;
                }
            }
        }

        // top-left to bottom-right (upper half)
        for(int colStart = 1; colStart < state.columns - state.winNumber; colStart++){
            int row, col;
            for( row = 0, col = colStart; row < state.rows && col < state.columns; row++, col++ ){
                if(board[row][col] == turn) {
                    turnCount[turn]++;
                }
                else if(board[row][col] == oppTurn){
                        turnCount[oppTurn]++;
                }
            }
        }
        diagonalOneWin = turnCount[turn]-turnCount[oppTurn];
        turnCount[turn] = 0;
        turnCount[oppTurn] = 0;

        // bottom-left to top-right (upper half)
        for(int rowStart = state.rows-1; rowStart >= 0 + state.winNumber-1; rowStart--){
            int row, col;
            for( row = rowStart, col = 0; row >= 0 && col < state.columns; row--, col++ ){
                if(board[row][col] == turn) {
                    turnCount[turn]++;
                }
                else if(board[row][col] == oppTurn){
                    turnCount[oppTurn]++;
                }
            }
        }

        // bottom-left to top-right (lower half)
        for(int colStart = 1; colStart < state.columns - state.winNumber; colStart++){
            int row, col;
            for( row = state.rows-1, col = colStart; row > 0 && col < state.columns; row--, col++ ){
                if(board[row][col] == turn) {
                    turnCount[turn]++;
                }
                else if(board[row][col] == oppTurn){
                    turnCount[oppTurn]++;
                }
            }
        }
        diagonalTwoWin = turnCount[turn]-turnCount[oppTurn];
        turnCount[turn] = 0;
        turnCount[oppTurn] = 0;

        return (horizontalWin+verticalWin+diagonalOneWin+diagonalTwoWin);
    }


}