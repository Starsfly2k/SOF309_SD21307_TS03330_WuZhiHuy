package com.poly.sof309.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import fisch.controller.ChiTietServlet;
import fisch.controller.HomeServlet;
import fisch.controller.SanPhamServlet;
import fisch.model.SanPham;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Unit test cho nhóm Controller sản phẩm: HomeServlet, SanPhamServlet (lọc/sắp xếp), ChiTietServlet.
 * Dữ liệu skin lấy từ SanPhamDAO (22 skin lưu sẵn trong bộ nhớ).
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Timeout(value = 2, unit = TimeUnit.SECONDS) // Bước 2.4: xử lý lọc/sắp xếp không được treo quá 2 giây
class ProductControllerTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock RequestDispatcher dispatcher;

    private final HomeServlet home = new HomeServlet();
    private final SanPhamServlet sanPham = new SanPhamServlet();
    private final ChiTietServlet chiTiet = new ChiTietServlet();

    @BeforeEach
    void setUp() {
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    private void get(HttpServlet servlet) throws Exception {
        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);
    }

    /** Lấy giá trị mà controller đã request.setAttribute(ten, ...). */
    private Object layThuocTinh(String ten) {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(request).setAttribute(eq(ten), captor.capture());
        return captor.getValue();
    }

    // =====================================================================
    // HomeServlet
    // =====================================================================
    @Test
    @DisplayName("GET /home -> có 5 skin nổi bật (a), 5 skin giảm giá (b) và forward index.jsp")
    void testHomeHienSkinNoiBatVaGiamGia() throws Exception {
        get(home);

        List<?> hot = (List<?>) layThuocTinh("hot");
        List<?> sale = (List<?>) layThuocTinh("sale");
        assertEquals(5, hot.size());
        assertEquals(5, sale.size());
        for (Object o : hot) {
            assertEquals("a", ((SanPham) o).getLoaidb());
        }
        for (Object o : sale) {
            assertEquals("b", ((SanPham) o).getLoaidb());
        }
        verify(request).getRequestDispatcher("/WEB-INF/views/index.jsp");
        verify(dispatcher).forward(request, response);
    }

    // =====================================================================
    // SanPhamServlet - Bước 2.2: Parameterized Test cho bộ lọc
    // =====================================================================
    @ParameterizedTest(name = "[{index}] q={0}, loai={1}, hiem={2}, sapxep={3} => {4} skin")
    @DisplayName("Lọc skin theo từ khóa / loại / độ hiếm với nhiều bộ dữ liệu biên")
    @CsvSource({
        "'', 0, 0, 0, 22",
        "'rod', 0, 0, 0, 9",
        "'ROD', 0, 0, 0, 9",
        "'  rod  ', 0, 0, 0, 9",
        "'', 1, 0, 0, 17",
        "'', 2, 0, 0, 5",
        "'', 0, 4, 0, 6",
        "'', 0, 3, 0, 6",
        "'rod', 1, 1, 0, 6",
        "'boat', 2, 0, 0, 2",
        "'', 2, 4, 0, 3",
        "'xyz', 0, 0, 0, 0",
        "'rod', 2, 0, 0, 0"
    })
    void testSanPhamLoc(String q, int loai, int dohiem, int sapXep, int expectedCount) throws Exception {
        when(request.getParameter("q")).thenReturn(q);
        when(request.getParameter("l")).thenReturn(String.valueOf(loai));
        when(request.getParameter("r")).thenReturn(String.valueOf(dohiem));
        when(request.getParameter("o")).thenReturn(String.valueOf(sapXep));

        get(sanPham);

        List<?> ds = (List<?>) layThuocTinh("ds");
        assertEquals(expectedCount, ds.size());
        // giữ lại điều kiện lọc để JSP hiển thị lại trên form
        verify(request).setAttribute("q", q);
        verify(request).setAttribute("l", loai);
        verify(request).setAttribute("r", dohiem);
        verify(request).setAttribute("o", sapXep);
        verify(request).getRequestDispatcher("/WEB-INF/views/sanpham.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Tham số sai kiểu (abc) hoặc thiếu (null) -> dùng giá trị mặc định, trả đủ 22 skin")
    void testSanPhamThamSoSaiVeMacDinh() throws Exception {
        when(request.getParameter("l")).thenReturn("abc");
        when(request.getParameter("r")).thenReturn("");
        // q và o không stub -> null

        get(sanPham);

        List<?> ds = (List<?>) layThuocTinh("ds");
        assertEquals(22, ds.size());
        verify(request).setAttribute("q", "");
        verify(request).setAttribute("l", 0);
        verify(request).setAttribute("r", 0);
        verify(request).setAttribute("o", 0);
    }

    @Test
    @DisplayName("Sắp xếp giá tăng dần (o=1)")
    void testSanPhamSapXepGiaTang() throws Exception {
        when(request.getParameter("o")).thenReturn("1");

        get(sanPham);

        List<?> ds = (List<?>) layThuocTinh("ds");
        assertEquals(22, ds.size());
        for (int i = 1; i < ds.size(); i++) {
            int truoc = ((SanPham) ds.get(i - 1)).getGiaban();
            int sau = ((SanPham) ds.get(i)).getGiaban();
            assertTrue(truoc <= sau, "Giá phải tăng dần tại vị trí " + i);
        }
        assertEquals(50, ((SanPham) ds.get(0)).getGiaban());
        assertEquals(2200, ((SanPham) ds.get(ds.size() - 1)).getGiaban());
    }

    @Test
    @DisplayName("Sắp xếp giá giảm dần (o=2)")
    void testSanPhamSapXepGiaGiam() throws Exception {
        when(request.getParameter("o")).thenReturn("2");

        get(sanPham);

        List<?> ds = (List<?>) layThuocTinh("ds");
        assertEquals(22, ds.size());
        for (int i = 1; i < ds.size(); i++) {
            int truoc = ((SanPham) ds.get(i - 1)).getGiaban();
            int sau = ((SanPham) ds.get(i)).getGiaban();
            assertTrue(truoc >= sau, "Giá phải giảm dần tại vị trí " + i);
        }
        assertEquals(2200, ((SanPham) ds.get(0)).getGiaban());
        assertEquals(50, ((SanPham) ds.get(ds.size() - 1)).getGiaban());
    }

    // =====================================================================
    // ChiTietServlet - Bước 2.2: Parameterized Test cho tham số id
    // =====================================================================
    @ParameterizedTest(name = "[{index}] id={0} => hiện skin số {1}")
    @DisplayName("Chi tiết skin: id hợp lệ hiện đúng skin, id sai/không tồn tại thì lấy skin đầu tiên")
    @CsvSource({
        "5, 5",
        "22, 22",
        "1, 1",
        "abc, 1",
        "999, 1",
        "0, 1",
        "-3, 1"
    })
    void testChiTietTimSkin(String idNhap, int idMongDoi) throws Exception {
        when(request.getParameter("id")).thenReturn(idNhap);

        get(chiTiet);

        SanPham sp = (SanPham) layThuocTinh("sp");
        assertEquals(idMongDoi, sp.getId());

        // skin liên quan: tối đa 4, cùng loại, không chứa chính nó
        List<?> lienQuan = (List<?>) layThuocTinh("lienquan");
        assertEquals(4, lienQuan.size());
        for (Object o : lienQuan) {
            SanPham x = (SanPham) o;
            assertEquals(sp.getLoai(), x.getLoai());
            assertNotEquals(sp.getId(), x.getId());
        }
        verify(request).getRequestDispatcher("/WEB-INF/views/chitiet.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("Thiếu tham số id (null) -> hiện skin đầu tiên")
    void testChiTietThieuId() throws Exception {
        get(chiTiet);

        SanPham sp = (SanPham) layThuocTinh("sp");
        assertEquals(1, sp.getId());
    }

    // =====================================================================
    // Bước 2.3 - Exception Testing (assertThrows)
    // =====================================================================
    @Test
    @DisplayName("Controller không nuốt lỗi: forward JSP lỗi -> ném ServletException")
    void testHomeNemServletExceptionKhiForwardLoi() throws Exception {
        doThrow(new ServletException("Không tìm thấy index.jsp")).when(dispatcher).forward(any(), any());

        ServletException ex = assertThrows(ServletException.class, () -> get(home));

        assertEquals("Không tìm thấy index.jsp", ex.getMessage());
    }

    @Test
    @DisplayName("Controller không nuốt lỗi: forward JSP lỗi I/O -> ném IOException")
    void testSanPhamNemIOExceptionKhiForwardLoi() throws Exception {
        doThrow(new IOException("Lỗi ghi response")).when(dispatcher).forward(any(), any());

        assertThrows(IOException.class, () -> get(sanPham));
    }
}
