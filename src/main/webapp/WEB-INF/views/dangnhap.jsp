<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fisch.util.Util" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng Nhập - Fisch Skin Trade</title>
    <link rel="stylesheet" href="css/chung.css">
    <link rel="stylesheet" href="css/form.css">
</head>
<body>
    <%@ include file="header.jspf" %>

    <div class="wrapper1">
        <h2>Đăng Nhập</h2>
        <% if (request.getAttribute("loi") != null) { %>
            <div class="loi"><%= Util.esc((String) request.getAttribute("loi")) %></div>
        <% } %>
        <form action="dangnhap" method="post">
            <label>Số phone hoặc Email</label>
            <input type="text" name="phone" value="<%= Util.esc((String) request.getAttribute("phone")) %>">

            <label>Mật khẩu</label>
            <input type="password" name="password">

            <input type="submit" value="Đăng nhập">
        </form>
    </div>

    <%@ include file="footer.jspf" %>
</body>
</html>
