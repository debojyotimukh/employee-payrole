package com.capgemini.assignment.payrole.service;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.capgemini.assignment.payrole.dao.EmployeePayrollDBService;
import com.capgemini.assignment.payrole.exception.EmployeePayrollException;
import com.capgemini.assignment.payrole.model.EmployeePayrollData;
import com.capgemini.assignment.payrole.service.EmployeePayrollService.IOService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class EmployeePayrollServiceTest {
    public EmployeePayrollService employeePayrollService;

    @Before
    public void init() {
        employeePayrollService = new EmployeePayrollService();
    }

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
        List<EmployeePayrollData> readEmployeePayrollData = employeePayrollService
                .readEmployeePayrollData(IOService.DB_IO);
        Assert.assertEquals(3, readEmployeePayrollData.size());
    }

    @Test
    public void givenNewSalaryForEmployee_whenUpdated_shouldSyncWithDB() throws EmployeePayrollException {
        List<EmployeePayrollData> readEmployeePayrollData = employeePayrollService
                .readEmployeePayrollData(IOService.DB_IO);
        employeePayrollService.updateEmployeeSalary("Terisa", 300000.0);
        Assert.assertEquals(3, readEmployeePayrollData.size());
        boolean result = employeePayrollService.checkEmplyoeePayrollSyncWithDB("Terisa");
        Assert.assertTrue(result);

    }

    @Test
    public void givenDateRange_whenRetrieved_shouldMatchEmployeeCount() throws EmployeePayrollException {

        LocalDate startDate = LocalDate.of(2019, 01, 01);
        LocalDate endDate = LocalDate.now();
        List<EmployeePayrollData> employeePayrollData = employeePayrollService
                .readEmployeeDataForDateRange(IOService.DB_IO, startDate, endDate);
        Assert.assertEquals(2, employeePayrollData.size());
    }

    @Test
    public void givenPayrollData_whenAverageSalaryReturnedByGender_shouldReturnExpectedValue()
            throws EmployeePayrollException {

        Map<String, Double> averageSalaryByGender = employeePayrollService.readAverageSalaryByGender(IOService.DB_IO);
        Assert.assertEquals(250000.0, averageSalaryByGender.get("M"), 0.0);
        Assert.assertEquals(300000.0, averageSalaryByGender.get("F"), 0.0);

    }

    @Test@Ignore
    public void givenNewEmployee_whenAdded_shouldSyncWithDB() throws EmployeePayrollException {

        EmployeePayrollData newEmployee = new EmployeePayrollData(-1, "Monika", 500000.0, LocalDate.of(2020, 1, 24),
                'F');

        employeePayrollService.addEmployeeToPayroll(newEmployee);
        Assert.assertEquals(4, employeePayrollService.countEntries(IOService.DB_IO));
        Assert.assertTrue(employeePayrollService.checkEmplyoeePayrollSyncWithDB("Monika"));
    }

    @Test@Ignore
    public void givenEmployeeName_whenDeleted_shouldNotShowInPayroll_ButRemainInactiveInDB()
            throws EmployeePayrollException {
                employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
        String name="Monika";
        employeePayrollService.removeFromPayroll(name);
        employeePayrollService.readEmployeePayrollData(IOService.DB_IO);
        employeePayrollService.printData(IOService.CONSOLE_IO);
        Assert.assertEquals(3, employeePayrollService.countEntries(IOService.DB_IO));
        Assert.assertTrue(employeePayrollService.checkEmplyoeePayrollSyncWithDB(name));
    }
}
