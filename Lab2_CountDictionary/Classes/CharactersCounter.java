package Classes;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class CharactersCounter {
    private File input_file;
    private File output_file;

    public CharactersCounter(File input_file, File output_file) {
        this.input_file = input_file;
        this.output_file = output_file;
    }

    public File getInput_file() {
        return input_file;
    }
    public File getOutput_file() {
        return output_file;
    }
    public void setInput_file(File input_file) {
        this.input_file = input_file;
    }
    public void setOutput_file(File output_file) {
        this.output_file = output_file;
    }

    public void countCharacters() throws IOException {
        Map<Character, Integer> charCounter = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(input_file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (char c : line.toCharArray()) {
                    if (Character.isLetter(c) && ((c >= 'A') && (c <= 'Z') || (c >= 'a') && (c <= 'z'))) {
                        charCounter.put(c, charCounter.getOrDefault(c, 0) + 1);
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException("Ошибка при чтении входного файла");
        }
        
        try {
            writeCharCounter(charCounter);
        } catch (IOException e) {
            throw new IOException("Ошибка при записи частотного словаря в выходной файл");
        }
    }

    private void writeCharCounter(Map<Character, Integer> charCounter) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(output_file))) {
            for (Map.Entry<Character, Integer> entry : charCounter.entrySet()) {
                writer.write(entry.getKey() + ": " + entry.getValue());
                writer.newLine();
            }
        }
    }
}
