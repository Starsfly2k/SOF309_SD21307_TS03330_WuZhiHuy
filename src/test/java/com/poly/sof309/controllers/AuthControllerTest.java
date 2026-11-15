package com.poly.sof309.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import fisch.controller.DangKyServlet;
import fisch.controller.DangNhapServlet;
import fisch.controller.DangXuatServlet;
import fisch.dao.NguoiDungDAO;
import fisch.model.NguoiDung;
import fisch.util.Util;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Unit test cho nhóm Controller xác thực: DangKyServlet, DangNhapServlet, DangXuatServlet.
 *
 * Servlet được gọi qua service() (public) nên test nằm package nào cũng được,
 * không cần gọi trực tiếp doGet/doPost (protected).
 * HttpServletRequest/Response/Session được giả lập bằng Mockito.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@Timeout(value = 2, unit = TimeUnit.SECONDS) // Bước 2.4: mọi test trong class không được chạy quá 2 giây
class AuthControllerTest {

    @Mock HttpServletRequest request;
    @Mock HttpServletResponse response;
    @Mock HttpSession session;
    @Mock RequestDispatcher dispatcher;

    private final DangKyServlet dangKy = new DangKyServlet();
    private final DangNhapServlet dangNhap = new DangNhapServlet();
    private final DangXuatServlet dangXuat = new DangXuatServlet();

    /** NguoiDungDAO lưu trong bộ nhớ (static) nên tạo sẵn 2 tài khoản nền cho các test. */
    @BeforeAll
    static void taoTaiKhoanNen() {
        NguoiDungDAO.them(new NguoiDung("Tester Login", "0911000111", "login.test@fisch.com",
                Util.sha256("Pass1234")));
        NguoiDungDAO.them(new NguoiDung("Nguoi Co San", "0922333444", "dup@fisch.test",
                Util.sha256("Pass@1234")));
    }

    @BeforeEach
    void setUp() {
        when(request.getSession()).thenReturn(session);
        when(request.getRequestDispatcher(anyString())).thenReturn(dispatcher);
    }

    private void get(HttpServlet servlet) throws Exception {
        when(request.getMethod()).thenReturn("GET");
        servlet.service(request, response);
    }

    private void post(HttpServlet servlet) throws Exception {
        when(request.getMethod()).thenReturn("POST");
        servlet.service(request, response);
    }

    // =====================================================================
    // Bước 2.2 - Parameterized Test: kiểm tra biên form ĐĂNG KÝ
    // =====================================================================
    @ParameterizedTest(name = "[{index}] {6}")
    @DisplayName("Kiểm tra validation đăng ký tài khoản với nhiều bộ dữ liệu biên")
    @CsvSource({
        "'', '0901111111', 'a@x.com', 'Pass@1234', 'Pass@1234', false, 'Bạn chưa nhập tên'",
        "'An', '', 'a@x.com', 'Pass@1234', 'Pass@1234', false, 'Bạn chưa nhập số phone'",
        "'An', '0901111111', '', 'Pass@1234', 'Pass@1234', false, 'Bạn chưa nhập email'",
        "'An', '0901111111', 'a@x.com', '', '', false, 'Bạn chưa nhập mật khẩu'",
        "'An', '0901111111', 'a@x.com', 'Pass@1234', '', false, 'Bạn chưa nhập lại mật khẩu'",
        "'An', '09abc1111', 'a@x.com', 'Pass@1234', 'Pass@1234', false, 'Số phone phải là kiểu số'",
        "'An', '0901111111', 'khongphaiemail', 'Pass@1234', 'Pass@1234', false, 'Email không đúng định dạng'",
        "'An', '0901111111', 'a@x.com', 'Pass@1234', 'Khac@1234', false, 'Mật khẩu và mật khẩu nhập lại không khớp'",
        "'An', '0922333444', 'moi@x.com', 'Pass@1234', 'Pass@1234', false, 'Tài khoản này đã đăng ký'",
        "'An', '0933444555', 'DUP@fisch.test', 'Pass@1234', 'Pass@1234', false, 'Tài khoản này đã đăng ký'",
        "'Nguyen Van A', '0912345678', 'valid.user@fisch.test', 'Pass@1234', 'Pass@1234', true, 'Tài khoản đăng ký thành công'"
    })
    void testDangKyValidation(String name, String phone, String email, String password,
                              String confirm, boolean expectedSuccess, String message) throws Exception {
        when(request.getParameter("name")).thenReturn(name);
        when(request.getParameter("phone")).thenReturn(phone);
        when(request.getParameter("email")).thenReturn(email);
        when(request.getParameter("password")).thenReturn(password);
        when(request.getParameter("confirm")).thenReturn(confirm);

        post(dangKy);

        if (expectedSuccess) {
            verify(session).setAttribute("msg", message);
            verify(response).sendRedirect("dangnhap");
            verify(dispatcher, never()).forward(any(), any());
        } else {
            verify(request).setAttribute("loi", message);
            // giữ lại dữ liệu đã nhập để hiện lại form
            verify(request).setAttribute("name", name);
            verify(request).setAttribute("phone", phone);
            verify(request).setAttribute("email", email);
            verify(request).getRequestDispatcher("/WEB-INF/views/dangky.jsp");
            verify(dispatcher).forward(request, response);
            verify(response, never()).sendRedirect(anyString());
        }
    }

