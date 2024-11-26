package ru.nsu;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Test {

    private static final String INPUT_JPG_PATH = "./forest.png";
    private static final String OUTPUT_JPG_PATH = "./embedded_forest.png";

    public static void test() throws IOException, NoSuchAlgorithmException {
        LSBReplacementAlgorithm lsbReplacementAlgorithm = LSBReplacementAlgorithm.create();

        String key = "Secret key";

        String message = "Лабораторная работа 6. Элементы стеганографии.";
        System.out.println("Сообщение, которое будет внедряться: " + message);
        System.out.println("Секретный ключ: " + key);
        lsbReplacementAlgorithm.embedMessage(INPUT_JPG_PATH, OUTPUT_JPG_PATH, message, INPUT_JPG_PATH);
        String extractedMessage = lsbReplacementAlgorithm.extractMessage(OUTPUT_JPG_PATH, INPUT_JPG_PATH);
        System.out.println("Извлеченное сообщение: " + extractedMessage);

        System.out.println("------------------------");

        message = "Любимым занятием нас всех, мальчишек, было лазание на вековые сосны. Мы забирались на" +
                " самые вершины. Оттуда, казалось, можно было дотянуться рукой до пышных летних облаков. Там сильно," +
                " до одури пахло нагретой смолой и во все стороны простирался великий неведомый лес.";
        System.out.println("Сообщение, которое будет внедряться: " + message);
        System.out.println("Секретный ключ: " + key);
        lsbReplacementAlgorithm.embedMessage(INPUT_JPG_PATH, OUTPUT_JPG_PATH, message, INPUT_JPG_PATH);
        extractedMessage = lsbReplacementAlgorithm.extractMessage(OUTPUT_JPG_PATH, INPUT_JPG_PATH);
        System.out.println("Извлеченное сообщение: " + extractedMessage);
    }
}
