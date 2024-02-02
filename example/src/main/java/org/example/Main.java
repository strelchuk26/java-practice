package org.example;

import org.mariadb.jdbc.Connection;
import org.mariadb.jdbc.Statement;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import static java.util.Arrays.sort;

public class Main {

    static String userName="root";
    static String password="";
    static String host = "localhost";
    static String port = "3306";
    static String database = "java_spu111";
    static String strConn ="jdbc:mariadb://"+host+":"+port+"/"+database;

    public static void main(String[] args)
    {
        //createCategory(strConn, userName, password);
        //insertCategory(strConn, userName, password, new CategoryCreate("Одяг", "Для дорослих"));
        //var list = listCategories(strConn, userName, password);
        //for (var c : list) {
        //    System.out.println(c);
        //}

        showMenu();
    }

    private static void showMenu()
    {
        Scanner scanner = new Scanner(System.in);

        System.out.println("1. Вивести список категорій");
        System.out.println("2. Додати категорію");
        System.out.println("3. Редагуваня категорію");
        System.out.println("4. Видалення категорії");
        System.out.println("5. Вихід");

        System.out.print("\nВиберіть номер операції: ");
        int operation = scanner.nextInt();

        switch (operation)
        {
            case 1:
                var list = listCategories(strConn, userName, password);
                for (var c : list) {
                    System.out.println(c);
                }
                break;
            case 2:
                Scanner newScanner = new Scanner(System.in);

                System.out.print("Введіть назву категорії: ");
                String newName = newScanner.nextLine();

                System.out.print("Введіть опис категорії: ");
                String newDescription = newScanner.nextLine();

                CategoryCreate newCategory = new CategoryCreate(newName, newDescription);

                insertCategory(strConn, userName, password, newCategory);
                break;
            case 3:
                Scanner newScanner1 = new Scanner(System.in);
                System.out.print("Введіть id категорії: ");
                int id = scanner.nextInt();

                System.out.print("Введіть назву категорії: ");
                String updatedName = newScanner1.nextLine();

                System.out.print("Введіть опис категорії: ");
                String updatedDescription = newScanner1.nextLine();

                CategoryItem updatedCategory = new CategoryItem(id, updatedName, updatedDescription);
                updateCategory(strConn, userName, password, updatedCategory);
                break;
            case 4:
                Scanner newScanner2 = new Scanner(System.in);
                System.out.print("Введіть id категорії: ");
                int idToDelete = newScanner2.nextInt();

                deleteCategory(strConn, userName, password, idToDelete);
                break;
            case 5:
                System.exit(0);
                break;
            default:
                System.out.println("Неправильна операція.");
                return;
        }
    }

    private static List<CategoryItem> listCategories(String strConn, String userName, String password)
    {
        try(Connection conn = (Connection) DriverManager.getConnection(strConn,userName,password)) {
            Statement statement = conn.createStatement();
            // SQL query to select all categories
            String selectQuery = "SELECT * FROM categories";
            // Create a PreparedStatement
            PreparedStatement preparedStatement = conn.prepareStatement(selectQuery);

            // Execute the SELECT query
            ResultSet resultSet = preparedStatement.executeQuery();

            List<CategoryItem> list = new ArrayList<>();
            // Process the ResultSet
            while (resultSet.next()) {
                CategoryItem category = new CategoryItem();
                // Retrieve data from the current row
                category.setId(resultSet.getInt("id"));
                category.setName(resultSet.getString("name"));
                category.setDescription(resultSet.getString("description"));
                list.add(category);
            }
            // Close the resources
            resultSet.close();
            preparedStatement.close();
            return list;
        }
        catch(Exception ex) {
            System.out.println("Помилка читання списку даних: "+ex.getMessage());
            return null;
        }
    }

    private static void createCategory(String strConn, String userName, String password) {
        try(Connection conn = (Connection) DriverManager.getConnection(strConn,userName,password)) {
            Statement statement = conn.createStatement();

            String createTableSQL = "CREATE TABLE IF NOT EXISTS categories ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(255) NOT NULL,"
                    + "description TEXT"
                    + ")";

            statement.execute(createTableSQL);

            statement.close();

            System.out.println("В БД створено таблицю categories");
        }
        catch(Exception ex) {
            System.out.println("Error connection: "+ex.getMessage());
        }
    }

    private static void insertCategory(String strConn, String userName, String password,
                                       CategoryCreate categoryCreate) {
        try(Connection conn = (Connection) DriverManager.getConnection(strConn,userName,password)) {
            Statement statement = conn.createStatement();

            String insertQuery = "INSERT INTO categories (name, description) VALUES (?, ?)";
            // Create a PreparedStatement
            PreparedStatement preparedStatement = conn.prepareStatement(insertQuery);

            // Set values for the placeholders
            preparedStatement.setString(1, categoryCreate.getName());
            preparedStatement.setString(2, categoryCreate.getDescription());

            // Execute the SQL query
            int rowsAffected = preparedStatement.executeUpdate();

            // Close the resources
            preparedStatement.close();
            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("Category inserted successfully.");
        }
        catch(Exception ex) {
            System.out.println("Помилка створення категорії: "+ex.getMessage());
        }
    }

    private static void updateCategory(String strConn, String userName, String password,
                                       CategoryItem categoryUpdate) {
        try(Connection conn = (Connection) DriverManager.getConnection(strConn, userName, password)) {
            String updateQuery = "UPDATE categories SET name = ?, description = ? WHERE id = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(updateQuery);

            preparedStatement.setString(1, categoryUpdate.getName());
            preparedStatement.setString(2, categoryUpdate.getDescription());
            preparedStatement.setInt(3, categoryUpdate.getId());

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("Category updated successfully.");
        } catch(Exception ex) {
            System.out.println("Помилка редагування категорії: " + ex.getMessage());
        }
    }

    private static void deleteCategory(String strConn, String userName, String password, int categoryId) {
        try(Connection conn = (Connection) DriverManager.getConnection(strConn, userName, password)) {
            String deleteQuery = "DELETE FROM categories WHERE id = ?";

            PreparedStatement preparedStatement = conn.prepareStatement(deleteQuery);

            preparedStatement.setInt(1, categoryId);

            int rowsAffected = preparedStatement.executeUpdate();

            preparedStatement.close();
            System.out.println("Rows affected: " + rowsAffected);

            if (rowsAffected > 0) {
                System.out.println("Category deleted successfully.");
            } else {
                System.out.println("Category with ID " + categoryId + " not found.");
            }
        } catch(Exception ex) {
            System.out.println("Помилка видалення категорії: " + ex.getMessage());
        }
    }

    private static void classes() {
        Person[] list = {
                new Person("Петро", "Підкаблучник")
        };

        for (var item : list) {
            System.out.println(item);
        }

        Person ivan = new Person();

        ivan.setFirstName("Іван");
        ivan.setLastName("Мельник");
    }

    private static void arrays() {
        int n = 10;
        int []arr = new int[n];

        for (int i = 0; i < n; i++) {
            arr[i] = getRandom(-99, 100);
        }

        for (int item : arr) {
            System.out.printf("%d\t", item);
        }

        int count = 0;
        for (int item : arr) {
            if (item > 0)
                count++;
        }
        System.out.println("\n Кількість додатніх чисел " + count);

        sort(arr);
        for (int item : arr) {
            System.out.printf("%d\t", item);
        }
    }

    private static int getRandom(int min, int max) {
        // Create an instance of the Random class
        Random random = new Random();
        // Generate a random value between min (inclusive) and max (exclusive)
        return random.nextInt(max - min) + min;
    }
}