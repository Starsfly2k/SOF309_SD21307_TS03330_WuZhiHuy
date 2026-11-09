package fisch.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import fisch.dao.NguoiDungDAO;
import fisch.model.NguoiDung;
import fisch.util.Util;

@WebServlet("/dangky")
public class DangKyServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/dangky.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String ten = trim(request.getParameter("name"));
        String dt = trim(request.getParameter("phone"));
        String email = trim(request.getParameter("email"));
        String mk = nz(request.getParameter("password"));
        String remk = nz(request.getParameter("confirm"));

        String loi = kiemTra(ten, dt, email, mk, remk);
        if (loi == null) {
            NguoiDung nd = new NguoiDung(ten, dt, email.toLowerCase(), Util.sha256(mk));
            if (NguoiDungDAO.them(nd)) {
                request.getSession().setAttribute("msg", "Tài khoản đăng ký thành công");
                response.sendRedirect("dangnhap");
                return;
            }
            loi = "Tài khoản này đã đăng ký";
        }

        // Có lỗi: hiện lại form kèm dữ liệu đã nhập
        request.setAttribute("loi", loi);
        request.setAttribute("name", ten);
        request.setAttribute("phone", dt);
        request.setAttribute("email", email);
        request.getRequestDispatcher("/WEB-INF/views/dangky.jsp").forward(request, response);
    }

    private static String kiemTra(String ten, String dt, String email, String mk, String remk) {
        if (ten.isEmpty()) return "Bạn chưa nhập tên";
        if (dt.isEmpty()) return "Bạn chưa nhập số phone";
        if (email.isEmpty()) return "Bạn chưa nhập email";
        if (mk.isEmpty()) return "Bạn chưa nhập mật khẩu";
        if (remk.isEmpty()) return "Bạn chưa nhập lại mật khẩu";
        if (!dt.matches("\\d+")) return "Số phone phải là kiểu số";
        if (email.indexOf('@') < 1 || email.indexOf('.') < 0) return "Email không đúng định dạng";
        if (!mk.equals(remk)) return "Mật khẩu và mật khẩu nhập lại không khớp";
        return null;
    }

    private static String nz(String s) {
        return s == null ? "" : s;
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
