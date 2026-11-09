<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="fisch.util.Util" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh Toán - Fisch Skin Trade</title>
    <link rel="stylesheet" href="css/chung.css">
    <link rel="stylesheet" href="css/form.css">
</head>
<body>
    <%@ include file="header.jspf" %>

    <div class="wrapper1 rong">
        <h2>Thông Tin Trade</h2>
        <form action="xacnhan" method="post">
            <label>Tên hiển thị</label>
            <input type="text" name="name" required value="<%= hdrUser != null ? Util.esc(hdrUser.getTen()) : "" %>">

            <label>Email</label>
            <input type="email" name="email" required value="<%= hdrUser != null ? Util.esc(hdrUser.getEmail()) : "" %>">

            <label>Username Roblox nhận skin</label>
            <textarea name="address" rows="3" required></textarea>

            <label>Phương thức thanh toán</label>
            <select name="payment">
                <option>Trade trực tiếp trong game</option>
                <option>Trả bằng Robux / chuyển khoản</option>
            </select>

            <input type="submit" value="Xác nhận trade">
        </form>
    </div>

    <%@ include file="footer.jspf" %>
</body>
</html>
