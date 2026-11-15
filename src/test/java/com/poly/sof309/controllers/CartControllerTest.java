package com.poly.sof309.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

import fisch.controller.GioHangServlet;
import fisch.controller.ThanhToanServlet;
import fisch.controller.XacNhanServlet;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Unit test cho nhóm Controller giỏ trade: GioHangServlet, ThanhToanServlet, XacNhanServlet.
 * Giỏ hàng lưu trong HttpSession (thuộc tính "gh": id skin -> số lượng); ở đây session được giả lập
 * bằng Mockito và trả về một LinkedHashMap thật để kiểm tra nội dung giỏ sau mỗi thao tác.
 *
 * Giá skin dùng trong test (SanPhamDAO): id 1 = 50, id 2 = 900, id 5 = 2200.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Timeout(value = 2, unit = TimeUnit.SECONDS) // Bước 2.4: xử lý giỏ hàng không được treo quá 2 giây
class CartControllerTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;

    private final GioHangServlet gioHangServlet = new GioHangServlet();
    private final ThanhToanServlet thanhToan = new ThanhToanServlet();
    private final XacNhanServlet xacNhan = new XacNhanServlet();

    /** Giỏ hàng thật mà session giả trả về. */
    private Map<Integer, Integer> gioHang;

    @BeforeEach
    void setUp() {
        gioHang = new LinkedHashMap<Integer, Integer>();
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute("gh")).thenReturn(gioHang);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
        when(request.getScheme()).thenReturn("http");
        when(request.getServerName()).thenReturn("localhost");
    }

    private void get(HttpServlet servlet) throws Exception {
        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);
    }

    private void post(HttpServlet servlet) throws Exception {
        when(request.getMethod()).thenReturn("POST");
        servlet.service(request, response);
    }

    private void thamSo(String action, String id, String sl) {
        when(request.getParameter("action")).thenReturn(action);
        when(request.getParameter("id")).thenReturn(id);
        when(request.getParameter("sl")).thenReturn(sl);
    }

    private Object layThuocTinh(String ten) {
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(request).setAttribute(eq(ten), captor.capture());
        return captor.getValue();
    }

    // =====================================================================
    // GioHangServlet - doGet (xem giỏ)
    // =====================================================================
    @Test
    @DisplayName("GET /giohang giỏ rỗng -> tổng tiền 0 và forward giohang.jsp")
    void testXemGioHangRong() throws Exception {
        get(gioHangServlet);

        List<?> dong = (List<?>) layThuocTinh("dong");
        assertTrue(dong.isEmpty());
        verify(request).setAttribute("tongStr", "0");
        verify(request).getRequestDispatcher("/WEB-INF/views/giohang.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /giohang có hàng -> tính đúng tổng tiền, bỏ qua skin không tồn tại")
    void testXemGioHangCoHang() throws Exception {
        gioHang.put(1, 2);   // 2 x 50   = 100
        gioHang.put(5, 1);   // 1 x 2200 = 2200
        gioHang.put(999, 3); // skin không tồn tại -> bị bỏ qua

        get(gioHangServlet);

        List<?> dong = (List<?>) layThuocTinh("dong");
        assertEquals(2, dong.size());
        verify(request).setAttribute("tongStr", "2,300");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /giohang khi session chưa có giỏ -> tự tạo giỏ mới lưu vào session")
    void testXemGioHangTaoGioMoi() throws Exception {
        when(session.getAttribute("gh")).thenReturn(null);

        get(gioHangServlet);

        verify(session).setAttribute(eq("gh"), any(Map.class));
        verify(request).setAttribute("tongStr", "0");
    }

    // =====================================================================
    // GioHangServlet - doPost action = add
    // =====================================================================
    @Test
    @DisplayName("POST add skin mới (next=giohang) -> thêm vào giỏ, báo msg và redirect giohang")
    void testThemSkinMoi() throws Exception {
        thamSo("add", "1", "2");
        when(request.getParameter("next")).thenReturn("giohang");

        post(gioHangServlet);

        assertEquals(2, gioHang.get(1));
        verify(session).setAttribute("msg", "Đã thêm skin vào giỏ trade!");
        verify(response).sendRedirect("giohang");
    }

    @Test
    @DisplayName("POST add skin đã có trong giỏ -> cộng dồn số lượng")
    void testThemSkinDaCoThiTangSoLuong() throws Exception {
        gioHang.put(1, 1);
        thamSo("add", "1", "2");

        post(gioHangServlet);

        assertEquals(3, gioHang.get(1));
    }

    @Test
    @DisplayName("POST add skin không tồn tại (id=999) -> giỏ không đổi, không có msg")
    void testThemSkinKhongTonTai() throws Exception {
        thamSo("add", "999", "1");

        post(gioHangServlet);

        assertTrue(gioHang.isEmpty());
        verify(session, never()).setAttribute(eq("msg"), any());
        verify(response).sendRedirect("sanpham");
    }

    @ParameterizedTest(name = "[{index}] sl nhập \"{0}\" => số lượng trong giỏ = {1}")
    @DisplayName("Số lượng không hợp lệ (âm, 0, chữ, rỗng) được đưa về 1")
    @CsvSource({
        "'-5', 1",
        "'0', 1",
        "'abc', 1",
        "'', 1",
        "'1', 1",
        "'3', 3",
        "'10', 10"
    })
    void testSoLuongBienKhiThem(String slNhap, int slMongDoi) throws Exception {
        thamSo("add", "2", slNhap);

        post(gioHangServlet);

        assertEquals(slMongDoi, gioHang.get(2));
    }

    @ParameterizedTest(name = "[{index}] Referer={0} => redirect {1}")
    @DisplayName("Sau khi thêm vào giỏ: chỉ quay lại Referer cùng host, ngược lại về sanpham")
    @CsvSource({
        "'http://localhost/sanpham?q=rod', 'http://localhost/sanpham?q=rod'",
        "'http://localhost:8080/FischSkinTrade/chitiet?id=3', 'http://localhost:8080/FischSkinTrade/chitiet?id=3'",
        "'http://evil.com/phish', 'sanpham'",
        "'https://localhost/sanpham', 'sanpham'",
        "'', 'sanpham'"
    })
    void testQuayLaiTrangTruoc(String referer, String redirectMongDoi) throws Exception {
        thamSo("add", "1", "1");
        when(request.getHeader("Referer")).thenReturn(referer);

        post(gioHangServlet);

        verify(response).sendRedirect(redirectMongDoi);
    }

    // =====================================================================
    // GioHangServlet - doPost action = update | remove | checkout
    // =====================================================================
    @Test
    @DisplayName("POST update skin có trong giỏ -> đổi số lượng")
    void testCapNhatSoLuong() throws Exception {
        gioHang.put(1, 1);
        thamSo("update", "1", "5");

        post(gioHangServlet);

        assertEquals(5, gioHang.get(1));
        verify(response).sendRedirect("giohang");
    }

    @Test
    @DisplayName("POST update skin không có trong giỏ -> không thêm mới")
    void testCapNhatSkinKhongCoTrongGio() throws Exception {
        thamSo("update", "2", "5");

        post(gioHangServlet);

        assertFalse(gioHang.containsKey(2));
        verify(response).sendRedirect("giohang");
    }

    @Test
    @DisplayName("POST remove -> xóa skin khỏi giỏ")
    void testXoaSkin() throws Exception {
        gioHang.put(1, 2);
        gioHang.put(5, 1);
        thamSo("remove", "1", null);

        post(gioHangServlet);

        assertFalse(gioHang.containsKey(1));
        assertTrue(gioHang.containsKey(5));
        verify(response).sendRedirect("giohang");
    }

    @Test
    @DisplayName("POST checkout giỏ rỗng -> báo giỏ trống, ở lại giohang")
    void testThanhToanGioRong() throws Exception {
        thamSo("checkout", null, null);

        post(gioHangServlet);

        verify(session).setAttribute("msg", "Giỏ hàng đang trống!");
        verify(response).sendRedirect("giohang");
    }

    @Test
    @DisplayName("POST checkout có hàng -> xóa giỏ, báo thành công, về home")
    void testThanhToanThanhCong() throws Exception {
        gioHang.put(1, 2);
        thamSo("checkout", null, null);

        post(gioHangServlet);

        assertTrue(gioHang.isEmpty());
        verify(session).setAttribute("msg", "Thanh toán thành công!");
        verify(response).sendRedirect("home");
        verify(response, never()).sendRedirect("giohang");
    }

    @Test
    @DisplayName("POST action lạ -> không đổi giỏ, redirect về giohang")
    void testActionLa() throws Exception {
        gioHang.put(1, 2);
        thamSo("xyz", "1", "9");

        post(gioHangServlet);

        assertEquals(2, gioHang.get(1));
        verify(response).sendRedirect("giohang");
    }

    // =====================================================================
    // ThanhToanServlet, XacNhanServlet
    // =====================================================================
    @Test
    @DisplayName("GET /thanhtoan -> forward thanhtoan.jsp")
    void testThanhToanHienForm() throws Exception {
        get(thanhToan);

        verify(request).getRequestDispatcher("/WEB-INF/views/thanhtoan.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("POST /xacnhan -> gửi tên sang xacnhan.jsp")
    void testXacNhanPost() throws Exception {
        when(request.getParameter("name")).thenReturn("Wu Zhi Huy");

        post(xacNhan);

        verify(request).setAttribute("name", "Wu Zhi Huy");
        verify(request).getRequestDispatcher("/WEB-INF/views/xacnhan.jsp");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /xacnhan -> redirect về thanhtoan (không cho vào thẳng)")
    void testXacNhanGetChuyenVeThanhToan() throws Exception {
        get(xacNhan);

        verify(response).sendRedirect("thanhtoan");
        verify(dispatcher, never()).forward(any(), any());
    }

    // =====================================================================
    // Bước 2.3 - Exception Testing (assertThrows)
    // =====================================================================
    @Test
    @DisplayName("Controller không nuốt lỗi: forward giohang.jsp lỗi -> ném ServletException")
    void testXemGioHangNemServletExceptionKhiForwardLoi() throws Exception {
        doThrow(new ServletException("Lỗi forward giohang.jsp")).when(dispatcher).forward(any(), any());

        ServletException ex = assertThrows(ServletException.class, () -> get(gioHangServlet));

        assertEquals("Lỗi forward giohang.jsp", ex.getMessage());
    }

    @Test
    @DisplayName("Controller không nuốt lỗi: redirect lỗi -> ném IOException")
    void testThemSkinNemIOExceptionKhiRedirectLoi() throws Exception {
        thamSo("add", "1", "1");
        when(request.getParameter("next")).thenReturn("giohang");
        doThrow(new IOException("Mất kết nối")).when(response).sendRedirect(anyString());

        assertThrows(IOException.class, () -> post(gioHangServlet));
    }
}