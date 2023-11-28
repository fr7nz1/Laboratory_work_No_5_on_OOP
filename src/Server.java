import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {
    private static final int PORT = 12345;
    private static final int BOARD_SIZE = 3;

    private static char[][] board;
    private static int currentPlayer;

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        System.out.println("Сервер запущен. Ожидание клиента...");

        Socket clientSocket1 = serverSocket.accept();
        System.out.println("Первый клиент подсоединен.");

        Socket clientSocket2 = serverSocket.accept();
        System.out.println("Второй клиент подсоединен.");

        Thread thread1 = new Thread(() -> {
            try {
                Scanner clientInput1 = new Scanner(clientSocket1.getInputStream());
                PrintWriter clientOutput1 = new PrintWriter(clientSocket1.getOutputStream(), true);
                handleClient(clientInput1, clientOutput1, 0);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket1.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread thread2 = new Thread(() -> {
            try {
                Scanner clientInput2 = new Scanner(clientSocket2.getInputStream());
                PrintWriter clientOutput2 = new PrintWriter(clientSocket2.getOutputStream(), true);
                handleClient(clientInput2, clientOutput2, 1);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    clientSocket2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
    }

    private static void handleClient(Scanner clientInput, PrintWriter clientOutput, int player) {
        initializeGame();

        while (true) {
            int row = clientInput.nextInt();
            int col = clientInput.nextInt();

            if (isValidMove(row, col)) {
                makeMove(row, col);
                if (hasPlayerWon(currentPlayer)) {
                    clientOutput.println("winner " + currentPlayer);
                    break;
                } else if (isBoardFull()) {
                    clientOutput.println("draw");
                    break;
                } else {
                    clientOutput.println("validMove");
                    currentPlayer = 1 - currentPlayer;
                }
            } else {
                clientOutput.println("invalidMove");
            }
        }
    }

    private static void initializeGame() {
        board = new char[BOARD_SIZE][BOARD_SIZE];
        currentPlayer = 0;

        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                board[i][j] = '-';
            }
        }
    }

    private static boolean isValidMove(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE && board[row][col] == '-';
    }

    private static void makeMove(int row, int col) {
        board[row][col] = currentPlayer == 0 ? 'X' : 'O';
        printBoard();
    }

    private static boolean hasPlayerWon(int player) {
        char symbol = player == 0 ? 'X' : 'O';

        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board[i][0] == symbol && board[i][1] == symbol && board[i][2] == symbol) { // проверка по горизонтали
                return true;
            }
            if (board[0][i] == symbol && board[1][i] == symbol && board[2][i] == symbol) { // проверка по вертикали
                return true;
            }
        }

        if (board[0][0] == symbol && board[1][1] == symbol && board[2][2] == symbol) { // проверка по диагонали
            return true;
        }
        if (board[2][0] == symbol && board[1][1] == symbol && board[0][2] == symbol) { // проверка по диагонали
            return true;
        }

        return false;
    }

    private static boolean isBoardFull() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (board[i][j] == '-') {
                    return false;
                }
            }
        }
        return true;
    }

    private static void printBoard() {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}