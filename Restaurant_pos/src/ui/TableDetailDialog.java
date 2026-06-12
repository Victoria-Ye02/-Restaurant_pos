package ui;

import dao.OrderDAO;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TableDetailDialog extends JDialog {

    private int tableId;
    private int tableNum;
    private int orderId;
    private TablePanel tablePanel;

    public TableDetailDialog(JFrame parent, 
                              int tableId, 
                              int tableNum,
                              TablePanel tablePanel) {
        super(parent, "Table " + tableNum, true);
        this.tableId   = tableId;
        this.tableNum  = tableNum;
        this.tablePanel = tablePanel;

        OrderDAO dao = new OrderDAO();
        this.orderId  = dao.getOpenOrderId(tableId);

        setSize(420, 520);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        // Title bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(26, 26, 46));
        topBar.setPreferredSize(new Dimension(0, 50));

        JLabel title = new JLabel(
            "  Table " + tableNum + " — Order Detail");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 15));
        topBar.add(title, BorderLayout.WEST);
        add(topBar, BorderLayout.NORTH);

        // Order items list
        OrderDAO dao = new OrderDAO();
        List<OrderItem> items = dao.getOrderItems(orderId);
        double subtotal       = dao.getOrderTotal(orderId);
        double tax            = subtotal * 0.07;
        double total          = subtotal + tax;

        JPanel listPanel = new JPanel();
        listPanel.setLayout(
            new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(Color.WHITE);
        listPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header
        JLabel header = new JLabel("မှာထားသော အစားအသောက်");
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBorder(
            BorderFactory.createEmptyBorder(0, 0, 8, 0));
        listPanel.add(header);

        // Item rows
        for (OrderItem oi : items) {
            JPanel row = new JPanel(new BorderLayout());
            row.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, 40));
            row.setBackground(Color.WHITE);
            row.setBorder(BorderFactory.createMatteBorder(
                0, 0, 1, 0, new Color(240, 240, 240)));

            JLabel name = new JLabel(
                "  " + oi.getMenu().getName());
            name.setFont(
                new Font("SansSerif", Font.PLAIN, 13));

            JLabel amt = new JLabel(
            	    oi.getQty() + " x " +
            	    String.format("₩ %,.0f", oi.getMenu().getPrice())
            	    + "  =  " +
            	    String.format("₩ %,.0f", oi.getSubTotal()),
            	    SwingConstants.RIGHT);
            amt.setFont(
                new Font("SansSerif", Font.PLAIN, 12));
            amt.setForeground(new Color(15, 110, 86));

            row.add(name, BorderLayout.WEST);
            row.add(amt, BorderLayout.EAST);
            listPanel.add(row);
        }

        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // Bottom — total + buttons
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(Color.WHITE);
        bottom.setBorder(BorderFactory.createMatteBorder(
            1, 0, 0, 0, Color.LIGHT_GRAY));

        // Total panel
        JPanel totalPanel = new JPanel(new GridLayout(3, 2, 4, 4));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(
            BorderFactory.createEmptyBorder(10, 14, 6, 14));

        totalPanel.add(new JLabel("Subtotal"));
        totalPanel.add(makeRightLabel(String.format("₩ %,.0f", subtotal)));

        totalPanel.add(new JLabel("Tax (7%)"));
        totalPanel.add(makeRightLabel(String.format("₩ %,.0f", tax)));

        JLabel totalLbl = new JLabel("Total");
        totalLbl.setFont(
            new Font("SansSerif", Font.BOLD, 15));
        JLabel totalAmt = 
        		makeRightLabel(String.format("₩ %,.0f", total));
        totalAmt.setFont(
            new Font("SansSerif", Font.BOLD, 15));
        totalAmt.setForeground(new Color(211, 84, 0));

        totalPanel.add(totalLbl);
        totalPanel.add(totalAmt);
        bottom.add(totalPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(
            new GridLayout(1, 2, 8, 8));
        btnPanel.setBorder(
            BorderFactory.createEmptyBorder(6, 10, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        // Order ထပ်ထည့် button
        JButton addOrderBtn = new JButton("주문한 메뉴");
        addOrderBtn.setBackground(new Color(83, 74, 183));
        addOrderBtn.setForeground(Color.WHITE);
        addOrderBtn.setOpaque(true);
        addOrderBtn.setBorderPainted(false);
        addOrderBtn.setFocusPainted(false);
        addOrderBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        addOrderBtn.setPreferredSize(new Dimension(0, 38));
        addOrderBtn.addActionListener(e -> {
            dispose(); // dialog ပိတ်
            // Order panel ဖွင့်
            MainFrame.orderPanel.setTable(tableId, tableNum);
            MainFrame.showPanel("ORDER");
        });

        // ငွေရှင်း button
        JButton payBtn = new JButton("Payment");
        payBtn.setBackground(new Color(46, 125, 50));
        payBtn.setForeground(Color.WHITE);
        payBtn.setOpaque(true);
        payBtn.setBorderPainted(false);
        payBtn.setFocusPainted(false);
        payBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        payBtn.setPreferredSize(new Dimension(0, 38));
        payBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "결제하시겠습니까?\\n합계: " +
                		String.format("₩ %,.0f", total),
                "Payment Confirm",
                JOptionPane.YES_NO_OPTION);

            if (confirm != JOptionPane.YES_OPTION) return;

            OrderDAO payDao = new OrderDAO();
            boolean ok = payDao.payOrder(orderId, tableId);

            if (ok) {
                JOptionPane.showMessageDialog(this,
                    "결제가 완료되었습니다! ✔",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                tablePanel.loadTables(); // table refresh
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error — ထပ်ကြိုးစားပါ",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnPanel.add(addOrderBtn);
        btnPanel.add(payBtn);
        bottom.add(btnPanel, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private JLabel makeRightLabel(String text) {
        JLabel lbl = new JLabel(text, SwingConstants.RIGHT);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return lbl;
    }
}