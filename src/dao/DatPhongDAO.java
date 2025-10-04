package dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import model.NhanVienPhong;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import utils.ChargeResult;

public class DatPhongDAO {

    private static final String TRANG_THAI_DA_DAT = "Đã đặt";
    private static final String TRANG_THAI_CHUA_DAT = "Chưa đặt";

    // ---------- HELPERS ----------
    private static boolean isRoomAvailable(Connection conn, int idPhong) throws SQLException {
        // 1) phòng chưa bị đánh dấu Đã đặt
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT trang_thai FROM quan_ly_khach_san.phong WHERE id = ?")) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return false; // không tồn tại phòng
                String st = rs.getString(1);
                if (TRANG_THAI_DA_DAT.equalsIgnoreCase(st)) return false;
            }
        }
        // 2) không có bản ghi đặt đang mở (chưa trả) cho phòng này
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT 1 " +
                "FROM quan_ly_khach_san.Nhan_vien_phong " +
                "WHERE id_phong = ? AND thoi_gian_tra_phong IS NULL LIMIT 1")) {
            ps.setInt(1, idPhong);
            try (ResultSet rs = ps.executeQuery()) {
                return !rs.next(); // không có ai đang thuê
            }
        }
    }

    private static void updateTrangThaiPhong(Connection conn, int idPhong, String status) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE quan_ly_khach_san.phong SET trang_thai = ? WHERE id = ?")) {
            ps.setString(1, status);
            ps.setInt(2, idPhong);
            ps.executeUpdate();
        }
    }

    // ---------- CREATE (ĐẶT PHÒNG) ----------
    public static boolean createRoomByUser(NhanVienPhong nvp) throws SQLException {
        // nvp cần: id_nhan_vien, id_phong, ten_khach_hang, so_cmnd_khach_hang, so_gio_thue, thoi_gian_thue, ghi_chu
        // thoi_gian_tra_phong để NULL khi mới đặt
        Connection conn = ConnectDB.getConnection();
        boolean prevAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            if (!isRoomAvailable(conn, nvp.getId_phong())) {
                conn.rollback();
                return false; // phòng đang bận
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO quan_ly_khach_san.Nhan_vien_phong " +
                            "(id_nhan_vien, id_phong, ten_khach_hang, so_cmnd_khach_hang, so_gio_thue, thoi_gian_thue, ghi_chu, thoi_gian_tra_phong) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, NULL)")) {
                ps.setInt(1, nvp.getId_nhan_vien());
                ps.setInt(2, nvp.getId_phong());
                ps.setString(3, nvp.getTen_khac_hang());
                ps.setString(4, nvp.getSo_cmnd_khach_hang());
                ps.setInt(5, nvp.getSo_gioi_thue());
                ps.setTimestamp(6, nvp.getThoi_gian_thue()); // lúc đặt
                ps.setString(7, nvp.getGhi_chu());
                int inserted = ps.executeUpdate();
                if (inserted == 0) {
                    conn.rollback();
                    return false;
                }
            }

            updateTrangThaiPhong(conn, nvp.getId_phong(), TRANG_THAI_DA_DAT);
            conn.commit();
            return true;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(prevAuto);
        }
    }

    // ---------- UPDATE (CẬP NHẬT PHIẾU ĐẶT) ----------
    // Khoá xác định bản ghi cũ: (oldIdNhanVien, oldIdPhong, oldThoiGianThue)
    public static boolean updateBooking(NhanVienPhong newData,
                                        int oldIdNhanVien, int oldIdPhong, Timestamp oldThoiGianThue) throws SQLException {

        Connection conn = ConnectDB.getConnection();
        boolean prevAuto = conn.getAutoCommit();
        conn.setAutoCommit(false);
        try {
            // Nếu đổi phòng: giải phóng phòng cũ & check phòng mới trống
            boolean changeRoom = newData.getId_phong() != oldIdPhong;

            if (changeRoom) {
                if (!isRoomAvailable(conn, newData.getId_phong())) {
                    conn.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE quan_ly_khach_san.Nhan_vien_phong SET " +
                            "id_nhan_vien = ?, id_phong = ?, ten_khach_hang = ?, so_cmnd_khach_hang = ?, " +
                            "so_gio_thue = ?, thoi_gian_thue = ?, ghi_chu = ?, thoi_gian_tra_phong = ? " +
                            "WHERE id_nhan_vien = ? AND id_phong = ? AND thoi_gian_thue = ?")) {
                ps.setInt(1, newData.getId_nhan_vien());
                ps.setInt(2, newData.getId_phong());
                ps.setString(3, newData.getTen_khac_hang());
                ps.setString(4, newData.getSo_cmnd_khach_hang());
                ps.setInt(5, newData.getSo_gioi_thue());
                ps.setTimestamp(6, newData.getThoi_gian_thue());
                ps.setString(7, newData.getGhi_chu());
                ps.setTimestamp(8, newData.getThoi_gian_tra_phong()); // có thể null nếu chưa trả

                ps.setInt(9, oldIdNhanVien);
                ps.setInt(10, oldIdPhong);
                ps.setTimestamp(11, oldThoiGianThue);

                int updated = ps.executeUpdate();
                if (updated == 0) {
                    conn.rollback();
                    return false;
                }
            }

            if (changeRoom) {
                // phòng cũ -> Chưa đặt, phòng mới -> Đã đặt
                updateTrangThaiPhong(conn, oldIdPhong, TRANG_THAI_CHUA_DAT);
                updateTrangThaiPhong(conn, newData.getId_phong(), TRANG_THAI_DA_DAT);
            } else {
                // nếu người dùng set thoi_gian_tra_phong khác null -> trả phòng
                if (newData.getThoi_gian_tra_phong() != null) {
                    updateTrangThaiPhong(conn, newData.getId_phong(), TRANG_THAI_CHUA_DAT);
                }
            }

            conn.commit();
            return true;
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(prevAuto);
        }
    }

    // ---------- CANCEL / TRẢ PHÒNG ----------
   // API mới: trả phòng + tính tiền (transaction)
