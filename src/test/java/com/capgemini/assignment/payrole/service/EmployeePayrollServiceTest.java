package com.capgemini.assignment.payrole.service;

import org.junit.Assert;
import org.junit.Test;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;

import com.capgemini.assignment.payrole.dao.EmployeePayrollDBService;
import com.capgemini.assignment.payrole.exception.EmployeePayrollException;
import com.capgemini.assignment.payrole.model.EmployeePayrollData;
import com.capgemini.assignment.payrole.service.EmployeePayrollService.IOService;

public class EmployeePayrollServiceTest {
    @Test
    public void givenPayrollData_whenWrittenToFile_shoouldMatchTheNumberOfEntriesWritten()
            throws EmployeePayrollException {
        EmployeePayrollData[] empArray = { new EmployeePayrollData(1, "Jeff Bezos", 100000.0),
                new EmployeePayrollData(2, "Bill Gates", 200000.0),
                new EmployeePayrollData(3, "Mark Zuckerberg", 300000.0) };
        EmployeePayrollService employeePayrollService = new EmployeePayrollService(Arrays.asList(empArray));
        employeePayrollService.writeEmployeeData(EmployeePayrollService.IOService.FILE_IO);
        long entries = employeePayrollService.countEntries(EmployeePayrollService.IOService.FILE_IO);
        Assert.assertEquals(3, entries);
    }

    @Test
    public void givenFile_whenEntriesCounted_shouldMatchTheNumberOfExpectedEntries() throws EmployeePayrollException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        long entries = employeePayrollService.countEntries(IOService.FILE_IO);
        Assert.assertEquals(3, entries);
    }

    @Test
    public void givenDBProperties_when_triedToConnect_shouldConnectSuccessfully() {
        try (Connection conn = EmployeePayrollDBService.getConnection()) {
            Assert.assertNotNull(conn);
            Assert.assertEquals("payroll_service", conn.getCatalog());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void givenEmployeePayrollDB_whenRetrieved_shouldMatchEmployeeCount() throws EmployeePayrollException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> readEmployeePayrollData = employeePayrollService
                .readEmployeePayrollData(IOService.DB_IO);
        Assert.assertEquals(3, readEmployeePayrollData.size());
    }

    @Test
    public void givenNewSalaryForEmployee_whenUpdated_shouldSyncWithDB() throws EmployeePayrollException {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        List<EmployeePayrollData> readEmployeePayrollData = employeePayrollService
                .readEmployeePayrollData(IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 300000.0);
        Assert.assertEquals(3, readEmployeePayrollData.size());
        boolean result = employeePayrollService.checkEmplyoeePayrollSyncWithDB("Terisa");
        Assert.assertTrue(result);

    }
}
