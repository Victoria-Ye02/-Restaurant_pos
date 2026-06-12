package  db;

//
//Source code recreated from a .class file by IntelliJ IDEA
//(powered by FernFlower decompiler)
//



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
 public static final String dbDriver = "com.mysql.cj.jdbc.Driver";
 public static final String dbUrl = "jdbc:mysql://localhost:3306/pos?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF8";
 public static final String dbUser = "root";
 public static final String dbPwd = "12341234";
 public static Connection conn = null;

 public static Connection connect() {
     try {
         Class.forName("com.mysql.cj.jdbc.Driver");
         conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/pos?serverTimezone=UTC&useUnicode=true&characterEncoding=UTF8", "root", "12341234");
         if (conn != null) {
             System.out.println("db connect success");
         } else {
             System.out.println("connect fail");
         }
     } catch (Exception e) {
         System.out.println("db connect fail");
         e.printStackTrace();
     }

     return conn;
 }

 public static void close() {
     try {
         if (conn != null && !conn.isClosed()) {
             conn.close();
             System.out.println("db close success");
         }
     } catch (SQLException e) {
         System.out.println("db close fail");
         e.printStackTrace();
     }

 }

 public static void main(String[] args) {
     connect();
     close();
 }
}
