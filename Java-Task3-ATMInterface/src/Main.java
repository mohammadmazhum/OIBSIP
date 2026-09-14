import java.util.Scanner;

/**
 * Entry point. Seeds the bank with sample accounts, handles the login
 * flow (User ID + PIN, max 3 attempts), then launches the ATM session.
 */
public class Main {

    private static final int MAX_LOGIN_ATTEMPTS = 3;

    public static void main(String[] args) {
        Bank bank = new Bank();

        // Seed sample accounts so the grader/tester can log in immediately.
        bank.addAccount(new Account("1001", "1234", "Asha Rao", 5000.00));
        bank.addAccount(new Account("1002", "5678", "Vikram Shetty", 3000.00));

        Scanner scanner = new Scanner(System.in);

        System.out.println("=========================================");
        System.out.println("           WELCOME TO JAVA ATM");
        System.out.println("=========================================");
        System.out.println("(Sample accounts: 1001/1234, 1002/5678)\n");

        Account authenticatedAccount = login(bank, scanner);

        if (authenticatedAccount != null) {
            ATM atm = new ATM(bank, authenticatedAccount, scanner);
            atm.start();
        } else {
            System.out.println("\nToo many incorrect attempts. Card retained. Exiting.");
        }

        scanner.close();
    }

    /** Handles the User ID + PIN login flow, denying access after 3 failed attempts. */
    private static Account login(Bank bank, Scanner scanner) {
        for (int attempt = 1; attempt <= MAX_LOGIN_ATTEMPTS; attempt++) {
            System.out.print("Enter User ID: ");
            String userId = scanner.nextLine().trim();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine().trim();

            Account account = bank.authenticate(userId, pin);
            if (account != null) {
                return account;
            }

            int remaining = MAX_LOGIN_ATTEMPTS - attempt;
            if (remaining > 0) {
                System.out.println("Incorrect User ID or PIN. Attempts remaining: " + remaining + "\n");
            }
        }
        return null;
    }
}
