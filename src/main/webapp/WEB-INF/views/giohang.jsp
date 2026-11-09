<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List, fisch.model.DongGioHang" %>
<%
    @SuppressWarnings("unchecked")
    List<DongGioHang> dong = (List<DongGioHang>) request.getAttribute("dong");
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Giỏ Trade - Fisch Skin Trade</title>
    <link rel="stylesheet" href="css/chung.css">
    <link rel="stylesheet" href="css/product.css">
    <link rel="stylesheet" href="css/form.css">
</head>
<body>
    <%@ include file="header.jspf" %>

    <div class="wrapper">
        <h1>Giỏ trade của bạn</h1>
        <div class="giohang">
        <% if (dong.isEmpty()) { %>
            <p style="padding: 20px;">Giỏ hàng của bạn đang trống. <a href="sanpham" style="color: #35d0ff;">Chọn skin ngay</a></p>
        <% } else { %>
            <table>
                <thead>
                    <tr>
                        <th>Skin</th>
                        <th>Giá</th>
                        <th>Số lượng</th>
                        <th>Thành tiền</th>
                        <th>Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                <% for (DongGioHang d : dong) { %>
                    <tr class="dong">
                        <td><img src="<%= d.getSp().getHinhUrl() %>" alt="<%= fisch.util.Util.esc(d.getSp().getTen()) %>" width="40" style="vertical-align: middle; border-radius: 4px; margin-right: 8px;"> <%= fisch.util.Util.esc(d.getSp().getTen()) %></td>
                        <td><%= d.getSp().getGiabanStr() %> R$</td>
                        <td>
                            <form method="post" action="giohang">
                                <input type="hidden" name="action" value="update">
                                <input type="hidden" name="id" value="<%= d.getSp().getId() %>">
                                <input type="number" name="sl" min="1" value="<%= d.getSl() %>" style="width: 60px;" onchange="this.form.submit()">
                            </form>
                        </td>
                        <td><%= d.getThanhTienStr() %> R$</td>
                        <td>
                            <form method="post" action="giohang">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="id" value="<%= d.getSp().getId() %>">
                                <button type="submit" class="btn">Xóa</button>
                            </form>
                        </td>
                    </tr>
                <% } %>
                </tbody>
            </table>
            <div style="margin-top: 20px; text-align: right;">
                <p style="font-size: 20px;"><b>Tổng tiền: ${tongStr} R$</b></p>
                <form method="post" action="giohang">
                    <input type="hidden" name="action" value="checkout">
                    <button type="submit" class="btn alt">Thanh toán</button>
                </form>
            </div>
        <% } %>
        </div>
    </div>

    <%@ include file="footer.jspf" %>
</body>
</html>
