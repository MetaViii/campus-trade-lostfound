<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="adminActive" value="category"/>
<c:set var="pageTitle" value="分类管理"/>
<%@ include file="/WEB-INF/views/common/admin_header.jsp" %>

<h1>分类管理</h1>
<c:if test="${not empty param.msg}"><div class="alert alert-danger">${param.msg}</div></c:if>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:22px;align-items:start;">

    <!-- 商品分类 -->
    <div class="panel">
        <div class="panel-head">📦 商品分类</div>
        <form action="${ctx}/admin/category/add" method="post" style="display:flex;gap:8px;padding:14px 16px;border-bottom:1px solid var(--border);">
            <input type="hidden" name="type" value="1">
            <input class="form-control" name="name" placeholder="新增商品分类名称" required>
            <select name="status" class="form-control" style="width:auto;">
                <option value="1">启用</option>
                <option value="0">停用</option>
            </select>
            <button class="btn btn-primary" type="submit">新增</button>
        </form>
        <table class="table">
            <thead><tr><th>ID</th><th>分类（可编辑）</th><th>操作</th></tr></thead>
            <tbody>
            <c:forEach var="cat" items="${goodsCategories}">
                <tr>
                    <td>${cat.id}</td>
                    <td>
                        <form action="${ctx}/admin/category/edit" method="post" style="display:flex;gap:6px;align-items:center;">
                            <input type="hidden" name="id" value="${cat.id}">
                            <input type="hidden" name="type" value="1">
                            <input class="form-control" name="name" value="${cat.name}" style="max-width:150px;">
                            <select name="status" class="form-control" style="width:auto;">
                                <option value="1" ${cat.status==1?'selected':''}>启用</option>
                                <option value="0" ${cat.status==0?'selected':''}>停用</option>
                            </select>
                            <button class="btn btn-sm btn-primary" type="submit">保存</button>
                        </form>
                    </td>
                    <td>
                        <form action="${ctx}/admin/category/delete" method="post">
                            <input type="hidden" name="id" value="${cat.id}">
                            <button class="btn btn-sm btn-danger" type="submit" data-confirm="删除该分类？">删除</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>

    <!-- 失物招领分类 -->
    <div class="panel">
        <div class="panel-head">🔍 失物招领分类</div>
        <form action="${ctx}/admin/category/add" method="post" style="display:flex;gap:8px;padding:14px 16px;border-bottom:1px solid var(--border);">
            <input type="hidden" name="type" value="2">
            <input class="form-control" name="name" placeholder="新增失物招领分类名称" required>
            <select name="status" class="form-control" style="width:auto;">
                <option value="1">启用</option>
                <option value="0">停用</option>
            </select>
            <button class="btn btn-primary" type="submit">新增</button>
        </form>
        <table class="table">
            <thead><tr><th>ID</th><th>分类（可编辑）</th><th>操作</th></tr></thead>
            <tbody>
            <c:forEach var="cat" items="${lostFoundCategories}">
                <tr>
                    <td>${cat.id}</td>
                    <td>
                        <form action="${ctx}/admin/category/edit" method="post" style="display:flex;gap:6px;align-items:center;">
                            <input type="hidden" name="id" value="${cat.id}">
                            <input type="hidden" name="type" value="2">
                            <input class="form-control" name="name" value="${cat.name}" style="max-width:150px;">
                            <select name="status" class="form-control" style="width:auto;">
                                <option value="1" ${cat.status==1?'selected':''}>启用</option>
                                <option value="0" ${cat.status==0?'selected':''}>停用</option>
                            </select>
                            <button class="btn btn-sm btn-primary" type="submit">保存</button>
                        </form>
                    </td>
                    <td>
                        <form action="${ctx}/admin/category/delete" method="post">
                            <input type="hidden" name="id" value="${cat.id}">
                            <button class="btn btn-sm btn-danger" type="submit" data-confirm="删除该分类？">删除</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/admin_footer.jsp" %>
