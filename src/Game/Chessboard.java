package Game;

import Pieces.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Chessboard extends JPanel {

    /**
     * UI Elements
     */
    private final Game theGame;
    private final Piece[][] board = new Piece[8][8]; // Sets the size of the board to an 8x8 2d Array
    private final ArrayList<JLabel> circles = new ArrayList<>();
    private final ArrayList<JLabel> test = new ArrayList<>();

    /**
     * Variables used in en-passant related moves
     */
    private Piece enPassantTarget;  // Piece which can be taken by En-Passant
    private int enPassantTurn = -1;  // The turn in which enPassant may occur

    /**
     * References to different sets of pieces
     */
    private final ArrayList<Piece> listOfPieces = new ArrayList<>();
    private final ArrayList<Piece> listOfWhitePieces = new ArrayList<>();
    private final ArrayList<Piece> listOfBlackPieces = new ArrayList<>();
    private final Piece[] theKings = new Piece[2]; // theKings[0] = black king, theKings[1] = white king

    /**
     * Accessor methods for the different Arraylists of pieces
     */
    public ArrayList<Piece> getList(){ return listOfPieces; }
    public ArrayList<Piece> getListOfWhitePieces(){ return listOfWhitePieces;}
    public ArrayList<Piece> getListOfBlackPieces(){ return listOfBlackPieces;}
    public ArrayList<Piece> getCurrentPlayerPieces(boolean current){
        return ((theGame.getTurn() == 1 && current) || (theGame.getTurn()==2 && !current)) ? listOfWhitePieces :
                listOfBlackPieces;
    }

    /**
     * @return - The 2D Array of Pieces on the chessboard.
     */
    public Piece[][] getBoard(){
        return board;
    }

    /**
     * @return the overall game object that the chessboard is associated with
     */
    public Game getTheGame(){ return theGame;}

    /**
     * @return The target square of legal En Passant.
     */
    public Piece getEnPassantTarget(){return enPassantTarget;}

    /**
     * @return - The turn that En Passant occurs before (if en-passant can occur on turn 10, this is 9)
     */
    public int getEnPassantTurn(){return enPassantTurn;}

    /**
     * This method assigns variables to the pawn which will allow the logic
     * for doing en passant
     * @param enPassantTarget - Must be the empty square between the pawn's initial position and final position
     *                                  when moving two forwards on the first move.
     */
    public void setEnPassant(Piece enPassantTarget){
        this.enPassantTarget = enPassantTarget;
        this.enPassantTurn = theGame.getTotalTurns();
    }

    /**
     * Getter method for the current king: 1 = Black's King : 2 = White's King
     * @return - Reference to the current player's king.
     */
    public Piece getCurrentKing(){
        return (theGame.getTurn() == 1) ? theKings[1] : theKings[0];
    }

    /**
     * Constructor for the Chessboard
     * Initializes the initial position of all the pieces
     */
    public Chessboard(Game theGame){
        for (int i = 0; i< 8; i++){
            if (i==0 || i==7){                                      // If i==0, player = 2. If i==7, player = 1
                board[i][0] = new Rook(i,0,i/-7 + 2,this);
                board[i][1] = new Knight(i,1,i/-7 + 2,this);
                board[i][2] = new Bishop(i,2,i/-7 + 2,this);
                board[i][3] = new Queen(i,3,i/-7 + 2,this);
                theKings[i/7] = board[i][4] = new King(i,4,i/-7 + 2,this);
                board[i][5] = new Bishop(i,5,i/-7 + 2,this);
                board[i][6] = new Knight(i,6,i/-7 + 2,this);
                board[i][7] = new Rook(i,7,i/-7 + 2,this);
            }else{
                for (int n = 0; n < 8; n++){
                    if (i==1 || i == 6){                              // If i==1; player = 2. If i==6; player = 1
                        board[i][n] = new Pawn(i,n,i % 6 + 1,this);
                    }else{
                        board[i][n] = new Piece(i,n,this);
                    }
                }
            }
        }
        this.theGame = theGame;
    }

    /**
     * Creates the checkerboard pattern in the graphics
     * @param g Graphics object from Java swing
     */
    public void paint(Graphics g){

        int xDist = theGame.getXDist();
        int yDist = theGame.getYDist();

        g.setColor(new Color(184,139,74)); // "Dark" square color
        g.fillRect(xDist, yDist, 400, 400);

        g.setColor(new Color(227,193,111)); // "Light" square color
        for (int i = xDist; i<xDist+400; i+=100){
            for (int j = xDist; j<xDist+400; j+=50) {
                int increment = ((j/50) % 2 == 1) ? 50 : 0;
                g.fillRect(i + increment, j, 50, 50);
            }
        }
    }

    /**
     * Prints the board into the console
     */
    public void printBoard(){
        System.out.println("     [A]    [B]    [C]    [D]    [E]    [F]    [G]    [H]\n\n");
        for (int i = 0; i<8; i++){
            System.out.print("["+(8-i)+"]");

            for (int n = 0; n<8; n++){
                System.out.print(board[i][n].getName()+" ");
            }
            System.out.println("\n\n\n");
        }
        System.out.println("     [A]    [B]    [C]    [D]    [E]    [F]    [G]    [H]");
    }

    /**
     * Moves the "start" piece to the "end" piece's location and
     * replaces the start with an empty piece.
     *
     * @param start The piece being moved
     * @param end The piece being moved to
     */
    public void move(Piece start, Piece end, boolean removePiece){
        int currentRank = start.getRank();
        int currentFile = start.getFile();
        int finalRank = end.getRank();
        int finalFile = end.getFile();
        int newYPos = end.getYPos();
        int newXPos = end.getXPos();

        //Only permanently removes piece if "removePiece" is true (called in MainTester)
        if (isThereAPieceHere(finalRank, finalFile) && removePiece){
            removePiece(board[finalRank][finalFile]);
        }else if (isThereAPieceHere(finalRank, finalFile)){ // Used if just testing moves under checkmate
            removeFromList(board[finalRank][finalFile]);
        }

        // Updates the values on the moved piece
        board[finalRank][finalFile] = start;
        board[finalRank][finalFile].setRank(finalRank);
        board[finalRank][finalFile].setFile(finalFile);
        board[finalRank][finalFile].setYPos(newYPos);
        board[finalRank][finalFile].setXPos(newXPos);
        board[finalRank][finalFile].getImage().setBounds(
                theGame.getXDist()+board[finalRank][finalFile].getXPos(),
                theGame.getYDist()+board[finalRank][finalFile].getYPos(),50,50);

        // Places an empty piece at old location
        board[currentRank][currentFile] = new Piece(currentRank,currentFile,this);

    }

    /**
     * Checks if the current player has any legal moves by looping through all their pieces.
     * @return - True if there are no legal moves for the current player, false otherwise.
     */
    public boolean isCheckmate(){
        ArrayList<Piece> list = new ArrayList<>(getCurrentPlayerPieces(true));

        for (Piece piece : list) {
            if (piece.getLegalMoves().size() > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * This function puts a gray circle icon over all the squares on the board
     * that "thePiece" can legally move to.
     *
     * @param thePiece - The piece in which to highlight its legal moves
     */
    public void highlightSquares(Piece thePiece){
        ArrayList<Piece> possibleMoves = thePiece.getLegalMoves();

        for (int i = 0; i < possibleMoves.size(); i++){
            circles.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/dot.png"))));
            theGame.getFrame().add(circles.get(i));
            circles.get(i).setBounds(theGame.getXDist()+possibleMoves.get(i).getXPos(),
                    theGame.getYDist()+possibleMoves.get(i).getYPos(),50,50);
            //puts the circle on top of the piece if there is a piece here
            if (isThereAPieceHere(possibleMoves.get(i).getRank(),possibleMoves.get(i).getFile())){
                theGame.getFrame().remove(possibleMoves.get(i).getImage());
                theGame.getFrame().add(possibleMoves.get(i).getImage());
                thePiece.getImage().setBounds(theGame.getXDist()+thePiece.getXPos(),
                        theGame.getYDist()+thePiece.getYPos(),50,50);
            }
        }
        theGame.getFrame().remove(this);
        theGame.getFrame().add(this);
        theGame.getFrame().repaint();
    }

    /**
     * Removes any gray circles currently on the Chessboard
     */
    public void removeCircles(){
        for (int i = circles.size()-1; i>=0; i--){
            theGame.getFrame().remove(circles.get(i));
            circles.remove(i);
        }
        theGame.getFrame().repaint();
    }

    public ArrayList<Piece> getPiecesUnderAttack(){
        ArrayList<Piece> piecesUnderAttack = new ArrayList<>();

        for (Piece playerPiece : getCurrentPlayerPieces(false)){
            for (Piece pieceUnderAttack : playerPiece.getLegalMoves()){
                if (!piecesUnderAttack.contains(pieceUnderAttack)){
                    piecesUnderAttack.add(pieceUnderAttack);
                }
            }
        }
        return piecesUnderAttack;
    }

    /**
     * This function puts an "x" icon over all the squares on the board which are under attack by the other player
     */
    public void underAttackSquares(){
        for (int i = 0; i<8; i++){
            for (int n = 0; n<8; n++){
                if (board[i][n].underAttack()){
                    test.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/x.png"))));
                    theGame.getFrame().add(test.get(test.size()-1));
                    test.get(test.size()-1).setBounds(theGame.getXDist()+board[i][n].getXPos(),
                            theGame.getYDist()+board[i][n].getYPos(),50,50);
                }
            }
        }
        theGame.getFrame().remove(this);
        theGame.getFrame().add(this);
    }

    public void newUnderAttackSquares(){
        for (Piece thePiece : getPiecesUnderAttack()){
            test.add(new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/x.png"))));
            theGame.getFrame().add(test.get(test.size()-1));
            test.get(test.size()-1).setBounds(theGame.getXDist()+thePiece.getXPos(),
                    theGame.getYDist()+thePiece.getYPos(),50,50);
        }
        theGame.getFrame().remove(this);
        theGame.getFrame().add(this);
    }

    /**
     * Removes any red "x" icons over the board from the "underAttackSquares()" method
     */
    public void removeUnderAttack(){
        for (int i = test.size()-1; i>=0; i--){
            theGame.getFrame().remove(test.get(i));
            test.remove(i);
        }
        theGame.getFrame().repaint();
    }

    /**
     * Checks if there is currently a piece belong to either player on the position (rank,file)
     * @param rank Rank of the Chessboard location
     * @param file File of the Chessboard location
     * @return True if there is a unique piece at the given rank and file location (or is out of bounds); otherwise false
     */
    public boolean isThereAPieceHere(int rank, int file){
        for (Piece check : listOfPieces){
            if (check.getRank() == rank && check.getFile() == file){
                return true;
            }
        }
        return theGame.isOutsideBoundaries(rank,file);
    }

    /**
     * This method removes "thePiece" from the Chessboard and ArrayLists.
     *
     * @param thePiece The piece being removed
     */
    public void removePiece(Piece thePiece){
        for (int i = 0; i<listOfPieces.size(); i++){
            if (listOfPieces.get(i) == thePiece){
                removeFromList(thePiece);
                theGame.getFrame().remove(listOfPieces.get(i).getImage());
                theGame.getFrame().repaint();
                listOfPieces.remove(i);
                break;
            }
        }

    }

    /**
     * Removes the designated piece from ArrayLists.
     * @param thePiece Piece to be removed
     */
    public void removeFromList(Piece thePiece){
        for (int i = 0; i < listOfWhitePieces.size(); i++){
            if (listOfWhitePieces.get(i) == thePiece){
                listOfWhitePieces.remove(i);
                break;
            }
        }
        for (int i = 0; i < listOfBlackPieces.size(); i++){
            if (listOfBlackPieces.get(i) == thePiece){
                listOfBlackPieces.remove(i);
                break;
            }
        }
    }

    /**
     * Removes the designated piece from "listOfPieces"
     * @param thePiece The piece to be removed
     */
    public void removeFromOverallList(Piece thePiece){
        for (int i = 0; i < listOfPieces.size(); i++){
            if (listOfPieces.get(i) == thePiece){
                listOfPieces.remove(i);
                break;
            }
        }
    }
}
