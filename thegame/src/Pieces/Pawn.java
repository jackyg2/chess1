package Pieces;

import Game.*;
import java.util.ArrayList;

public class Pawn extends Piece {

    /**
     * The direction the pawn is moving.
     *          Player 1 is "-1" --> Up ||
     *          Player 2 is "1" --> Down
     */
    private final int pawnDirection;

    /**
     * Constructor
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     */
    public Pawn(int rank, int file, int player, Chessboard board){
        super(" Pawn ", rank, file, player, getNewImage(player,6), board);
        pawnDirection = player == 1 ? -1 : 1;
    }

    /**
     * This function checks if the current piece is able to move to "thePiece" by checking if
     * "thePiece" is a piece part of its list of legal moves. Overridden to facilitate processes for En Passant
     * and promotions.
     *
     * @param thePiece - The Piece which is being moved towards
     * @return - Returns true if "thePiece" is one of the legal moves of the pawn; otherwise false.
     */
    @Override
    public boolean canMove(Piece thePiece){
        ArrayList<Piece> legalMoves = getLegalMoves();
        Piece[][] theBoard = getChessboard().getBoard();
        Game theGame = getChessboard().getTheGame();

        for (Piece legalMove : legalMoves) {
            if (legalMove.getRank() == thePiece.getRank() && legalMove.getFile() == thePiece.getFile()) {

                //If there is a pawn moving two forward and there is a pawn besides the end position,
                //allow it to en passant the current pawn.
                if (Math.abs(thePiece.getRank()-getRank()) == 2 && theGame.getTurn() == getPlayer()) {
                    for (int n = ((thePiece.getFile() == 0) ? 1 : -1); n < ((thePiece.getFile() == 7) ? 1 : 2); n += 2) {
                        if (theBoard[thePiece.getRank()][thePiece.getFile() + n] instanceof Pawn) {
                            getChessboard().setEnPassant(
                                    theBoard[thePiece.getRank()-pawnDirection][thePiece.getFile()]);
                            break;
                        }
                    }
                }

                // Checks if the pawn is doing En-Passant and will remove the taken piece if true
                if (theBoard[thePiece.getRank()][thePiece.getFile()].equals(getChessboard().getEnPassantTarget())
                        && theGame.getTurn() == getPlayer()){
                    getChessboard().removePiece(getChessboard().getBoard()[thePiece.getRank()-pawnDirection][thePiece.getFile()]);
                    theBoard[thePiece.getRank()-pawnDirection][thePiece.getFile()] = new Piece(
                            thePiece.getRank()-pawnDirection, thePiece.getFile(),getChessboard());
                }

                // Starts promotion process if the pawn is moving to end rank
                if (thePiece.getRank() == 0 || thePiece.getRank() == 7){
                    theGame.startPromotion(this);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Overridden check for where pawns can attack (only 1 diagonally in front)
     * @param thePiece The piece being attacked.
     * @return True if "thePiece" is 1 rank forward and 1 file to any side of this pawn's location
     */
    @Override
    public boolean canAttack(Piece thePiece) {
        return getRank()+pawnDirection == thePiece.getRank() && Math.abs(getFile()-thePiece.getFile())==1;
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

        int i = pawnDirection;
        int currentRank = getRank();
        int currentFile = getFile();

        // One space ahead or two
        if (!getChessboard().isThereAPieceHere(currentRank+i,currentFile)){
            list.add(theBoard[currentRank+i][currentFile]);
            //Two spaces ahead if first turn
            if (!getHasMoved() && !getChessboard().isThereAPieceHere(currentRank+i*2,currentFile)){
                list.add(theBoard[currentRank+i*2][currentFile]);
            }
        }

        // Diagonal and En-Passant
        for (int n = ((currentFile == 0) ? 1 : -1); n < ((currentFile == 7) ? 1 : 2); n += 2) {
            if ((getChessboard().isThereAPieceHere(currentRank+i, currentFile+n) &&
                    !theGame.doesThisPieceBelongToMe(currentRank+i, currentFile+n)) ||
                    (getChessboard().getEnPassantTurn() + 1 == theGame.getTotalTurns() &&
                            (theBoard[currentRank+i][currentFile+n].equals(getChessboard().getEnPassantTarget())))) {
                list.add(theBoard[currentRank+i][currentFile+n]);
            }
        }

        // Restricts moves if the current king is in check or piece is pinned
        if ((getChessboard().getCurrentKing().getPlayer() == getPlayer())
                && (isPinned() || getChessboard().getCurrentKing().underAttack())){
            return movesToPreventCheck(list);
        }
        return list;
    }
}