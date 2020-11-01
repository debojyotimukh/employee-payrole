package com.capgemini.assignment.payrole.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.capgemini.assignment.payrole.dao.EmployeePayrollDBService;
import com.capgemini.assignment.payrole.exception.DBException;
import com.capgemini.assignment.payrole.exception.EmployeePayrollException;
import com.capgemini.assignment.payrole.fileio.EmployeePayrollFileIOService;
import com.capgemini.assignment.payrole.model.EmployeePayrollData;

public class EmployeePayrollService {
    public enum IOService {
        CONSOLE_IO, FILE_IO, DB_IO
    };

    private List<EmployeePayrollData> employeePayrollDataList;
    private EmployeePayrollDBService employeePayrollDBService;

    public EmployeePayrollService() {
        this.employeePayrollDataList = new ArrayList<>();
        this.employeePayrollDBService = EmployeePayrollDBService.getInstance();
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollDataList) {
        this();
        this.employeePayrollDataList = employeePayrollDataList;
    }

    /**
     * Get employee payroll data from local list
     * 
     * @param name name of the employee (case-sensitive)
     * @return
     */
    private EmployeePayrollData getEmployeePayrollData(String name) {
        return this.employeePayrollDataList.stream().filter(emp -> emp.getName().equals(name)).findFirst().orElse(null);
    }

    /**
     * Checks weather the local list and database are in sync or not, used for
     * testing
     * 
     * @param name name of the employee
     * @return
     */
    public boolean checkEmplyoeePayrollSyncWithDB(String name) {
        List<EmployeePayrollData> employeePayrollDataList = null;
        try {
            employeePayrollDataList = employeePayrollDBService.getEmployeePayrollData(name);
        } catch (DBException e) {
            e.printStackTrace();
        }
        return employeePayrollDataList.get(0).equals(this.getEmployeePayrollData(name));
    }

    public List<EmployeePayrollData> readEmployeePayrollData(IOService ioService) throws EmployeePayrollException {
        if (ioService.equals(IOService.CONSOLE_IO))
            return employeePayrollDataList;
        if (ioService.equals(IOService.DB_IO)) {
            try {
                this.employeePayrollDataList = employeePayrollDBService.readData();
            } catch (DBException e) {
                throw new EmployeePayrollException("Failed to update employee salary: " + e.getMessage());
            }
        }
        return this.employeePayrollDataList;
    }

    public void writeEmployeeData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            System.out.println("Writing Employee Payroll Roster in Console\n" + employeePayrollDataList);
        else if (ioService.equals(IOService.FILE_IO)) {
            EmployeePayrollFileIOService.writeData(employeePayrollDataList);
        }

    }

    public long countEntries(IOService ioService) throws EmployeePayrollException {
        if (ioService.equals(IOService.CONSOLE_IO))
            return employeePayrollDataList.size();
        else if (ioService.equals(IOService.FILE_IO))
            return EmployeePayrollFileIOService.countEntries();
        else if (ioService.equals(IOService.DB_IO)) {
            return readEmployeePayrollData(IOService.DB_IO).size();
        }
        return 0;
    }

    public void printData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            writeEmployeeData(IOService.CONSOLE_IO);
        else if (ioService.equals(IOService.FILE_IO))
            EmployeePayrollFileIOService.printData();
    }

    /**
     * Update an employee's salary in both local list and DB
     * 
     * @param name
     * @param salary
     * @throws EmployeePayrollException
     */
    public void updateEmployeeSalary(String name, double salary) throws EmployeePayrollException {
        // update in DB
        int result;
        try {
            result = employeePayrollDBService.updateEmployeeData(name, salary);
        } catch (DBException e) {
            throw new EmployeePayrollException("Failed to update employee salary: " + e.getMessage());
        }
        if (result == 0)
            return;
        // update locally
        EmployeePayrollData employeePayrollData = this.getEmployeePayrollData(name);
        if (employeePayrollData != null)
            employeePayrollData.setSalary(salary);

    }

    /**
     * Helper method to read employee data from console
     * 
     * @param consoleIn
     */
    private void readFromConsole(Scanner consoleIn) {
        System.out.print("Enter Employee ID: ");
        int id = consoleIn.nextInt();
        consoleIn.nextLine();
        System.out.print("Enter Employee Name: ");
        String name = consoleIn.next();
        consoleIn.nextLine();
        System.out.print("Enter Employee Salary: ");
        double salary = consoleIn.nextDouble();
        employeePayrollDataList.add(new EmployeePayrollData(id, name, salary));
    }

    public static void main(String[] args) {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        Scanner sc = new Scanner(System.in);
        employeePayrollService.readFromConsole(sc);
        employeePayrollService.writeEmployeeData(IOService.FILE_IO);
        employeePayrollService.printData(IOService.FILE_IO);
        sc.close();
    }

}
