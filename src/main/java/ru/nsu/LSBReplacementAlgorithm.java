package ru.nsu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Класс реализует алгоритм замены младшего значащего бита (LSB) для встраивания и извлечения текстовых сообщений из
 * изображений.
 * <p>
 * Данный алгоритм изменяет младший бит каждого пикселя изображения для кодирования сообщения.
 * Предполагается, что сообщение записывается в первые 32 бита (4 байта) для хранения длины сообщения,
 * а затем непосредственно записывается само сообщение.
 */
public class LSBReplacementAlgorithm {

    /**
     * Встраивает текстовое сообщение в изображение.
     *
     * @param inputImagePath  путь к исходному изображению
     * @param outputImagePath путь для сохранения изображения с внедренным сообщением
     * @param message         текст сообщения для встраивания
     * @throws IOException              если файл изображения не найден или не может быть прочитан
     * @throws IllegalArgumentException если сообщение слишком длинное для данного изображения
     */
    public static void embedMessage(String inputImagePath, String outputImagePath, String message) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImagePath));
        byte[] messageBytes = message.getBytes();
        int totalPixels = image.getWidth() * image.getHeight();

        if (messageBytes.length * 8 + 32 > totalPixels) {
            throw new IllegalArgumentException("Сообщение слишком длинное для данного изображения");
        }

        // Кодируется длина сообщения в первых 32 битах
        int pixelIndex = 0;
        for (int i = 0; i < 32; i++) {
            int bit = (messageBytes.length >> (31 - i)) & 1;
            modifyPixel(image, pixelIndex++, bit);
        }

        // Кодируется сообщение
        for (byte b : messageBytes) {
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                modifyPixel(image, pixelIndex++, bit);
            }
        }

        ImageIO.write(image, "png", new File(outputImagePath));
    }

    /**
     * Извлекает текстовое сообщение из изображения.
     *
     * @param inputImagePath путь к изображению с внедренным сообщением
     * @return извлеченное текстовое сообщение
     * @throws IOException              если файл изображения не найден или не может быть прочитан
     * @throws IllegalArgumentException если данные некорректны или файл поврежден
     */
    public static String extractMessage(String inputImagePath) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImagePath));
        int totalPixels = image.getWidth() * image.getHeight();

        // Извлекается длина сообщения
        int messageLength = 0;
        for (int i = 0; i < 32; i++) {
            messageLength = (messageLength << 1) | extractBit(image, i);
        }

        if (messageLength * 8 + 32 > totalPixels) {
            throw new IllegalArgumentException("Данные некорректны или файл поврежден");
        }

        // Извлекается сообщение
        byte[] messageBytes = new byte[messageLength];
        for (int i = 0; i < messageLength; i++) {
            int b = 0;
            for (int j = 0; j < 8; j++) {
                b = (b << 1) | extractBit(image, 32 + i * 8 + j);
            }
            messageBytes[i] = (byte) b;
        }

        return new String(messageBytes);
    }

    /**
     * Извлекает младший значащий бит определенного пикселя изображения.
     *
     * @param image изображение, из которого извлекается бит
     * @param index индекс пикселя (по порядку)
     * @return младший значащий бит (0 или 1)
     */
    private static int extractBit(BufferedImage image, int index) {
        int x = index % image.getWidth();
        int y = index / image.getWidth();
        int rgb = image.getRGB(x, y);
        return rgb & 1;
    }

    /**
     * Модифицирует младший значащий бит определенного пикселя изображения.
     *
     * @param image изображение, в котором изменяется бит
     * @param index индекс пикселя (по порядку)
     * @param bit   новый младший значащий бит (0 или 1)
     */
    private static void modifyPixel(BufferedImage image, int index, int bit) {
        int x = index % image.getWidth();
        int y = index / image.getWidth();
        int rgb = image.getRGB(x, y);
        int lsbModifiedRgb = (rgb & ~1) | bit;
        image.setRGB(x, y, lsbModifiedRgb);
    }
}
