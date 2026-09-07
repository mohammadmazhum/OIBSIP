import java.sql.*;

/**
 * Handles the SQLite connection and creates/seeds all required tables
 * the very first time the application is run.
 */
public class DBConnection {

    // The DB file will be created in the same folder the app is run from.
    private static final String URL = "jdbc:sqlite:reservation.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /** Creates tables if they don't exist yet and seeds sample data. */
    public static void initializeDatabase() {
        String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                "username TEXT PRIMARY KEY," +
                "password TEXT NOT NULL)";

        String trainsTable = "CREATE TABLE IF NOT EXISTS trains (" +
                "train_no TEXT PRIMARY KEY," +
                "train_name TEXT NOT NULL," +
                "source TEXT NOT NULL," +
                "destination TEXT NOT NULL)";

        String reservationsTable = "CREATE TABLE IF NOT EXISTS reservations (" +
                "pnr TEXT PRIMARY KEY," +
                "passenger_name TEXT NOT NULL," +
                "train_no TEXT NOT NULL," +
                "train_name TEXT NOT NULL," +
                "class_type TEXT NOT NULL," +
                "journey_date TEXT NOT NULL," +
                "source TEXT NOT NULL," +
                "destination TEXT NOT NULL," +
                "status TEXT NOT NULL DEFAULT 'BOOKED')";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(usersTable);
            stmt.execute(trainsTable);
            stmt.execute(reservationsTable);

            // Seed a default login (username: admin / password: admin123)
            stmt.execute("INSERT OR IGNORE INTO users (username, password) " +
                    "VALUES ('admin', 'admin123')");

            // Seed a few sample trains so the reservation form has data to look up
            stmt.execute("INSERT OR IGNORE INTO trains VALUES " +
                    "('12001','Shatabdi Express','New Delhi','Bhopal')");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES " +
                    "('12951','Mumbai Rajdhani','Mumbai','New Delhi')");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES " +
                    "('12628','Karnataka Express','Bangalore','New Delhi')");
            stmt.execute("INSERT OR IGNORE INTO trains VALUES " +
                    "('16022','Kaveri Express','Bangalore','Chennai')");

        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Database initialization failed: " + e.getMessage(),
                    "Fatal Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}
