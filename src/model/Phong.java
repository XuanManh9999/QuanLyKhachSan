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
public class Phong {
    private Integer id;
    private String ten_phong;
    private Float gia_phong;
    private String mo_ta;
    private String trang_thai;
    private Timestamp ngay_them;
    private Timestamp ngay_cap_nhat;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTen_phong() {
        return ten_phong;
    }

    public void setTen_phong(String ten_phong) {
        this.ten_phong = ten_phong;
    }

    public Float getGia_phong() {
        return gia_phong;
    }

    public void setGia_phong(Float gia_phong) {
        this.gia_phong = gia_phong;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }

    public String getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }

    public Timestamp getNgay_them() {
        return ngay_them;
    }

    public void setNgay_them(Timestamp ngay_them) {
        this.ngay_them = ngay_them;
    }

    public Timestamp getNgay_cap_nhat() {
        return ngay_cap_nhat;
    }

    public void setNgay_cap_nhat(Timestamp ngay_cap_nhat) {
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

   
    
}
