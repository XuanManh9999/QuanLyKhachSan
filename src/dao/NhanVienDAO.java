/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

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
/**
 *
 * @author Admin
 */
public class NhanVienDAO {
    public static List<NhanVien> getAllNhanVien(String searchByName) throws SQLException {
        Connection db = ConnectDB.getConnection();
        List<NhanVien> danhSachNhanVien = new ArrayList<>();
        if (searchByName.isEmpty()) {
            String sql = "select * from nhan_vien";
            Statement st = db.createStatement();
            ResultSet datas = st.executeQuery(sql);
            
           
            while (datas.next()) {
                NhanVien nhanVien = new NhanVien();
                nhanVien.setId(datas.getInt("id"));
                nhanVien.setHo_ten(datas.getString("ho_ten"));
                nhanVien.setDia_chi(datas.getString("dia_chi"));
                nhanVien.setGioi_tinh(datas.getString("gioi_tinh"));
                nhanVien.setNgay_sinh(datas.getDate("ngay_sinh"));
                nhanVien.setSo_cmnd(datas.getString("so_cmnd"));
                nhanVien.setNgay_vao_lam(datas.getDate("ngay_vao_lam"));
                danhSachNhanVien.add(nhanVien);
            }
            return danhSachNhanVien;
        }else {
            String sql = "select * from nhan_vien where ho_ten like '%" + searchByName + "%'";
            Statement st = db.createStatement();
            ResultSet datas = st.executeQuery(sql);
           
            while (datas.next()) {
                NhanVien nhanVien = new NhanVien();
                nhanVien.setId(datas.getInt("id"));
                nhanVien.setHo_ten(datas.getString("ho_ten"));
                nhanVien.setDia_chi(datas.getString("dia_chi"));
                nhanVien.setGioi_tinh(datas.getString("gioi_tinh"));
                nhanVien.setNgay_sinh(datas.getDate("ngay_sinh"));
                nhanVien.setSo_cmnd(datas.getString("so_cmnd"));
                nhanVien.setNgay_vao_lam(datas.getDate("ngay_vao_lam"));
                danhSachNhanVien.add(nhanVien);
            }
            return danhSachNhanVien;
        }
    }
    
    
     public static boolean createNhanVien(NhanVien nhanvien) throws SQLException {
        Connection db = ConnectDB.getConnection();
        String sql = "INSERT INTO quan_Ly_khach_san.nhan_vien (ho_ten, dia_chi, gioi_tinh, ngay_sinh, so_cmnd, ngay_vao_lam) VALUES(?, ?, ?, ?, ?, ?);";
        PreparedStatement p =  db.prepareStatement(sql);
        p.setString(1, nhanvien.getHo_ten());
        p.setString(2, nhanvien.getDia_chi());
        p.setString(3, nhanvien.getGioi_tinh());
        p.setDate(4, (Date) nhanvien.getNgay_sinh());
        p.setString(5, nhanvien.getSo_cmnd());
        p.setDate(6, (Date) nhanvien.getNgay_vao_lam());
        int result = p.executeUpdate();// != 0 thì nó là true, 0 thì ko có gì
        return result != 0;// true, flase
    }
     
     public static boolean deleteNhanVien (Integer idNhanVien) throws SQLException {
          Connection db = ConnectDB.getConnection();
          Statement st = db.createStatement();
          String query = "DELETE FROM nhan_vien where id = " + idNhanVien + ";";
          // DELETE FROM nhan_vien where id = 1;
          Integer result = st.executeUpdate(query);
          return result != 0;
     }
     
     
     public static boolean updateNhanVien(NhanVien nhanvien) throws SQLException {
        Connection db = ConnectDB.getConnection();
        String sql = "UPDATE quan_Ly_khach_san.nhan_vien SET ho_ten=?, dia_chi=?, gioi_tinh=?, ngay_sinh=?, so_cmnd=?, ngay_vao_lam=? WHERE id=?;";
        PreparedStatement p =  db.prepareStatement(sql);
        p.setString(1, nhanvien.getHo_ten());
        p.setString(2, nhanvien.getDia_chi());
        p.setString(3, nhanvien.getGioi_tinh());
        p.setDate(4, (Date) nhanvien.getNgay_sinh());
        p.setString(5, nhanvien.getSo_cmnd());
        p.setDate(6, (Date) nhanvien.getNgay_vao_lam());
        p.setInt(7, nhanvien.getId());
        int result = p.executeUpdate();// != 0 thì nó là true, 0 thì ko có gì
        return result != 0;// true, flase
    }
}
