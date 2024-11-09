import Classes.Person;
import Exceptions.InvalidInputException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Input FIO
            System.out.print("Введите ФИО: ");
            String full_name = scanner.nextLine().trim(); // trim() - delete " " in start/end of the string
            String[] name_in_parts = full_name.split("\\s+");// split string " "
            if (name_in_parts.length < 2) {
                throw new InvalidInputException("Неправильно введенное ФИО. Пожалуйста, введите полное ФИО (3 слова или 2, если нет отчества).");
            }
            String lastName = name_in_parts[0];
            String firstName = name_in_parts[1];
            String patronymic;
            if (name_in_parts.length == 3) {
                patronymic = name_in_parts[2];
            } else {
                patronymic = "";
            }
            // Input birth date
            System.out.print("Введите дату рождения (в формате ДД.ММ.ГГГГ): ");
            String birthDateString = scanner.nextLine();
            LocalDate birthDate;

            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
                birthDate = LocalDate.parse(birthDateString, formatter);
            } catch (InvalidInputException e) {
                throw new InvalidInputException("Hеверный формат даты. Используйте ДД.ММ.ГГГГ.");
            }

            Person person = new Person(lastName, firstName, patronymic, birthDate);
            person.printInfo();

        } catch (InvalidInputException e) {
            System.out.println("Ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Неизвестная ошибка");
        } finally {
            scanner.close();
        }
    }
}

