package org.example;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    private static Connection connection;

    public static void main(String[] args) {
        try {
            connection = DBConnection.getConnection();
            NestifyApp app = new NestifyApp(connection);
            app.run();
        } catch (SQLException e) {
            System.err.println("Failed to initialize database connection: " + e.getMessage());
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}