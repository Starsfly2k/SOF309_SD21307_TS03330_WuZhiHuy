<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, fisch.model.SanPham, fisch.util.Util" %>
<%@ taglib prefix="t" tagdir="/WEB-INF/tags" %>
<%
    @SuppressWarnings("unchecked")
    List<SanPham> ds = (List<SanPham>) request.getAttribute("ds");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Tất Cả Skin - Fisch Skin Trade</title>
    <link rel="stylesheet" href="css/chung.css">
    <link rel="stylesheet" href="css/product.css">
    <link rel="stylesheet" href="css/form.css">
</head>
<body>
    <%@ include file="header.jspf" %>

    <div class="wrapper">
        <h1 style="text-align: center">Tất cả skin</h1>

        <!-- BỘ LỌC -->
        <form class="filters" method="get" action="sanpham">
            <input name="q" placeholder="Tìm tên skin..." value="<%= Util.esc((String) request.getAttribute("q")) %>">

            <select name="l" onchange="this.form.submit()">
                <option value="0" ${l == 0 ? 'selected' : ''}>Mọi loại</option>
                <option value="1" ${l == 1 ? 'selected' : ''}>Cần câu</option>
                <option value="2" ${l == 2 ? 'selected' : ''}>Thuyền</option>
            </select>

            <select name="r" onchange="this.form.submit()">
                <option value="0" ${r == 0 ? 'selected' : ''}>Mọi độ hiếm</option>
                <option value="1" ${r == 1 ? 'selected' : ''}>Common</option>
                <option value="2" ${r == 2 ? 'selected' : ''}>Rare</option>
                <option value="3" ${r == 3 ? 'selected' : ''}>Legendary</option>
                <option value="4" ${r == 4 ? 'selected' : ''}>Mythic</option>
            </select>

            <select name="o" onchange="this.form.submit()">
                <option value="0" ${o == 0 ? 'selected' : ''}>Sắp xếp</option>
                <option value="1" ${o == 1 ? 'selected' : ''}>Giá tăng dần</option>
                <option value="2" ${o == 2 ? 'selected' : ''}>Giá giảm dần</option>
            </select>

            <button type="submit" class="btn">Tìm</button>
        </form>

        <!-- DANH SÁCH SKIN -->
        <div class="container clearfix">
            <% if (ds.isEmpty()) { %>
                <p style="padding: 20px;">Không tìm thấy skin phù hợp.</p>
            <% } else { for (SanPham s : ds) { %>
                <t:card sp="<%= s %>"/>
            <% } } %>
        </div>
    </div>

    <%@ include file="footer.jspf" %>
</body>
</html>
