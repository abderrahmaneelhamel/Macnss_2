package macnss.service;

import macnss.db.DatabaseConnection;
import macnss.model.Admin;
import macnss.model.Agent;
import macnss.model.Company;
import macnss.model.Patient;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Scanner;
import macnss.util.tools;

public class MaCNSSService {
    private static MaCNSSService instance;
    private final AuthenticationService authService;
    private final AgentService agentService;
    private final PatientService patientService;
    private final AdminService adminService;
    private final CompanyService companyService;
    private final FileService fileService;
    private final Connection connection;

    private MaCNSSService() throws SQLException {
        connection = DatabaseConnection.getInstance().getConnection();
        authService = new AuthenticationService(connection);
        agentService = new AgentService(connection);
        patientService = new PatientService(connection);
        adminService = new AdminService(connection);
        companyService = new CompanyService(connection);
        fileService = new FileService(connection);
    }

    public static MaCNSSService getInstance() throws SQLException {
        if (instance == null) {
            instance = new MaCNSSService();
        }
        return instance;
    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void start() {
//        tools.insertSalaryHistory(connection);
        Scanner scanner = new Scanner(System.in);

        System.out.println("************************************************************");
        System.out.println("                  Welcome to MaCNSS                         ");
        System.out.println("                                                            ");
        System.out.println("  Please select your role:                                  ");
        System.out.println("  1. Admin                                                  ");
        System.out.println("  2. Agent                                                  ");
        System.out.println("  3. Patient                                                ");
        System.out.println("  4. Company                                                ");
        System.out.println("                                                            ");
        System.out.println("************************************************************");
        System.out.print("Enter your choice: ");

            int choice = tools.tryParse(scanner.nextLine());
            while(choice < 1 || choice > 4){
                System.out.print("Invalid, Enter your choice again: ");
                choice = tools.tryParse(scanner.nextLine());
            }

            switch (choice) {
                case 1 -> {
                    Admin admin = authService.adminAuth(scanner);
                    if (admin != null) {
                        adminService.displayMenu(admin);
                    }
                }
                case 2 -> {
                    Agent agent = authService.agentAuth(scanner);
                    if (agent != null) {
                        agentService.showMenu(agent);
                    }
                }
                case 3 -> {
                    Patient patient = authService.patientAuth(scanner);
                    if (patient != null) {
                        patientService.showMenu(patient, fileService);
                    }
                }
                case 4 -> {
                    Company company = authService.companyAuth(scanner);
                    if (company != null) {
                        companyService.showMenu(company);
                    }
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        scanner.close();
        closeConnection();
    }
}
