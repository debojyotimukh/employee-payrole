package com.capgemini.assignment.payrole.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.capgemini.assignment.payrole.exception.DBException;
import com.capgemini.assignment.payrole.model.EmployeePayrollData;

/**
 * Singeleton object for Employee Database operations
 */
public class EmployeePayrollDBService {
    private PreparedStatement prepareStatement;
    private static EmployeePayrollDBService employeePayrollDBService;

    private EmployeePayrollDBService() {
    }

    public static EmployeePayrollDBService getInstance() {
        employeePayrollDBService = new EmployeePayrollDBService();
        return employeePayrollDBService;
    }

    /**
     * Reads db.properties file and establish connection to database
     * 
     * @return connection to the database
     * @throws DBException
     */
    public static Connection getConnection() throws DBException {

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

        } catch (IOException e1) {
            throw new DBException("Failed to read db.properties");
        } catch (SQLException e2) {
            throw new DBException(e2.getMessage());
        }
        return connection;
    }

    /**
     * Helper method to read get a list of employee payroll from result set
     * 
     * @param resultSet
     * @return
     * @throws DBException
     */
    private List<EmployeePayrollData> getEmployeePayrollData(ResultSet resultSet) throws DBException {
        List<EmployeePayrollData> employeePayrollDatas = new ArrayList<>();
        try {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("emp_name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start_dt").toLocalDate();
                employeePayrollDatas.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
        return employeePayrollDatas;
    }

    /**
     * Read entire employee payroll data from database
     * 
     * @return List of employee payroll data
     * @throws DBException
     * 
     */
    public List<EmployeePayrollData> readData() throws DBException {
        String sql = "SELECT * FROM employee_payroll";
        List<EmployeePayrollData> employeePayrollDatas = new ArrayList<>();
        try (Connection connection = EmployeePayrollDBService.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)) {

            employeePayrollDatas = getEmployeePayrollData(resultSet);
        } catch (Exception e) {
            throw new DBException(e.getMessage());
        }
        return employeePayrollDatas;
    }

    /**
     * Get a list of employee data from the employee name
     * 
     * @param name employee name to fetch
     * @return
     * @throws DBException
     * 
     */
    public List<EmployeePayrollData> getEmployeePayrollData(String name) throws DBException {
        List<EmployeePayrollData> employeePayrollDatas = null;

        String sql = String.format("select * from employee_payroll where emp_name = '%s'", name);
        try (Connection connection = EmployeePayrollDBService.getConnection()) {
            System.out.println("Connection is successfull!!! " + connection);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            employeePayrollDatas = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

        return employeePayrollDatas;
    }

    private int updateEmployeeDataUsingStatement(String name, double salary) throws DBException {
        String sql = String.format("update employee_payroll set salary = %.2f where emp_name = '%s'", salary, name);
        try (Connection connection = EmployeePayrollDBService.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    public int updateEmployeeData(String name, double salary) throws DBException {
        return updateEmployeeDataUsingStatement(name, salary);

    }

    private void prepareStatementForEmployeeData() throws DBException {
        try (Connection connection = EmployeePayrollDBService.getConnection()) {
            String sql = "SELECT * FROM employee_payroll WHERE emp_name= ?";
            prepareStatement = connection.prepareStatement(sql);

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }

    }

}
