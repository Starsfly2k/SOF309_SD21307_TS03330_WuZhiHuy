package fisch.model;

/** Một skin (cần câu / thuyền) - tương ứng 1 phần tử của mảng sanpham trong mangsp.js. */
public class SanPham {
    public static final int LOAI_CAN_CAU = 1;
    public static final int LOAI_THUYEN = 2;

    private final int id;
    private final String ten;
    private final int giagoc;
    private final int giaban;
    private final String hinh;
    private final int loai;     // 1 - Cần câu, 2 - Thuyền
    private final int dohiem;   // 1 - Common, 2 - Rare, 3 - Legendary, 4 - Mythic
    private final String loaidb; // "a" - Nổi bật, "b" - Giảm giá, "c" - Mới, "" - Bình thường

    public SanPham(int id, String ten, int giagoc, int giaban, String hinh, int loai, int dohiem, String loaidb) {
        this.id = id;
        this.ten = ten;
        this.giagoc = giagoc;
        this.giaban = giaban;
        this.hinh = hinh;
        this.loai = loai;
        this.dohiem = dohiem;
        this.loaidb = loaidb;
    }

    public int getId() { return id; }
    public String getTen() { return ten; }
    public int getGiagoc() { return giagoc; }
    public int getGiaban() { return giaban; }
    public String getHinh() { return hinh; }
    public int getLoai() { return loai; }
    public int getDohiem() { return dohiem; }
    public String getLoaidb() { return loaidb; }

    /** Đường dẫn ảnh (tương đối) đã mã hóa khoảng trắng và dấu nháy. */
    public String getHinhUrl() {
        return "imgs/" + hinh.replace(" ", "%20").replace("'", "%27");
    }

    public String getGiabanStr() { return String.format(java.util.Locale.US, "%,d", giaban); }
}
