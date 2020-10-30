package com.capgemini.assignment.payrole.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.capgemini.assignment.payrole.exception.EmployeePayrollException;
import com.capgemini.assignment.payrole.model.EmployeePayrollData;
import java.sql.Statement;
import java.time.LocalDate;

public class EmployeePayrollDBService {

    public List<EmployeePayrollData> readData() throws EmployeePayrollException {
        String sql = "SELECT * FROM employee_payroll";
        List<EmployeePayrollData> employeePayrollDatas = new ArrayList<>();
        try (Connection connection = JDBCUtil.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("emp_name");
                double salary = resultSet.getDouble("salary");
                LocalDate startDate = resultSet.getDate("start_dt").toLocalDate();
                employeePayrollDatas.add(new EmployeePayrollData(id, name, salary, startDate));
            }
        } catch (Exception e) {
            throw new EmployeePayrollException("Cannot read data from database!");
        }
        return employeePayrollDatas;
    }

}
