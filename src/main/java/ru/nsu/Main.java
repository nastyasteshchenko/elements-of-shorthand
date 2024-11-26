package ru.nsu;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

public class Main {

    private static final String INPUT_JPG_PATH = "./forest.png";
    private static final String OUTPUT_JPG_PATH = "./embedded_forest.png";

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Test.test();
        embedEnteredLines();
    }

    public static void embedEnteredLines() throws IOException, NoSuchAlgorithmException {
        LSBReplacementAlgorithm lsbReplacementAlgorithm = LSBReplacementAlgorithm.create();

        System.out.println("-------------------------");

        Scanner scanner = new Scanner(System.in);
        String message;
        String key;

        System.out.println("Введите данные (для выхода введите 'exit'):");

        while (true) {
            System.out.print("> Сообщение: ");
            message = scanner.nextLine();

            if (message.equalsIgnoreCase("exit")) {
                System.out.println("Выход из программы.");
                break;
            }

            System.out.print("> Секретный ключ: ");
            key = scanner.nextLine();

            System.out.println("Сообщение, которое будет внедряться: " + message);
            System.out.println("Секретный ключ: " + key);
            lsbReplacementAlgorithm.embedMessage(INPUT_JPG_PATH, OUTPUT_JPG_PATH, message, key);
            String extractedMessage = lsbReplacementAlgorithm.extractMessage(OUTPUT_JPG_PATH, key);
            System.out.println("Извлеченное сообщение: " + extractedMessage);
        }
    }
}