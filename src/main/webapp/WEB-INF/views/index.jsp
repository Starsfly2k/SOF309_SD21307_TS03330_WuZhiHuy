<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, fisch.model.SanPham" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
    @SuppressWarnings("unchecked")
    List<SanPham> hot = (List<SanPham>) request.getAttribute("hot");
    @SuppressWarnings("unchecked")
    List<SanPham> sale = (List<SanPham>) request.getAttribute("sale");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Fisch Skin Trade - Trade Skin Fisch Roblox</title>
    <link rel="stylesheet" href="css/chung.css">
    <link rel="stylesheet" href="css/product.css">
    <link rel="stylesheet" href="css/form.css">
</head>
<body>
    <%@ include file="header.jspf" %>

    <!-- BANNER -->
    <div class="hero">
        <h1>🎣 Fisch Skin Trade</h1>
        <p>Mua - bán - trade skin cần câu và thuyền cho Fisch trên Roblox</p>
        <a href="sanpham"><button class="btn" type="button">Xem tất cả skin</button></a>
    </div>

    <div class="wrapper">
        <h2 style="text-align: center">Skin Mythic nổi bật</h2>
        <div class="container clearfix">
            <% for (SanPham s : hot) { %>
                <t:card sp="<%= s %>"/>
            <% } %>
        </div>

        <h2 style="text-align: center">Đang giảm giá</h2>
        <div class="container clearfix">
            <% for (SanPham s : sale) { %>
                <t:card sp="<%= s %>"/>
            <% } %>
        </div>
    </div>

    <%@ include file="footer.jspf" %>
</body>
</html>
