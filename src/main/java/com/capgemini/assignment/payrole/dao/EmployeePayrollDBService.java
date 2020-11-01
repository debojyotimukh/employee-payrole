package com.capgemini.assignment.payrole.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
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
    private static EmployeePayrollDBService employeePayrollDBService;
    private PreparedStatement selectStatementCache = null;
    private PreparedStatement updateStatementCache = null;
    private PreparedStatement selectAllStatementCache;
    private PreparedStatement selectDateRangeStatementCache;

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

    private void prepareStatementForEmployeeData() throws DBException {
        String sqlUpdateSalary = "update employee_payroll set salary = ? where emp_name = ?";
        String sqlSelectByName = "select * from employee_payroll where emp_name = ?";
        String sqlSelectAll = "SELECT * FROM employee_payroll";
        String sqlSelectDateRange = "SELECT * FROM employee_payroll WHERE start_dt BETWEEN ? AND ?";
        try {
            Connection connection = EmployeePayrollDBService.getConnection();
            updateStatementCache = connection.prepareStatement(sqlUpdateSalary);
            selectStatementCache = connection.prepareStatement(sqlSelectByName);
            selectAllStatementCache = connection.prepareStatement(sqlSelectAll);
            selectDateRangeStatementCache = connection.prepareStatement(sqlSelectDateRange);

        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    /**
     * Read entire employee payroll data from database
     * 
     * @return List of employee payroll data
     * @throws DBException
     * 
     */
    public List<EmployeePayrollData> readData() throws DBException {
        List<EmployeePayrollData> employeePayrollDatas = null;
        if (selectAllStatementCache == null)
            prepareStatementForEmployeeData();
        try {
            ResultSet resultSet = selectAllStatementCache.executeQuery();
            employeePayrollDatas = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            throw new DBException("Failed to read: " + e.getMessage());
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
        if (selectStatementCache == null)
            prepareStatementForEmployeeData();
        try {
            selectStatementCache.setString(1, name);
            ResultSet resultSet = selectStatementCache.executeQuery();
            employeePayrollDatas = this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            throw new DBException("Failed to read: " + e.getMessage());
        }

        return employeePayrollDatas;
    }

    @SuppressWarnings("unused")
    private int updateEmployeeDataUsingStatement(String name, double salary) throws DBException {
        String sql = String.format("update employee_payroll set salary = %.2f where emp_name = '%s'", salary, name);
        try (Connection connection = EmployeePayrollDBService.getConnection()) {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new DBException(e.getMessage());
        }
    }

    private int updateEmployeeDataUsingPreparedStatement(String name, double salary) throws DBException {
        if (updateStatementCache == null)
            prepareStatementForEmployeeData();
        try {
            updateStatementCache.setDouble(1, salary);
            updateStatementCache.setString(2, name);
            return updateStatementCache.executeUpdate();
        } catch (SQLException e) {
            throw new DBException("Failed to update: " + e.getMessage());
        }
    }

    public int updateEmployeeData(String name, double salary) throws DBException {
        return updateEmployeeDataUsingPreparedStatement(name, salary);

    }

    public List<EmployeePayrollData> readEmployeeDataForDateRange(LocalDate startDate, LocalDate endDate)
            throws DBException {
        if (updateStatementCache == null)
            prepareStatementForEmployeeData();
        try {
            selectDateRangeStatementCache.setDate(1, Date.valueOf(startDate));
            selectDateRangeStatementCache.setDate(2, Date.valueOf(endDate));
            ResultSet resultSet = selectDateRangeStatementCache.executeQuery();
            return this.getEmployeePayrollData(resultSet);
        } catch (SQLException e) {
            throw new DBException("Failed to read: " + e.getMessage());
        }
    }

}
