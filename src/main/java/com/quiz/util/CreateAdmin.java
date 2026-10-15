package com.quiz.util;

import com.quiz.dao.UserDAO;
import com.quiz.model.User;

import java.io.Console;
import java.util.Scanner;

/**
 * Command-line tool that creates an ADMIN account with a BCrypt-hashed password.
 * Run it with:  mvn compile exec:java -Dexec.mainClass=com.quiz.util.CreateAdmin
 */
public class CreateAdmin {

    public static void main(String[] args) throws Exception {
        Console console = System.console();
        Scanner scanner = console == null ? new Scanner(System.in) : null;

        String name = ask(console, scanner, "Admin name: ", false);
        String email = ask(console, scanner, "Admin email: ", false).toLowerCase();
        String password = ask(console, scanner, "Admin password (min 8 chars): ", true);

        if (name.isEmpty() || !WebUtil.isValidEmail(email) || password.length() < 8 || password.length() > 72) {
            System.out.println("Invalid input. Name is required, email must be valid, password must be 8-72 characters.");
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.emailExists(email)) {
            System.out.println("An account with that email already exists.");
            return;
        }
        dao.create(new User(name, email, PasswordUtil.hash(password), "ADMIN"));
        System.out.println("Admin account created for " + email);
    }

    private static String ask(Console console, Scanner scanner, String prompt, boolean secret) {
        if (console != null) {
            if (secret) {
                char[] chars = console.readPassword(prompt);
                return chars == null ? "" : new String(chars);
            }
            String line = console.readLine(prompt);
            return line == null ? "" : line.trim();
        }
        System.out.print(prompt);
        return scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    }
}
