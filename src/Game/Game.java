package Game;

import Pieces.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Game extends MouseAdapter implements ActionListener {

    /**
     * GUI Variables
     */
    private final Chessboard theChessboard = new Chessboard(this);
    private final Piece[][] board = theChessboard.getBoard();
    private final JFrame frame = new JFrame("Basic Chess Game - by Jacky Gao");
    private final JLabel displayText = new JLabel("Current Turn: Player 1");
    private final ArrayList<JButton> promotionButtons = new ArrayList<>(Arrays.asList(new JButton("Queen"),
            new JButton("Rook"), new JButton("Knight"), new JButton("Bishop")));

    /**
     * Initial variables used throughout the class
     * meant to be changed
     */
    private int turn = 1;
    private int startRankIndex = 4;
    private int startFileIndex = 0;
    private int endRankIndex = 4;
    private int endFileIndex = 0;
    private final int xDist = 100;
    private final int yDist = 100;

    /**
     * Variables used in promotions
     */
    private boolean inPromotion = false;
    private Piece promotionPiece;

    /**
     * @return - The current player turn
     **/
    public int getTurn(){ return (turn % 2 == 1) ? 1 : 2; }

    /**
     * @return - The total number of turns
     */
    public int getTotalTurns(){
        return turn;
    }

    /**
     * @return - Frame holding everything from Java Swing
     */
    public JFrame getFrame(){return frame;}

    /**
     * @return - Starting x-coordinate of the chessboard
     */
    public int getXDist(){return xDist;}

    /**
     * @return - Starting y-coordinate of the chessboard
     */
    public int getYDist(){return yDist;}

    /**
     * Constructor - sets initial settings of the board
     */
    public Game(){
        // Initial setup of JFrame
        frame.setSize(600, 650);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.LIGHT_GRAY);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Adding the images of the pieces to the frame
        for (Piece thePiece : theChessboard.getList()){
            frame.add(thePiece.getImage());
            thePiece.getImage().setBounds(xDist+thePiece.getXPos(),yDist+thePiece.getYPos(),50,50);
        }

        // Set location of display text above the board
        displayText.setBounds(xDist, yDist-50, 300,50);
        frame.add(displayText);

        // Creates and adds JLabels of the numbers/letters on the side of the Chessboard
        ArrayList<JLabel> bottomLetters = new ArrayList<>();
        ArrayList<JLabel> sideNumbers = new ArrayList<>();
        for (int i = 0; i<8; i++){
            bottomLetters.add(new JLabel(""+(char)(65+i)));
            bottomLetters.get(i).setBounds(20+xDist+i*50, yDist+400,50,50);
            frame.add(bottomLetters.get(i));

            sideNumbers.add(new JLabel(""+(8-i)));
            sideNumbers.get(i).setBounds(xDist-30, yDist+i*50,50,50);
            frame.add(sideNumbers.get(i));
        }

        // Sets the settings for the JButtons for pawn promotion (but not place them)
        for (int i = 0; i<4; i++){
            promotionButtons.get(i).setBounds(xDist+410, yDist+(i*50), 80,30);
            promotionButtons.get(i).setOpaque(true);
            promotionButtons.get(i).setBackground(Color.LIGHT_GRAY);
            promotionButtons.get(i).addActionListener(this);
        }

        // Adding MouseAdapter event listener to the frame
        frame.addMouseListener(this);

        // Adds the Chessboard to the frame
        frame.add(theChessboard);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Game();
    }

    /**
     * Mouse click event which takes in the user input and plays the game.
     * Players make their first selection, then second selection, and the piece will move
     * if the path is legal. Game ends when king is in checkmate or stalemate.
     * @param e MouseEvent
     */
    @Override
    public void mouseClicked(MouseEvent e){
        int rankIndex = (e.getY()- yDist-30)/50;
        int fileIndex = (e.getX()- xDist)/50;
        if (isOutsideBoundaries(rankIndex,fileIndex)){ return; } // exits if mouse click is outside the chessboard
        boolean firstSelection = false;

        // If user selects their piece, make it their first selection
        if (doesThisPieceBelongToMe(rankIndex, fileIndex)){
            theChessboard.removeCircles();
            //theChessboard.removeUnderAttack(); //testing
            startRankIndex = rankIndex;
            startFileIndex = fileIndex;
            firstSelection = true;
            theChessboard.highlightSquares(board[startRankIndex][startFileIndex]);
            //theChessboard.newUnderAttackSquares(); //testing
        }else{ // second selection
            endRankIndex = rankIndex;
            endFileIndex = fileIndex;
        }

        // Moves the piece if the user makes a legal move
        if (!firstSelection && board[startRankIndex][startFileIndex].canMove(
                board[endRankIndex][endFileIndex])){
            theChessboard.removeCircles();
            //theChessboard.removeUnderAttack(); //testing
            board[startRankIndex][startFileIndex].hasMoved();
            if (inPromotion){
                displayText.setText("Please choose a promotion for your pawn.");
                frame.removeMouseListener(this);
                frame.repaint();
                return;
            }
            theChessboard.move(board[startRankIndex][startFileIndex], board[endRankIndex][endFileIndex],true);
            turn++;
            displayText.setText("Current Turn: Player "+getTurn());
            frame.repaint();
        }

        // Ends the game - Checkmate
        if (theChessboard.getCurrentKing().underAttack() && theChessboard.isCheckmate()){
            JLabel death = new JLabel(new ImageIcon(ClassLoader.getSystemResource("Images/x.png")));
            frame.add(death);
            death.setBounds(xDist+theChessboard.getCurrentKing().getXPos(),
                    xDist+theChessboard.getCurrentKing().getYPos(),50,50);
            frame.remove(theChessboard);
            frame.add(theChessboard);
            turn--;
            displayText.setText("Checkmate. Player "+getTurn()+" wins!");
            frame.removeMouseListener(this);
        }else if (theChessboard.isCheckmate() || theChessboard.getList().size() == 2){ // Stalemate
            displayText.setText("Stalemate! Draw.");
            frame.removeMouseListener(this);
        }
    }

    /**
     * This function will be called when the user hits a button choosing a pawn promotion.
     * This function will promote "promotionPiece" to the designated button choice.
     * Post-condition: "promotionPiece" will be promoted to either Rook, Knight, Bishop, or Queen
     *
     * @param e ActionEvent
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        int currentRank = promotionPiece.getRank();
        int currentFile = promotionPiece.getFile();
        theChessboard.removePiece(promotionPiece);

        // Changes the piece to option chosen
        switch(((JButton)e.getSource()).getText()){
            case "Rook":
                board[currentRank][currentFile] = new Rook(currentRank,currentFile, getTurn(), theChessboard);
                break;
            case "Knight":
                board[currentRank][currentFile] = new Knight(currentRank,currentFile, getTurn(), theChessboard);
                break;
            case "Bishop":
                board[currentRank][currentFile] = new Bishop(currentRank,currentFile, getTurn(), theChessboard);
                break;
            case "Queen":
                board[currentRank][currentFile] = new Queen(currentRank,currentFile, getTurn(), theChessboard);
                break;
            default:
                break;
        }

        // Updating the image
        frame.add(board[currentRank][currentFile].getImage());
        board[currentRank][currentFile].getImage().setBounds(board[currentRank][currentFile].getXPos(),
                board[currentRank][currentFile].getYPos(),50,50);
        frame.remove(theChessboard);
        frame.add(theChessboard);

        // Reset settings back to regular gameplay
        frame.addMouseListener(this);
        inPromotion = false;
        promotionPiece = null;
        for (int i = 0; i<4; i++){
            frame.remove(promotionButtons.get(i));
        }
        theChessboard.move(board[startRankIndex][startFileIndex], board[endRankIndex][endFileIndex],true);
        turn++;
        displayText.setText("Current Turn: Player "+getTurn());
        frame.repaint();
    }

    /**
     * This function initiates the process of promoting "thePawn"
     * @param thePawn The pawn being promoted
     */
    public void startPromotion(Piece thePawn){
        inPromotion = true;
        promotionPiece = thePawn;
        for (int i = 0; i<4; i++){
            frame.add(promotionButtons.get(i));
        }
        frame.remove(theChessboard);
        frame.add(theChessboard);
        frame.repaint();
    }

    /**
     * @param rank Rank position on the Chessboard
     * @param file File position on the Chessboard
     * @return Returns true if the piece belongs to current player and is not outside of boundaries
     */
    public boolean doesThisPieceBelongToMe(int rank, int file){
        return !isOutsideBoundaries(rank, file) && board[rank][file].getPlayer() == getTurn();
    }

    /**
     * @param rank Must be an integer
     * @param file Must be an integer
     * @return Returns true if rank or file are below 0 or above 7
     */
    public boolean isOutsideBoundaries(int rank, int file){
        return rank > 7 || file > 7 || rank < 0 || file < 0;
    }
}
