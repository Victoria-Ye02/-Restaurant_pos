package ui;

import dao.TableDAO;
import table.TableModel;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TablePanel extends JPanel {

    private JPanel gridPanel;
    

    public TablePanel() {

        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 46));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(26, 26, 46));
        topBar.setPreferredSize(new Dimension(0, 55));

        JLabel title = new JLabel("  Mango Restaurant — Tables");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        topBar.add(title, BorderLayout.WEST);

        add(topBar, BorderLayout.NORTH);

        // Grid panel (table button တွေ ထည့်မယ်)
        gridPanel = new JPanel(new GridLayout(3, 4, 12, 12));
        gridPanel.setBackground(new Color(30, 30, 46));
        gridPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        loadTables();
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(new Color(163, 45, 45));
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setOpaque(true);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        logoutBtn.addActionListener(e -> {
            Session.logout();
            MainFrame.showPanel("LOGIN");
        });

        JPanel rightWrap = new JPanel(
            new FlowLayout(FlowLayout.RIGHT, 10, 10));
        rightWrap.setOpaque(false);
        rightWrap.add(logoutBtn);
        topBar.add(rightWrap, BorderLayout.EAST);

        add(gridPanel, BorderLayout.CENTER);
    }

    void loadTables() {
        gridPanel.removeAll();

        TableDAO dao = new TableDAO();
        List<TableModel> tables = dao.getAllTables();

        for (TableModel t : tables) {
            JButton btn = createTableButton(t);
            gridPanel.add(btn);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }
    private JButton createTableButton(TableModel t) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);

        // Status အရ အရောင်ပြောင်း
        switch (t.getStatus()) {
            case "available":
                btn.setBackground(new Color(44, 44, 66));
                break;
            case "ordering":
                btn.setBackground(new Color(214, 234, 248));
                break;
            case "bill":
                btn.setBackground(new Color(250, 236, 231));
                break;
            default:
                btn.setBackground(new Color(44, 44, 66));
        }

        // Table number label
        JLabel numLabel = new JLabel("T" + t.getTableNum(), SwingConstants.CENTER);
        numLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        numLabel.setForeground(
            t.getStatus().equals("available") ? Color.WHITE : new Color(30, 30, 46)
        );

        // Seats label
        JLabel seatLabel = new JLabel(t.getSeats() + " seats", SwingConstants.CENTER);
        seatLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        seatLabel.setForeground(Color.GRAY);
        seatLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

        // Status badge
        String badgeText = switch (t.getStatus()) {
            case "ordering" -> "Ordering";
            case "bill"     -> "Bill Pending";
            default         -> "Available";
        };
        Color badgeBg = switch (t.getStatus()) {
            case "ordering" -> new Color(13, 71, 161);
            case "bill"     -> new Color(183, 28, 28);
            default         -> new Color(46, 125, 50);
        };

        JLabel badge = new JLabel(badgeText, SwingConstants.CENTER);
        badge.setFont(new Font("SansSerif", Font.BOLD, 11));
        badge.setForeground(Color.WHITE);
        badge.setBackground(badgeBg);
        badge.setOpaque(true);
        badge.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));

        JPanel badgePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        badgePanel.setOpaque(false);
        badgePanel.add(badge);

        btn.add(numLabel, BorderLayout.CENTER);
        btn.add(seatLabel, BorderLayout.NORTH);
        btn.add(badgePanel, BorderLayout.SOUTH);

    
        btn.addActionListener(e -> {
            if (t.getStatus().equals("available")) {
                // available → Order screen သွား
                MainFrame.orderPanel.setTable(t.getId(), t.getTableNum());
                MainFrame.showPanel("ORDER");
            } else {
                // ordering/bill → Detail dialog ဖွင့်
                new TableDetailDialog(
                    (JFrame) SwingUtilities.getWindowAncestor(this),
                    t.getId(),
                    t.getTableNum(),
                    this
                );
            }
        });
		return btn;
    }
        

   
}