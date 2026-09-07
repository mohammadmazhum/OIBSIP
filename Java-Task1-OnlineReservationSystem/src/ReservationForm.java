import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Random;

public class ReservationForm extends JFrame {

    private JTextField nameField, trainNoField, dateField;
    private JTextField trainNameField, sourceField, destField; // auto-populated, read-only
    private JComboBox<String> classCombo;
    private JButton lookupButton, bookButton;

    public ReservationForm() {
        setTitle("New Reservation");
        setSize(480, 420);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Passenger Name:"), gbc);
        nameField = new JTextField(18);
        gbc.gridx = 1; panel.add(nameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Train Number:"), gbc);
        trainNoField = new JTextField(18);
        gbc.gridx = 1; panel.add(trainNoField, gbc);
        row++;

        gbc.gridx = 1; gbc.gridy = row;
        lookupButton = new JButton("Lookup Train");
        panel.add(lookupButton, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Train Name:"), gbc);
        trainNameField = new JTextField(18);
        trainNameField.setEditable(false);
        gbc.gridx = 1; panel.add(trainNameField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Source Station:"), gbc);
        sourceField = new JTextField(18);
        sourceField.setEditable(false);
        gbc.gridx = 1; panel.add(sourceField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Destination Station:"), gbc);
        destField = new JTextField(18);
        destField.setEditable(false);
        gbc.gridx = 1; panel.add(destField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Class Type:"), gbc);
        classCombo = new JComboBox<>(new String[]{"Sleeper", "AC 3-Tier", "AC 2-Tier", "AC First Class", "General"});
        gbc.gridx = 1; panel.add(classCombo, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Date of Journey (yyyy-mm-dd):"), gbc);
        dateField = new JTextField(18);
        gbc.gridx = 1; panel.add(dateField, gbc);
        row++;

        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        bookButton = new JButton("Book / Insert Reservation");
        panel.add(bookButton, gbc);

        add(panel);

        lookupButton.addActionListener(e -> lookupTrain());
        bookButton.addActionListener(e -> bookTicket());
    }

    /** Looks up the train number in the trains table and auto-fills details. */
    private void lookupTrain() {
        String trainNo = trainNoField.getText().trim();

        if (trainNo.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a train number first.",
                    "Missing Train Number", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!trainNo.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Train number must be numeric.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "SELECT * FROM trains WHERE train_no = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, trainNo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                trainNameField.setText(rs.getString("train_name"));
                sourceField.setText(rs.getString("source"));
                destField.setText(rs.getString("destination"));
            } else {
                trainNameField.setText("");
                sourceField.setText("");
                destField.setText("");
                JOptionPane.showMessageDialog(this,
                        "No train found with number " + trainNo +
                        ".\nTry: 12001, 12951, 12628, or 16022 (sample data).",
                        "Train Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Validates input, saves the reservation, generates a PNR, and shows confirmation. */
    private void bookTicket() {
        String name = nameField.getText().trim();
        String trainNo = trainNoField.getText().trim();
        String trainName = trainNameField.getText().trim();
        String source = sourceField.getText().trim();
        String destination = destField.getText().trim();
        String classType = (String) classCombo.getSelectedItem();
        String dateText = dateField.getText().trim();

        // --- Validation ---
        if (name.isEmpty() || trainNo.isEmpty() || dateText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields (name, train number, date).",
                    "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!trainNo.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Train number must be numeric.",
                    "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (trainName.isEmpty() || source.isEmpty() || destination.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please click 'Lookup Train' and confirm a valid train first.",
                    "Train Not Verified", JOptionPane.WARNING_MESSAGE);
            return;
        }
        LocalDate journeyDate;
        try {
            journeyDate = LocalDate.parse(dateText, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Date must be in valid yyyy-mm-dd format (e.g. 2026-09-20).",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (journeyDate.isBefore(LocalDate.now())) {
            JOptionPane.showMessageDialog(this,
                    "Journey date cannot be in the past.",
                    "Invalid Date", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String pnr = generatePNR();

        String sql = "INSERT INTO reservations " +
                "(pnr, passenger_name, train_no, train_name, class_type, journey_date, source, destination, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'BOOKED')";

        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            ps.setString(2, name);
            ps.setString(3, trainNo);
            ps.setString(4, trainName);
            ps.setString(5, classType);
            ps.setString(6, dateText);
            ps.setString(7, source);
            ps.setString(8, destination);
            ps.executeUpdate();

            String details = String.format(
                    "Booking Confirmed!\n\n" +
                    "PNR Number: %s\n" +
                    "Passenger: %s\n" +
                    "Train: %s (%s)\n" +
                    "Route: %s -> %s\n" +
                    "Class: %s\n" +
                    "Date: %s",
                    pnr, name, trainName, trainNo, source, destination, classType, dateText);

            JOptionPane.showMessageDialog(this, details,
                    "Reservation Confirmed", JOptionPane.INFORMATION_MESSAGE);

            clearForm();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Generates a random 6-digit PNR and guarantees it's unique in the DB. */
    private String generatePNR() {
        Random rand = new Random();
        String pnr;
        boolean exists;
        do {
            pnr = String.valueOf(100000 + rand.nextInt(900000)); // 6-digit number
            exists = pnrExists(pnr);
        } while (exists);
        return pnr;
    }

    private boolean pnrExists(String pnr) {
        String sql = "SELECT 1 FROM reservations WHERE pnr = ?";
        try (Connection conn = DBConnection.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pnr);
            return ps.executeQuery().next();
        } catch (SQLException ex) {
            return false;
        }
    }

    private void clearForm() {
        nameField.setText("");
        trainNoField.setText("");
        trainNameField.setText("");
        sourceField.setText("");
        destField.setText("");
        dateField.setText("");
        classCombo.setSelectedIndex(0);
    }
}
