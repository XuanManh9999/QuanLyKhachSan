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

// Class proto
public class NhanVien {
    private Integer id;
    private String ho_ten;
    private String dia_chi;
    private String gioi_tinh;
    private Date ngay_sinh;
    private String so_cmnd;
    private Date ngay_vao_lam;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHo_ten() {
        return ho_ten;
    }

    public void setHo_ten(String ho_ten) {
        this.ho_ten = ho_ten;
    }

    public String getDia_chi() {
        return dia_chi;
    }

    public void setDia_chi(String dia_chi) {
        this.dia_chi = dia_chi;
    }

    public String getGioi_tinh() {
        return gioi_tinh;
    }

    public void setGioi_tinh(String gioi_tinh) {
        this.gioi_tinh = gioi_tinh;
    }

    public Date getNgay_sinh() {
        return ngay_sinh;
    }

    public void setNgay_sinh(Date ngay_sinh) {
        this.ngay_sinh = ngay_sinh;
    }

    public String getSo_cmnd() {
        return so_cmnd;
    }

    public void setSo_cmnd(String so_cmnd) {
        this.so_cmnd = so_cmnd;
    }

    public Date getNgay_vao_lam() {
        return ngay_vao_lam;
    }

    public void setNgay_vao_lam(Date ngay_vao_lam) {
        this.ngay_vao_lam = ngay_vao_lam;
    }
    
    
}
