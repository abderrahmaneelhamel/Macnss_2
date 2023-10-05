package macnss.service;
import macnss.dao.UserDAOImpl;
import macnss.model.User;
import macnss.dao.RefundFileDAOImpl;
import java.sql.Connection;
import java.util.Scanner;
import macnss.util.tools;

public class AgentService {
    private final Connection connection;
    private final FileService FileService;
    private final AuthenticationService authenticationService;



    public AgentService(Connection connection) {
        this.connection = connection;
        this.FileService = new FileService(connection);
        this.authenticationService = new AuthenticationService(connection);
    }

    public void showMenu(User authenticatedUser) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nAgents Menu:");
            System.out.println("1. Add a New File");
            System.out.println("2. Edit a File");
            System.out.println("3. Add a new Patient");
            System.out.println("4. Add a new Company");
            System.out.println("5. Logout");

            System.out.print("Enter your choice: ");
                int choice = tools.tryParse(scanner.nextLine());
                while(choice < 1 || choice > 5){
                    System.out.print("Invalid, Enter your choice again: ");
                    choice = tools.tryParse(scanner.nextLine());
                }

                switch (choice) {

                    case 1 -> FileService.addFile(scanner);
                    case 2 -> FileService.updateFileStatus();
                    case 3 -> authenticationService.addPatient(scanner);
                    case 4 -> authenticationService.addCompany(scanner);
                    case 5 -> {
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
        }
    }

}

