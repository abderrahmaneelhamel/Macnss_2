package macnss.service;
import macnss.dao.CompanyDAOImpl;
import macnss.dao.UserDAOImpl;
import macnss.model.Patient;
import macnss.model.User;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import macnss.util.tools;


public class PatientService {
    private final Connection connection;
    private final UserDAOImpl UserDAOImpl;
    private final CompanyDAOImpl CompanyDAOImpl;



    public PatientService(Connection connection) {
        this.connection = connection;
        this.UserDAOImpl = new UserDAOImpl(connection);
        this.CompanyDAOImpl = new CompanyDAOImpl(connection);
    }

    public void showMenu(User authenticatedUser,FileService FileService) {
        Patient patient = (Patient) authenticatedUser;
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\nClient Menu:");
            System.out.println("1. check your files state");
            System.out.println("2. check your Retirement status");
            System.out.println("3. Logout");

            System.out.print("Enter your choice: ");
            int choice = tools.tryParse(scanner.nextLine());
            while(choice < 1 || choice > 3){
                System.out.print("Invalid, Enter your choice again: ");
                choice = tools.tryParse(scanner.nextLine());
            }
                switch (choice) {
                    case 1 -> FileService.checkClientFiles(patient);
                    case 2 -> checkRetirementStatus(patient);
                    case 3 -> {
                        return; // Logout
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
        }
    }

    public void checkRetirementStatus(Patient patient) {
        Double newSalary = checkEmployeeRetirementStatus(patient);
        if(newSalary != -1.0){
            patient.setSalary(newSalary);
            patient.setStatus("retired");
        }
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate =  LocalDate.parse(patient.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        int age = Period.between(birthDate, currentDate).getYears();
        if (patient.getRetirementStatus().equals("retired") && age >= 55) {
            System.out.println("Your  Retirement status Mr/Miss " + patient.getName() + " is " + patient.getRetirementStatus() + " and your Retirement salary is : " + patient.getSalary() + "$");
        }else if (patient.getRetirementStatus().equals("retired")) {
            System.out.println("Your  Retirement status Mr/Miss " + patient.getName() + " is " + patient.getRetirementStatus() + " and your Retirement salary is : " + patient.getSalary() + "$ , you will get it  when you retire at 55 years old");
        }else {
            System.out.println("Your status Mr/Miss " + patient.getName() + " is " + patient.getRetirementStatus());
        }
    }
    public Double checkEmployeeRetirementStatus(Patient employee) {
        // Check if the employee is at least 55 years old
        LocalDate currentDate = LocalDate.now();
        LocalDate birthDate =  LocalDate.parse(employee.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        int age = Period.between(birthDate, currentDate).getYears();
        if (age < 55) {
            return -1.0; // The employee is not eligible for retirement
        }

        // Calculate the total number of days worked by the employee
        int totalDaysWorked = employee.getDays();

        // Check if the employee has accumulated at least 1320 days of insurance
        if (totalDaysWorked >= 1320) {
            // Calculate the average salary (considering the last 96 months)
            double averageSalary = CompanyDAOImpl.calculateAverageSalary(employee.getId()); // Implement this method to calculate the average salary

            // Calculate the pension rate based on additional days worked beyond 3240 days
            int additionalDays = Math.max(totalDaysWorked - 3240, 0); // Additional days beyond 3240
            double additionalPensionRate = (double) additionalDays / 216; // 1% increase for every 216 days

            // Ensure the pension rate doesn't exceed 70%
            double pensionRate = Math.min(50 + additionalPensionRate, 70); // Maximum is 70%

            // Calculate the retirement salary
            double retirementSalary = (pensionRate / 100) * averageSalary;

            // Update the employee's retirement status and retirement salary in the database
            CompanyDAOImpl.updateRetirementStatusAndSalary(employee.getId(), retirementSalary);
            return retirementSalary;
        }
        return -1.0; // The employee is not eligible for retirement
    }
}

