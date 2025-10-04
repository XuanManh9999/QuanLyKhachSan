/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;
import dao.DatPhongDAO;
import dao.NhanVienDAO;
import dao.PhongDAO;
import java.math.BigDecimal;
import model.NhanVien;
import model.NhanVienPhong;
import model.Phong;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import utils.ChargeResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 *
 * @author Admin
 */
public class DatPhong extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(DatPhong.class.getName());

    // Khóa cũ của dòng đang chọn để cập nhật/huỷ: (idNV, idPhong, thoiGianThue)
    private Integer selectedOldIdNV = null;
    private Integer selectedOldIdPhong = null;
    private Timestamp selectedOldThoiGianThue = null;

    private static final java.time.format.DateTimeFormatter TS_FMT =
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates new form DatPhong
     */
    public DatPhong() throws SQLException {
      initComponents();
    setLocationRelativeTo(null);    

    reloadCombos();                
    loadTable("");                  
    // setupTableRenderers();        
    }
    
    private void setupTableRenderers() {
    javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
    center.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    javax.swing.table.DefaultTableCellRenderer right = new javax.swing.table.DefaultTableCellRenderer();
    right.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);

    int count = JTableDatPhong.getColumnModel().getColumnCount();
    int[] centerCols = {0, 2, 6, 8, 10, 11}; // idNV, idPhong, soGio, thue, tra, soPhut
    for (int c : centerCols) {
        if (c < count) JTableDatPhong.getColumnModel().getColumn(c).setCellRenderer(center);
    }
    if (12 < count) {
        JTableDatPhong.getColumnModel().getColumn(12).setCellRenderer(right); // Tiền phòng
    }
}

     
     private static java.sql.Timestamp parseTs(String s) {
    if (s == null || s.isBlank()) return null;
    try {
        return java.sql.Timestamp.valueOf(s);
    } catch (IllegalArgumentException e) {
        try {
            return java.sql.Timestamp.valueOf(java.time.LocalDateTime.parse(s, TS_FMT));
        } catch (Exception ex) {
            return null;
        }
    }
}

