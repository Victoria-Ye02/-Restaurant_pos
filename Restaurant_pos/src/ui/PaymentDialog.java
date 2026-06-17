package ui;

import dao.OrderDAO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PaymentDialog extends JDialog {

    private boolean paid = false;

    public PaymentDialog(JFrame parent, int orderId, int tableId, int tableNum, double total,
                         TablePanel tablePanel) {
        super(parent, "ငွေရှင်း — Table T" + tableNum, true);
        setSize(380, 440);
        setLocationRelativeTo(parent);
        setResizable(false);
        setLayout(new BorderLayout());

        // ===== TITLE BAR =====
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(26, 26, 46));
        topBar.setPreferredSize(new Dimension(0, 50));
        JLabel title = new JLabel("  💰  ငွေရှင်း — Table T" + tableNum);
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 14));
        topBar.add(title, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // ===== BODY =====
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(BorderFactory.createEmptyBorder(20, 24, 10, 24));

        // Total display
        JLabel totalTitle = new JLabel("합계 (Total)");
        totalTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        totalTitle.setForeground(Color.GRAY);
        totalTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel totalAmt = new JLabel(String.format("₩ %,.0f", total));
        totalAmt.setFont(new Font("SansSerif", Font.BOLD, 28));
        totalAmt.setForeground(new Color(26, 26, 46));
        totalAmt.setAlignmentX(Component.CENTER_ALIGNMENT);

        body.add(totalTitle);
        body.add(Box.createVerticalStrut(4));
        body.add(totalAmt);
        body.add(Box.createVerticalStrut(20));

        // Divider
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        body.add(sep);
        body.add(Box.createVerticalStrut(16));

        // Payment type label
        JLabel typeLabel = new JLabel("결제 방법 선택");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        body.add(typeLabel);
        body.add(Box.createVerticalStrut(12));

        // 현금 / 카드 toggle buttons
        JPanel togglePanel = new JPanel(new GridLayout(1, 2, 10, 0));
        togglePanel.setOpaque(false);
        togglePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JButton cashBtn = new JButton("현금  💵");
        JButton cardBtn = new JButton("카드  💳");

        styleToggle(cashBtn, false);
        styleToggle(cardBtn, false);

        togglePanel.add(cashBtn);
        togglePanel.add(cardBtn);
        body.add(togglePanel);
        body.add(Box.createVerticalStrut(16));

        // Cash input area (hidden by default)
        JPanel cashPanel = new JPanel(new BorderLayout(0, 6));
        cashPanel.setOpaque(false);
        cashPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JLabel cashLabel = new JLabel("받은 금액 (현금)");
        cashLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        cashLabel.setForeground(new Color(60, 60, 60));

        JTextField cashInput = new JTextField();
        cashInput.setFont(new Font("SansSerif", Font.PLAIN, 16));
        cashInput.setPreferredSize(new Dimension(0, 42));
        cashInput.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(4, 10, 4, 10)));

        cashPanel.add(cashLabel, BorderLayout.NORTH);
        cashPanel.add(cashInput, BorderLayout.CENTER);
        cashPanel.setVisible(false);
        body.add(cashPanel);

        // Change display
        JLabel changeLabel = new JLabel("");
        changeLabel.setFont(new Font("SansSerif", Font.BOLD, 15));
        changeLabel.setForeground(new Color(46, 125, 50));
        changeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        changeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        body.add(Box.createVerticalStrut(4));
        body.add(changeLabel);

        add(body, BorderLayout.CENTER);

        // ===== BOTTOM BUTTONS =====
        JPanel btnArea = new JPanel(new GridLayout(1, 2, 10, 0));
        btnArea.setBackground(Color.WHITE);
        btnArea.setBorder(BorderFactory.createEmptyBorder(8, 24, 16, 24));

        JButton cancelBtn = new JButton("취소");
        cancelBtn.setBackground(new Color(200, 200, 200));
        cancelBtn.setForeground(Color.BLACK);
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        cancelBtn.setPreferredSize(new Dimension(0, 44));

        JButton confirmBtn = new JButton("결제 완료 ✔");
        confirmBtn.setBackground(new Color(150, 150, 150)); // disabled color initially
        confirmBtn.setForeground(Color.WHITE);
        confirmBtn.setOpaque(true);
        confirmBtn.setBorderPainted(false);
        confirmBtn.setFocusPainted(false);
        confirmBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        confirmBtn.setEnabled(false);

        btnArea.add(cancelBtn);
        btnArea.add(confirmBtn);
        add(btnArea, BorderLayout.SOUTH);

        // ===== STATE =====
        final String[] selectedType = { null };

        // Toggle logic
        cashBtn.addActionListener(e -> {
            selectedType[0] = "cash";
            styleToggle(cashBtn, true);
            styleToggle(cardBtn, false);
            cashPanel.setVisible(true);
            changeLabel.setText("");
            confirmBtn.setEnabled(false);
            confirmBtn.setBackground(new Color(150, 150, 150));
            cashInput.requestFocus();
            revalidate();
            repaint();
        });

        cardBtn.addActionListener(e -> {
            selectedType[0] = "card";
            styleToggle(cardBtn, true);
            styleToggle(cashBtn, false);
            cashPanel.setVisible(false);
            changeLabel.setText("");
            confirmBtn.setEnabled(true);
            confirmBtn.setBackground(new Color(46, 125, 50));
            revalidate();
            repaint();
        });

        // Cash input listener — calculate change
        cashInput.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { calc(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { calc(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calc(); }

            void calc() {
                try {
                    double given = Double.parseDouble(
                        cashInput.getText().trim().replace(",", ""));
                    double change = given - total;
                    if (change >= 0) {
                        changeLabel.setText(String.format(
                            "거스름돈 (잔돈): ₩ %,.0f", change));
                        changeLabel.setForeground(new Color(46, 125, 50));
                        confirmBtn.setEnabled(true);
                        confirmBtn.setBackground(new Color(46, 125, 50));
                    } else {
                        changeLabel.setText(String.format(
                            "부족: ₩ %,.0f", Math.abs(change)));
                        changeLabel.setForeground(new Color(163, 45, 45));
                        confirmBtn.setEnabled(false);
                        confirmBtn.setBackground(new Color(150, 150, 150));
                    }
                } catch (NumberFormatException ex) {
                    changeLabel.setText("");
                    confirmBtn.setEnabled(false);
                    confirmBtn.setBackground(new Color(150, 150, 150));
                }
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        confirmBtn.addActionListener(e -> {
            OrderDAO dao = new OrderDAO();
            boolean ok = dao.payOrder(orderId, tableId, selectedType[0]);
            if (ok) {
                paid = true;
                JOptionPane.showMessageDialog(this,
                    "결제가 완료되었습니다! ✔",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                tablePanel.loadTables();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error — ထပ်ကြိုးစားပါ",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        setVisible(true);
    }

    private void styleToggle(JButton btn, boolean selected) {
        if (selected) {
            btn.setBackground(new Color(83, 74, 183));
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(new Color(230, 230, 230));
            btn.setForeground(new Color(60, 60, 60));
        }
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setPreferredSize(new Dimension(0, 48));
    }

    public boolean isPaid() {
        return paid;
    }
}
