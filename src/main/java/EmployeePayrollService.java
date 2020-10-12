import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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

    private void writeEmployeeData() {
        System.out.println("Writing Employee Payroll Roster in Console\n" + employeePayrollDataList);
    }

    public static void main(String[] args) {
        ArrayList<EmployeePayrollData> employeePayrollData = new ArrayList<>();
        EmployeePayrollService employeePayrollService = new EmployeePayrollService();
        Scanner sc = new Scanner(System.in);
        employeePayrollService.readEmployeeData(sc);
        employeePayrollService.writeEmployeeData();
        sc.close();
    }
}
