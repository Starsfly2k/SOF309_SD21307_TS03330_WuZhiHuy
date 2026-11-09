<%@ tag language="java" pageEncoding="UTF-8" %>
<%@ attribute name="sp" required="true" type="fisch.model.SanPham" %>
<div class="product">
    <a href="chitiet?id=${sp.id}">
        <img src="${sp.hinhUrl}" alt="${sp.ten}">
    </a>
    <h3>${sp.ten}</h3>
    <span class="price-new">${sp.giaban} R$</span>
    <span class="price-old">${sp.giagoc} R$</span>
    <br><br>
    <form method="post" action="giohang">
        <input type="hidden" name="action" value="add">
        <input type="hidden" name="id" value="${sp.id}">
        <input type="hidden" name="sl" value="1">
        <button type="submit" class="btn">Thêm vào giỏ</button>
    </form>
</div>
