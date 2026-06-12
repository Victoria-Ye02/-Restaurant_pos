package ui;

import javax.swing.*;

import dao.OrderDAO;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class OrderPanel extends JPanel {

    private int tableId;
    private int tableNum;
	private int orderId=-1;

    public OrderPanel() {
        setLayout(new BorderLayout());
    }

    public void setTable(int tableId, int tableNum) {
        this.tableId  = tableId;
        this.tableNum = tableNum;

        // Ordering ဆိုရင် existing order load လုပ်
        OrderDAO dao = new OrderDAO();
        this.orderId  = dao.getOpenOrderId(tableId);

        buildUI();
    }

    private void buildUI() {
        removeAll();

        // Shared order list
        List<OrderItem> orderItems = new ArrayList<>();
       

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(26, 26, 46));
        topBar.setPreferredSize(new Dimension(0, 55));

        JLabel title = new JLabel(
            "  Table " + tableNum + " — Order");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        topBar.add(title, BorderLayout.WEST);

        JButton backBtn = new JButton("← Tables");
        backBtn.setBackground(new Color(211, 84, 0));
        backBtn.setForeground(Color.WHITE);
        backBtn.setBorderPainted(false);
        backBtn.setFocusPainted(false);
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backBtn.addActionListener(
            e -> MainFrame.showPanel("TABLE"));

        JPanel backWrap = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 10));
        backWrap.setOpaque(false);
        backWrap.add(backBtn);
        topBar.add(backWrap, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        // Summary panel (right)
        OrderSummaryPanel summaryPanel =
        	    new OrderSummaryPanel(orderItems, tableId, orderId);

        // Menu grid (center)
        MenuGridPanel menuGridPanel =
            new MenuGridPanel(orderItems, summaryPanel);

        add(menuGridPanel, BorderLayout.CENTER);
        add(summaryPanel,  BorderLayout.EAST);

        revalidate();
        repaint();
    }
}