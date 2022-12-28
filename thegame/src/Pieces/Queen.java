package Pieces;

import Game.*;
import javax.swing.JLabel;
import java.util.ArrayList;

public class Queen extends Piece{

    private final int totalMoveDirections;

    /**
     * Constructor for Queen
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     */
    public Queen(int rank, int file, int player, Chessboard board){
        super(" Queen", rank, file, player, getNewImage(player, 4), board);
        totalMoveDirections = 8;
    }

    /**
     * Constructor for Rook and Bishop
     * @param name Name of the piece
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     * @param image Image of the piece
     */
    public Queen(String name, int rank, int file, int player, JLabel image, Chessboard board){
        super(name, rank, file, player, image, board);
        totalMoveDirections = 4;
    }

    /**
     * Used to determine the direction the Queen is to move
     *
     * @param direction - 1 --> upLeft, 2 --> up, 3 --> upRight, 4 --> right,
     *                    5 --> downRight, 6 --> down, 7 --> downLeft, 8 --> left
     * @return - Returns an array of two integers. First value is the rank increment,
     *              second value is the file increment
     */
    int[] getIncrements(int direction){
        return new int[]{(direction < 4) ? -1 : (direction > 4 && direction < 8) ? 1 : 0,
                (direction > 6 || direction < 2) ? -1 : (direction > 2 && direction < 6) ? 1 : 0};
    }

    /**
     * This function gets a list of legal moves for the current piece
     *
     * @return - Returns an ArrayList of Pieces representing the legal moves of "this" piece
     */
    @Override
    public ArrayList<Piece> getLegalMoves(){
        ArrayList<Piece> list = new ArrayList<>();
        Game theGame = getChessboard().getTheGame();

        int currentRank = getRank();
        int currentFile = getFile();

        for (int move = 1; move <= totalMoveDirections; move++){
            // increment[0] = rIncrement, increment[1] = fIncrement
            int[] increment = getIncrements(move);
            // Checks if square is empty until it reaches a piece or out of bounds
            while (!getChessboard().isThereAPieceHere(currentRank+increment[0],currentFile+increment[1])) {
                list.add(getChessboard().getBoard()[currentRank+increment[0]][currentFile+increment[1]]);
                increment[0] += (increment[0] == 0) ? 0 : increment[0] / Math.abs(increment[0]);
                increment[1] += (increment[1] == 0) ? 0 : increment[1] / Math.abs(increment[1]);
            }
            // Checks end piece if it belongs to current player
            if(!((theGame.doesThisPieceBelongToMe(currentRank+increment[0],currentFile+increment[1]) &&
                    theGame.getTurn() == getPlayer()) ||
                    theGame.isOutsideBoundaries(currentRank+increment[0],currentFile+increment[1]))){
                list.add(getChessboard().getBoard()[currentRank+increment[0]][currentFile+increment[1]]);
            }
        }

        // Restricts moves if the current king is in check or piece is pinned
        if ((theGame.getTurn() == getPlayer()) && (isPinned() || (getChessboard().getCurrentKing().underAttack()))){
            return movesToPreventCheck(list);
        }
        return list;
    }
}