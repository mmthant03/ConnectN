package Utilities;

import java.util.ArrayList;

/**
 * This class is a new extension class of the original StateTree class
 *
 * @author Myo Min Thant, Robert Dutile
 */
public class RMStateTree extends StateTree {

    public RMStateTree(int r, int c, int w, int t, boolean p1, boolean p2, StateTree p) {
        super(r, c, w, t, p1, p2, p);
        this.boardMatrix = new int[rows][columns];
        this.children = new ArrayList<>();
    }

    // Myo Min Thant
    // getter for pop1
    public boolean getPop1() {
        return pop1;
    }

    // Myo Min Thant
    // getter for pop2
    public boolean getPop2() {
        return pop2;
    }

    //Robert Dutile
    //helper function, setter for the board matrix.
    public void setBoardMatrix(int[][] newBoard) {
        for(int i=0; i<this.rows; i++) {
            for (int j=0; j < columns; j++) {
                this.boardMatrix[i][j] = newBoard[i][j];
            }
        }
    }

    //Robert Dutile:
    //Helper function that creates and returns a child board, that is at creation identical to the parent(i.e. this)
    //save for it's parent and children
    public RMStateTree makeChild() {
        RMStateTree newChild = new RMStateTree(rows, columns, winNumber, turn, getPop1(), getPop2(), this);
        newChild.setBoardMatrix(getBoardMatrix());
        this.children.add(newChild);
        return newChild;
    }

    //Myo Min Thant
    //helper function, to return all the legal moves or actions in current State
    public ArrayList<Move> getLegalMoves()
    {
        ArrayList<Move> legalMoves = new ArrayList<Move>();
        for (int j = 0; j < this.columns; j++) {
            // check the top row for any 0
            // if there is that column can be dropped a piece
            if(this.getBoardMatrix()[this.rows-1][j] == 0) {
                Move m = new Move(false, j);
                legalMoves.add(m);
            }
            // check the bottom row for any available pop
            if(this.turn == 1){
                if(!getPop1() && this.getBoardMatrix()[0][j] == 1) {
                    Move m = new Move (true, j);
                    legalMoves.add(m);
                }
            }
            else {
                if(!getPop2() && this.getBoardMatrix()[0][j] == 2) {
                    Move m = new Move (true, j);
                    legalMoves.add(m);
                }
            }
        }
        return legalMoves;
    }
}
