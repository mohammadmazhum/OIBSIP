import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {

    public MainMenu() {
        setTitle("Train Reservation System - Main Menu");
        setSize(400, 250);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel title = new JLabel("Main Menu");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton bookButton = new JButton("Book a Ticket (New Reservation)");
        JButton cancelButton = new JButton("Cancel a Reservation");
        JButton logoutButton = new JButton("Logout");

        for (JButton b : new JButton[]{bookButton, cancelButton, logoutButton}) {
            b.setAlignmentX(Component.CENTER_ALIGNMENT);
            b.setMaximumSize(new Dimension(280, 35));
        }

        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(bookButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(cancelButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(logoutButton);

        add(panel);

        bookButton.addActionListener(e -> new ReservationForm().setVisible(true));
        cancelButton.addActionListener(e -> new CancellationForm().setVisible(true));
        logoutButton.addActionListener(e -> {
            this.dispose();
            new LoginForm().setVisible(true);
        });
    }
}
