package macnss.dao;

import macnss.model.Company;
import macnss.model.Patient;

import java.sql.*;
import java.time.LocalDate;

public class CompanyDAOImpl implements CompanyDAO{
    private final Connection connection;

    public CompanyDAOImpl(Connection connection) {
        this.connection = connection;
    }
    @Override
    public boolean addEmployee(Patient existingEmployee, Company company) {
        // Update the company_id for the existing employee
        String updateEmployeeSQL = "UPDATE patients SET company_id = ?, status = 'employed', salary = ? WHERE id = ?";
        try (PreparedStatement updateEmployeeStatement = connection.prepareStatement(updateEmployeeSQL)) {
            updateEmployeeStatement.setInt(1, company.getId());
            updateEmployeeStatement.setDouble(2, existingEmployee.getSalary());
            updateEmployeeStatement.setInt(3, existingEmployee.getId());

            int rowsAffected = updateEmployeeStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Insert a row into the salary_history table
                String insertSalaryHistorySQL = "INSERT INTO salary_history (employee_id, salary, date) VALUES (?, ?, NOW())";
                try (PreparedStatement insertSalaryHistoryStatement = connection.prepareStatement(insertSalaryHistorySQL)) {
                    insertSalaryHistoryStatement.setInt(1, existingEmployee.getId());
                    insertSalaryHistoryStatement.setDouble(2, existingEmployee.getSalary());

                    int historyRowsAffected = insertSalaryHistoryStatement.executeUpdate();

                    return historyRowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public Patient findEmployeeByMatriculeInCompany(int matricule,Company company) {
        // Search for an employee by matricule
        String findEmployeeSQL = "SELECT * FROM patients WHERE matricule = ? AND  company_id = ?";
        try (PreparedStatement findEmployeeStatement = connection.prepareStatement(findEmployeeSQL)) {
            findEmployeeStatement.setInt(1, matricule);
            findEmployeeStatement.setInt(2, company.getId());
            ResultSet resultSet = findEmployeeStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                int company_id = resultSet.getInt("company_id");
                int days = resultSet.getInt("number_of_days");
                String status = resultSet.getString("status");
                Double salary = resultSet.getDouble("salary");
                String BirthDate = resultSet.getString("birth_date");

                return new Patient(id, name, email, password, matricule, BirthDate, days, salary, company_id, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public Patient findEmployeeByMatricule(int matricule) {
        // Search for an employee by matricule
        String findEmployeeSQL = "SELECT * FROM patients WHERE matricule = ? AND status = 'unemployed'";
        try (PreparedStatement findEmployeeStatement = connection.prepareStatement(findEmployeeSQL)) {
            findEmployeeStatement.setInt(1, matricule);
            ResultSet resultSet = findEmployeeStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                int company_id = resultSet.getInt("company_id");
                int days = resultSet.getInt("number_of_days");
                String status = resultSet.getString("status");
                Double salary = resultSet.getDouble("salary");
                String BirthDate = resultSet.getString("birth_date");


                return new Patient(id, name, email, password, matricule, BirthDate, days, salary, company_id, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public boolean removeEmployee(Patient employee, Company company) {
        String removePatientSQL = "UPDATE patients SET company_id = null , status = 'unemployed'  WHERE id = ?";
        try (PreparedStatement removePatientStatement = connection.prepareStatement(removePatientSQL)) {
            removePatientStatement.setInt(1, employee.getId());
            int rowsAffected = removePatientStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @Override
    public boolean updateEmployeeSalary(Patient employee) {
        String updateSalarySQL = "UPDATE patients SET salary = ? WHERE id = ?";
        String checkIfRecordExistsSQL = "SELECT COUNT(*) FROM salary_history WHERE employee_id = ? AND " +
                "EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ?";
        String insertSalaryHistorySQL = "INSERT INTO salary_history (employee_id, salary, date) VALUES (?, ?, ?)";

        try (PreparedStatement updateSalaryStatement = connection.prepareStatement(updateSalarySQL);
             PreparedStatement checkIfRecordExistsStatement = connection.prepareStatement(checkIfRecordExistsSQL);
             PreparedStatement insertSalaryHistoryStatement = connection.prepareStatement(insertSalaryHistorySQL)) {

            // Set the new salary in the 'patients' table
            updateSalaryStatement.setDouble(1, employee.getSalary());
            updateSalaryStatement.setInt(2, employee.getId());

            int rowsAffected = updateSalaryStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Extract year and month components from the current date
                LocalDate currentDate = LocalDate.now();
                int currentYear = currentDate.getYear();
                int currentMonth = currentDate.getMonthValue();

                // Check if a record with the same employee_id, year, and month already exists in 'salary_history'
                checkIfRecordExistsStatement.setInt(1, employee.getId());
                checkIfRecordExistsStatement.setInt(2, currentYear);
                checkIfRecordExistsStatement.setInt(3, currentMonth);
                ResultSet resultSet = checkIfRecordExistsStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // If the record exists, update it
                    String updateExistingRecordSQL = "UPDATE salary_history SET salary = ? " +
                            "WHERE employee_id = ? AND EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ?";
                    PreparedStatement updateExistingRecordStatement = connection.prepareStatement(updateExistingRecordSQL);
                    updateExistingRecordStatement.setDouble(1, employee.getSalary());
                    updateExistingRecordStatement.setInt(2, employee.getId());
                    updateExistingRecordStatement.setInt(3, currentYear);
                    updateExistingRecordStatement.setInt(4, currentMonth);
                    updateExistingRecordStatement.executeUpdate();
                } else {
                    // If the record doesn't exist, insert a new one
                    insertSalaryHistoryStatement.setInt(1, employee.getId());
                    insertSalaryHistoryStatement.setDouble(2, employee.getSalary());
                    insertSalaryHistoryStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                    insertSalaryHistoryStatement.executeUpdate();
                }

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateEmployeeWorkDays(Patient employee) {
        String updateWorkDaysSQL = "UPDATE patients SET number_of_days = number_of_days + ? WHERE id = ?";
        String checkIfRecordExistsSQL = "SELECT COUNT(*) FROM salary_history WHERE employee_id = ? AND " +
                "EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ?";
        String insertSalaryHistorySQL = "INSERT INTO salary_history (employee_id, salary, date) VALUES (?, ?, ?)";

        try (PreparedStatement updateWorkDaysStatement = connection.prepareStatement(updateWorkDaysSQL);
             PreparedStatement checkIfRecordExistsStatement = connection.prepareStatement(checkIfRecordExistsSQL);
             PreparedStatement insertSalaryHistoryStatement = connection.prepareStatement(insertSalaryHistorySQL)) {

            // Update the number of workdays in the 'patients' table
            updateWorkDaysStatement.setInt(1, employee.getDays());
            updateWorkDaysStatement.setInt(2, employee.getId());

            int rowsAffected = updateWorkDaysStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Extract year and month components from the current date
                LocalDate currentDate = LocalDate.now();
                int currentYear = currentDate.getYear();
                int currentMonth = currentDate.getMonthValue();

                // Check if a record with the same employee_id, year, and month already exists in 'salary_history'
                checkIfRecordExistsStatement.setInt(1, employee.getId());
                checkIfRecordExistsStatement.setInt(2, currentYear);
                checkIfRecordExistsStatement.setInt(3, currentMonth);
                ResultSet resultSet = checkIfRecordExistsStatement.executeQuery();

                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    // If the record exists, update it
                    String updateExistingRecordSQL = "UPDATE salary_history SET salary = ? " +
                            "WHERE employee_id = ? AND EXTRACT(YEAR FROM date) = ? AND EXTRACT(MONTH FROM date) = ?";
                    PreparedStatement updateExistingRecordStatement = connection.prepareStatement(updateExistingRecordSQL);

                    // Calculate the total salary change based on the difference in workdays
                    double totalSalaryChange = (employee.getDays() - 26) * (employee.getSalary() / 26);
                    updateExistingRecordStatement.setDouble(1, employee.getSalary() + totalSalaryChange);
                    updateExistingRecordStatement.setInt(2, employee.getId());
                    updateExistingRecordStatement.setInt(3, currentYear);
                    updateExistingRecordStatement.setInt(4, currentMonth);
                    updateExistingRecordStatement.executeUpdate();
                } else {
                    // If the record doesn't exist, insert a new one
                    insertSalaryHistoryStatement.setInt(1, employee.getId());

                    // Calculate the total salary change based on the difference in workdays
                    double totalSalaryChange = (employee.getDays() - 26) * (employee.getSalary() / 26);
                    insertSalaryHistoryStatement.setDouble(2, employee.getSalary() + totalSalaryChange);
                    insertSalaryHistoryStatement.setDate(3, new java.sql.Date(System.currentTimeMillis()));
                    insertSalaryHistoryStatement.executeUpdate();
                }

                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateRetirementStatusAndSalary(int employeeId, double retirementSalary) {
        String updateSQL = "UPDATE patients SET status = ?, company_id = null, salary = ? WHERE id = ?";
        try (PreparedStatement updateStatement = connection.prepareStatement(updateSQL)) {
            updateStatement.setString(1, "Retired");
            updateStatement.setDouble(2, retirementSalary);
            updateStatement.setInt(3, employeeId);

            int rowsAffected = updateStatement.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public double calculateAverageSalary(int employeeId) {
        String querySQL = "SELECT salary FROM salary_history WHERE employee_id = ? ORDER BY date DESC LIMIT 96";

        try (PreparedStatement queryStatement = connection.prepareStatement(querySQL)) {
            queryStatement.setInt(1, employeeId);

            ResultSet resultSet = queryStatement.executeQuery();

            double totalSalary = 0.0;
            int count = 0;

            while (resultSet.next()) {
                double salary = resultSet.getDouble("salary");
                totalSalary += salary;
                count++;
            }

            // Calculate the average salary only if there is data
            if (count > 0) {
                return totalSalary / count;
            } else {
                return 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0; // Default average salary if there's an error or no data is found
    }
}