    @Test
    @DisplayName("Đăng ký thành công: tên được trim, email về chữ thường, mật khẩu lưu dạng SHA-256")
    void testDangKyLuuDuLieuDaChuanHoa() throws Exception {
        when(request.getParameter("name")).thenReturn("  Hash User  ");
        when(request.getParameter("phone")).thenReturn("0987654321");
        when(request.getParameter("email")).thenReturn("Hash.Test@Fisch.Test");
        when(request.getParameter("password")).thenReturn("Pass@1234");
        when(request.getParameter("confirm")).thenReturn("Pass@1234");

        post(dangKy);

        NguoiDung nd = NguoiDungDAO.timTheoSdtHoacEmail("0987654321");
        assertNotNull(nd, "Người dùng phải được lưu vào DAO");
        assertEquals("Hash User", nd.getTen());
        assertEquals("hash.test@fisch.test", nd.getEmail());
        assertEquals(Util.sha256("Pass@1234"), nd.getMatKhauMaHoa());
        assertNotEquals("Pass@1234", nd.getMatKhauMaHoa(), "Không được lưu mật khẩu dạng thô");
    }

    @Test
    @DisplayName("GET /dangky -> forward sang dangky.jsp")
    void testDangKyGetHienForm() throws Exception {
        get(dangKy);

        verify(request).getRequestDispatcher("/WEB-INF/views/dangky.jsp");
        verify(dispatcher).forward(request, response);
    }

    // =====================================================================
    // Bước 2.2 - Parameterized Test: ĐĂNG NHẬP (bộ dữ liệu hợp lệ + không hợp lệ)
    // =====================================================================
    @ParameterizedTest(name = "[{index}] {3}")
    @DisplayName("Kiểm tra đăng nhập với nhiều bộ dữ liệu")
    @CsvSource({
        "'0911000111', 'Pass1234', true, 'Đăng nhập thành công'",
        "'LOGIN.TEST@FISCH.COM', 'Pass1234', true, 'Đăng nhập thành công'",
        "'0911000111', 'saiMatKhau', false, 'Mật khẩu không đúng'",
        "'0000000000', 'Pass1234', false, 'Tài khoản chưa đăng ký'",
        "'', '', false, 'Tài khoản chưa đăng ký'"
    })
    void testDangNhapValidation(String phone, String password, boolean expectedSuccess,
                                String message) throws Exception {
        when(request.getParameter("phone")).thenReturn(phone);
        when(request.getParameter("password")).thenReturn(password);

        post(dangNhap);

        if (expectedSuccess) {
            verify(session).setAttribute(eq("dangnhap"), any(NguoiDung.class));
            verify(session).setAttribute("msg", message);
            verify(response).sendRedirect("home");
        } else {
            verify(request).setAttribute("loi", message);
            verify(request).setAttribute("phone", phone);
            verify(dispatcher).forward(request, response);
            verify(session, never()).setAttribute(eq("dangnhap"), any());
            verify(response, never()).sendRedirect(anyString());
        }
    }

    @Test
    @DisplayName("POST /dangnhap thiếu tham số (null) -> coi như tài khoản chưa đăng ký")
    void testDangNhapThieuThamSo() throws Exception {
        post(dangNhap); // getParameter không được stub nên trả về null

        verify(request).setAttribute("loi", "Tài khoản chưa đăng ký");
        verify(dispatcher).forward(request, response);
    }

    @Test
    @DisplayName("GET /dangnhap -> forward sang dangnhap.jsp")
    void testDangNhapGetHienForm() throws Exception {
        get(dangNhap);

        verify(request).getRequestDispatcher("/WEB-INF/views/dangnhap.jsp");
        verify(dispatcher).forward(request, response);
    }

    // =====================================================================
    // ĐĂNG XUẤT
    // =====================================================================
    @Test
    @DisplayName("GET /dangxuat -> xóa session 'dangnhap' và redirect về home")
    void testDangXuat() throws Exception {
        get(dangXuat);

        verify(session).removeAttribute("dangnhap");
        verify(response).sendRedirect("home");
    }

    // =====================================================================
    // Bước 2.3 - Exception Testing (assertThrows)
    // =====================================================================
    @Test
    @DisplayName("Controller không nuốt lỗi: forward JSP lỗi -> ném ServletException")
    void testDangKyNemServletExceptionKhiForwardLoi() throws Exception {
        doThrow(new ServletException("Lỗi forward JSP")).when(dispatcher).forward(any(), any());

        ServletException ex = assertThrows(ServletException.class, () -> get(dangKy));

        assertEquals("Lỗi forward JSP", ex.getMessage());
    }

    @Test
    @DisplayName("Controller không nuốt lỗi: redirect lỗi -> ném IOException")
    void testDangXuatNemIOExceptionKhiRedirectLoi() throws Exception {
        doThrow(new IOException("Mất kết nối")).when(response).sendRedirect(anyString());

        assertThrows(IOException.class, () -> get(dangXuat));
    }

    @Test
    @DisplayName("Util.sha256(null) (dùng khi băm mật khẩu) -> ném IllegalStateException")
    void testSha256NullNemIllegalStateException() {
        assertThrows(IllegalStateException.class, () -> Util.sha256(null));
    }
}
