package org.example;

import org.apache.commons.io.FileUtils;
import org.example.models.Category;
import org.example.models.Product;
import org.example.models.ProductImage;
import org.example.utils.HibernateUtil;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner in = new Scanner(System.in);
    private static SessionFactory sf = HibernateUtil.getSessionFactory();

    public static void main(String[] args) {
        Menu();
        //AddProduct();
        //ShowProducts();
    }

    private static  void Menu() {
        int action=0;
        Scanner in = new Scanner(System.in);
        do {
            System.out.println("0.Вихід");
            System.out.println("1.Додати продукт1");
            System.out.println("2.Показати всі");
            System.out.println("3.Редагувати");
            System.out.println("4.Видалити");
            System.out.print("->_");
            action=in.nextInt();
            switch(action) {
                case 1: {
                    AddProduct();
                    break;
                }
                case 2: {
                    ShowCategories();
                    break;
                }
                case 3: {
                    EditCategory();
                    break;
                }
                case 4: {
                    DeleteCategory();
                    break;
                }
            }
        }while(action!=0);
    }

    private static void ShowProducts() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()) {
            context.beginTransaction();
            List<Product> list = context.createQuery("from Product ", Product.class).getResultList();
            for (Product product : list) {
                System.out.println("Product: " + product);
            }
            context.getTransaction().commit();
        }
    }

    private static String savePhoto(String sourcePath) {
        String currentWorkingDirectory = System.getProperty("user.dir");
        String destinationPath = Paths.get(currentWorkingDirectory, "src", "main", "images").toString();

        String fileName = "";

        try {
            File sourceFile = new File(sourcePath);

            // Переконайтеся, що папка існує
            File destinationFolder = new File(destinationPath);
            if (!destinationFolder.exists()) {
                destinationFolder.mkdirs(); // Створюємо папку, якщо її немає
            }

            fileName = sourceFile.getName();
            File destinationFile = new File(destinationFolder, fileName);

            FileUtils.copyFile(sourceFile, destinationFile);
            System.out.println("Фотографія збережена успішно.");
        } catch (IOException e) {
            System.out.println("Помилка при збереженні фотографії: " + e.getMessage());
        }

        return fileName;
    }

    private static void AddProduct() {
        Scanner in = new Scanner(System.in);
        try(Session context = sf.openSession()) {
            context.beginTransaction();
            Product product = new Product();
            System.out.print("Вкажіть назву: ");
            product.setName(in.nextLine());
            System.out.print("Вкажіть опис: ");
            product.setDescription(in.nextLine());
            System.out.print("Вкажіть id категорії: ");
            Category category = new Category();
            category.setId(in.nextInt());
            product.setCategory(category);
            in.nextLine();
            System.out.print("Вкажіть адресу зображення: ");
            String sourcePath = in.nextLine();
            ProductImage productImage = new ProductImage();
            productImage.setImagePath(savePhoto(sourcePath));
            productImage.setProduct(product);
            System.out.print("Вкажіть ціну: ");
            product.setPrice(in.nextDouble());
            context.save(product);
            context.save(productImage);
            context.getTransaction().commit();
        }
    }

    private static void AddCategory() {
        Calendar calendar = Calendar.getInstance();
        try(Session context = sf.openSession()) {
            context.beginTransaction();
            Category category = new Category();
            System.out.print("Вкажіть назву: ");
            category.setName(in.nextLine());
            System.out.print("Вкажіть фото: ");
            category.setImage(in.nextLine());
            category.setDateCreated(calendar.getTime());
            context.save(category);
            context.getTransaction().commit();
        }
    }

    private static void EditCategory() {
        try (Session context = sf.openSession()) {
            context.beginTransaction();
            System.out.print("Введіть ID категорії для редагування: ");
            int categoryId = in.nextInt();
            in.nextLine();

            Category category = context.get(Category.class, categoryId);

            if (category != null) {
                System.out.print("Вкажіть нову назву: ");
                category.setName(in.nextLine());
                System.out.print("Вкажіть нове фото: ");
                category.setImage(in.nextLine());
                context.update(category);
                context.getTransaction().commit();
            } else {
                System.out.println("Категорія з вказаним ID не знайдена.");
            }
        }
    }

    private static void DeleteCategory() {
        try (Session context = sf.openSession()) {
            context.beginTransaction();
            System.out.print("Введіть ID категорії для видалення: ");
            int categoryId = in.nextInt();
            in.nextLine();

            Category category = context.get(Category.class, categoryId);

            if (category != null) {
                context.delete(category);
                context.getTransaction().commit();
            } else {
                System.out.println("Категорія з вказаним ID не знайдена.");
            }
        }
    }

    private static void ShowCategories() {
        SessionFactory sf = HibernateUtil.getSessionFactory();
        try(Session context = sf.openSession()) {
            context.beginTransaction();
            List<Category> list = context.createQuery("from Category ", Category.class).getResultList();
            for (Category category : list) {
                System.out.println("Category: " + category);
            }
            context.getTransaction().commit();
        }
    }
}