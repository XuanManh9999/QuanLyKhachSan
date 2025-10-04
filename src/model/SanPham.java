/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author Admin
 */
public class SanPham {
      private Integer id;
    private String ten_san_pham;
    private Float gia_san_pham;
    private String kich_thuoc;
    private String trong_luong;
    private String mau_sac;
    private String nguon_goc;
    private Date ngay_them;
    private Date ngay_cap_nhat;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTen_san_pham() {
        return ten_san_pham;
    }

    public void setTen_san_pham(String ten_san_pham) {
        this.ten_san_pham = ten_san_pham;
    }

    public Float getGia_san_pham() {
        return gia_san_pham;
    }

    public void setGia_san_pham(Float gia_san_pham) {
        this.gia_san_pham = gia_san_pham;
    }

    public String getKich_thuoc() {
        return kich_thuoc;
    }

    public void setKich_thuoc(String kich_thuoc) {
        this.kich_thuoc = kich_thuoc;
    }

    public String getTrong_luong() {
        return trong_luong;
    }

    public void setTrong_luong(String trong_luong) {
        this.trong_luong = trong_luong;
    }

    public String getMau_sac() {
        return mau_sac;
    }

    public void setMau_sac(String mau_sac) {
        this.mau_sac = mau_sac;
    }

    public String getNguon_goc() {
        return nguon_goc;
    }

    public void setNguon_goc(String nguon_goc) {
        this.nguon_goc = nguon_goc;
    }

    public Date getNgay_them() {
        return ngay_them;
    }

    public void setNgay_them(Date ngay_them) {
        this.ngay_them = ngay_them;
    }

    public Date getNgay_cap_nhat() {
        return ngay_cap_nhat;
    }

    public void setNgay_cap_nhat(Date ngay_cap_nhat) {
        this.ngay_cap_nhat = ngay_cap_nhat;
    }

}
