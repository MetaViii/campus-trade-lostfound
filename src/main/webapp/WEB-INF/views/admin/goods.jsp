<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="adminActive" value="goods"/>
<c:set var="pageTitle" value="商品管理"/>
<%@ include file="/WEB-INF/views/common/admin_header.jsp" %>

<h1>商品管理</h1>

<form class="toolbar" action="${ctx}/admin/goods/list" method="get">
    <input class="form-control grow" type="text" name="keyword" value="${keyword}" placeholder="搜索标题或描述">
    <select class="form-control" name="categoryId">
        <option value="">全部分类</option>
        <c:forEach var="cat" items="${categories}">
            <option value="${cat.id}" ${categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
        </c:forEach>
    </select>
    <select class="form-control" name="status">
        <option value="">全部状态</option>
        <option value="1" ${status == 1 ? 'selected' : ''}>在售</option>
        <option value="2" ${status == 2 ? 'selected' : ''}>已售</option>
        <option value="0" ${status == 0 ? 'selected' : ''}>下架</option>
    </select>
    <button class="btn btn-primary" type="submit">搜索</button>
    <a class="btn btn-outline" href="${ctx}/admin/goods/list">重置</a>
</form>

<div class="panel">
    <table class="table">
        <thead>
        <tr><th>ID</th><th>图片</th><th>标题</th><th>分类</th><th>价格</th><th>状态</th><th>发布者</th><th>浏览</th><th>时间</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="g" items="${page.list}">
            <tr>
                <td>${g.id}</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty g.image}"><img class="thumb" src="${ctx}/static/uploads/${g.image}"></c:when>
                        <c:otherwise><span class="thumb" style="display:inline-flex;align-items:center;justify-content:center;background:#eef2ff;">📦</span></c:otherwise>
                    </c:choose>
                </td>
                <td><a href="${ctx}/goods/detail?id=${g.id}">${g.title}</a></td>
                <td>${g.categoryName}</td>
                <td class="price">¥<fmt:formatNumber value="${g.price}" pattern="0.00"/></td>
                <td><span class="badge ${g.status==1?'badge-success':(g.status==2?'badge-warning':'badge-muted')}">${g.statusText}</span></td>
                <td>${g.publisherName}</td>
                <td>${g.viewCount}</td>
                <td class="text-muted" style="font-size:13px;"><fmt:formatDate value="${g.createTime}" pattern="yyyy-MM-dd"/></td>
                <td>
                    <div class="actions">
                        <form action="${ctx}/admin/goods/status" method="post" style="display:inline-flex;gap:4px;">
                            <input type="hidden" name="id" value="${g.id}">
                            <select name="status" class="form-control" style="padding:4px 6px;font-size:12px;width:auto;">
                                <option value="1" ${g.status==1?'selected':''}>在售</option>
                                <option value="2" ${g.status==2?'selected':''}>已售</option>
                                <option value="0" ${g.status==0?'selected':''}>下架</option>
                            </select>
                            <button class="btn btn-sm btn-outline" type="submit">更新</button>
                        </form>
                        <form action="${ctx}/admin/goods/delete" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${g.id}">
                            <button class="btn btn-sm btn-danger" type="submit" data-confirm="确定删除该商品吗？">删除</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty page.list}"><tr><td colspan="10" class="text-muted text-center" style="padding:30px;">没有找到商品</td></tr></c:if>
        </tbody>
    </table>
</div>

<c:if test="${page.totalPages > 1}">
    <div class="pagination">
        <c:forEach var="i" begin="1" end="${page.totalPages}">
            <c:url var="pl" value="/admin/goods/list"><c:param name="keyword" value="${keyword}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${i}"/></c:url>
            <c:choose>
                <c:when test="${i == page.pageNum}"><span class="current">${i}</span></c:when>
                <c:otherwise><a href="${pl}">${i}</a></c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
</c:if>

<%@ include file="/WEB-INF/views/common/admin_footer.jsp" %>
