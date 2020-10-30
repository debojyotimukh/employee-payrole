package com.capgemini.assignment.payrole.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.capgemini.assignment.payrole.exception.EmployeePayrollException;

public class JDBCUtil {
    public static Connection getConnection() throws SQLException, EmployeePayrollException {

        Connection connection = null;
        try (FileInputStream fStream = new FileInputStream("db.properties")) {

            // load properties file
            Properties properties = new Properties();
            properties.load(fStream);

            // assign db parameters
            String url = properties.getProperty("url");
            String user = properties.getProperty("user");
            String password = properties.getProperty("password");

            // create a connection from the properties
            connection = DriverManager.getConnection(url, user, password);

        } catch (IOException e) {
            throw new EmployeePayrollException(e.getMessage());
        }
        return connection;
    }

    public static void main(String[] args) {
        try (Connection conn = JDBCUtil.getConnection()) {
            System.out.println(String.format("Connected to %s " + "database sucessfully!", conn.getCatalog()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
