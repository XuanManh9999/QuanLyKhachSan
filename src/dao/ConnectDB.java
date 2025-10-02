/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.PreparableStatement;
import java.sql.Connection; // Đối tượng connect của JDBC (cơ sở dữ liệu MYsql) nó đại diện cho một phiên làm việc (JDBC Driver)
import java.sql.DriverManager; // Driver ảo để kết nối tới csdl
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement; // Đối tượng dùng để thực hiện truy vấn
import java.sql.ResultSet;
/**
 *
 * @author Admin
 */
public class ConnectDB {
    private static String DB_URL = "jdbc:mysql://localhost:3306/quan_Ly_khach_san";
    private static String USER_NAME = "root"; // tên người dùng DB
    private static String PASSWORD = "123456";
    
    
    public static Connection getConnection() {
        String dbURL = DB_URL;
        String userName = USER_NAME;
        String password = PASSWORD;
       Connection conn = null; // đại diện cho 1 phiên làm việc
       try {
           Class.forName("com.mysql.cj.jdbc.Driver"); // Driver mặc định để connect mysql

           conn = DriverManager.getConnection(dbURL, userName, password);
           System.out.println("connect successfully!");
       } catch (Exception ex) {
           System.out.println("connect failure!");
           ex.printStackTrace();
       }
       return conn;
   }
    
}
