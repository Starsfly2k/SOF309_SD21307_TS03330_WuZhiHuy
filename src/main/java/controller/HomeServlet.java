package fisch.controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import fisch.dao.SanPhamDAO;

/** Trang chủ: skin nổi bật (loaidb = a) và đang giảm giá (loaidb = b). */
@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("hot", SanPhamDAO.theoLoaiDacBiet("a"));
        request.setAttribute("sale", SanPhamDAO.theoLoaiDacBiet("b"));
        request.getRequestDispatcher("/WEB-INF/views/index.jsp").forward(request, response);
    }
}
