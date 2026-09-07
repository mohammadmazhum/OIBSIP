import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class CancellationForm extends JFrame {

    private JTextField pnrField;
    private JButton fetchButton, cancelButton;
    private JTextArea detailsArea;
    private String currentPnr = null; // tracks the last successfully fetched PNR

    public CancellationForm() {
        setTitle("Cancel Reservation");
        setSize(450, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Enter PNR:"));
        pnrField = new JTextField(12);
        topPanel.add(pnrField);
        fetchButton = new JButton("Fetch");
        topPanel.add(fetchButton);

        detailsArea = new JTextArea(10, 30);
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        detailsArea.setBorder(BorderFactory.createTitledBorder("Booking Details"));

        cancelButton = new JButton("Confirm Cancellation");
        cancelButton.setEnabled(false);

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        panel.add(cancelButton, BorderLayout.SOUTH);

        add(panel);

        fetchButton.addActionListener(e -> fetchBooking());
        cancelButton.addActionListener(e -> confirmAndCancel());
    }

    private void fetchBooking() {
        String pnr = pnrField.getText().trim();

        if (pnr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a PNR number.",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!pnr.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "PNR must be numeric.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM reservations WHERE pnr = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                String details = String.format(
                        "PNR:          %s\n" +
                        "Passenger:    %s\n" +
                        "Train:        %s (%s)\n" +
                        "Route:        %s -> %s\n" +
                        "Class:        %s\n" +
                        "Journey Date: %s\n" +
                        "Status:       %s",
                        rs.getString("pnr"), rs.getString("passenger_name"),
                        rs.getString("train_name"), rs.getString("train_no"),
                        rs.getString("source"), rs.getString("destination"),
                        rs.getString("class_type"), rs.getString("journey_date"),
                        status);

                detailsArea.setText(details);

                if ("CANCELLED".equalsIgnoreCase(status)) {
                    cancelButton.setEnabled(false);
                    currentPnr = null;
                    JOptionPane.showMessageDialog(this,
                            "This booking has already been cancelled.",
                            "Already Cancelled", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    cancelButton.setEnabled(true);
                    currentPnr = pnr;
                }
            } else {
                detailsArea.setText("");
                cancelButton.setEnabled(false);
                currentPnr = null;
                JOptionPane.showMessageDialog(this,
                        "No booking found for PNR " + pnr + ".",
                        "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void confirmAndCancel() {
        if (currentPnr == null) return;

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel booking PNR " + currentPnr + "?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        // Requirement says "removes booking from the database on confirmation"
        String sql = "DELETE FROM reservations WHERE pnr = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, currentPnr);
            int rows = ps.executeUpdate();

            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Booking " + currentPnr + " has been cancelled and removed.",
                        "Cancelled", JOptionPane.INFORMATION_MESSAGE);
                detailsArea.setText("");
                pnrField.setText("");
                cancelButton.setEnabled(false);
                currentPnr = null;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
