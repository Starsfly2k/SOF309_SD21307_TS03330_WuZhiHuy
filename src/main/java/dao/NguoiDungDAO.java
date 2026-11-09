package fisch.dao;

import java.util.ArrayList;
import java.util.List;

import fisch.model.NguoiDung;

/** Danh sách người dùng đã đăng ký (thay cho localStorage 'u'). Lưu trong bộ nhớ, mất khi tắt server. */
public final class NguoiDungDAO {

    private static final List<NguoiDung> DS = new ArrayList<NguoiDung>();

    private NguoiDungDAO() {
    }

    /** Tìm theo số phone hoặc email (email không phân biệt hoa thường). */
    public static synchronized NguoiDung timTheoSdtHoacEmail(String khoa) {
        if (khoa == null) {
            return null;
        }
        String k = khoa.trim();
        for (NguoiDung u : DS) {
            if (u.getSdt().equals(k) || u.getEmail().equalsIgnoreCase(k)) {
                return u;
            }
        }
        return null;
    }

    /** @return false nếu trùng số phone hoặc email. */
    public static synchronized boolean them(NguoiDung nd) {
        for (NguoiDung u : DS) {
            if (u.getSdt().equals(nd.getSdt()) || u.getEmail().equals(nd.getEmail())) {
                return false;
            }
        }
        DS.add(nd);
        return true;
    }
}
