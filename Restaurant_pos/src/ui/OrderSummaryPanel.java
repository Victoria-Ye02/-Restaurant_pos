package ui;

import javax.swing.*;
import dao.OrderDAO;
import java.awt.*;
import java.util.List;

public class OrderSummaryPanel extends JPanel {

	private static final String tableNum = null;
	private JPanel itemListPanel;
	private JLabel totalLabel;
	private List<OrderItem> orderItems;
	private int tableId;
	private int orderId = -1;

	// Constructor
	public OrderSummaryPanel(List<OrderItem> orderItems, int tableId, int orderId) {
		this.orderItems = orderItems;
		this.tableId = tableId;
		this.orderId = orderId; // ← field ထည့်

// Existing order ဆိုရင် items load
		if (orderId != -1) {
			OrderDAO dao = new OrderDAO();
			List<OrderItem> existing = dao.getOrderItems(orderId);
			orderItems.addAll(existing);
		}

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(260, 0));
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Color.LIGHT_GRAY));
		buildUI();
		 refresh();
	}

	private void buildUI() {

		// ================= TITLE =================
		JLabel title = new JLabel("  Order Summary");

		title.setFont(new Font("SansSerif", Font.BOLD, 14));

		title.setOpaque(true);

		title.setBackground(new Color(26, 26, 46));

		title.setForeground(Color.WHITE);

		title.setPreferredSize(new Dimension(0, 45));

		add(title, BorderLayout.NORTH);

		// ================= ITEM LIST =================
		itemListPanel = new JPanel();

		itemListPanel.setLayout(new BoxLayout(itemListPanel, BoxLayout.Y_AXIS));

		itemListPanel.setBackground(Color.WHITE);

		JScrollPane scroll = new JScrollPane(itemListPanel);

		scroll.setBorder(null);

		add(scroll, BorderLayout.CENTER);

		// ================= BOTTOM PANEL =================
		JPanel bottomPanel = new JPanel(new BorderLayout());

		bottomPanel.setBackground(Color.WHITE);

		bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.LIGHT_GRAY));

		totalLabel = new JLabel("  Total: $0.00");

		totalLabel.setFont(new Font("SansSerif", Font.BOLD, 16));

		totalLabel.setPreferredSize(new Dimension(0, 45));

		bottomPanel.add(totalLabel, BorderLayout.CENTER);

		// ================= BUTTON PANEL =================
		JPanel btnPanel;

		if (orderId != -1) {
			// Ordering table — Clear, Submit, ငွေရှင်း
			btnPanel = new JPanel(new GridLayout(2, 2, 6, 6));
		} else {
			// New order — Clear, Submit
			btnPanel = new JPanel(new GridLayout(1, 2, 6, 6));
		}

		btnPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		btnPanel.setBackground(Color.WHITE);

		// CLEAR BUTTON
		JButton clearBtn = new JButton("Clear");

		clearBtn.setBackground(new Color(200, 200, 200));
		clearBtn.setOpaque(true);
		clearBtn.setContentAreaFilled(true);
		clearBtn.setForeground(Color.BLACK);

		clearBtn.setForeground(Color.BLACK);

		clearBtn.setBorderPainted(false);

		clearBtn.setFocusPainted(false);

		clearBtn.setFont(new Font("SansSerif", Font.BOLD, 12));

		clearBtn.addActionListener(e -> {

			orderItems.clear();

			refresh();
		});

		// SUBMIT BUTTON
		JButton submitBtn = new JButton("Submit");

		submitBtn.setBackground(new Color(46, 125, 50));

		submitBtn.setForeground(Color.WHITE);
		submitBtn.setOpaque(true);

		submitBtn.setContentAreaFilled(true);
		submitBtn.setBorderPainted(false);

		submitBtn.setFocusPainted(false);

		submitBtn.setFont(new Font("SansSerif", Font.BOLD, 13));

		submitBtn.addActionListener(e -> submitOrder());

		btnPanel.add(clearBtn);
		btnPanel.add(submitBtn);
		if (orderId != -1) {
			// ငွေရှင်း button — full width
			JButton payBtn = new JButton("💰 결제가 완료되었습니다 / Receipt");
			payBtn.setBackground(new Color(46, 125, 50));
			payBtn.setForeground(Color.WHITE);
			payBtn.setBorderPainted(false);
			payBtn.setFocusPainted(false);
			payBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
			payBtn.addActionListener(e -> showReceipt());

			// Full width အတွက် panel ခွဲ
			JPanel payPanel = new JPanel(new BorderLayout());
			payPanel.setOpaque(false);
			payPanel.setBorder(BorderFactory.createEmptyBorder(0, 6, 6, 6));
			payPanel.add(payBtn, BorderLayout.CENTER);

			bottomPanel.add(btnPanel, BorderLayout.NORTH);
			bottomPanel.add(payPanel, BorderLayout.SOUTH);
		} else {
			bottomPanel.add(btnPanel, BorderLayout.SOUTH);
		}

		add(bottomPanel, BorderLayout.SOUTH);

		bottomPanel.add(btnPanel, BorderLayout.SOUTH);

		add(bottomPanel, BorderLayout.SOUTH);
	}

	private void showReceipt() {
		// Receipt dialog ပြမယ်
		OrderDAO dao = new OrderDAO();
		double subtotal = dao.getOrderTotal(orderId);
		double tax = subtotal * 0.07;
		double total = subtotal + tax;

		// Receipt text ဆောက်
		StringBuilder sb = new StringBuilder();
		sb.append("================================\n");
		sb.append("      Mango Restaurant\n");
		sb.append("================================\n");
		sb.append("Table: ").append(tableNum).append("\n");
		sb.append("--------------------------------\n");

		for (OrderItem oi : orderItems) {
			sb.append(
					String.format("%-15s %2dx ₩ %,.0f\n", oi.getMenu().getName(), oi.getQty(), oi.getMenu().getPrice()));
		}

		sb.append("--------------------------------\n");
		sb.append(String.format("Subtotal:      ₩ %,.0f\n", subtotal));
		sb.append(String.format("Tax (7%%):      ₩ %,.0f\n", tax));
		sb.append(String.format("Total:         ₩ %,.0f\n", total));
		sb.append("================================\n");
		sb.append(" 이용해 주셔서 감사합니다\n");
		sb.append("================================\n");

		// Receipt panel ဆောက်
		JTextArea receipt = new JTextArea(sb.toString());
		receipt.setFont(new Font("Monospaced", Font.PLAIN, 13));
		receipt.setEditable(false);
		receipt.setBackground(new Color(255, 255, 250));

		JScrollPane scroll = new JScrollPane(receipt);
		scroll.setPreferredSize(new Dimension(320, 380));

		// Confirm dialog
		int choice = JOptionPane.showOptionDialog(this, scroll, "Receipt — Table " + tableNum,
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new String[] { "💰 ငွေရှင်းပြီး", "Cancel" }, "💰 ငွေရှင်းပြီး");

		if (choice == 0) {
			// DB update
			boolean ok = dao.payOrder(orderId, tableId);
			if (ok) {
				JOptionPane.showMessageDialog(this, "ငွေရှင်းပြီးပြီ ✔", "Success", JOptionPane.INFORMATION_MESSAGE);
				orderItems.clear();
				refresh();
				MainFrame.tablePanel.loadTables();
				MainFrame.showPanel("TABLE");
			} else {
				JOptionPane.showMessageDialog(this, "Error!", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// ====================================================
	// REFRESH
	// ====================================================
	public void refresh() {

		if (itemListPanel == null)
			return;

		itemListPanel.removeAll();

		double total = 0;

		for (int i = 0; i < orderItems.size(); i++) {

			OrderItem oi = orderItems.get(i);

			total += oi.getSubTotal();

			final int idx = i;

			// ================= ROW PANEL =================
			JPanel row = new JPanel(new BorderLayout());

			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

			row.setMinimumSize(new Dimension(0, 70));

			row.setBackground(Color.WHITE);

			row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));

			// ================= NAME PANEL =================
			JPanel namePanel = new JPanel(new GridLayout(2, 1));

			namePanel.setOpaque(false);

			namePanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

			JLabel nameLabel = new JLabel(oi.getMenu().getName());

			nameLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

			JLabel subLabel = new JLabel(String.format("₩ %,.0f", oi.getSubTotal()));

			subLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));

			subLabel.setForeground(new Color(15, 110, 86));

			namePanel.add(nameLabel);
			namePanel.add(subLabel);

			// ================= QTY PANEL =================
			JPanel qtyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 8));

			qtyPanel.setOpaque(false);

			qtyPanel.setPreferredSize(new Dimension(100, 50));

			// ================= MINUS BUTTON =================
			JButton minus = new JButton("-");

			minus.setPreferredSize(new Dimension(30, 30));

			minus.setBorderPainted(true);

			minus.setFocusPainted(false);

			minus.setBackground(new Color(240, 240, 240));

			minus.setFont(new Font("SansSerif", Font.BOLD, 14));

			minus.setOpaque(true);

			minus.addActionListener(e -> {

				oi.minusQty();

				if (oi.getQty() <= 0) {

					orderItems.remove(idx);
				}

				refresh();
			});

			// ================= QTY LABEL =================
			JLabel qtyLabel = new JLabel(String.valueOf(oi.getQty()), SwingConstants.CENTER);

			qtyLabel.setFont(new Font("SansSerif", Font.BOLD, 13));

			qtyLabel.setPreferredSize(new Dimension(20, 28));

			// ================= PLUS BUTTON =================
			JButton plus = new JButton("+");

			plus.setPreferredSize(new Dimension(30, 30));

			plus.setBorderPainted(true);

			plus.setFocusPainted(false);

			plus.setBackground(new Color(83, 74, 183));

			plus.setForeground(Color.black);

			plus.setFont(new Font("SansSerif", Font.BOLD, 14));

			plus.setOpaque(true);

			plus.addActionListener(e -> {

				oi.addQty();

				refresh();
			});

			// ================= ADD COMPONENT =================
			qtyPanel.add(minus);
			qtyPanel.add(qtyLabel);
			qtyPanel.add(plus);

			row.add(namePanel, BorderLayout.CENTER);

			row.add(qtyPanel, BorderLayout.EAST);

			itemListPanel.add(row);
		}

		// ================= TOTAL =================
		double tax = total * 0.07;

		double grand = total + tax;

		totalLabel.setText(String.format("  Total: ₩ %,.0f  (tax ₩ %,.0f)", grand, tax));

		itemListPanel.revalidate();

		itemListPanel.repaint();
	}

	// ====================================================
	// SUBMIT ORDER
	// ====================================================
	private void submitOrder() {

		if (orderItems.isEmpty()) {

			JOptionPane.showMessageDialog(this, "주문 항목이 없습니다!", "Warning", JOptionPane.WARNING_MESSAGE);

			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this, "주문을 제출하시겠습니까?", "Confirm",
				JOptionPane.YES_NO_OPTION);

		if (confirm != JOptionPane.YES_OPTION)
			return;

		OrderDAO dao = new OrderDAO();

		boolean ok = dao.submitOrder(tableId, orderItems);

		if (ok) {

			JOptionPane.showMessageDialog(this, "주문이 완료되었습니다! ✔", "Success", JOptionPane.INFORMATION_MESSAGE);

			orderItems.clear();

			refresh();

			MainFrame.tablePanel.loadTables();

			MainFrame.showPanel("TABLE");

		} else {

			JOptionPane.showMessageDialog(this, "오류가 발생했습니다. 다시 시도해주세요.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}