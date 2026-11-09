package fisch.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import fisch.dao.SanPhamDAO;
import fisch.model.DongGioHang;
import fisch.model.SanPham;
import fisch.util.Util;

/**
 * Giỏ trade. Giỏ lưu trong HttpSession (thuộc tính "gh": id skin -> số lượng).
 * POST action = add | update | remove | checkout
 */
@WebServlet("/giohang")
public class GioHangServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Map<Integer, Integer> gh = layGioHang(request.getSession());
        List<DongGioHang> dong = new ArrayList<DongGioHang>();
        long tong = 0;
        for (Map.Entry<Integer, Integer> e : gh.entrySet()) {
            SanPham sp = SanPhamDAO.timTheoId(e.getKey());
            if (sp == null) {
                continue;
            }
            DongGioHang d = new DongGioHang(sp, e.getValue());
            tong += d.getThanhTien();
            dong.add(d);
        }
        request.setAttribute("dong", dong);
        request.setAttribute("tongStr", String.format(java.util.Locale.US, "%,d", tong));
        request.getRequestDispatcher("/WEB-INF/views/giohang.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Map<Integer, Integer> gh = layGioHang(session);
        String action = request.getParameter("action");
        int id = Util.toInt(request.getParameter("id"), -1);
        int sl = Util.toInt(request.getParameter("sl"), 1);
        if (sl < 1) {
            sl = 1; // số lượng không hợp lệ thì về 1
        }

        if ("add".equals(action)) {
            if (SanPhamDAO.timTheoId(id) != null) {
                Integer cu = gh.get(id);
                gh.put(id, cu == null ? sl : cu + sl); // đã có => tăng số lượng, chưa có => thêm mới
                session.setAttribute("msg", "Đã thêm skin vào giỏ trade!");
            }
            if ("giohang".equals(request.getParameter("next"))) {
                response.sendRedirect("giohang");
            } else {
                response.sendRedirect(quayLai(request));
            }
            return;
        }

        if ("update".equals(action)) {
            if (gh.containsKey(id)) {
                gh.put(id, sl);
            }
        } else if ("remove".equals(action)) {
            gh.remove(id);
        } else if ("checkout".equals(action)) {
            if (gh.isEmpty()) {
                session.setAttribute("msg", "Giỏ hàng đang trống!");
            } else {
                gh.clear();
                session.setAttribute("msg", "Thanh toán thành công!");
                response.sendRedirect("home");
                return;
            }
        }
        response.sendRedirect("giohang");
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, Integer> layGioHang(HttpSession session) {
        Map<Integer, Integer> gh = (Map<Integer, Integer>) session.getAttribute("gh");
        if (gh == null) {
            gh = new LinkedHashMap<Integer, Integer>();
            session.setAttribute("gh", gh);
        }
        return gh;
    }

    /** Quay lại trang vừa bấm "Thêm vào giỏ" (chỉ khi cùng host), không thì về danh sách skin. */
    private static String quayLai(HttpServletRequest request) {
        String ref = request.getHeader("Referer");
        String host = request.getScheme() + "://" + request.getServerName();
        if (ref != null && ref.startsWith(host)) {
            return ref;
        }
        return "sanpham";
    }
}
