<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fisch.util.Util" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đăng Ký - Fisch Skin Trade</title>
    <link rel="stylesheet" href="css/chung.css">
    <link rel="stylesheet" href="css/form.css">
</head>
<body>
    <%@ include file="header.jspf" %>

    <div class="wrapper1">
        <h2>Đăng Ký Tài Khoản</h2>
        <% if (request.getAttribute("loi") != null) { %>
            <div class="loi"><%= Util.esc((String) request.getAttribute("loi")) %></div>
        <% } %>
        <form action="dangky" method="post">
            <label>Tên hiển thị</label>
            <input type="text" name="name" value="<%= Util.esc((String) request.getAttribute("name")) %>">

            <label>Số phone</label>
            <input type="text" name="phone" value="<%= Util.esc((String) request.getAttribute("phone")) %>">

            <label>Email</label>
            <input type="text" name="email" value="<%= Util.esc((String) request.getAttribute("email")) %>">

            <label>Mật khẩu</label>
            <input type="password" name="password">

            <label>Nhập lại mật khẩu</label>
            <input type="password" name="confirm">

            <input type="submit" value="Đăng ký">
        </form>
    </div>

    <%@ include file="footer.jspf" %>
</body>
</html>
