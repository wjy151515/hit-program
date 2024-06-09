package com.demo.hospital;

import java.io.*;

public class PasswordManager {

    private static final String PASSWORD_FILE_PATH = "./src/main/java/com/demo/resources/password.txt";

    private static PasswordManager instance;

    private PasswordManager() {
    }

    public static PasswordManager getInstance() {
        if (instance == null) {
            instance = new PasswordManager();
        }
        return instance;
    }

    public String getPassword() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PASSWORD_FILE_PATH))) {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setPassword(String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PASSWORD_FILE_PATH))) {
            writer.write(password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
