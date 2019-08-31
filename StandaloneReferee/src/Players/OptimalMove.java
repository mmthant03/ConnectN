package Players;

import Utilities.Move;

public class OptimalMove {
    public Move move;
    public int utility;

    public OptimalMove(Move move, int utility) {
        this.move = move;
        this.utility = utility;
    }

}
