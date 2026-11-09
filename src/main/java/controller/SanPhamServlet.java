package fisch.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import fisch.dao.SanPhamDAO;
import fisch.util.Util;

/** Trang tất cả skin + bộ lọc: ?q=...&l=0|1|2&r=0..4&o=0|1|2 */
@WebServlet("/sanpham")
public class SanPhamServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String q = request.getParameter("q");
        int l = Util.toInt(request.getParameter("l"), 0);
        int r = Util.toInt(request.getParameter("r"), 0);
        int o = Util.toInt(request.getParameter("o"), 0);

        request.setAttribute("ds", SanPhamDAO.loc(q, l, r, o));
        request.setAttribute("q", q == null ? "" : q);
        request.setAttribute("l", l);
        request.setAttribute("r", r);
        request.setAttribute("o", o);
        request.getRequestDispatcher("/WEB-INF/views/sanpham.jsp").forward(request, response);
    }
}