public static ChargeResult cancelBookingAndComputeCharge(int idNhanVien, int idPhong,
                                                        Timestamp thoiGianThue, Timestamp thoiGianTra) throws SQLException {
    Connection conn = ConnectDB.getConnection();
    boolean prevAuto = conn.getAutoCommit();
    conn.setAutoCommit(false);
    try {
        Timestamp tra = (thoiGianTra == null) ? new Timestamp(System.currentTimeMillis()) : thoiGianTra;

        // 1) Set thời gian trả
        int affected;
        try (PreparedStatement ps = conn.prepareStatement(
                "UPDATE quan_ly_khach_san.Nhan_vien_phong " +
                "SET thoi_gian_tra_phong = ? " +
                "WHERE id_nhan_vien = ? AND id_phong = ? AND thoi_gian_thue = ?")) {
            ps.setTimestamp(1, tra);
            ps.setInt(2, idNhanVien);
            ps.setInt(3, idPhong);
            ps.setTimestamp(4, thoiGianThue);
            affected = ps.executeUpdate();
        }
        if (affected == 0) { conn.rollback(); return new ChargeResult(false, 0, BigDecimal.ZERO, BigDecimal.ZERO); }

        // 2) Lấy đơn giá + số phút sử dụng
        int minutesUsed = 0;
        BigDecimal rate = BigDecimal.ZERO;
        try (PreparedStatement ps = conn.prepareStatement(
                "SELECT p.gia_phong, " +
                "       GREATEST(0, TIMESTAMPDIFF(MINUTE, nvp.thoi_gian_thue, nvp.thoi_gian_tra_phong)) AS minutes_used " +
                "FROM quan_ly_khach_san.Nhan_vien_phong nvp " +
                "JOIN quan_ly_khach_san.phong p ON p.id = nvp.id_phong " +
                "WHERE nvp.id_nhan_vien = ? AND nvp.id_phong = ? AND nvp.thoi_gian_thue = ?")) {
            ps.setInt(1, idNhanVien);
            ps.setInt(2, idPhong);
            ps.setTimestamp(3, thoiGianThue);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Gợi ý: về lâu dài đổi gia_phong -> DECIMAL trong DB để tránh sai số
                    rate = BigDecimal.valueOf(rs.getDouble("gia_phong"));
                    minutesUsed = rs.getInt("minutes_used");
                } else {
                    conn.rollback();
                    return new ChargeResult(false, 0, BigDecimal.ZERO, BigDecimal.ZERO);
                }
            }
        }

        // 3) Tính tiền: rate (đồng/giờ) * phút/60, làm tròn 0 chữ số (VND)
        BigDecimal amount = rate
                .multiply(BigDecimal.valueOf(minutesUsed))
                .divide(BigDecimal.valueOf(60), 0, RoundingMode.HALF_UP);

        // 4) Trả trạng thái phòng
        updateTrangThaiPhong(conn, idPhong, TRANG_THAI_CHUA_DAT);

        conn.commit();
        return new ChargeResult(true, minutesUsed, rate, amount);
    } catch (SQLException ex) {
        conn.rollback();
        throw ex;
    } finally {
        conn.setAutoCommit(prevAuto);
    }
}


    // ---------- SELECT (JOIN HIỂN THỊ) ----------
   public static List<NhanVienPhongView> listBookings(String keyword) throws SQLException {
    String like = (keyword == null || keyword.isEmpty()) ? "%" : "%" + keyword + "%";

    String sql = """
        SELECT nvp.id_nhan_vien,
               nv.ho_ten       AS ten_nhan_vien,
               nvp.id_phong,
               p.ten_phong,
               nvp.ten_khach_hang,
               nvp.so_cmnd_khach_hang,
               nvp.so_gio_thue,
               nvp.thoi_gian_thue,
               nvp.ghi_chu,
               nvp.thoi_gian_tra_phong,
               p.trang_thai,
               CASE
                 WHEN nvp.thoi_gian_tra_phong IS NULL THEN NULL
                 ELSE GREATEST(0, TIMESTAMPDIFF(MINUTE, nvp.thoi_gian_thue, nvp.thoi_gian_tra_phong))
               END AS so_phut_su_dung,
               CASE
                 WHEN nvp.thoi_gian_tra_phong IS NULL THEN NULL
                 ELSE ROUND(p.gia_phong * GREATEST(0, TIMESTAMPDIFF(MINUTE, nvp.thoi_gian_thue, nvp.thoi_gian_tra_phong)) / 60, 0)
               END AS tien_phong
        FROM quan_ly_khach_san.Nhan_vien_phong nvp
        JOIN quan_ly_khach_san.nhan_vien nv ON nv.id = nvp.id_nhan_vien
        JOIN quan_ly_khach_san.phong p      ON p.id  = nvp.id_phong
        WHERE nv.ho_ten LIKE ? OR p.ten_phong LIKE ? OR nvp.ten_khach_hang LIKE ? OR nvp.so_cmnd_khach_hang LIKE ?
        ORDER BY nvp.thoi_gian_thue DESC
    """;

    try (Connection conn = ConnectDB.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, like);
        ps.setString(2, like);
        ps.setString(3, like);
        ps.setString(4, like);

        try (ResultSet rs = ps.executeQuery()) {
            List<NhanVienPhongView> list = new ArrayList<>();
            while (rs.next()) {
                NhanVienPhongView v = new NhanVienPhongView();
                v.setId_nhan_vien(rs.getInt("id_nhan_vien"));
                v.setTen_nhan_vien(rs.getString("ten_nhan_vien"));
                v.setId_phong(rs.getInt("id_phong"));
                v.setTen_phong(rs.getString("ten_phong"));
                v.setTen_khach_hang(rs.getString("ten_khach_hang"));
                v.setSo_cmnd_khach_hang(rs.getString("so_cmnd_khach_hang"));
                v.setSo_gio_thue(rs.getInt("so_gio_thue"));
                v.setThoi_gian_thue(rs.getTimestamp("thoi_gian_thue"));
                v.setGhi_chu(rs.getString("ghi_chu"));
                v.setThoi_gian_tra_phong(rs.getTimestamp("thoi_gian_tra_phong"));
                v.setTrang_thai_phong(rs.getString("trang_thai"));

                // >>> ĐẶT 2 DÒNG NÀY Ở ĐÂY <<<
                v.setSo_phut_su_dung( rs.getInt("so_phut_su_dung")); // nhận được null nếu chưa trả
                v.setTien_phong(rs.getBigDecimal("tien_phong"));                  // nhận được null nếu chưa trả

                list.add(v);
            }
            return list;
        }
    }
}

    // View-model đơn giản để trả dữ liệu join ra UI
    public static class NhanVienPhongView {
        private int id_nhan_vien;
        private String ten_nhan_vien;
        private int id_phong;
        private String ten_phong;
        private String ten_khach_hang;
        private String so_cmnd_khach_hang;
        private int so_gio_thue;
        private Timestamp thoi_gian_thue;
        private String ghi_chu;
        private Timestamp thoi_gian_tra_phong;
        private String trang_thai_phong;
private Integer so_phut_su_dung;   // có thể null
private BigDecimal tien_phong;     // có thể null
        // getters/setters ...
        public int getId_nhan_vien() { return id_nhan_vien; }
        public void setId_nhan_vien(int v) { id_nhan_vien = v; }
        public String getTen_nhan_vien() { return ten_nhan_vien; }
        public void setTen_nhan_vien(String v) { ten_nhan_vien = v; }
        public int getId_phong() { return id_phong; }
        public void setId_phong(int v) { id_phong = v; }
        public String getTen_phong() { return ten_phong; }
        public void setTen_phong(String v) { ten_phong = v; }
        public String getTen_khach_hang() { return ten_khach_hang; }
        public void setTen_khach_hang(String v) { ten_khach_hang = v; }
        public String getSo_cmnd_khach_hang() { return so_cmnd_khach_hang; }
        public void setSo_cmnd_khach_hang(String v) { so_cmnd_khach_hang = v; }
        public int getSo_gio_thue() { return so_gio_thue; }
        public void setSo_gio_thue(int v) { so_gio_thue = v; }
        public Timestamp getThoi_gian_thue() { return thoi_gian_thue; }
        public void setThoi_gian_thue(Timestamp v) { thoi_gian_thue = v; }
        public String getGhi_chu() { return ghi_chu; }
        public void setGhi_chu(String v) { ghi_chu = v; }
        public Timestamp getThoi_gian_tra_phong() { return thoi_gian_tra_phong; }
        public void setThoi_gian_tra_phong(Timestamp v) { thoi_gian_tra_phong = v; }
        public String getTrang_thai_phong() { return trang_thai_phong; }
        public void setTrang_thai_phong(String v) { trang_thai_phong = v; }
        public Integer getSo_phut_su_dung() { return so_phut_su_dung; }
        public void setSo_phut_su_dung(Integer v) { so_phut_su_dung = v; }
        public BigDecimal getTien_phong() { return tien_phong; }
        public void setTien_phong(BigDecimal v) { tien_phong = v; }

    }
}
