package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

	public static AdminPanel adminPanel = new AdminPanel();
	public static CardLayout cardLayout = new CardLayout();
	public static JPanel mainPanel = new JPanel(cardLayout);
	public static OrderPanel orderPanel = new OrderPanel();
	public static TablePanel tablePanel = new TablePanel();

	public MainFrame() {
		setTitle("먄맛집 — POS System");
		setSize(900, 620);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		mainPanel.add(new LoginPanel(), "LOGIN");
		mainPanel.add(tablePanel, "TABLE");
		mainPanel.add(orderPanel, "ORDER");
		mainPanel.add(adminPanel, "ADMIN");
		add(mainPanel);
		cardLayout.show(mainPanel, "LOGIN");


		setVisible(true);
	}

	// Screen ပြောင်းဖို့ method
	public static void showPanel(String name) {
		cardLayout.show(mainPanel, name);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(MainFrame::new);
	}
}