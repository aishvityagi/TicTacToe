import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class TicTacToe extends JFrame implements ActionListener {

    private JButton[][] buttons = new JButton[3][3];
    private boolean playerXTurn = true; // X starts first
    private String player1, player2;
    private int player1Wins = 0, player2Wins = 0;
    private boolean playAgainstComputer = false;
    private boolean gameActive = true; // Track if the game is active
    private JLabel scoreLabel;

    public TicTacToe() {
        setTitle("Tic Tac Toe");
        setSize(400, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Get player names and mode
        getPlayerInfo();

        JPanel boardPanel = new JPanel(new GridLayout(3, 3));

        // Create buttons for the game board
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col] = new JButton("");
                buttons[row][col].setFont(new Font("Arial", Font.PLAIN, 80));
                buttons[row][col].setFocusPainted(false);
                buttons[row][col].addActionListener(this);
                boardPanel.add(buttons[row][col]);
            }
        }

        // Add the game board to the center
        add(boardPanel, BorderLayout.CENTER);

        // Create a label for the scoreboard
        scoreLabel = new JLabel(player1 + " (X): " + player1Wins + " | " + player2 + " (O): " + player2Wins);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        add(scoreLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    // Method to input player names and game mode
    private void getPlayerInfo() {
        player1 = JOptionPane.showInputDialog(this, "Enter name for Player 1 (X):");
        if (player1 == null || player1.trim().isEmpty()) {
            player1 = "Player 1";
        }

        String[] options = {"Play against Player", "Play against Computer"};
        int mode = JOptionPane.showOptionDialog(this, "Choose the mode:", "Tic Tac Toe",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (mode == 1) { // Play against computer
            playAgainstComputer = true;
            player2 = "Computer";
        } else {
            player2 = JOptionPane.showInputDialog(this, "Enter name for Player 2 (O):");
            if (player2 == null || player2.trim().isEmpty()) {
                player2 = "Player 2";
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (!gameActive) return; // Prevent actions if the game is not active

        JButton clickedButton = (JButton) e.getSource();

        // If button is already clicked, do nothing
        if (!clickedButton.getText().equals("")) {
            return;
        }

        // Player's move
        clickedButton.setText(playerXTurn ? "X" : "O");

        // Check for win or draw after every move
        if (checkWin()) {
            String winner = playerXTurn ? player1 : player2;
            JOptionPane.showMessageDialog(this, winner + " wins!");
            updateScore(winner);
            resetBoard();
            return;
        } else if (isBoardFull()) {
            JOptionPane.showMessageDialog(this, "It's a draw!");
            resetBoard();
            return;
        }

        // Switch player turn
        playerXTurn = !playerXTurn;

        // If playing against computer, trigger computer move
        if (playAgainstComputer && !playerXTurn) {
            computerMove();
            // After computer move, check again if it won
            if (checkWin()) {
                JOptionPane.showMessageDialog(this, "Computer wins!");
                updateScore(player2);
                resetBoard();
            } else if (isBoardFull()) {
                JOptionPane.showMessageDialog(this, "It's a draw!");
                resetBoard();
            } else {
                playerXTurn = !playerXTurn; // Switch back to player's turn
            }
        }
    }

    // AI: Computer makes a move based on strategy
    private void computerMove() {
        // 1. Try to win
        if (makeBestMove("O")) return;

        // 2. Try to block player's win
        if (makeBestMove("X")) return;

        // 3. Make a random move as fallback
        makeRandomMove();
    }

    // Try to find a winning/blocking move for the given mark ("X" or "O")
    private boolean makeBestMove(String mark) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().equals("")) {
                    buttons[row][col].setText(mark); // Test move
                    if (checkWin()) {
                        if (mark.equals("O")) { // Computer's winning move
                            buttons[row][col].setText("O"); // Computer takes the spot
                        } else { // Block player's win
                            buttons[row][col].setText("O"); // Computer blocks
                        }
                        return true; // Winning or blocking move found
                    }
                    buttons[row][col].setText(""); // Undo the test move
                }
            }
        }
        return false; // No winning or blocking move found
    }

    // Make a random move for the computer
    private void makeRandomMove() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().equals("")) {
                    buttons[row][col].setText("O");
                    return;
                }
            }
        }
    }

    // Check if the board is full
    private boolean isBoardFull() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (buttons[row][col].getText().equals("")) {
                    return false; // Empty space found
                }
            }
        }
        return true; // No empty spaces
    }

    // Check if there's a winning combination
    private boolean checkWin() {
        String[][] board = new String[3][3];
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                board[row][col] = buttons[row][col].getText();
            }
        }

        // Check rows, columns, and diagonals
        for (int i = 0; i < 3; i++) {
            if (board[i][0].equals(board[i][1]) && board[i][1].equals(board[i][2]) && !board[i][0].equals("")) {
                return true;
            }
            if (board[0][i].equals(board[1][i]) && board[1][i].equals(board[2][i]) && !board[0][i].equals("")) {
                return true;
            }
        }
        if (board[0][0].equals(board[1][1]) && board[1][1].equals(board[2][2]) && !board[0][0].equals("")) {
            return true;
        }
        if (board[0][2].equals(board[1][1]) && board[1][1].equals(board[2][0]) && !board[0][2].equals("")) {
            return true;
        }

        return false;
    }

    // Update the scoreboard
    private void updateScore(String winner) {
        if (winner.equals(player1)) {
            player1Wins++;
        } else {
            player2Wins++;
        }
        scoreLabel.setText(player1 + " (X): " + player1Wins + " | " + player2 + " (O): " + player2Wins);
    }

    // Reset the game board for a new round
    private void resetBoard() {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                buttons[row][col].setText("");
            }
        }
        gameActive = true; // Reactivate the game
        playerXTurn = true; // X starts the next game
    }

    public static void main(String[] args) {
        new TicTacToe();
    }
}
