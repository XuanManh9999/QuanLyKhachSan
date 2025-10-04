/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import java.sql.Timestamp;

/**
 *
 * @author Admin
 */
public class NhanVienPhong {
    private Integer id_nhan_vien;
    private Integer id_phong;
    private String ten_khac_hang;
    private String so_cmnd_khach_hang;
    private Integer so_gioi_thue;
    private Timestamp thoi_gian_thue;
    private Timestamp thoi_gian_tra_phong;
    private String ghi_chu;

    public String getGhi_chu() {
        return ghi_chu;
    }

    public void setGhi_chu(String ghi_chu) {
        this.ghi_chu = ghi_chu;
    }

    public Integer getId_nhan_vien() {
        return id_nhan_vien;
    }

    public void setId_nhan_vien(Integer id_nhan_vien) {
        this.id_nhan_vien = id_nhan_vien;
    }

    public Integer getId_phong() {
        return id_phong;
    }

    public void setId_phong(Integer id_phong) {
        this.id_phong = id_phong;
    }

    public String getTen_khac_hang() {
        return ten_khac_hang;
    }

    public void setTen_khac_hang(String ten_khac_hang) {
        this.ten_khac_hang = ten_khac_hang;
    }

    public String getSo_cmnd_khach_hang() {
        return so_cmnd_khach_hang;
    }

    public void setSo_cmnd_khach_hang(String so_cmnd_khach_hang) {
        this.so_cmnd_khach_hang = so_cmnd_khach_hang;
    }

    public Integer getSo_gioi_thue() {
        return so_gioi_thue;
    }

    public void setSo_gioi_thue(Integer so_gioi_thue) {
        this.so_gioi_thue = so_gioi_thue;
    }

    public Timestamp getThoi_gian_thue() {
        return thoi_gian_thue;
    }

    public void setThoi_gian_thue(Timestamp thoi_gian_thue) {
        this.thoi_gian_thue = thoi_gian_thue;
    }

    public Timestamp getThoi_gian_tra_phong() {
        return thoi_gian_tra_phong;
    }

    public void setThoi_gian_tra_phong(Timestamp thoi_gian_tra_phong) {
        this.thoi_gian_tra_phong = thoi_gian_tra_phong;
    }

   
    
    
}
