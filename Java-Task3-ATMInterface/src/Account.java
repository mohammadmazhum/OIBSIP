import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single bank account. Fields are private (encapsulation) and
 * only exposed through getters/setters, per the OOP requirement.
 */
public class Account {

    private final String accountId;
    private final String pin;
    private final String holderName;
    private double balance;
    private final List<Transaction> history;

    public Account(String accountId, String pin, String holderName, double initialBalance) {
        this.accountId = accountId;
        this.pin = pin;
        this.holderName = holderName;
        this.balance = initialBalance;
        this.history = new ArrayList<>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getHolderName() {
        return holderName;
    }

    public double getBalance() {
        return balance;
    }

    /** Verifies a PIN attempt against the account's stored PIN. */
    public boolean checkPin(String attempt) {
        return pin.equals(attempt);
    }

    public List<Transaction> getHistory() {
        return history;
    }

    public void deposit(double amount) {
        balance += amount;
    }

    /** Returns true if the withdrawal succeeded, false if funds were insufficient. */
    public boolean withdraw(double amount) {
        if (amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public void addTransaction(Transaction t) {
        history.add(t);
    }
}
