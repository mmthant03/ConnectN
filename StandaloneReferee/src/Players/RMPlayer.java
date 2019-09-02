package Players;

import Utilities.Move;
import Utilities.RMStateTree;
import Utilities.StateTree;
import Referee.Referee;


/**
 * This is an example of how to make a player.
 * This player is extremely simple and does no tree building
 * but its good to test against at first.
 * 
 * @author Ethan Prihar
 *
 */
public class RMPlayer extends Player
{
    public int optimalDepth = 0;

	public RMPlayer(String n, int t, int l)
	{
		super(n, t, l);
	}

	public Move getMove(StateTree state)
	{
	    if(optimalDepth == 0) optimalDepth = state.winNumber;
	    RMStateTree st = new RMStateTree(state.rows, state.columns, state.winNumber, state.turn, state.pop1, state.pop2, state.parent);
	    st.setBoardMatrix(state.getBoardMatrix());
	    long startTime = System.currentTimeMillis();
	    System.out.println(this.optimalDepth);
	    RMOptimalMove m = minimaxAB(st, null, this.optimalDepth, -100000, 100000, true, startTime);
        optimalDepth = optimalDepth+1;
	    return m.move;
	}

    /**
     * This function is an implementation of minimax + alpha-beta pruning
     *
     * @author Myo Min Thant, Robert Dutile
     *
     * @param state the current state it is going to evaluate
     * @param move the current action the player is going to do
     * @param depth the depth limit of the state tree
     * @param alpha the best value for max player
     * @param beta the best value for min player
     * @param maxPlayer to determine whose turn it is in the state tree
     * @param startTime to calculate and limit the function at certain time
     * @return RMOptimalMove, returns the best move for the current state and its utility value
     */
	public RMOptimalMove minimaxAB(RMStateTree state, Move move, int depth, int alpha, int beta, boolean maxPlayer, long startTime)
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
            return new RMOptimalMove(move, e);
		}
        else {
            // if turn == this.turn do the MAX
            if (maxPlayer) {
                int v = -100000; // set it -100000 instead of -infinity
                for (Move m : state.getLegalMoves()) {
                    // check whether the player is not popping up other piece. If so, skip this iteration
                    if(m.getPop() && state.getBoardMatrix()[0][m.getColumn()] != this.turn) continue;
                    // create a child and make a move from the legal Moves
                    RMStateTree child = state.makeChild();
                    child.makeMove(m);
                    // After making move, recursively check for minimax and alpha-beta pruning
                    RMOptimalMove optimalMove = minimaxAB(child, m, depth - 1, alpha, beta, false, startTime);
                    // compare the better move
                    move = (v >= optimalMove.utility) ? move : m;
                    v = Math.max(v, optimalMove.utility);
                    // set the new alpha
                    alpha = Math.max(alpha, v);
                    // prune all the other moves that are left in the arraylist
                    if (beta <= alpha) {
                        break;
                    }
                }
                return new RMOptimalMove(move, v);
            } else {
                int v = +100000; // set it 100000 instead of infinity
                for (Move m : state.getLegalMoves()) {
                    // check whether the player is not popping up other piece. If so, skip this iteration
                    if(m.getPop() && state.getBoardMatrix()[0][m.getColumn()] == this.turn) continue;
                    // create a child and make a move from the legal Moves
                    RMStateTree child = state.makeChild();
                    child.makeMove(m);
                    // After making move, recursively check for minimax and alpha-beta pruning
                    RMOptimalMove optimalMove = minimaxAB(child, m, depth - 1, alpha, beta, true, startTime);
                    // Compare the better move
                    move = (v <= optimalMove.utility) ? move : m;
                    v = Math.min(v, optimalMove.utility);
                    // set the new beta
                    beta = Math.min(beta, v);
                    // prune the moves
                    if (beta <= alpha) {
                        break;
                    }
                }
                return new RMOptimalMove(move, v);
            }
        }
	}

    /**
     *  This function is a heuristic evaluation function to determine the value of each player
     *  It checks the winning conditions, and winning chances of each player in horizontal, vertical,
     *  and two diagonal ways.
     *  Winning conditions are weighted more because if another player is winning in next turn, it should
     *  block it instead of building a winning path for itself.
     *  The winning chances are calculated by determining white spaces that each player could build up to
     *  its winning conditions.
     * @author Myo Min Thant, Robert Dutile
     * @param state StateTree of the given state
     * @return int, positive for self, negative for opponent and 0 for tie
     */
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
        // However, if he is blocked by another player before N, his chance got reduced.
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
        // if blocked, reduce the chance of winning by that player
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
        // for each diagonal check if white space can give a winning chance to either of the player
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
        return winChances+horizontalWin+verticalWin+diagonalOneWin+diagonalTwoWin;
    }


}