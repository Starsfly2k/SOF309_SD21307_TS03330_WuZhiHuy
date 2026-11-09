package fisch.model;

import java.util.Locale;

/** Một dòng trong giỏ hàng: skin + số lượng. */
public class DongGioHang {
    private final SanPham sp;
    private final int sl;

    public DongGioHang(SanPham sp, int sl) {
        this.sp = sp;
        this.sl = sl;
    }

    public SanPham getSp() { return sp; }
    public int getSl() { return sl; }
    public long getThanhTien() { return (long) sp.getGiaban() * sl; }
    public String getThanhTienStr() { return String.format(Locale.US, "%,d", getThanhTien()); }
}