private static String formatVND(java.math.BigDecimal v) {
    if (v == null) return "";
    java.text.DecimalFormat df = new java.text.DecimalFormat("#,##0");
    return df.format(v) + " VND";
}
private static String tsToStr(java.sql.Timestamp ts) {
    return (ts == null) ? "" : ts.toLocalDateTime().format(TS_FMT);
}

    private void reloadCombos() throws SQLException {
        cbDsNhanVien.removeAllItems();
        cmDsPhong.removeAllItems();

        List<NhanVien> dsNhanVien = NhanVienDAO.getAllNhanVien("");
        // Phòng: lấy tất cả, để khi cập nhật có thể chuyển sang phòng đang “Đã đặt” của chính bản ghi khác
        List<Phong> dsPhong = PhongDAO.getAllPhong("", "");

        for (NhanVien nv : dsNhanVien) {
            cbDsNhanVien.addItem(nv.getId() + "-" + nv.getHo_ten());
        }
        for (Phong p : dsPhong) {
            cmDsPhong.addItem(p.getId() + "-" + p.getTen_phong());
        }
    }

    private Integer parseIdFromCombo(JComboBox<String> cb) {
        Object o = cb.getSelectedItem();
        if (o == null) return null;
        String s = o.toString();
        int idx = s.indexOf('-');
        if (idx < 0) return null;
        try { return Integer.parseInt(s.substring(0, idx)); } catch (NumberFormatException e) { return null; }
    }

    private void resetFields() {
        txtSoCmnd.setText("");
        txtSoGioThue.setText("");
        txtTenKhachHang.setText("");
        txtGhiChu.setText("");
        txtTimKiem.setText("");
        if (cbDsNhanVien.getItemCount() > 0) cbDsNhanVien.setSelectedIndex(0);
        if (cmDsPhong.getItemCount() > 0) cmDsPhong.setSelectedIndex(0);

        selectedOldIdNV = null;
        selectedOldIdPhong = null;
        selectedOldThoiGianThue = null;
        JTableDatPhong.clearSelection();
    }
    
    
    
    private boolean validateForm(boolean showDialog) {
        String tenKH = txtTenKhachHang.getText().trim();
        String cmnd = txtSoCmnd.getText().trim();
        String soGio = txtSoGioThue.getText().trim();
        Integer idNV = parseIdFromCombo(cbDsNhanVien);
        Integer idPhong = parseIdFromCombo(cmDsPhong);

        StringBuilder err = new StringBuilder();
        if (idNV == null) err.append("• Vui lòng chọn nhân viên\n");
        if (idPhong == null) err.append("• Vui lòng chọn phòng\n");
        if (tenKH.isEmpty()) err.append("• Tên khách hàng không được để trống\n");
        if (cmnd.isEmpty()) {
            err.append("• CMND không được để trống\n");
        } else if (!cmnd.matches("\\d{9,12}")) {
            err.append("• CMND phải là số 9–12 ký tự\n");
        }
        if (soGio.isEmpty()) {
            err.append("• Số giờ thuê không được để trống\n");
        } else if (!soGio.matches("\\d+")) {
            err.append("• Số giờ thuê phải là số nguyên dương\n");
        } else if (Integer.parseInt(soGio) <= 0) {
            err.append("• Số giờ thuê phải > 0\n");
        }

        if (err.length() > 0) {
            if (showDialog) JOptionPane.showMessageDialog(this, err.toString(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

   private void loadTable(String keyword) throws SQLException {
    JTableDatPhong.setModel(new javax.swing.table.DefaultTableModel(
            new Object[][]{},
            new String[]{
                    "Id nhân viên", "Tên nhân viên",
                    "Id phòng", "Tên phòng",
                    "Tên khách hàng", "Số CMND", "Số giờ thuê",
                    "Ghi chú", "Thời gian thuê", "Trạng thái phòng", "Thời gian trả",
                    "Số phút dùng", "Tiền phòng (VND)"
            }
    ) {
        final boolean[] canEdit = new boolean[]{ false,false,false,false,false,false,false,false,false,false,false,false,false };
        public boolean isCellEditable(int r, int c) { return canEdit[c]; }
    });

    javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) JTableDatPhong.getModel();
    java.util.List<dao.DatPhongDAO.NhanVienPhongView> list = dao.DatPhongDAO.listBookings(keyword);
    for (dao.DatPhongDAO.NhanVienPhongView v : list) {
        Object minutes = (v.getSo_phut_su_dung() == null) ? "" : v.getSo_phut_su_dung();
        String money   = formatVND(v.getTien_phong());
        System.out.println("Thời gian trả phòng: " + v.getThoi_gian_tra_phong());
        String trang_thai_phong = v.getThoi_gian_tra_phong() != null ? "Đã trả phòng" : v.getTrang_thai_phong();
        model.addRow(new Object[]{
                v.getId_nhan_vien(), v.getTen_nhan_vien(),
                v.getId_phong(), v.getTen_phong(),
                v.getTen_khach_hang(), v.getSo_cmnd_khach_hang(),
                v.getSo_gio_thue(), v.getGhi_chu(),
                tsToStr(v.getThoi_gian_thue()),
                trang_thai_phong,
                tsToStr(v.getThoi_gian_tra_phong()),
                minutes, money
        });
    }
    setupTableRenderers(); // áp renderer sau khi set model để tránh out-of-bounds
}

    private  void loadDatas () throws SQLException {
        List<NhanVien> dsNhanVien =  NhanVienDAO.getAllNhanVien("");
        List<Phong> dsPhong = PhongDAO.getAllPhong("", "Chưa đặt");
        
        for (NhanVien nv : dsNhanVien) {
            String formatData = nv.getId() + "-" + nv.getHo_ten();
             cbDsNhanVien.addItem(formatData);
        }
        for (Phong phong : dsPhong) {
            String formatData = phong.getId() + "-" + phong.getTen_phong();
             cmDsPhong.addItem(formatData);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        JTableDatPhong = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtSoCmnd = new javax.swing.JTextField();
        btnXoa = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        txtTimKiem = new javax.swing.JTextField();
        btnReset = new javax.swing.JButton();
        btnTimKiem = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtTenKhachHang = new javax.swing.JTextField();
        txtSoGioThue = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtGhiChu = new javax.swing.JTextArea();
        cmDsPhong = new javax.swing.JComboBox<>();
        cbDsNhanVien = new javax.swing.JComboBox<>();
        btnTroLai = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        JTableDatPhong.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id nhân viên", "Tên nhân viên", "Id phòng", "Tên phòng", "Tên khách hàng", "Số CMND", "Số giờ thuê", "Ghi chú"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, true, false, true, true, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTableDatPhong.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                JTableDatPhongAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        JTableDatPhong.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTableDatPhongMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(JTableDatPhong);

        jLabel2.setText("Ds nhân viên");

        jLabel4.setText("Số cmnd");

        jLabel5.setText("Ds phòng");

        btnXoa.setText("Huỷ phòng/Trả phòng");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnThem.setText("Đặt phòng");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setText("Cập nhật phòng");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnTimKiem.setText("Tìm kiếm");
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Đặt phòng");

        jLabel7.setText("Tên khách hàng:");

        jLabel8.setText("Số giờ thuê");

        jLabel9.setText("Ghi chú");

        txtGhiChu.setColumns(20);
        txtGhiChu.setRows(5);
        jScrollPane2.setViewportView(txtGhiChu);

        btnTroLai.setText("Trở lại");
        btnTroLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTroLaiActionPerformed(evt);
            }
        });

        jButton1.setText("Xuất báo cáo excel");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(405, 405, 405)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtSoCmnd, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbDsNhanVien, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(jLabel5))
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmDsPhong, 0, 189, Short.MAX_VALUE)
                            .addComponent(txtSoGioThue))
                        .addGap(25, 25, 25)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnSua)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnXoa)
                                .addGap(18, 18, 18)
                                .addComponent(jButton1)
                                .addGap(4, 4, 4))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnTroLai, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(25, 25, 25))
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jScrollPane1))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(cbDsNhanVien, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel7)
                        .addComponent(txtTenKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmDsPhong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(13, 13, 13)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(txtSoCmnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtSoGioThue, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8)
                        .addComponent(jLabel9))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnTroLai, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(1, 1, 1)))
                .addGap(38, 38, 38)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void JTableDatPhongAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_JTableDatPhongAncestorAdded

    }//GEN-LAST:event_JTableDatPhongAncestorAdded

    private void JTableDatPhongMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTableDatPhongMouseClicked
    int row = JTableDatPhong.getSelectedRow();
    if (row < 0) return;

    // --- Lưu khóa cũ để update/huỷ ---
    Object idNVObj    = JTableDatPhong.getValueAt(row, 0); // Id nhân viên
    Object idPhongObj = JTableDatPhong.getValueAt(row, 2); // Id phòng
    selectedOldIdNV    = (idNVObj instanceof Integer)    ? (Integer) idNVObj
                        : Integer.valueOf(String.valueOf(idNVObj));
    selectedOldIdPhong = (idPhongObj instanceof Integer) ? (Integer) idPhongObj
                        : Integer.valueOf(String.valueOf(idPhongObj));

    // Cột 8 = "Thời gian thuê" (có thể là Timestamp hoặc String "yyyy-MM-dd HH:mm:ss")
    Object thueVal = JTableDatPhong.getValueAt(row, 8);
    selectedOldThoiGianThue = null;
    if (thueVal != null) {
        if (thueVal instanceof java.sql.Timestamp) {
            selectedOldThoiGianThue = (java.sql.Timestamp) thueVal;
        } else {
            String s = thueVal.toString().trim();
            if (!s.isEmpty()) {
                // Chuẩn hoá chuỗi: thay 'T' = ' ' và cắt phần mili nếu có
                s = s.replace('T', ' ');
                if (s.length() >= 19) s = s.substring(0, 19); // yyyy-MM-dd HH:mm:ss
                try {
                    selectedOldThoiGianThue = java.sql.Timestamp.valueOf(s);
                } catch (IllegalArgumentException e) {
                    try {
                        java.time.format.DateTimeFormatter FMT =
                                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        selectedOldThoiGianThue = java.sql.Timestamp.valueOf(
                                java.time.LocalDateTime.parse(s, FMT)
                        );
                    } catch (Exception ignore) { /* để null nếu parse fail */ }
                }
            }
        }
    }

    // --- Fill form theo dòng chọn ---
    String nvLabel = selectedOldIdNV + "-";
    for (int i = 0; i < cbDsNhanVien.getItemCount(); i++) {
        String item = cbDsNhanVien.getItemAt(i);
        if (item != null && item.startsWith(nvLabel)) { 
            cbDsNhanVien.setSelectedIndex(i); 
            break; 
        }
    }
    String pLabel = selectedOldIdPhong + "-";
    for (int i = 0; i < cmDsPhong.getItemCount(); i++) {
        String item = cmDsPhong.getItemAt(i);
        if (item != null && item.startsWith(pLabel)) { 
            cmDsPhong.setSelectedIndex(i); 
            break; 
        }
    }

    Object tenKHObj = JTableDatPhong.getValueAt(row, 4); // Tên KH
    Object cmndObj  = JTableDatPhong.getValueAt(row, 5); // CMND
    Object soGioObj = JTableDatPhong.getValueAt(row, 6); // Số giờ thuê
    Object ghiChuObj= JTableDatPhong.getValueAt(row, 7); // Ghi chú

    txtTenKhachHang.setText(tenKHObj == null ? "" : tenKHObj.toString());
    txtSoCmnd.setText(cmndObj == null ? "" : cmndObj.toString());
    txtSoGioThue.setText(soGioObj == null ? "" : String.valueOf(soGioObj));
    txtGhiChu.setText(ghiChuObj == null ? "" : ghiChuObj.toString());
    }//GEN-LAST:event_JTableDatPhongMouseClicked

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
    try {
        if (selectedOldIdNV == null || selectedOldIdPhong == null || selectedOldThoiGianThue == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần huỷ/trả phòng", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận huỷ/trả phòng?", "Xác nhận",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        ChargeResult res = dao.DatPhongDAO.cancelBookingAndComputeCharge(
                selectedOldIdNV, selectedOldIdPhong, selectedOldThoiGianThue, new java.sql.Timestamp(System.currentTimeMillis())
        );
        if (!res.success) {
            JOptionPane.showMessageDialog(this, "Huỷ phòng thất bại", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String msg = "Trả phòng thành công.\n"
                + "Số phút sử dụng: " + res.minutesUsed + " phút\n"
                + "Đơn giá (đồng/giờ): " + res.ratePerHour.toPlainString() + "\n"
                + "Tiền phòng: " + formatVND(res.amount);
        JOptionPane.showMessageDialog(this, msg, "Hoá đơn tạm tính", JOptionPane.INFORMATION_MESSAGE);

        reloadCombos();
        loadTable("");
        resetFields();
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Huỷ phòng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
        logger.log(java.util.logging.Level.SEVERE, null, ex);
    }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
 try {
            if (!validateForm(true)) return;

            Integer idNV = parseIdFromCombo(cbDsNhanVien);
            Integer idPhong = parseIdFromCombo(cmDsPhong);

            NhanVienPhong nvp = new NhanVienPhong();
            nvp.setId_nhan_vien(idNV);
            nvp.setId_phong(idPhong);
            nvp.setTen_khac_hang(txtTenKhachHang.getText().trim());
            nvp.setSo_cmnd_khach_hang(txtSoCmnd.getText().trim());
            nvp.setSo_gioi_thue(Integer.parseInt(txtSoGioThue.getText().trim()));
            nvp.setThoi_gian_thue(new Timestamp(System.currentTimeMillis()));
            nvp.setGhi_chu(txtGhiChu.getText().trim());
            nvp.setThoi_gian_tra_phong(null);

            boolean ok = DatPhongDAO.createRoomByUser(nvp);
            if (!ok) {
                JOptionPane.showMessageDialog(this,
                        "Đặt phòng thất bại. Có thể phòng đang bận.",
                        "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Đặt phòng thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            reloadCombos();
            loadTable("");
            resetFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Đặt phòng thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        

    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
  try {
            if (selectedOldIdNV == null || selectedOldIdPhong == null || selectedOldThoiGianThue == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng cần cập nhật", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateForm(true)) return;

            Integer newIdNV = parseIdFromCombo(cbDsNhanVien);
            Integer newIdPhong = parseIdFromCombo(cmDsPhong);

            NhanVienPhong newData = new NhanVienPhong();
            newData.setId_nhan_vien(newIdNV);
            newData.setId_phong(newIdPhong);
            newData.setTen_khac_hang(txtTenKhachHang.getText().trim());
            newData.setSo_cmnd_khach_hang(txtSoCmnd.getText().trim());
            newData.setSo_gioi_thue(Integer.parseInt(txtSoGioThue.getText().trim()));
            // Có thể cho phép đổi cả thời điểm bắt đầu thuê (tuỳ nghiệp vụ),
            // ở đây giữ nguyên thời điểm thuê cũ để làm khóa mới:
            newData.setThoi_gian_thue(selectedOldThoiGianThue);
            newData.setGhi_chu(txtGhiChu.getText().trim());
            // Nếu muốn đánh dấu trả phòng khi cập nhật, bạn có thể set thời gian trả tại đây.
            newData.setThoi_gian_tra_phong(null);

            boolean ok = DatPhongDAO.updateBooking(
                    newData, selectedOldIdNV, selectedOldIdPhong, selectedOldThoiGianThue
            );
            if (!ok) {
                JOptionPane.showMessageDialog(this, "Cập nhật thất bại (phòng có thể đang bận hoặc bản ghi không tồn tại).", "Thông báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Cập nhật thành công", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            reloadCombos();
            loadTable("");
            resetFields();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Cập nhật thất bại", "Lỗi", JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }       


    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
  try {
            reloadCombos();
            loadTable("");
            resetFields();
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }     

    }//GEN-LAST:event_btnResetActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
 try {
            loadTable(txtTimKiem.getText().trim());
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }   
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnTroLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTroLaiActionPerformed
 this.dispose(); // đóng form hiện tại
    java.awt.EventQueue.invokeLater(() -> new TrangChu().setVisible(true));        // TODO add your handling code here:
    }//GEN-LAST:event_btnTroLaiActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
  try {
        javax.swing.JFileChooser fc = new javax.swing.JFileChooser();
        String ts = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fc.setSelectedFile(new File("dat_phong_" + ts + ".csv"));
        int ans = fc.showSaveDialog(this);
        if (ans != javax.swing.JFileChooser.APPROVE_OPTION) return;

        File f = fc.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".csv")) {
            f = new File(f.getParentFile(), f.getName() + ".csv");
        }

        exportBookingsToCSV(txtTimKiem.getText().trim(), f);
        javax.swing.JOptionPane.showMessageDialog(this,
                "Xuất file thành công:\n" + f.getAbsolutePath(),
                "Thông báo", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    } catch (Exception ex) {
        logger.log(java.util.logging.Level.SEVERE, null, ex);
        javax.swing.JOptionPane.showMessageDialog(this,
                "Xuất file thất bại",
                "Lỗi", javax.swing.JOptionPane.ERROR_MESSAGE);
    }        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new DatPhong().setVisible(true);
            } catch (SQLException ex) {
                System.getLogger(DatPhong.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }
    
private void exportBookingsToCSV(String keyword, File file) throws Exception {
    // Lấy dữ liệu JOIN từ DAO (đã có phút dùng & tiền)
    java.util.List<dao.DatPhongDAO.NhanVienPhongView> list = dao.DatPhongDAO.listBookings(keyword);

    try (OutputStream os = new FileOutputStream(file);
         OutputStreamWriter ow = new OutputStreamWriter(os, StandardCharsets.UTF_8);
         BufferedWriter bw = new BufferedWriter(ow)) {

        // BOM để Excel hiển thị tiếng Việt đúng
        bw.write('\uFEFF');

        // Header
        String[] header = {
                "Id nhân viên","Tên nhân viên","Id phòng","Tên phòng",
                "Tên khách hàng","Số CMND","Số giờ thuê","Ghi chú",
                "Thời gian thuê","Trạng thái phòng","Thời gian trả",
                "Số phút dùng","Tiền phòng (VND)"
        };
        bw.write(csvLine(header));
        bw.newLine();

        // Body
        for (dao.DatPhongDAO.NhanVienPhongView v : list) {
            String[] row = new String[] {
                String.valueOf(v.getId_nhan_vien()),
                safe(v.getTen_nhan_vien()),
                String.valueOf(v.getId_phong()),
                safe(v.getTen_phong()),
                safe(v.getTen_khach_hang()),
                safe(v.getSo_cmnd_khach_hang()),
                String.valueOf(v.getSo_gio_thue()),
                safe(v.getGhi_chu()),
                tsToStr(v.getThoi_gian_thue()),
                safe(v.getTrang_thai_phong()),
                tsToStr(v.getThoi_gian_tra_phong()),
                v.getSo_phut_su_dung()==null? "" : String.valueOf(v.getSo_phut_su_dung()),
                (v.getTien_phong()==null? "" : v.getTien_phong().toPlainString())
            };
            bw.write(csvLine(row));
            bw.newLine();
        }
    }
}

private static String safe(String s) { return s == null ? "" : s; }

// Ghép một dòng CSV với escape dấu "
private static String csvLine(String[] cols) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < cols.length; i++) {
        if (i > 0) sb.append(',');
        String c = cols[i] == null ? "" : cols[i];
        // escape " -> ""
        c = c.replace("\"", "\"\"");
        // luôn bọc trong dấu "
        sb.append('"').append(c).append('"');
    }
    return sb.toString();
}
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTable JTableDatPhong;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnTroLai;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cbDsNhanVien;
    private javax.swing.JComboBox<String> cmDsPhong;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtGhiChu;
    private javax.swing.JTextField txtSoCmnd;
    private javax.swing.JTextField txtSoGioThue;
    private javax.swing.JTextField txtTenKhachHang;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}
