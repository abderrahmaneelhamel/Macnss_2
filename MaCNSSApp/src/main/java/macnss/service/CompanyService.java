package macnss.service;

import macnss.dao.CompanyDAOImpl;
import macnss.model.Company;
import macnss.model.Patient;

import java.sql.Connection;
import java.util.Scanner;
import macnss.util.tools;


public class CompanyService {
    private final Connection connection;
    private final CompanyDAOImpl CompanyDAOImpl;
    private final AuthenticationService authenticationService;


    public CompanyService(Connection connection) {
        this.connection = connection;
        this.CompanyDAOImpl = new CompanyDAOImpl(connection);
        this.authenticationService = new AuthenticationService(connection);
    }

    public void showMenu(Company company) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nCompany Menu:");
            System.out.println("1. Add an Employee");
            System.out.println("2. Remove an Employee");
            System.out.println("3. Update Employee Salary");
            System.out.println("4. Update Employee Work Days");
            System.out.println("5. Logout");

            System.out.print("Enter your choice: ");
            int choice = tools.tryParse(scanner.nextLine());
            while(choice < 1 || choice > 5){
                System.out.print("Invalid, Enter your choice again: ");
                choice = tools.tryParse(scanner.nextLine());
            }

                switch (choice) {
                    case 1 -> addEmployee(company);
                    case 2 -> removeEmployee(company);
                    case 3 -> updateEmployeeSalary(company);
                    case 4 -> updateEmployeeWorkDays(company);
                    case 5 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
        }
    }

    private void addEmployee(Company company) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Do you want to create a new employee or enter an existing one?");
        System.out.println("1. Create New Employee");
        System.out.println("2. Enter Existing Employee");

        System.out.print("Enter your choice: ");
        int choice = tools.tryParse(scanner.nextLine());
        while(choice < 1 || choice > 2){
            System.out.print("Invalid, Enter your choice again: ");
            choice = tools.tryParse(scanner.nextLine());
        }

        if (choice == 1) {
            // Create a new employee
            Patient newEmployee = authenticationService.addPatient(scanner);

            if (CompanyDAOImpl.addEmployee(newEmployee,company)) {
                System.out.println("entre the employee salary : ");
                Double salary = scanner.nextDouble();
                newEmployee.setSalary(salary);
                CompanyDAOImpl.updateEmployeeSalary(newEmployee);
                System.out.println("New employee created and registered to the company.");
            } else {
                System.out.println("Failed to add employee.");
            }
        } else {
            System.out.print("Enter Employee Matricule: ");
            int matricule = tools.tryParse(scanner.nextLine());
            while(matricule == 0){
                System.out.print("Invalid, Enter your matricule again: ");
                matricule = tools.tryParse(scanner.nextLine());
            }

            // Check if the employee exists
            Patient existingEmployee = CompanyDAOImpl.findEmployeeByMatricule(matricule);

            if (existingEmployee != null) {
                System.out.println("Employee found:");
                System.out.println("Name: " + existingEmployee.getName());
                System.out.println("Email: " + existingEmployee.getEmail());

                if (CompanyDAOImpl.addEmployee(existingEmployee,company)) {
                    System.out.println("entre the employee salary : ");
                    Double salary = scanner.nextDouble();
                    existingEmployee.setSalary(salary);
                    CompanyDAOImpl.updateEmployeeSalary(existingEmployee);
                    System.out.println("New employee registered to the company.");
                } else {
                    System.out.println("Failed to add employee.");
                }

            } else {
                System.out.println("Employee not found.");
            }

        }
    }

    private void removeEmployee(Company company) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the Matricule of the Employee to remove: ");
        int matricule = tools.tryParse(scanner.nextLine());
        while(matricule == 0){
            System.out.print("Invalid, Enter your matricule again: ");
            matricule = tools.tryParse(scanner.nextLine());
        }

        // Check if the employee with the specified matricule exists in the company
        Patient employeeToRemove = CompanyDAOImpl.findEmployeeByMatriculeInCompany(matricule,company);

        if (employeeToRemove != null) {
            // Employee found, confirm removal
            System.out.println("Employee found:");
            System.out.println("Matricule: " + employeeToRemove.getMatricule());
            System.out.println("Name: " + employeeToRemove.getName());
            System.out.println("Email: " + employeeToRemove.getEmail());
            System.out.println("Salary: " + employeeToRemove.getSalary());

            System.out.print("Are you sure you want to remove this employee? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                // Remove the employee from the company and update the database
                if (CompanyDAOImpl.removeEmployee(employeeToRemove,company)) {
                    employeeToRemove.setSalary(0.0);
                    CompanyDAOImpl.updateEmployeeSalary(employeeToRemove);
                    System.out.println("Employee removed successfully.");
                } else {
                    System.out.println("Failed to remove employee.");
                }
            } else {
                System.out.println("Removal canceled.");
            }
        } else {
            System.out.println("Employee not found with Matricule: " + matricule);
        }
    }


    private void updateEmployeeSalary(Company company) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the Matricule of the Employee to update salary: ");
        int matriculeToUpdate = tools.tryParse(scanner.nextLine());
        while(matriculeToUpdate == 0){
            System.out.print("Invalid, Enter your matricule again: ");
            matriculeToUpdate = tools.tryParse(scanner.nextLine());
        }

        // Check if the employee with the specified matricule exists in the company
        Patient employeeToUpdate = CompanyDAOImpl.findEmployeeByMatriculeInCompany(matriculeToUpdate,company);

        if (employeeToUpdate != null) {
            // Employee found, display current salary and prompt for the new salary
            System.out.println("Current Salary: " + employeeToUpdate.getSalary());
            System.out.print("Enter the new Salary: ");
            double newSalary = scanner.nextDouble();
            scanner.nextLine(); // Consume the newline character

            // Update the employee's salary and update the database
            employeeToUpdate.setSalary(newSalary);

            if (CompanyDAOImpl.updateEmployeeSalary(employeeToUpdate)) {
                System.out.println("Employee's salary updated successfully.");
            } else {
                System.out.println("Failed to update employee's salary.");
            }
        } else {
            System.out.println("Employee not found with Matricule: " + matriculeToUpdate);
        }
    }


    private void updateEmployeeWorkDays(Company company) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the Matricule of the Employee to update work days: ");
        int matriculeToUpdate = tools.tryParse(scanner.nextLine());
        while(matriculeToUpdate == 0){
            System.out.print("Invalid, Enter your matricule again: ");
            matriculeToUpdate = tools.tryParse(scanner.nextLine());
        }

        // Check if the employee with the specified matricule exists in the company
        Patient employeeToUpdate = CompanyDAOImpl.findEmployeeByMatriculeInCompany(matriculeToUpdate,company);

        if (employeeToUpdate != null) {
            System.out.println("Current Work Days: " + employeeToUpdate.getDays());

            // Prompt for the new Sick Days and Absent Days
            System.out.print("Enter the Sick Days of this month: ");
            int newSickDays = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            System.out.print("Enter the Absent Days of this month: ");
            int newAbsentDays = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            // Calculate the updated Work Days based on the new Sick Days and Absent Days
            int totalWorkDays = 26 - newSickDays - newAbsentDays;
            if (totalWorkDays < 0){
                totalWorkDays = 0;
            }
            employeeToUpdate.setDays(totalWorkDays);

            // Update the employee's information in the company and database
            if (CompanyDAOImpl.updateEmployeeWorkDays(employeeToUpdate)) {
                System.out.println("Employee's work days updated successfully.");
            } else {
                System.out.println("Failed to update employee's work days.");
            }
        } else {
            System.out.println("Employee not found with Matricule: " + matriculeToUpdate);
        }
    }
}
