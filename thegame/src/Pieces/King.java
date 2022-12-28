package Pieces;

import Game.*;
import java.util.ArrayList;

public class King extends Piece{

    /**
     * Constructor
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     */
    public King(int rank, int file, int player, Chessboard board){
        super(" King ", rank, file, player, getNewImage(player,5), board);
    }

    /**
     * This function checks if the current piece is able to move to "thePiece" by checking if
     * "thePiece" is a piece part of its list of legal moves. Overridden to facilitate castling
     *
     * @param thePiece The Piece which is being moved towards
     * @return Returns true if "thePiece" is one of the legal moves of the pawn; otherwise false.
     */
    @Override
    public boolean canMove(Piece thePiece){
        ArrayList<Piece> legalMoves = getLegalMoves();
        Piece[][] theBoard = getChessboard().getBoard();
        for (Piece legalMove : legalMoves) {
            if (legalMove.getRank() == thePiece.getRank() && legalMove.getFile() == thePiece.getFile()) {
                // If the King is castling, then move the Rook to its corresponding position.
                if (Math.abs(thePiece.getFile()-getFile())==2 && getChessboard().getTheGame().getTurn() == getPlayer()){
                    int rookDistance = (thePiece.getFile() > getFile()) ? 3 : -4;
                    int increment = (thePiece.getFile() > getFile()) ? 1 : -1;
                    getChessboard().move(theBoard[getRank()][getFile()+rookDistance],
                            theBoard[getRank()][getFile()+increment], true);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Overridden check for where the king can attack (1 in any direction)
     * @param thePiece The piece being attacked.
     * @return True if "thePiece" is 1 square away from this king
     */
    @Override
    public boolean canAttack(Piece thePiece) {
        return Math.abs(thePiece.getRank()-getRank()) <= 1 && Math.abs(thePiece.getFile()-getFile()) <=1;
    }

    /**
     * This function gets a list of legal moves for the current piece
     *
     * @return Returns an ArrayList of Pieces representing the legal moves of "this" piece
     */
    @Override
    public ArrayList<Piece> getLegalMoves(){
        ArrayList<Piece> list = new ArrayList<>();
        Piece[][] theBoard = getChessboard().getBoard();
        Game theGame = getChessboard().getTheGame();

        int currentRank = getRank();
        int currentFile = getFile();

        // Checks if castling is legal
        if (!getHasMoved() && !underAttack()){
            for (int i = 0; i < 2; i++){
                int rookDistance = (i==0) ? 3 : -4; // Castling Right : Castling Left
                if ((theBoard[currentRank][currentFile+rookDistance] instanceof Rook) &&
                        !(theBoard[currentRank][currentFile+rookDistance].getHasMoved()) &&
                        !theBoard[currentRank][currentFile+rookDistance].underAttack() &&
                        emptyCastlingSpaces(currentFile+rookDistance)){
                    list.add(theBoard[currentRank][currentFile+rookDistance+((rookDistance == 3)?-1:2)]);
                }
            }
        }

        // 1-upLeft, 2-up, 3-upRight, 4-right, 5-downRight, 6-down, 7-downLeft, 8-left
        for (int move = 1; move<9; move++){
            int distRank = (move < 4) ? -1 : (move > 4 && move < 8) ? 1 : 0;
            int distFile = (move > 6 || move < 2) ? -1 : (move > 2 && move < 6) ? 1 : 0;
            if ((!theGame.doesThisPieceBelongToMe((currentRank+distRank),(currentFile+distFile)) ||
                    !(theGame.getTurn() == getPlayer())) &&
                    !theGame.isOutsideBoundaries(currentRank+distRank,currentFile+distFile) &&
                            !(theBoard[currentRank+distRank][currentFile+distFile]).underAttack()){
                list.add(theBoard[currentRank+distRank][currentFile+distFile]);
            }
        }

        // Restricts moves if the current king is in check or piece is pinned
        if ((getChessboard().getCurrentKing().getPlayer() == getPlayer())
                && (isPinned() || getChessboard().getCurrentKing().underAttack())){
            return movesToPreventCheck(list);
        }
        return list;
    }

    /**
     * This method checks whether the spaces between the King square
     * and Rook square are empty to facilitate castling.
     *
     * Precondition: endingFile must not equal the file of the King
     * Post-condition: Returns true if empty; false if there is a piece
     *
     * @param endingFile - Ending file position of the King
     * @return - False if there is a piece between the Rook and King; otherwise true.
     */
    private boolean emptyCastlingSpaces(int endingFile){
        int increment = (endingFile > getFile()) ? 1 : -1;
        for (int i = getFile()+increment; i != endingFile; i+=increment){
            if (getChessboard().isThereAPieceHere(getRank(), i) ||
                    getChessboard().getBoard()[getRank()][i].underAttack()){
                return false;
            }
        }
        return true;
    }
}