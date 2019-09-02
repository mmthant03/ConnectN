package Players;

import Utilities.Move;

/**
 * This class is a helper class for minimax+alpha-beta pruning
 * to keep track of the utility value of each move.
 *
 * @author Myo Min Thant, Robert Dutile
 */
public class RMOptimalMove {
    public Move move;
    public int utility;

    public RMOptimalMove(Move move, int utility) {
        this.move = move;
        this.utility = utility;
    }

}
