import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single logged transaction (deposit, withdrawal, or transfer).
 */
public class Transaction {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String type;
    private final double amount;
    private final String description;
    private final LocalDateTime timestamp;

    public Transaction(String type, double amount, String description) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.timestamp = LocalDateTime.now();
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return String.format("[%s] %-10s Rs. %-10.2f %s",
                timestamp.format(FORMAT), type, amount, description);
    }
}
