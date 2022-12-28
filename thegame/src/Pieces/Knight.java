package Pieces;

import Game.*;
import java.util.ArrayList;

public class Knight extends Piece{

    /**
     * Constructor
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     */
    public Knight(int rank, int file, int player, Chessboard board){
        super("Knight", rank, file, player, getNewImage(player,2), board);
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

        // 1 --> upLeft, 2 --> upRight, 3 --> rightUp, 4 --> rightDown,
        // 5 --> downRight, 6 --> downLeft, 7 --> leftDown, 8 --> leftUp
        for (int move = 1; move < 9; move++){
            int rIncrement = (move == 2 || move == 1) ? -2 : (move == 3 || move == 8) ? -1 :
                    (move == 4 || move == 7) ? 1 : 2;
            int fIncrement = (move == 6 || move == 1) ? -1 : (move == 2 || move == 5) ? 1 :
                    (move == 3 || move == 4) ? 2 : -2;

            if (!((theGame.doesThisPieceBelongToMe(currentRank+rIncrement, currentFile+fIncrement) &&
                    theGame.getTurn() == getPlayer()) ||
                    theGame.isOutsideBoundaries(currentRank+rIncrement,currentFile+fIncrement))){
                list.add(getChessboard().getBoard()[currentRank + rIncrement][currentFile + fIncrement]);
            }
        }

        // Restricts moves if the current king is in check or piece is pinned
        if ((getChessboard().getCurrentKing().getPlayer() == getPlayer()) &&
                (isPinned() || getChessboard().getCurrentKing().underAttack())){
            return movesToPreventCheck(list);
        }
        return list;
    }
}