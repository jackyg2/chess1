package Pieces;

import Game.Chessboard;

public class Bishop extends Queen{

    /**
     * Constructor
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     */
    public Bishop(int rank, int file, int player, Chessboard board){
        super("Bishop", rank, file, player, getNewImage(player,3), board);
    }

    /**
     * Used to determine the direction the Bishop is to move
     *
     * @param direction 1 --> downLeft, 2 --> downRight, 3 --> upLeft, 4 --> upRight
     * @return Returns an array of two integers. First value is the rank increment,
     *              second value is the file increment
     */
    @Override
    int[] getIncrements(int direction){
        return new int[]{(direction < 3) ? -1: 1, (direction % 2 == 0) ? 1 : -1};
    }
}
