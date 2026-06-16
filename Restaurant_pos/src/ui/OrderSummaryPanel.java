package ui;

import javax.swing.*;
import dao.OrderDAO;
import java.awt.*;
import java.util.List;

public class OrderSummaryPanel extends JPanel {

	private int tableNum;
	private JPanel itemListPanel;
	private JLabel totalLabel;
	private List<OrderItem> orderItems;
	private int tableId;
	private int orderId = -1;

	// Constructor
	public OrderSummaryPanel(List<OrderItem> orderItems, int tableId, int tableNum, int orderId) {
		this.orderItems = orderItems;
		this.tableId = tableId;
		this.tableNum = tableNum;
		this.orderId = orderId;

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

		totalLabel = new JLabel("  Total: ₩ 0");

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
	}

	private void showReceipt() {
		OrderDAO dao = new OrderDAO();
		double subtotal = dao.getOrderTotal(orderId);
		double tax = subtotal * 0.07;
		double total = subtotal + tax;

		// ── Step 1: 결제 방법 선택 (현금 / 카드) ──
		String[] payMethods = { "💵 현금 (Cash)", "💳 카드 (Card)" };
		int methodChoice = JOptionPane.showOptionDialog(
			this,
			"결제 방법을 선택하세요\nTotal: ₩ " + String.format("%,.0f", total),
			"결제 방법 — Table T" + tableNum,
			JOptionPane.DEFAULT_OPTION,
			JOptionPane.QUESTION_MESSAGE,
			null,
			payMethods,
			payMethods[0]
		);

		if (methodChoice == JOptionPane.CLOSED_OPTION) return;

		boolean isCash = (methodChoice == 0);
		double change = 0;

		// ── Step 2: 현금이면 받은 금액 입력 ──
		if (isCash) {
			while (true) {
				String input = JOptionPane.showInputDialog(
					this,
					String.format("총 금액: ₩ %,.0f\n받은 금액을 입력하세요 (₩):", total),
					"현금 결제 — Table T" + tableNum
				);
				if (input == null) return; // Cancel 누르면 취소
				try {
					double received = Double.parseDouble(input.replace(",", "").trim());
					if (received < total) {
						JOptionPane.showMessageDialog(this,
							String.format("금액이 부족합니다!\n총 금액: ₩ %,.0f\n받은 금액: ₩ %,.0f", total, received),
							"Error", JOptionPane.ERROR_MESSAGE);
					} else {
						change = received - total;
						break;
					}
				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(this, "숫자만 입력하세요!", "Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}

		// ── Step 3: Receipt 출력 ──
		StringBuilder sb = new StringBuilder();
		sb.append("================================\n");
		sb.append("         먄맛집\n");
		sb.append("================================\n");
		sb.append("Table: T").append(tableNum).append("\n");
		sb.append("--------------------------------\n");

		for (OrderItem oi : orderItems) {
			sb.append(String.format("%-15s %2dx ₩ %,.0f\n",
				oi.getMenu().getName(), oi.getQty(), oi.getMenu().getPrice()));
		}

		sb.append("--------------------------------\n");
		sb.append(String.format("Subtotal:      ₩ %,.0f\n", subtotal));
		sb.append(String.format("Tax (7%%):      ₩ %,.0f\n", tax));
		sb.append(String.format("Total:         ₩ %,.0f\n", total));
		sb.append(String.format("결제 방법:      %s\n", isCash ? "현금" : "카드"));
		if (isCash) {
			sb.append(String.format("거스름돈:      ₩ %,.0f\n", change));
		}
		sb.append("================================\n");
		sb.append(" 이용해 주셔서 감사합니다!\n");
		sb.append("================================\n");

		JTextArea receipt = new JTextArea(sb.toString());
		receipt.setFont(new Font("Monospaced", Font.PLAIN, 13));
		receipt.setEditable(false);
		receipt.setBackground(new Color(255, 255, 250));

		JScrollPane scroll = new JScrollPane(receipt);
		scroll.setPreferredSize(new Dimension(320, 400));

		// ── Step 4: 최종 확인 ──
		String confirmMsg = isCash
			? String.format("거스름돈: ₩ %,.0f  —  결제 완료하시겠습니까?", change)
			: "카드 결제 완료하시겠습니까?";

		int confirm = JOptionPane.showOptionDialog(
			this, scroll, confirmMsg,
			JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
			new String[] { "✅ 결제 완료", "Cancel" }, "✅ 결제 완료"
		);

		if (confirm == 0) {
			boolean ok = dao.payOrder(orderId, tableId);
			if (ok) {
				String successMsg = isCash
					? String.format("결제 완료 ✔\n거스름돈: ₩ %,.0f", change)
					: "카드 결제 완료 ✔";
				JOptionPane.showMessageDialog(this, successMsg, "Success", JOptionPane.INFORMATION_MESSAGE);
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