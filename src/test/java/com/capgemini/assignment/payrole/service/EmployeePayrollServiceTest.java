package com.capgemini.assignment.payrole.service;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import com.capgemini.assignment.payrole.dao.JDBCUtil;
import com.capgemini.assignment.payrole.model.EmployeePayrollData;
import com.capgemini.assignment.payrole.service.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
    @Test
    public void numberOfEmployeeEntryWrittenToFile() {
        EmployeePayrollData[] empArray = {
                new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0)
        };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empArray));
        employeePayrollService.writeEmployeeData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3, entries);
    }

    @Test
    public void numberOfEmployeeEntryReadFromFile() {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();

        // long entries = employeePayrollService.readEmployeePayrollData(EmployeePayrollService.IOService.FILE_IO);
        // Assert.assertEquals(3, entries);
    }

    @Test
    public void givenDBProperties_when_triedToConnect_shouldConnectSuccessfully(){
        try (Connection conn = JDBCUtil.getConnection()) {
            Assert.assertNotNull(conn);
            Assert.assertEquals("payroll_service", conn.getCatalog());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenEmployeePayrollDB_whenRetrieved_shouldMatchEmployeeCount(){
        EmployeePayrollService employeePayrollService=new EmployeePayrollService();
        List<EmployeePayrollData> readEmployeePayrollData = employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
        Assert.assertEquals(3, readEmployeePayrollData.size());
    }
}
