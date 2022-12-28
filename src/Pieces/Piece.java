package Pieces;

import Game.Chessboard;
import javax.swing.*;
import java.util.ArrayList;

public class Piece{

    private final String name;
    private int rank;
    private int file;
    private int player;
    private int xPos;
    private int yPos;
    private JLabel image;
    private boolean hasMoved = false;
    private final Chessboard board;

    /**
     * Constructor for unique pieces
     *
     * @param name Name of the piece
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @param player Player that the piece belongs to
     * @param image Image Icon of the piece
     */
    public Piece(String name, int rank, int file, int player, JLabel image, Chessboard board){
        this.rank = rank;
        this.file = file;
        this.xPos = file*50;
        this.yPos = rank*50;
        this.player = player;
        this.image = image;
        this.board = board;

        if (player == 1){
            this.name = name.toUpperCase();
            board.getListOfWhitePieces().add(this);
        }else{
            this.name = name.toLowerCase();
            board.getListOfBlackPieces().add(this);
        }
        board.getList().add(this);
    }

    /**
     * Constructors for empty spaces
     *
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     */
    public Piece(int rank, int file, Chessboard board){
        this.name = "  []  ";
        this.rank = rank;
        this.file = file;
        this.xPos = file*50;
        this.yPos = rank*50;
        this.board = board;
    }

    /**
     * Getter and Setter methods for all variables
     */
    public String getName(){ return name; }
    public int getRank(){ return rank; }
    public void setRank(int rank){ this.rank = rank; }
    public int getFile(){ return file; }
    public void setFile(int file){ this.file = file; }
    public int getXPos(){ return xPos; }
    public void setXPos(int xPos){ this.xPos = xPos; }
    public int getYPos(){ return yPos; }
    public void setYPos(int yPos){ this.yPos = yPos; }
    public int getPlayer() { return player; }
    public boolean getHasMoved(){ return hasMoved; }
    public void hasMoved(){ this.hasMoved = true;}
    public JLabel getImage(){ return image; }
    public Chessboard getChessboard(){ return board; }

    /**
     * Returns an ArrayList of legal moves for a Piece. Overridden in subclasses
     * @return - An arraylist of all the legal pieces this piece can move to.
     */
    public ArrayList<Piece> getLegalMoves(){
        return new ArrayList<>();
    }

    /**
     * This function checks if the current piece is able to move to "thePiece" by checking if
     * "thePiece" is a piece part of its list of legal moves. Overridden in King and Pawn
     *
     * @param thePiece The Piece which is being moved towards
     * @return Returns true if "thePiece" is one of the legal moves of the piece; otherwise false.
     */
    public boolean canMove(Piece thePiece){
        for (Piece legalMove : getLegalMoves()) {
            if (legalMove.getRank() == thePiece.getRank() && legalMove.getFile() == thePiece.getFile()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if "this" piece can attack "thePiece". Overridden in King and Pawn
     * @param thePiece - The piece being attacked.
     * @return - True if this can attack thePiece, false otherwise
     */
    public boolean canAttack(Piece thePiece){
        return canMove(thePiece);
    }

    /**
     * Checks if "this" piece is under attack
     * @return - True if this piece can be attacked by any of the other player's pieces, false otherwise
     */
    public boolean underAttack(){
        for (Piece thePiece : board.getCurrentPlayerPieces(false)){
            if (thePiece.canAttack(this)){
                return true;
            }
        }
        return false;
    }

    /**
     * This method is called whenever a piece is trying to be moved while the King is in check OR the piece is pinned.
     * It returns a new ArrayList of legal moves out of the normal list given to it in the parameter.
     *
     * @param listOfMoves List of moves that this piece can make under normal circumstances
     * @return An ArrayList of pieces that this piece can move to without putting the king in check
     */
    public ArrayList<Piece> movesToPreventCheck(ArrayList<Piece> listOfMoves){
        ArrayList<Piece> newList = new ArrayList<>();
        Piece[][] theBoard = board.getBoard();

        for (Piece potentialMove : listOfMoves) {

            int currentRank = getRank();
            int currentFile = getFile();
            int finalRank = potentialMove.getRank();
            int finalFile = potentialMove.getFile();

            // Moves the piece
            getChessboard().move(this, potentialMove,false);
            if (!getChessboard().getCurrentKing().underAttack()) { // Checks if the move puts the king in check
                newList.add(potentialMove); // Adds move to new list
            }
            // Undoes the move
            getChessboard().move(this, getChessboard().getBoard()[currentRank][currentFile],false);
            theBoard[finalRank][finalFile] = potentialMove;

            // Add the piece back to ArrayLists
            if (theBoard[finalRank][finalFile].getPlayer() == 1){
                board.getListOfWhitePieces().add(theBoard[finalRank][finalFile]);
            }else if (theBoard[finalRank][finalFile].getPlayer() == 2){
                board.getListOfBlackPieces().add(theBoard[finalRank][finalFile]);
            }
        }
        return newList;
    }

    /**
     * This method checks whether "this" piece is pinned to the King. (If the piece is moved the King is put in check)
     * @return - True if the piece is pinned, false if not
     */
    public boolean isPinned(){
        boolean cannotMove = false;

        // Removes piece from ArrayLists
        board.removeFromList(this);
        board.removeFromOverallList(this);

        // Checks if king is under attack if the piece is moved
        if (getChessboard().getCurrentKing().underAttack()) {
            cannotMove = true;
        }

        // Puts piece back in ArrayLists
        if (this.getPlayer() == 1){
            board.getListOfWhitePieces().add(this);
            board.getList().add(this);
        }else if (this.getPlayer() == 2){
            board.getListOfBlackPieces().add(this);
            board.getList().add(this);
        }

        return cannotMove;
    }


    /**
     * This function returns the image of a piece depending on "player" and "piece"
     *
     * @param player - Designates whose piece (1=white/2=black)
     * @param piece - Designates which type (1=Rook, 2=Knight, 3=Bishop, 4=Queen, 5=King, 6=Pawn)
     * @return - Returns a JLabel of the Image representing the designated piece
     */
    public static JLabel getNewImage(int player, int piece){
        return switch (player) {
            case 1 -> switch (piece) {
                case 1 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/wRook.png")));
                case 2 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/wKnight.png")));
                case 3 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/wBishop.png")));
                case 4 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/wQueen.png")));
                case 5 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/wKing.png")));
                case 6 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/wPawn.png")));
                default -> null;
            };
            case 2 -> switch (piece) {
                case 1 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/bRook.png")));
                case 2 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/bKnight.png")));
                case 3 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/bBishop.png")));
                case 4 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/bQueen.png")));
                case 5 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/bKing.png")));
                case 6 -> new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/bPawn.png")));
                default -> null;
            };
            default -> null;
        };
    }
}