import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientPlayer1 {
    private static final String HOST = "localhost";
    private static final int PORT = 12345;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        System.out.println("Подключение к серверу установлено.");

        try {
            Scanner serverInput = new Scanner(socket.getInputStream());
            PrintWriter serverOutput = new PrintWriter(socket.getOutputStream(), true);
            Scanner userInput = new Scanner(System.in);

            int currentPlayer = 1; // начинает игрок 1

            while (true) {
                if (currentPlayer == 1) {
                    System.out.print("Игрок 1, введите номер строки (от 0 до 2): ");
                } else if (currentPlayer == 2) {
                    System.out.print("Игрок 2, введите номер строки (от 0 до 2): ");
                }
                int row = userInput.nextInt();
                System.out.print("Введите номер столбца (от 0 до 2): ");
                int col = userInput.nextInt();

                serverOutput.println(row + " " + col);

                String response = serverInput.nextLine();
                if (response.startsWith("validMove")) {
                    System.out.println("Ход принят.");
                    currentPlayer = 3 - currentPlayer; // обновляем игрока
                } else if (response.startsWith("winner")) {
                    int player = Integer.parseInt(response.split(" ")[1]);
                    System.out.println("Игрок " + player + " выиграл!");
                    break;
                } else if (response.startsWith("draw")) {
                    System.out.println("Ничья!");
                    break;
                } else if (response.startsWith("invalidMove")) {
                    System.out.println("Недопустимый ход!");
                }
            }
        } finally {
            socket.close();
        }
    }
}

