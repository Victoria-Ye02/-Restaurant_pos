package dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import db.DBConnection;
import table.MenuModel;

public class MenuDAO {

    public List<MenuModel> getAllMenus() {
        List<MenuModel> list = new ArrayList<>();
        String sql = "SELECT * FROM menu_items ORDER BY category";

        Connection conn = DBConnection.connect();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                list.add(new MenuModel(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDouble("price"),
                    rs.getString("category")
                ));
            }
            rs.close();
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
        return list;
    }
    public void addMenu(String name, double price, String cat) {
        String sql = "INSERT INTO menu_items (name, price, category) VALUES (?, ?, ?)";
        Connection conn = DBConnection.connect();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, cat);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
    }

    public void updateMenu(int id, String name, double price, String cat) {
        String sql = "UPDATE menu_items SET name=?, price=?, category=? WHERE id=?";
        Connection conn = DBConnection.connect();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, cat);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
    }

public void deleteMenu(int id) {
    Connection conn = DBConnection.connect();
    try {
        conn.setAutoCommit(false);

        // order_items အရင် ဖျက်
        String sql1 = 
            "DELETE FROM order_items WHERE menu_id=?";
        PreparedStatement ps1 = 
            conn.prepareStatement(sql1);
        ps1.setInt(1, id);
        ps1.executeUpdate();

        // menu_items ဖျက်
        String sql2 = 
            "DELETE FROM menu_items WHERE id=?";
        PreparedStatement ps2 = 
            conn.prepareStatement(sql2);
        ps2.setInt(1, id);
        ps2.executeUpdate();

        conn.commit();

    } catch (SQLException e) {
        try { conn.rollback(); }
        catch (SQLException ex) { ex.printStackTrace(); }
        e.printStackTrace();
    } finally {
        DBConnection.close();
    }
}
}