package projects.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import projects.exception.DbException;

public class DbConnection {
    final static String HOST = "localhost";
    final static String PASSWORD = "projects";
    final static int PORT = 3306;
    final static String SCHEMA = "projects";
    final static String USER = "projects";

    public static Connection getConnection() {
        String uri = "jdbc:mysql://" + HOST + ":" + PORT + "/" + SCHEMA + "?user=" + USER + "&password=" + PASSWORD;

        try {
            System.out.println("Attempting to connect");
            Connection connection = DriverManager.getConnection(uri);
            System.out.println("Connection was successful at " + uri);
            return connection;
        } catch (SQLException exception) {
            System.out.println("Could not connect at " + uri);
            throw new DbException("Could not connect at " + uri);
        }
    }
}
