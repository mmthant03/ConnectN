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
    public int optimalDepth = 0;

	public SimplePlayer1(String n, int t, int l)
	{
		super(n, t, l);
	}

	public Move getMove(StateTree state)
	{
	    if(optimalDepth == 0) optimalDepth = state.winNumber;
	    StateTree st = new RefereeBoard(state.rows, state.columns, state.winNumber, state.turn, state.pop1, state.pop2, state.parent);
	    st.setBoardMatrix(state.getBoardMatrix());
	    long startTime = System.currentTimeMillis();
	    System.out.println(this.optimalDepth);
	    OptimalMove m = minimaxAB(st, null, this.optimalDepth, -100000, 100000, true, startTime);
        optimalDepth = optimalDepth+1;
	    return m.move;
	}

	// Myo Min Thant
	// minimax + alpha beta Pruning
	public OptimalMove minimaxAB(StateTree state, Move move, int depth, int alpha, int beta, boolean maxPlayer, long startTime)
    {
        // terminal test
        long stopTime = System.currentTimeMillis();
        long total = stopTime - startTime;
        long newTimeLimit = ((this.timeLimit * 1000) * 5) / 6;
        boolean timeUp = total>=newTimeLimit;
		if (state.getLegalMoves().isEmpty() || depth == 0 || Referee.checkFull(state) || Referee.checkForWinner(state)!=0 || timeUp)
		{
            // return the utility function
            int e = evaluation(state);
            return new OptimalMove(move, e);
		}
        else {
            // if turn == this.turn do the MAX
            if (maxPlayer) {
                int v = -100000; // set it -100000 instead of -infinity
                for (Move m : state.getLegalMoves()) {
                    // make a move from the legal Moves
                    if(m.getPop() && state.getBoardMatrix()[0][m.getColumn()] != this.turn) continue;
                    StateTree child = state.makeChild();
                    child.makeMove(m);
                    // After making move, we will get a new state
                    // and recursively check for minimax

                    OptimalMove optimalMove = minimaxAB(child, m, depth - 1, alpha, beta, false, startTime);
                    //move = (v >= optimalMove.utility) ? move : optimalMove.move;
                    move = (v >= optimalMove.utility) ? move : m;
                    v = Math.max(v, optimalMove.utility);
                    // set the new alpha
                    alpha = Math.max(alpha, v);
                    // prune the moves
                    if (beta <= alpha) {
                        break;
                    }
                }
                return new OptimalMove(move, v);
            } else {
                int v = +100000; // set it 100000 instead of infinity
                for (Move m : state.getLegalMoves()) {
                    // make a move from the legal Moves
                    if(m.getPop() && state.getBoardMatrix()[0][m.getColumn()] == this.turn) continue;
                    StateTree child = state.makeChild();
                    child.makeMove(m);
                    // After making move, we will get a new state
                    // and recursively check for minimax
                    OptimalMove optimalMove = minimaxAB(child, m, depth - 1, alpha, beta, true, startTime);
                    //move = (v <= optimalMove.utility) ? move : optimalMove.move;
                    move = (v <= optimalMove.utility) ? move : m;
                    v = Math.min(v, optimalMove.utility);
                    // set the new beta
                    beta = Math.min(beta, v);
                    // prune the moves
                    if (beta <= alpha) {
                        break;
                    }
                }
                return new OptimalMove(move, v);
            }
        }
	}

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
        // for each row, the more white spaces a player can use, he is more likely to win
        // However, if he is blocked by another player before N, his chance goes back to 0.
        for(int i=0; i<state.rows; i++)
        {
            for(int j=0; j<state.columns; j++)
            {
                if(board[i][j] == turn) {
                    if (j > 0 && j < state.columns - 1 && (board[i][j - 1] == 0 || board[i][j + 1] == 0)) {
                        turnCount[turn] = turnCount[turn] + turnCount[0];
                        turnCount[turn]++;
                        turnCount[0] = 0;
                    }
                    else {
                        turnCount[turn]++;
                    }
                }
                else if(board[i][j] == oppTurn) {
                    if (j > 0 && j < state.columns - 1 && (board[i][j - 1] == 0 || board[i][j + 1] == 0)) {
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

        // check verticals
        // for each column, check who is at the top
        // for each column, if one piece is blocked by another, check how many he has in a row.
        // for each column, if the top one has more white spaces up to N, he is likely to win.
        int verOneCount = 0;
        int verTwoCount = 0;
        for(int j=0;j<state.columns;j++)
        {
            for(int i=0;i<state.rows;i++) {
                if (board[i][j] == turn) {
                    if (i < state.rows - 1 && board[i + 1][j] != oppTurn) {
                        turnCount[turn]++;
                        verOneCount++;
                    } else if (i < state.rows - 1 && board[i + 1][j] == oppTurn) {
                        turnCount[turn] = (state.winNumber > verOneCount) ? turnCount[turn]-- : turnCount[turn];
                        verOneCount = 0;
                    } else {
                        turnCount[turn]++;
                    }
                }
                else if (board[i][j] == oppTurn) {
                    if (i < state.rows - 1 && board[i + 1][j] != turn) {
                        turnCount[oppTurn]++;
                        verTwoCount++;
                    } else if (i < state.rows - 1 && board[i + 1][j] == turn) {
                        turnCount[oppTurn] = (state.winNumber > verTwoCount) ? turnCount[oppTurn]-- : turnCount[oppTurn];
                    } else {
                        turnCount[oppTurn]++;
                    }
                }
                else if (board[i][j] == 0) {
                    if(i > 0 && board[i-1][j] == turn) {
                        turnCount[turn]+= state.rows-i;
                    }
                    else if(i > 0 && board[i-1][j] == oppTurn){
                        turnCount[oppTurn]+= state.rows-i;
                    }
                }
            }
        }
        verticalWin = (turnCount[turn]-turnCount[oppTurn]);
        turnCount[turn] = 0;
        turnCount[oppTurn] = 0;

        // check diagonals in both ways
        // for each diagonal check if white space can give a winning chance to both player
        // bottom-left to top-right (upper half)
        int rowBound = state.rows - state.winNumber;
        int colBound = state.columns - state.winNumber;
        for(int rowStart = 0; rowStart < rowBound; rowStart++){
            int row, col;
            for( row = rowStart, col = 0; row < state.rows && col < state.columns; row++, col++ ){
                if (board[row][col] == turn) {
                    if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] != oppTurn) {
                        turnCount[turn]++;
                    } else if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] == oppTurn) {
                       // turnCount[turn] = (state.winNumber > turnCount[turn]) ? turnCount[turn]-- : turnCount[turn];
                        turnCount[turn]--;
                    } else {
                        turnCount[turn]++;
                    }
                }
                else if (board[row][col] == oppTurn) {
                    if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] != turn) {
                        turnCount[oppTurn]++;
                    } else if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] == turn) {
                        //turnCount[oppTurn] = (state.winNumber > turnCount[oppTurn]) ? turnCount[turn]-- : turnCount[oppTurn];
                        turnCount[oppTurn]--;
                    } else {
                        turnCount[oppTurn]++;
                    }
                }
                else if (board[row][col] == 0) {
                    if(row > 0 && col > 0 && board[row-1][col - 1] == turn) {
                        turnCount[turn]++;
                    }
                    else if(row > 0 && col > 0 && board[row-1][col - 1] == oppTurn){
                        turnCount[oppTurn]++;
                    }
                }
            }
        }

        // bottom-left to top-right (lower half)
        for(int colStart = 1; colStart < colBound; colStart++){
            int row, col;
            for( row = 0, col = colStart; row < state.rows && col < state.columns; row++, col++ ){
                if (board[row][col] == turn) {
                    if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] != oppTurn) {
                        turnCount[turn]++;
                    } else if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] == oppTurn) {
                        //turnCount[turn] = (state.winNumber > turnCount[turn]) ? turnCount[turn]-- : turnCount[turn];
                        turnCount[oppTurn]--;
                    } else {
                        turnCount[turn]++;
                    }
                }
                else if (board[row][col] == oppTurn) {
                    if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] != turn) {
                        turnCount[oppTurn]++;
                    } else if (row < state.rows - 1 && col < state.columns - 1 && board[row + 1][col + 1] == turn) {
                        //turnCount[oppTurn] = (state.winNumber > turnCount[oppTurn]) ? turnCount[oppTurn]-- : turnCount[oppTurn];
                        turnCount[oppTurn]--;
                    } else {
                        turnCount[oppTurn]++;
                    }
                }
                else if (board[row][col] == 0) {
                    if(row > 0 && col > 0 && row < state.rows - 1 && col < state.columns - 1) {
                        if (board[row - 1][col - 1] == turn && board[row + 1][col + 1] != oppTurn) {
                            turnCount[turn]++;
                        } else if (board[row - 1][col - 1] == oppTurn && board[row + 1][col + 1] != turn) {
                            turnCount[oppTurn]++;
                        }
                    }
                }
            }
        }
        diagonalOneWin = turnCount[turn]-turnCount[oppTurn];
        turnCount[turn] = 0;
        turnCount[oppTurn] = 0;

        // top-left to bottom-right (lower half)
        int prevTurn = 0;
        rowBound = 0 + state.winNumber - 1;
        for(int rowStart = state.rows-1; rowStart >= rowBound; rowStart--){
            int row, col;
            for( row = rowStart, col = 0; row >= 0 && col < state.columns; row--, col++ ){
                if(board[row][col] == turn) {
                    if (prevTurn != oppTurn) {
                        //turnCount[turn] = (state.winNumber > turnCount[turn]) ? turnCount[turn]-- : turnCount[turn];
                        turnCount[turn]--;
                    } else {
                        turnCount[turn]++;
                        prevTurn = turn;
                    }
                }
                else if(board[row][col] == oppTurn) {
                    if (prevTurn != turn) {
                        //turnCount[oppTurn] = (state.winNumber > turnCount[oppTurn]) ? turnCount[oppTurn]-- : turnCount[oppTurn];
                        turnCount[oppTurn]--;
                    } else {
                        turnCount[oppTurn]++;
                        prevTurn = oppTurn;
                    }
                }
                else {
                    if(row > 0 && col > 0 && row < state.rows - 1 && col < state.columns - 1) {
                        if (board[row - 1][col - 1] == turn && board[row + 1][col + 1] != oppTurn) {
                            turnCount[turn]++;
                            prevTurn = turn;
                        } else if (board[row - 1][col - 1] == oppTurn && board[row + 1][col + 1] != turn) {
                            turnCount[oppTurn]++;
                            prevTurn = oppTurn;
                        } else if (turnCount[turn] > turnCount[oppTurn]) {
                            turnCount[turn]++;
                            prevTurn = 0;
                        } else if (turnCount[turn] < turnCount[oppTurn]) {
                            turnCount[oppTurn]++;
                            prevTurn = 0;
                        }
                    }
                }
            }
        }

        // bottom-left to top-right (lower half)
        for(int colStart = 1; colStart < colBound; colStart++){
            int row, col;
            for( row = state.rows-1, col = colStart; row > 0 && col < state.columns; row--, col++ ){
                if(board[row][col] == turn) {
                    if (prevTurn != oppTurn) {
                        //turnCount[turn] = (state.winNumber > turnCount[turn]) ? turnCount[turn]-- : turnCount[turn];
                        turnCount[oppTurn]--;
                    } else {
                        turnCount[turn]++;
                        prevTurn = turn;
                    }
                }
                else if(board[row][col] == oppTurn) {
                    if (prevTurn != turn) {
                        //turnCount[oppTurn] = (state.winNumber > turnCount[oppTurn]) ? turnCount[oppTurn]-- : turnCount[oppTurn];
                        turnCount[oppTurn]--;
                    } else {
                        turnCount[oppTurn]++;
                        prevTurn = oppTurn;
                    }
                }
                else {
                    if(row > 0 && col > 0 && row < state.rows - 1 && col < state.columns - 1) {
                        if (board[row - 1][col - 1] == turn && board[row + 1][col + 1] != oppTurn) {
                            turnCount[turn]++;
                            prevTurn = turn;
                        } else if (board[row - 1][col - 1] == oppTurn && board[row + 1][col + 1] != turn) {
                            turnCount[oppTurn]++;
                            prevTurn = oppTurn;
                        } else if (turnCount[turn] > turnCount[oppTurn]) {
                            turnCount[turn]++;
                            prevTurn = 0;
                        } else if (turnCount[turn] < turnCount[oppTurn]) {
                            turnCount[oppTurn]++;
                            prevTurn = 0;
                        }
                    }
                }
            }
        }
        diagonalTwoWin = turnCount[turn]-turnCount[oppTurn];
        turnCount[turn] = 0;
        turnCount[oppTurn] = 0;
        int winChances = 0;
        int winnerExist = (Referee.checkForWinner(state)!=0) ? 0 : Referee.checkForWinner(state);
        if(turn == 1) {
            winChances = 100 * winnerExist;
        } else {
            winChances = -1 * 100 * winnerExist;
        }
        //if(winnerExist!=0) return winnerExist;
        return winnerExist+horizontalWin+verticalWin+diagonalOneWin+diagonalTwoWin;
    }


}