package macnss.dao;
import macnss.model.Agent;
import macnss.model.Company;
import macnss.model.Patient;
import macnss.model.User;

import java.util.List;

public interface UserDAO {

    public Patient authenticatePatient(int matricule, String password);
    public Agent authenticateAgent(String email, String password);
    public Company authenticateCompany(String email, String password);

    public Patient addPatient(Patient patient);

    public boolean addCompany(Company company);

    public boolean addAgent(Agent agent);

}