package org.example;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введіть перше число: ");
        double num1 = scanner.nextDouble();

        System.out.print("Введіть друге число: ");
        double num2 = scanner.nextDouble();

        System.out.println("1. Додавання (+)");
        System.out.println("2. Віднімання (-)");
        System.out.println("3. Множення (*)");
        System.out.println("4. Ділення (/)");

        System.out.print("Виберіть номер операції: ");
        int operation = scanner.nextInt();

        double result = 0;

        switch (operation) {
            case 1:
                result = num1 + num2;
                break;
            case 2:
                result = num1 - num2;
                break;
            case 3:
                result = num1 * num2;
                break;
            case 4:
                if (num2 != 0) {
                    result = num1 / num2;
                } else {
                    System.out.println("Не можна ділити на 0.");
                    return;
                }
                break;
            default:
                System.out.println("Неправильна операція.");
                return;
        }

        System.out.println("Результат: " + result);
    }
}