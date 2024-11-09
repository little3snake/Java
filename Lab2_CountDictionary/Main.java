import Classes.CharactersCounter;

import java.io.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Input input-filename
            System.out.print("Введите имя входного файла: ");
            String inputFileName = scanner.nextLine();
            File inputFile = new File(inputFileName);
            // Check file existance
            if (!inputFile.exists()) {
                throw new FileNotFoundException("Файл с данным именем не найден.");
            }
            // Input output-filename
            System.out.print("Введите имя выходного файла: ");
            String outputFileName = scanner.nextLine();
            File outputFile = new File(outputFileName);
            CharactersCounter counter = new CharactersCounter(inputFile, outputFile);
            counter.countCharacters();
            System.out.println("Результаты сохранены в файл: " + outputFileName);

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Произошла ошибка при работе с файлами: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Произошла неизвестная ошибка");

        } finally {
            scanner.close();
        }
    }
}
