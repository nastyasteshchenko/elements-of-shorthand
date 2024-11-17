package ru.nsu;

import java.io.IOException;
import java.util.Scanner;

import static ru.nsu.LSBReplacementAlgorithm.embedMessage;
import static ru.nsu.LSBReplacementAlgorithm.extractMessage;

public class Main {

    private static final String INPUT_JPG_PATH = "./forest.png";
    private static final String OUTPUT_JPG_PATH = "./embedded_forest.png";

    public static void main(String[] args) throws IOException {
        Test.test();
        embedEnteredLines();
    }

    public static void embedEnteredLines() throws IOException {
        System.out.println("-------------------------");

        Scanner scanner = new Scanner(System.in);
        String input;

        System.out.println("Введите строку (для выхода введите 'exit'):");

        while (true) {
            System.out.print("> ");
            input = scanner.nextLine();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }

            System.out.println("Сообщение, которое будет внедряться: " + input);
            embedMessage(INPUT_JPG_PATH, OUTPUT_JPG_PATH, input);
            String extractedMessage = extractMessage(OUTPUT_JPG_PATH);
            System.out.println("Извлеченное сообщение: " + extractedMessage);
        }
    }
}