/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import dao.NhanVienDAO;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import model.NhanVien;

/**
 *
 * @author Admin
 */
public class QuanLyNhanVien extends javax.swing.JFrame {
    private Integer IdNhanVien = null;
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(QuanLyNhanVien.class.getName());

    // Formatters
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
private static final SimpleDateFormat DATETIME_FMT =
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * Creates new form QuanLyNguoiDung
     */
    public QuanLyNhanVien() throws SQLException {
         initComponents();
        // Center window on screen
        setLocationRelativeTo(null);

        // Căn giữa các cột thời gian trên JTable
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        JTableNhanVien.getColumnModel().getColumn(0).setCellRenderer(center); // Id
        JTableNhanVien.getColumnModel().getColumn(4).setCellRenderer(center); // Ngày sinh
        JTableNhanVien.getColumnModel().getColumn(6).setCellRenderer(center); // Ngày thêm
        JTableNhanVien.getColumnModel().getColumn(7).setCellRenderer(center); // Ngày cập nhật

        loadDatas("");
    }
    
    private static String dtToString(java.util.Date d) {
    if (d == null) return "";
    return DATETIME_FMT.format(d);
}
    private void loadDatas (String search) throws SQLException {
          List<NhanVien> nhanViens = NhanVienDAO.getAllNhanVien(search);
        DefaultTableModel model = (DefaultTableModel) JTableNhanVien.getModel();
        model.setRowCount(0);

        for (NhanVien nv : nhanViens) {
            // Giả định model NhanVien trả về:
            // getNgay_sinh() -> java.sql.Date
            // getNgay_them(), getNgay_cap_nhat() -> java.sql.Timestamp (có cả time)
        String ngaySinhStr    = nv.getNgay_sinh() == null ? "" : nv.getNgay_sinh().toString();
        String ngayThemStr    = dtToString(nv.getNgay_them());
        String ngayCapNhatStr = dtToString(nv.getNgay_cap_nhat());

            Object[] row = {
                nv.getId(),
                nv.getHo_ten(),
                nv.getDia_chi(),
                nv.getGioi_tinh(),
                ngaySinhStr,
                nv.getSo_cmnd(),
                ngayThemStr,
                ngayCapNhatStr
            };
            model.addRow(row);
        }
    }
private static String tsToString(java.sql.Timestamp ts) {
    if (ts == null) return "";
    return DATETIME_FMT.format(ts);
}
    // ====== VALIDATION ======
    private boolean validateForm(boolean showDialog) {
        String hoTen = txtHoTen.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String soCmnd = txtSoCmnd.getText().trim();
        String ngaySinhInput = txtNgaySinh.getText().trim(); // yyyy-MM-dd
        String gioiTinh = (String) ComboGioiTinh.getSelectedItem();

        StringBuilder err = new StringBuilder();

        if (hoTen.isEmpty()) err.append("• Họ tên không được để trống\n");
        if (diaChi.isEmpty()) err.append("• Địa chỉ không được để trống\n");

        if (soCmnd.isEmpty()) {
            err.append("• Số CMND không được để trống\n");
        } else if (!soCmnd.matches("\\d{9,12}")) {
            err.append("• Số CMND phải là số, 9-12 ký tự\n");
        }

        if (!ngaySinhInput.isEmpty()) {
            try {
                LocalDate dob = LocalDate.parse(ngaySinhInput, DATE_FMT);
                if (dob.isAfter(LocalDate.now())) {
                    err.append("• Ngày sinh không được lớn hơn hôm nay\n");
                } else if (dob.isBefore(LocalDate.of(1900, 1, 1))) {
                    err.append("• Ngày sinh không hợp lệ (trước 1900)\n");
                }
            } catch (DateTimeException ex) {
                err.append("• Ngày sinh không đúng định dạng yyyy-MM-dd\n");
            }
        } else {
            err.append("• Ngày sinh không được để trống (định dạng yyyy-MM-dd)\n");
        }

        if (gioiTinh == null || gioiTinh.isEmpty()) {
            err.append("• Vui lòng chọn giới tính\n");
        }

        if (err.length() > 0) {
            if (showDialog) {
                JOptionPane.showMessageDialog(this, err.toString(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            }
            return false;
        }
        return true;
    }
    
    public void resetDataEmpty() {
        txtDiaChi.setText("");
        txtHoTen.setText("");
        txtNgaySinh.setText("");
        txtSoCmnd.setText("");
        txtTimKiem.setText("");
        ComboGioiTinh.setSelectedIndex(0);
        IdNhanVien = null;
        JTableNhanVien.clearSelection();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JTableNhanVien = new javax.swing.JTable();
        txtHoTen = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtDiaChi = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtNgaySinh = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtSoCmnd = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        ComboGioiTinh = new javax.swing.JComboBox<>();
        btnXoa = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        txtTimKiem = new javax.swing.JTextField();
        btnReset = new javax.swing.JButton();
        btnTimKiem1 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel1.setText("Quản lý nhân viên");

        JTableNhanVien.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Họ tên", "Địa chỉ", "Giới tính", "Ngày sinh", "Số CMND", "Ngày thêm", "Ngày cập nhật"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                true, true, true, true, true, true, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        JTableNhanVien.addAncestorListener(new javax.swing.event.AncestorListener() {
            public void ancestorAdded(javax.swing.event.AncestorEvent evt) {
                JTableNhanVienAncestorAdded(evt);
            }
            public void ancestorMoved(javax.swing.event.AncestorEvent evt) {
            }
            public void ancestorRemoved(javax.swing.event.AncestorEvent evt) {
            }
        });
        JTableNhanVien.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JTableNhanVienMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(JTableNhanVien);

        jLabel2.setText("Họ tên");

        jLabel3.setText("Số cmnd");

        jLabel4.setText("Ngày sinh");

        jLabel5.setText("Địa chỉ");

        ComboGioiTinh.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Nam", "Nữ", "Khác" }));

        btnXoa.setText("Xoá");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
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

        btnTimKiem1.setText("Tìm kiếm");
        btnTimKiem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiem1ActionPerformed(evt);
            }
        });

        jLabel7.setText("Giới tính");

        jButton1.setText("Trở lại");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(289, 289, 289))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(15, 15, 15)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(16, 16, 16)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtSoCmnd, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addComponent(btnThem, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(ComboGioiTinh, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(39, 39, 39))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(365, 365, 365)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnTimKiem1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnReset, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtDiaChi, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addComponent(ComboGioiTinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7))
                    .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtNgaySinh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSoCmnd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(29, 29, 29)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnReset)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem1)
                    .addComponent(jButton1))
                .addGap(12, 12, 12)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

   
    
    
    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
          try {
            loadDatas("");
            resetDataEmpty();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Tải lại dữ liệu thất bại (CSDL).",
                "Thông báo",
                JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnResetActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
       try {
            if (!validateForm(true)) return;

            NhanVien nhanVien = new NhanVien();
            nhanVien.setHo_ten(txtHoTen.getText().trim());
            nhanVien.setDia_chi(txtDiaChi.getText().trim());
            nhanVien.setSo_cmnd(txtSoCmnd.getText().trim());

            LocalDate date_ngay_sinh = LocalDate.parse(txtNgaySinh.getText().trim(), DATE_FMT);
            java.sql.Date ngay_sinh = java.sql.Date.valueOf(date_ngay_sinh);
            nhanVien.setNgay_sinh(ngay_sinh);

            nhanVien.setGioi_tinh(ComboGioiTinh.getSelectedItem().toString());

            // Không set ngay_them/ngay_cap_nhat: DB tự xử lý
            boolean isSucsess = NhanVienDAO.createNhanVien(nhanVien);
            if (isSucsess) {
                JOptionPane.showMessageDialog(this,
                    "Thêm nhân viên thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
                resetDataEmpty();
                loadDatas("");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Thêm nhân viên thất bại",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Thêm nhân viên thất bại (CSDL).",
                "Thông báo",
                JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Thêm nhân viên thất bại",
                "Cảnh báo",
                JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnTimKiem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiem1ActionPerformed
         try {
            loadDatas(txtTimKiem.getText().trim());
        } catch (SQLException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnTimKiem1ActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
          try {
            if (IdNhanVien == null) {
                JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn nhân viên trước khi sửa",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!validateForm(true)) return;

            NhanVien nhanVien = new NhanVien();
            nhanVien.setId(IdNhanVien);
            nhanVien.setHo_ten(txtHoTen.getText().trim());
            nhanVien.setDia_chi(txtDiaChi.getText().trim());
            nhanVien.setSo_cmnd(txtSoCmnd.getText().trim());

            LocalDate date_ngay_sinh = LocalDate.parse(txtNgaySinh.getText().trim(), DATE_FMT);
            nhanVien.setNgay_sinh(java.sql.Date.valueOf(date_ngay_sinh));

            nhanVien.setGioi_tinh(ComboGioiTinh.getSelectedItem().toString());

            // Không set ngay_cap_nhat: DB tự cập nhật
            boolean isSucsess = NhanVienDAO.updateNhanVien(nhanVien);
            if (isSucsess) {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật nhân viên thành công",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE
                );
                resetDataEmpty();
                loadDatas("");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Cập nhật nhân viên thất bại",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Cập nhật nhân viên thất bại (CSDL).",
                "Thông báo",
                JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Cập nhật nhân viên thất bại",
                "Cảnh báo",
                JOptionPane.ERROR_MESSAGE);
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        int row = JTableNhanVien.getSelectedRow();
        if (row != -1) {
            Integer rowData = (Integer) JTableNhanVien.getValueAt(row, 0);
            try {
                boolean isSucsess = NhanVienDAO.deleteNhanVien(rowData);
                if (isSucsess) {
                    JOptionPane.showMessageDialog(this,
                        "Xoá nhân viên thành công",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    resetDataEmpty();
                    loadDatas("");
                } else {
                    JOptionPane.showMessageDialog(this,
                        "Xoá nhân viên thất bại",
                        "Thông báo",
                        JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(this,
                    "Xoá nhân viên thất bại (CSDL).",
                    "Thông báo",
                    JOptionPane.ERROR_MESSAGE);
                logger.log(java.util.logging.Level.SEVERE, null, ex);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Vui lòng chọn nhân viên cần xoá",
                "Cảnh báo",
                JOptionPane.WARNING_MESSAGE);
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void JTableNhanVienAncestorAdded(javax.swing.event.AncestorEvent evt) {//GEN-FIRST:event_JTableNhanVienAncestorAdded
    }//GEN-LAST:event_JTableNhanVienAncestorAdded

    private void JTableNhanVienMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_JTableNhanVienMouseClicked
             int row = JTableNhanVien.getSelectedRow();
        if (row < 0) return;

        Integer id = (Integer) JTableNhanVien.getValueAt(row, 0);
        String ho_ten = (String) JTableNhanVien.getValueAt(row, 1);
        String dia_chi = (String) JTableNhanVien.getValueAt(row, 2);
        String gioi_tinh = (String) JTableNhanVien.getValueAt(row, 3);
        String ngay_sinh_str = (String) JTableNhanVien.getValueAt(row, 4); // đã là chuỗi yyyy-MM-dd
        String so_cmnd = (String) JTableNhanVien.getValueAt(row, 5);

        txtHoTen.setText(ho_ten);
        txtDiaChi.setText(dia_chi);
        ComboGioiTinh.setSelectedItem(gioi_tinh);
        txtNgaySinh.setText(ngay_sinh_str != null ? ngay_sinh_str : "");
        txtSoCmnd.setText(so_cmnd);
        IdNhanVien = id;
    }//GEN-LAST:event_JTableNhanVienMouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
         this.dispose(); // đóng form hiện tại
    java.awt.EventQueue.invokeLater(() -> new TrangChu().setVisible(true));
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
                new QuanLyNhanVien().setVisible(true);
            } catch (SQLException ex) {
                System.getLogger(QuanLyNhanVien.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> ComboGioiTinh;
    private javax.swing.JTable JTableNhanVien;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem1;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtDiaChi;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtNgaySinh;
    private javax.swing.JTextField txtSoCmnd;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables

   
}
