package dao;

import db.DBConnection;
import table.TableModel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAO {

    // Table အားလုံး ဆွဲထုတ်
    public List<TableModel> getAllTables() {
        List<TableModel> list = new ArrayList<>();
        String sql = "SELECT * FROM tables ORDER BY table_num";
        
        try (Connection conn = DBConnection.getConnection();
             Statement st   = conn.createStatement();
             ResultSet rs   = st.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(new TableModel(
                    rs.getInt("id"),
                    rs.getInt("table_num"),
                    rs.getInt("seats"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Table status ပြောင်း
    public void updateStatus(int id, String status) {
        String sql = "UPDATE tables SET status=? WHERE id=?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}