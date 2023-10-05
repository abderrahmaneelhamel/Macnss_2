package macnss.dao;

import macnss.model.Agent;
import macnss.model.Company;
import macnss.model.Patient;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserDAOImpl implements UserDAO {
    protected Connection connection; // Change access level to protected

    public UserDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Patient authenticatePatient(int matricule, String password) {
        String query = "SELECT * FROM patients WHERE matricule = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, matricule);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String hashedPassword = resultSet.getString("password");
                String birthDay = resultSet.getString("birth_date");
                String status = resultSet.getString("status");
                int days = resultSet.getInt("number_of_days");
                Double salary = resultSet.getDouble("salary");
                int companyId = resultSet.getInt("company_id");


                // Compare the provided password with the hashed password from the database
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return new Patient(id, name, email, hashedPassword, matricule, birthDay, days, salary, companyId, status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Authentication failed
    }

    @Override
    public Agent authenticateAgent(String email, String password) {
        String query = "SELECT * FROM agents WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String hashedPassword = resultSet.getString("password");

                // Compare the provided password with the hashed password from the database
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return new Agent(id, name, email, hashedPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Authentication failed
    }

    @Override
    public Company authenticateCompany(String email, String password) {
        String query = "SELECT * FROM Company WHERE email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String hashedPassword = resultSet.getString("password");

                // Compare the provided password with the hashed password from the database
                if (BCrypt.checkpw(password, hashedPassword)) {
                    return new Company(id, name, email, hashedPassword);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Authentication failed
    }

    @Override
    public boolean addAgent(Agent agent) {
        String sql = "INSERT INTO agents (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, agent.getName());
            preparedStatement.setString(2, agent.getEmail());

            // Hash the agent's password before storing it in the database
            String hashedPassword = BCrypt.hashpw(agent.getPassword(), BCrypt.gensalt());
            preparedStatement.setString(3, hashedPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public boolean addCompany(Company company) {
        String sql = "INSERT INTO company (name, email, password) VALUES (?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, company.getName());
            preparedStatement.setString(2, company.getEmail());

            // Hash the company's password before storing it in the database
            String hashedPassword = BCrypt.hashpw(company.getPassword(), BCrypt.gensalt());
            preparedStatement.setString(3, hashedPassword);

            int rowsAffected = preparedStatement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    @Override
    public Patient addPatient(Patient patient) {
        String sql = "INSERT INTO patients (name, email, password, matricule, number_of_days, company_id, status, salary, birth_date) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, patient.getName());
            preparedStatement.setString(2, patient.getEmail());

            // Hash the patient's password before storing it in the database
            String hashedPassword = BCrypt.hashpw(patient.getPassword(), BCrypt.gensalt());
            preparedStatement.setString(3, hashedPassword);

            preparedStatement.setInt(4, patient.getMatricule());
            preparedStatement.setInt(5, patient.getDays());
            preparedStatement.setInt(6, patient.getCompany_id());
            preparedStatement.setString(7, patient.getStatus());
            preparedStatement.setDouble(8, patient.getSalary());

            // Parse the birth date string and convert it to a SQL Date
            preparedStatement.setDate(9, java.sql.Date.valueOf(LocalDate.parse(patient.getBirthDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"))));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    System.out.println(userId);
                    patient.setId(userId);
                }
                return patient;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Patient getUserByMatricule(int matricule) {
        String sql = "SELECT * FROM patients WHERE matricule = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, matricule);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return new Patient(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("email"),
                        resultSet.getString("password"), resultSet.getInt("matricule"), resultSet.getString("birth_date"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
