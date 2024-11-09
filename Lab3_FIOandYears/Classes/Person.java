package Classes;

import java.time.LocalDate;
import java.time.Period;

public class Person {
    private final String name;
    private final String surname;
    private final String patronymic;
    private final LocalDate date_of_birth;
    private String gender; // maybe be changed
    private String age_for_String;

    public Person(String surname, String name, String patronymic, LocalDate date_of_birth) {
        this.surname = surname;
        this.name = name;
        this.patronymic = patronymic;
        this.date_of_birth = date_of_birth;
        this.gender = determineGender();
        this.age_for_String = formatAge();
    }

    public String getName() {
        return name;
    }
    public String getSurname() {
        return surname;
    }
    public String getPatronymic() {
        return patronymic;
    }
    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }
    public String getGender() {
        return gender;
    }
    public String getAge_for_String() {
        return age_for_String;
    }
    //for trans
    public void setGender(String gender) {
        this.gender = gender;
    }

    private int calculateAge(LocalDate date_of_birth) {
        return Period.between(this.date_of_birth, LocalDate.now()).getYears();
    }

    private String formatAge() {
        int age = calculateAge(date_of_birth);
        int lastDigit = age % 10;
        int lastTwoDigits = age % 100;
        if (lastTwoDigits >= 11 && lastTwoDigits <= 14) {
            return age + " лет";
        }
        switch (lastDigit) {
            case 1: return age + " год";
            case 2: case 3: case 4: return age + " года";
            default: return age + " лет";
        }
    }

    private String determineGender() {
        if (patronymic.endsWith("ич")) {
            return "МУЖСКОЙ";
        } else if (patronymic.endsWith("на")) {
            return "ЖЕНСКИЙ";
        } else {
            //if patronymic is absent
            return "ОПРЕДЕЛИТЬ_НЕ_УДАЛОСЬ";
        }
    }

    public String getInitials() {
        if (!patronymic.isEmpty()) {
            return surname + " " + name.charAt(0) + ". " + patronymic.charAt(0) + ".";
        } else {
            // if patronymic is absent
            return surname + " " + name.charAt(0) + ". ";
        }
    }

    public void printInfo () {
        System.out.println("Инициалы: " + getInitials());
        System.out.println("Пол: " + getGender());
        System.out.println("Возраст: " + getAge_for_String());
    }

}
