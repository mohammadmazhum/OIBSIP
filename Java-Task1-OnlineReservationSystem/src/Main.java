import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Initialize the SQLite database and tables before showing any UI
        DBConnection.initializeDatabase();

        // Use the system look and feel for a native appearance
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
