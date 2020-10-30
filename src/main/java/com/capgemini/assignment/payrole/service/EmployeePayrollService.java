package com.capgemini.assignment.payrole.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.capgemini.assignment.payrole.fileio.EmployeePayrollFileIOService;
import com.capgemini.assignment.payrole.model.EmployeePayrollData;

public class EmployeePayrollService {
    public enum IOService {CONSOLE_IO, FILE_IO}

    List<EmployeePayrollData> employeePayrollDataList = new ArrayList<>();

    public EmployeePayrollService() {
    }

    public EmployeePayrollService(List<EmployeePayrollData> employeePayrollDataList) {
        this.employeePayrollDataList = employeePayrollDataList;
    }

    private void readEmployeeData(Scanner consoleIn) {
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

    public void writeEmployeeData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            System.out.println("Writing Employee Payroll Roster in Console\n" + employeePayrollDataList);
        else if (ioService.equals(IOService.FILE_IO)) {
            EmployeePayrollFileIOService.writeData(employeePayrollDataList);
        }

    }

    public long countEntries(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            return employeePayrollDataList.size();
        else if (ioService.equals(IOService.FILE_IO))
            return EmployeePayrollFileIOService.countEntries();
        return 0;
    }

    public void printData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            writeEmployeeData(IOService.CONSOLE_IO);
        else if (ioService.equals(IOService.FILE_IO))
            EmployeePayrollFileIOService.printData();
    }

    public long readEmployeePayrollData(IOService ioService) {
        if (ioService.equals(IOService.CONSOLE_IO))
            return employeePayrollDataList.size();
        else if (ioService.equals(IOService.FILE_IO))
            return EmployeePayrollFileIOService.countEntries();
        return 0;
    }

    public static void main(String[] args) {
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        Scanner sc = new Scanner(System.in);
        employeePayrollService.readEmployeeData(sc);
        employeePayrollService.writeEmployeeData(IOService.FILE_IO);
        employeePayrollService.printData(IOService.FILE_IO);
        sc.close();
    }
}
