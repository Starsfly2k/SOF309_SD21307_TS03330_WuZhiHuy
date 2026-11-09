package fisch.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import fisch.dao.SanPhamDAO;
import fisch.model.SanPham;
import fisch.util.Util;

/** Chi tiết skin: /chitiet?id=5. Không tìm thấy thì lấy skin đầu tiên (giống bản JS). */
@WebServlet("/chitiet")
public class ChiTietServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SanPham sp = SanPhamDAO.timTheoId(Util.toInt(request.getParameter("id"), -1));
        if (sp == null) {
            sp = SanPhamDAO.tatCa().get(0);
        }
        request.setAttribute("sp", sp);
        request.setAttribute("lienquan", SanPhamDAO.lienQuan(sp, 4));
        request.getRequestDispatcher("/WEB-INF/views/chitiet.jsp").forward(request, response);
    }
}
