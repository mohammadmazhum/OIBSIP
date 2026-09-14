import java.util.HashMap;
import java.util.Map;

/**
 * Represents the bank as a whole: holds every account and handles
 * authentication and cross-account transfers.
 */
public class Bank {

    private final Map<String, Account> accounts = new HashMap<>();

    public void addAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    public boolean accountExists(String accountId) {
        return accounts.containsKey(accountId);
    }

    /**
     * Authenticates a user by account ID + PIN.
     * Returns the Account on success, or null on failure.
     */
    public Account authenticate(String accountId, String pin) {
        Account account = accounts.get(accountId);
        if (account != null && account.checkPin(pin)) {
            return account;
        }
        return null;
    }

    /**
     * Transfers funds from one account to another.
     * Returns true on success, false if the recipient doesn't exist or
     * the sender has insufficient funds.
     */
    public boolean transfer(Account from, String toAccountId, double amount) {
        Account to = accounts.get(toAccountId);
        if (to == null) {
            return false;
        }
        if (!from.withdraw(amount)) {
            return false;
        }
        to.deposit(amount);

        from.addTransaction(new Transaction("TRANSFER-OUT", amount,
                "To account " + toAccountId));
        to.addTransaction(new Transaction("TRANSFER-IN", amount,
                "From account " + from.getAccountId()));
        return true;
    }
}
