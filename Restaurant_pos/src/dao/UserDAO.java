package dao;

         // ကိုယ့် package အတိုင်း
import db.DBConnection;
import table.UserModel;
import java.sql.*;
import java.util.ArrayList;   // ← ဒါထည့်
import java.util.List;        // ← ဒါထည့်

public class UserDAO {

    public UserModel login(String username, 
                            String password) {
        String sql = "SELECT * FROM users " +
                     "WHERE username=? " +
                     "AND password=? " +
                     "AND status='active'";
        Connection conn = DBConnection.connect();
        try {
            PreparedStatement ps = 
                conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new UserModel(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
        return null;
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> list = new ArrayList<>();
        String sql = "SELECT * FROM users";
        Connection conn = DBConnection.connect();
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new UserModel(
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("role"),
                    rs.getString("status")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
        return list;
    }

    public void addUser(String username,
                         String password, 
                         String role) {
        String sql = "INSERT INTO users " +
                     "(username,password,role) " +
                     "VALUES (?,?,?)";
        Connection conn = DBConnection.connect();
        try {
            PreparedStatement ps = 
                conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
    }

    public void updateUser(int id, String username,
            String password, String role, 
            String status) {
        String sql = password.isEmpty()
            ? "UPDATE users SET username=?," +
              "role=?,status=? WHERE id=?"
            : "UPDATE users SET username=?," +
              "password=?,role=?,status=? WHERE id=?";

        Connection conn = DBConnection.connect();
        try {
            PreparedStatement ps = 
                conn.prepareStatement(sql);
            if (password.isEmpty()) {
                ps.setString(1, username);
                ps.setString(2, role);
                ps.setString(3, status);
                ps.setInt(4, id);
            } else {
                ps.setString(1, username);
                ps.setString(2, password);
                ps.setString(3, role);
                ps.setString(4, status);
                ps.setInt(5, id);
            }
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
    }

    public void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        Connection conn = DBConnection.connect();
        try {
            PreparedStatement ps = 
                conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DBConnection.close();
        }
    }


}