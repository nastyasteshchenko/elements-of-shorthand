package ru.nsu;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class LSBReplacementAlgorithm {

    private final MessageDigest digest;

    private LSBReplacementAlgorithm(MessageDigest digest) {
        this.digest = digest;
    }

    public static LSBReplacementAlgorithm create() throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return new LSBReplacementAlgorithm(digest);
    }

    /**
     * Встраивает текстовое сообщение в изображение на основе ключа.
     *
     * @param inputImagePath  путь к исходному изображению
     * @param outputImagePath путь для сохранения изображения с внедренным сообщением
     * @param message         текст сообщения для встраивания
     * @param secretKey       секретный ключ для выбора пикселей
     * @throws IOException              если файл изображения не найден или не может быть прочитан
     * @throws IllegalArgumentException если сообщение слишком длинное для данного изображения или введен неверный ключ
     */
    public void embedMessage(String inputImagePath, String outputImagePath, String message, String secretKey)
            throws IOException {

        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Не указан секретный ключ");
        }

        BufferedImage image = ImageIO.read(new File(inputImagePath));
        byte[] messageBytes = message.getBytes();
        int totalPixels = image.getWidth() * image.getHeight();

        if (messageBytes.length * 8 + 32 > totalPixels) {
            throw new IllegalArgumentException("Сообщение слишком длинное для данного изображения");
        }

        List<Integer> pixelIndexes = generatePixelIndexes(image, secretKey);

        int pixelIndex = 0;

        // Кодируется длина сообщения в первых 32 битах
        for (; pixelIndex < 32; pixelIndex++) {
            int bit = (messageBytes.length >> (31 - pixelIndex)) & 1;
            modifyPixel(image, pixelIndexes.get(pixelIndex), bit);
        }

        // Кодируется сообщение
        for (byte b : messageBytes) {
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                modifyPixel(image, pixelIndexes.get(pixelIndex++), bit);
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
    public String extractMessage(String inputImagePath, String secretKey) throws IOException {
        BufferedImage image = ImageIO.read(new File(inputImagePath));
        int totalPixels = image.getWidth() * image.getHeight();

        List<Integer> pixelIndexes = generatePixelIndexes(image, secretKey);

        // Извлекается длина сообщения
        int pixelIndex = 0;
        int messageLength = 0;
        for (; pixelIndex < 32; pixelIndex++) {
            messageLength = (messageLength << 1) | extractBit(image, pixelIndexes.get(pixelIndex));
        }

        if (messageLength * 8 + 32 > totalPixels) {
            throw new IllegalArgumentException("Данные некорректны или файл поврежден");
        }

        // Извлекается сообщение
        byte[] messageBytes = new byte[messageLength];
        for (int i = 0; i < messageLength; i++) {
            int b = 0;
            for (int j = 0; j < 8; j++) {
                b = (b << 1) | extractBit(image, pixelIndexes.get(pixelIndex++));
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
    private int extractBit(BufferedImage image, int index) {
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
    private void modifyPixel(BufferedImage image, int index, int bit) {
        int x = index % image.getWidth();
        int y = index / image.getWidth();
        int rgb = image.getRGB(x, y);
        int lsbModifiedRgb = (rgb & ~1) | bit;
        image.setRGB(x, y, lsbModifiedRgb);
    }

    /**
     * Генерирует список индексов пикселей для встраивания данных.
     * Использует значения, зависящие от секретного ключа.
     *
     * @param image     изображение, в котором будет происходить встраивание
     * @param secretKey секретный ключ для генерации псевдослучайных чисел
     * @return список индексов пикселей
     * @throws IllegalArgumentException если введен неверный ключ
     */
    private List<Integer> generatePixelIndexes(BufferedImage image, String secretKey) {

        if (secretKey == null || secretKey.isEmpty()) {
            throw new IllegalArgumentException("Не указан секретный ключ");
        }

        int totalPixels = image.getWidth() * image.getHeight();
        List<Integer> pixelIndexes = new ArrayList<>(totalPixels);

        // Заполняем список индексов всех пикселей
        for (int i = 0; i < totalPixels; i++) {
            pixelIndexes.add(i);
        }

        // Перемешиваем индексы с использованием псевдослучайных чисел, основанных на хэше ключе
        byte[] hash = digest.digest(secretKey.getBytes());
        Random rand = new Random(Arrays.hashCode(hash));
        Collections.shuffle(pixelIndexes, rand);

        return pixelIndexes;
    }
}
