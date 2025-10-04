/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

/**
 *
 * @author Admin
 */
import java.util.List;
import model.NhanVien;
import com.mysql.cj.protocol.Resultset;
import com.mysql.cj.xdevapi.PreparableStatement;
import java.sql.Connection; // Đối tượng connect của JDBC (cơ sở dữ liệu MYsql) nó đại diện cho một phiên làm việc (JDBC Driver)
import java.sql.Date;
import java.sql.DriverManager; // Driver ảo để kết nối tới csdl
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement; // Đối tượng dùng để thực hiện truy vấn
import java.sql.ResultSet;
import java.util.ArrayList;
import model.NhanVienPhong;
import model.Phong;
import java.sql.Timestamp;

public class PhongDAO {
    public static List<Phong> getAllPhong(String searchByName, String trang_thai_phong) throws SQLException {
        System.out.println(searchByName);
        System.out.println(trang_thai_phong);
        Connection db = ConnectDB.getConnection();
        List<Phong> danhSachPhong = new ArrayList<>();
          String sql = "select * from phong ";
            if (!searchByName.isEmpty() &&  trang_thai_phong.isEmpty()) {
                sql += "where ten_phong like '%" + searchByName + "%'";
            }
            if (!trang_thai_phong.isEmpty() && searchByName.isEmpty()) {
                sql += "where trang_thai like '%" + trang_thai_phong + "%'";
            }
            
            if (!trang_thai_phong.isEmpty() && !searchByName.isEmpty()) {
                sql += "where ten_phong like '%" + searchByName + "%'" + " or trang_thai like '%" + trang_thai_phong + "%'";
            }
            
            System.out.println("sql"+sql);
            
            // select * from phong where ten_phong like '%phòng 1%' or  trang_thai like '%Đã đặt%'
            Statement st = db.createStatement();
            ResultSet datas = st.executeQuery(sql);
           
            while (datas.next()) {
                   Phong phong = new Phong();
                phong.setId(datas.getInt("id"));
                phong.setGia_phong(datas.getFloat("gia_phong"));
                phong.setMo_ta(datas.getString("mo_ta"));
                phong.setTen_phong(datas.getString("ten_phong"));
                phong.setNgay_them(datas.getTimestamp("ngay_them"));
                phong.setNgay_cap_nhat(datas.getTimestamp("ngay_cap_nhat"));
                phong.setTrang_thai(datas.getString("trang_thai"));
                danhSachPhong.add(phong);
            }
            return danhSachPhong;
    }
    
    
     public static boolean createPhong(Phong phong) throws SQLException {
        Connection db = ConnectDB.getConnection();
        String sql = "INSERT INTO quan_ly_khach_san.phong (ten_phong, gia_phong, mo_ta, trang_thai) VALUES(?, ?, ?, ?);";
        PreparedStatement p =  db.prepareStatement(sql);
        p.setString(1, phong.getTen_phong());
        p.setFloat(2, phong.getGia_phong());
        p.setString(3, phong.getMo_ta());
        p.setString(4, phong.getTrang_thai());
//        p.setDate(5, (Date) phong.getNgay_them());
//        p.setDate(6, (Date) phong.getNgay_cap_nhat());
        int result = p.executeUpdate();// != 0 thì nó là true, 0 thì ko có gì
        return result != 0;// true, flase
    }
     
     public static boolean deletePhong (Integer idPhong) throws SQLException {
          Connection db = ConnectDB.getConnection();
          Statement st = db.createStatement();
          String query = "DELETE FROM phong where id = " + idPhong + ";";
          // DELETE FROM nhan_vien where id = 1;
          Integer result = st.executeUpdate(query);
          return result != 0;
     }
     
     
     public static boolean updatePhong(Phong phong) throws SQLException {
        Connection db = ConnectDB.getConnection();
        String sql = "UPDATE quan_ly_khach_san.phong SET ten_phong=?, gia_phong=?, mo_ta=?, trang_thai=?, ngay_them=?, ngay_cap_nhat=? WHERE id=?;";
        PreparedStatement p =  db.prepareStatement(sql);
        p.setString(1, phong.getTen_phong());
        p.setFloat(2, phong.getGia_phong());
        p.setString(3, phong.getMo_ta());
        p.setString(4, phong.getTrang_thai());
        p.setTimestamp(5,  phong.getNgay_them());
        p.setTimestamp(6,  phong.getNgay_cap_nhat());
        p.setInt(7, phong.getId());
        int result = p.executeUpdate();// != 0 thì nó là true, 0 thì ko có gì
        return result != 0;// true, flase
    }
     
     
}
