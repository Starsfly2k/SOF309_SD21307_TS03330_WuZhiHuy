package fisch.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fisch.model.SanPham;

/** Dữ liệu skin (thay cho mangsp.js). Lưu trong bộ nhớ. */
public final class SanPhamDAO {

    private static final List<SanPham> DS = new ArrayList<SanPham>();

    static {
        DS.add(new SanPham(1,  "Golden Aurora Rod",         90,   50,   "Skin_Golden_Aurora_Rod.png",     1, 1, "b"));
        DS.add(new SanPham(2,  "Depthseeker Rod",           900,  900,  "depthseeker-rod.webp",           1, 3, ""));
        DS.add(new SanPham(3,  "Coral Rod",                 100,  80,   "Skin_Coral_Specter.png",         1, 2, "b"));
        DS.add(new SanPham(4,  "Driftwood Rod",             70,   70,   "Skin_Driftwood_Voyager.png",     1, 1, ""));
        DS.add(new SanPham(5,  "Demonwake Boat",            2500, 2200, "Demonwake-Boat-Fisch.avif",      2, 4, "a"));
        DS.add(new SanPham(6,  "Atlantean Jetski",          1000, 850,  "Atlantean Jetski.jpg",           2, 3, "b"));
        DS.add(new SanPham(7,  "Frostbite",                 450,  450,  "Frostbite.png",                  2, 2, ""));
        DS.add(new SanPham(8,  "Scarwing",                  1000, 900,  "Skin_Scarwing.png",              1, 4, "a"));
        DS.add(new SanPham(9,  "Fischmas Speedboat Render", 2500, 2000, "Fischmas_Speedboat_Render.png",  2, 4, "a"));
        DS.add(new SanPham(10, "Heaven Rod",                100,  90,   "Skin_Heaven's_Love.png",         1, 1, "c"));
        DS.add(new SanPham(11, "Voided Fabulous Rod",       100,  90,   "Skin_Voided_Fabulous_Rod.png",   1, 1, "c"));
        DS.add(new SanPham(12, "Tideglass",                 200,  170,  "Tideglass.jpg",                  1, 3, ""));
        DS.add(new SanPham(13, "Malevolence",               600,  550,  "Skin_Malevolence.png",           1, 3, ""));
        DS.add(new SanPham(14, "Dreamline",                 1000, 900,  "Skin_Dreamline.png",             1, 4, "a"));
        DS.add(new SanPham(15, "Rainbow Revolution",        100,  80,   "Skin_Rainbow_Revolution.png",    1, 1, "b"));
        DS.add(new SanPham(16, "Rod Of The Depths",         75,   50,   "Rod_Of_The_Depths.png",          1, 1, "b"));
        DS.add(new SanPham(17, "Great Dreamer Rod",         300,  250,  "Great_Dreamer_Rod.jpg",          1, 4, "c"));
        DS.add(new SanPham(18, "Ethereal Prism Rod",        200,  175,  "Ethereal_Prism_Rod.png",         1, 1, "c"));
        DS.add(new SanPham(19, "Godbreaker",                800,  600,  "Godbreaker.png",                 1, 3, ""));
        DS.add(new SanPham(20, "Olympian Godbreaker",       1000, 850,  "Olympian_Godbreaker.png",        1, 3, ""));
        DS.add(new SanPham(21, "Neptune Demand",            80,   65,   "Skin_Neptune's_Demand.png",      1, 1, "c"));
        DS.add(new SanPham(22, "Ghost Ship",                400,  350,  "Ghost Ship Skin.webp",           2, 4, "a"));
    }

    private SanPhamDAO() {
    }

    public static List<SanPham> tatCa() {
        return Collections.unmodifiableList(DS);
    }

    public static SanPham timTheoId(int id) {
        for (SanPham sp : DS) {
            if (sp.getId() == id) {
                return sp;
            }
        }
        return null;
    }

    /** Skin theo loại đặc biệt: "a" - nổi bật, "b" - giảm giá. */
    public static List<SanPham> theoLoaiDacBiet(String loaidb) {
        List<SanPham> kq = new ArrayList<SanPham>();
        for (SanPham sp : DS) {
            if (sp.getLoaidb().equals(loaidb)) {
                kq.add(sp);
            }
        }
        return kq;
    }

    /** Tối đa 'toiDa' skin cùng loại (trừ chính nó). */
    public static List<SanPham> lienQuan(SanPham goc, int toiDa) {
        List<SanPham> kq = new ArrayList<SanPham>();
        for (SanPham sp : DS) {
            if (sp.getLoai() == goc.getLoai() && sp.getId() != goc.getId() && kq.size() < toiDa) {
                kq.add(sp);
            }
        }
        return kq;
    }

    /**
     * Lọc skin theo tên, loại, độ hiếm (0 = tất cả) và sắp xếp (0 = không, 1 = giá tăng, 2 = giá giảm).
     */
    public static List<SanPham> loc(String q, int loai, int dohiem, int sapXep) {
        String tuKhoa = q == null ? "" : q.trim().toLowerCase();
        List<SanPham> kq = new ArrayList<SanPham>();
        for (SanPham sp : DS) {
            boolean okTen = sp.getTen().toLowerCase().contains(tuKhoa);
            boolean okLoai = loai == 0 || sp.getLoai() == loai;
            boolean okHiem = dohiem == 0 || sp.getDohiem() == dohiem;
            if (okTen && okLoai && okHiem) {
                kq.add(sp);
            }
        }
        if (sapXep == 1) {
            Collections.sort(kq, new Comparator<SanPham>() {
                public int compare(SanPham a, SanPham b) { return a.getGiaban() - b.getGiaban(); }
            });
        } else if (sapXep == 2) {
            Collections.sort(kq, new Comparator<SanPham>() {
                public int compare(SanPham a, SanPham b) { return b.getGiaban() - a.getGiaban(); }
            });
        }
        return kq;
    }
}
