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

@WebServlet("/dangnhap")
public class DangNhapServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/dangnhap.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String khoa = request.getParameter("phone");
        String ps = request.getParameter("password");
        if (khoa == null) khoa = "";
        if (ps == null) ps = "";

        NguoiDung nd = NguoiDungDAO.timTheoSdtHoacEmail(khoa);
        String loi = null;
        if (nd == null) {
            loi = "Tài khoản chưa đăng ký";
        } else if (!nd.getMatKhauMaHoa().equals(Util.sha256(ps))) {
            loi = "Mật khẩu không đúng";
        }

        if (loi == null) {
            request.getSession().setAttribute("dangnhap", nd);
            request.getSession().setAttribute("msg", "Đăng nhập thành công");
            response.sendRedirect("home");
        } else {
            request.setAttribute("loi", loi);
            request.setAttribute("phone", khoa);
            request.getRequestDispatcher("/WEB-INF/views/dangnhap.jsp").forward(request, response);
        }
    }
}
