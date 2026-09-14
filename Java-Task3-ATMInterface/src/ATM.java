import java.util.Scanner;

/**
 * Represents the ATM machine's interactive session for a single
 * authenticated account: displays the menu and dispatches to the
 * corresponding operation.
 */
public class ATM {

    private final Bank bank;
    private final Account account;
    private final Scanner scanner;

    public ATM(Bank bank, Account account, Scanner scanner) {
        this.bank = bank;
        this.account = account;
        this.scanner = scanner;
    }

    /** Runs the main menu loop until the user chooses to quit. */
    public void start() {
        System.out.println("\nWelcome, " + account.getHolderName() + "!");

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showTransactionHistory();
                    break;
                case "2":
                    handleWithdraw();
                    break;
                case "3":
                    handleDeposit();
                    break;
                case "4":
                    handleTransfer();
                    break;
                case "5":
                    running = false;
                    System.out.println("\nThank you for using our ATM. Goodbye, " +
                            account.getHolderName() + "!");
                    break;
                default:
                    System.out.println("Invalid option. Please choose 1-5.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\n----- MAIN MENU -----");
        System.out.println("Balance: Rs. " + String.format("%.2f", account.getBalance()));
        System.out.println("1. Transaction History");
        System.out.println("2. Withdraw");
        System.out.println("3. Deposit");
        System.out.println("4. Transfer");
        System.out.println("5. Quit");
        System.out.print("Choose an option: ");
    }

    private void showTransactionHistory() {
        System.out.println("\n----- TRANSACTION HISTORY -----");
        if (account.getHistory().isEmpty()) {
            System.out.println("No transactions yet.");
        } else {
            for (Transaction t : account.getHistory()) {
                System.out.println(t);
            }
        }
    }

    private void handleWithdraw() {
        double amount = readPositiveAmount("Enter amount to withdraw: ");
        if (amount <= 0) return;

        if (account.withdraw(amount)) {
            account.addTransaction(new Transaction("WITHDRAW", amount, "Cash withdrawal"));
            System.out.println("Withdrawal successful. New balance: Rs. " +
                    String.format("%.2f", account.getBalance()));
        } else {
            System.out.println("Insufficient Funds. Current balance: Rs. " +
                    String.format("%.2f", account.getBalance()));
        }
    }

    private void handleDeposit() {
        double amount = readPositiveAmount("Enter amount to deposit: ");
        if (amount <= 0) return;

        account.deposit(amount);
        account.addTransaction(new Transaction("DEPOSIT", amount, "Cash deposit"));
        System.out.println("Deposit successful. New balance: Rs. " +
                String.format("%.2f", account.getBalance()));
    }

    private void handleTransfer() {
        System.out.print("Enter recipient account ID: ");
        String recipientId = scanner.nextLine().trim();

        if (recipientId.equals(account.getAccountId())) {
            System.out.println("You cannot transfer to your own account.");
            return;
        }
        if (!bank.accountExists(recipientId)) {
            System.out.println("No account found with ID " + recipientId + ".");
            return;
        }

        double amount = readPositiveAmount("Enter amount to transfer: ");
        if (amount <= 0) return;

        boolean success = bank.transfer(account, recipientId, amount);
        if (success) {
            System.out.println("Transfer successful. New balance: Rs. " +
                    String.format("%.2f", account.getBalance()));
        } else {
            System.out.println("Insufficient Funds. Current balance: Rs. " +
                    String.format("%.2f", account.getBalance()));
        }
    }

    /** Reads and validates a positive numeric amount. Returns 0 if input was invalid (caller should treat as "no-op"). */
    private double readPositiveAmount(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            double amount = Double.parseDouble(input);
            if (amount <= 0) {
                System.out.println("Amount must be greater than zero.");
                return 0;
            }
            return amount;
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount entered.");
            return 0;
        }
    }
}
