package fisch.model;

public class NguoiDung {
    private final String ten;
    private final String sdt;
    private final String email;
    private final String matKhauMaHoa;

    public NguoiDung(String ten, String sdt, String email, String matKhauMaHoa) {
        this.ten = ten;
        this.sdt = sdt;
        this.email = email;
        this.matKhauMaHoa = matKhauMaHoa;
    }

    public String getTen() { return ten; }
    public String getSdt() { return sdt; }
    public String getEmail() { return email; }
    public String getMatKhauMaHoa() { return matKhauMaHoa; }
}
