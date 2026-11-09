<% %>@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, fisch.model.SanPham" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
    @SuppressWarnings("unchecked")
    List<SanPham> lienquan = (List<SanPham>) request.getAttribute("lienquan");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>${sp.ten} - Fisch Skin Trade</title>
    <link rel="stylesheet" href="css/chung.css">
    <link rel="stylesheet" href="css/product.css">
    <link rel="stylesheet" href="css/form.css">
</head>
<body>
    <%@ include file="header.jspf" %>

    <div class="wrapper">
        <!-- CHI TIẾT SKIN -->
        <div class="clearfix" style="margin-top: 30px">
            <div style="float: left; width: 35%;">
                <img src="${sp.hinhUrl}" alt="${sp.ten}" style="width: 100%; border-radius: 8px;">
            </div>
            <div style="float: left; width: 60%; margin-left: 5%;">
                <h1>${sp.ten}</h1>
                <p><strong>Giá bán:</strong> <span class="price-new" style="font-size: 24px;">${sp.giaban} R$</span></p>
                <p><strong>Giá gốc:</strong> <span class="price-old"><del>${sp.giagoc} R$</del></span></p>

                <form method="post" action="giohang">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="id" value="${sp.id}">
                    <p>Số lượng: <input type="number" name="sl" min="1" value="1" style="width: 50px;"></p>
                    <br>
                    <button type="submit" class="btn">Thêm vào giỏ</button>
                    <button type="submit" class="btn alt" name="next" value="giohang">Trade ngay</button>
                </form>

                <h3 style="margin-top: 20px;">Thông tin trade</h3>
                <ul>
                    <li>Giao skin trực tiếp trong game Fisch</li>
                    <li>Kiểm tra username Roblox trước khi trade</li>
                    <li>Hỗ trợ trung gian uy tín 24/7</li>
                </ul>
            </div>
        </div>

        <h2>Skin liên quan</h2>
        <div class="container clearfix">
            <% for (SanPham item : lienquan) { %>
                <t:card sp="<%= item %>"/>
            <% } %>
        </div>
    </div>

    <%@ include file="footer.jspf" %>
</body>
</html>
