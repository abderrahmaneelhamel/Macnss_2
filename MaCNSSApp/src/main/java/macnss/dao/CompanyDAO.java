package macnss.dao;

import macnss.model.Company;
import macnss.model.Patient;

public interface CompanyDAO {
    boolean addEmployee(Patient existingEmployee, Company company);
    Patient findEmployeeByMatriculeInCompany(int matricule, Company company);
    Patient findEmployeeByMatricule(int matricule);
    boolean removeEmployee(Patient employee, Company company);
    boolean updateEmployeeSalary(Patient employee);
    boolean updateEmployeeWorkDays(Patient employee);
}
