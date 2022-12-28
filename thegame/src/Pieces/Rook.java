package Pieces;

import Game.Chessboard;

public class Rook extends Queen{

    /**
     * Constructor
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     */
    public Rook(int rank, int file, int player, Chessboard board){
        super(" Rook ", rank, file, player, getNewImage(player,1), board);
    }

    /**
     * Used to determine the direction the Rook is to move
     *
     * @param direction 1 --> up, 2 --> right, 3 --> down, 4 --> left
     * @return Returns an array of two integers. First value is the rank increment,
     *              second value is the file increment
     */
    @Override
    int[] getIncrements(int direction){
        return new int[]{(direction == 1) ? -1 : (direction == 3) ? 1 : 0, (direction == 4) ? -1 : (direction == 2) ? 1 : 0};
    }
}