<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="adminActive" value="lostfound"/>
<c:set var="pageTitle" value="失物招领管理"/>
<%@ include file="/WEB-INF/views/common/admin_header.jsp" %>

<h1>失物招领管理</h1>

<form class="toolbar" action="${ctx}/admin/lostfound/list" method="get">
    <input class="form-control grow" type="text" name="keyword" value="${keyword}" placeholder="搜索名称或特征">
    <select class="form-control" name="type">
        <option value="">全部类型</option>
        <option value="1" ${type == 1 ? 'selected' : ''}>失物</option>
        <option value="2" ${type == 2 ? 'selected' : ''}>招领</option>
    </select>
    <select class="form-control" name="categoryId">
        <option value="">全部分类</option>
        <c:forEach var="cat" items="${categories}">
            <option value="${cat.id}" ${categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
        </c:forEach>
    </select>
    <select class="form-control" name="status">
        <option value="">全部状态</option>
        <option value="1" ${status == 1 ? 'selected' : ''}>寻找中</option>
        <option value="2" ${status == 2 ? 'selected' : ''}>已找回</option>
        <option value="3" ${status == 3 ? 'selected' : ''}>待认领</option>
        <option value="4" ${status == 4 ? 'selected' : ''}>已认领</option>
        <option value="0" ${status == 0 ? 'selected' : ''}>已关闭</option>
    </select>
    <button class="btn btn-primary" type="submit">搜索</button>
    <a class="btn btn-outline" href="${ctx}/admin/lostfound/list">重置</a>
</form>

<div class="panel">
    <table class="table">
        <thead>
        <tr><th>ID</th><th>类型</th><th>名称</th><th>分类</th><th>地点</th><th>状态</th><th>发布者</th><th>时间</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="lf" items="${page.list}">
            <tr>
                <td>${lf.id}</td>
                <td><span class="badge ${lf.type==1?'badge-danger':'badge-success'}">${lf.typeText}</span></td>
                <td><a href="${ctx}/lostfound/detail?id=${lf.id}">${lf.name}</a></td>
                <td>${lf.categoryName}</td>
                <td>${lf.place}</td>
                <td><span class="badge badge-muted">${lf.statusText}</span></td>
                <td>${lf.publisherName}</td>
                <td class="text-muted" style="font-size:13px;"><fmt:formatDate value="${lf.createTime}" pattern="yyyy-MM-dd"/></td>
                <td>
                    <div class="actions">
                        <form action="${ctx}/admin/lostfound/status" method="post" style="display:inline-flex;gap:4px;">
                            <input type="hidden" name="id" value="${lf.id}">
                            <select name="status" class="form-control" style="padding:4px 6px;font-size:12px;width:auto;">
                                <option value="1" ${lf.status==1?'selected':''}>寻找中</option>
                                <option value="2" ${lf.status==2?'selected':''}>已找回</option>
                                <option value="3" ${lf.status==3?'selected':''}>待认领</option>
                                <option value="4" ${lf.status==4?'selected':''}>已认领</option>
                                <option value="0" ${lf.status==0?'selected':''}>已关闭</option>
                            </select>
                            <button class="btn btn-sm btn-outline" type="submit">更新</button>
                        </form>
                        <form action="${ctx}/admin/lostfound/delete" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${lf.id}">
                            <button class="btn btn-sm btn-danger" type="submit" data-confirm="确定删除该信息吗？">删除</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty page.list}"><tr><td colspan="9" class="text-muted text-center" style="padding:30px;">没有找到信息</td></tr></c:if>
        </tbody>
    </table>
</div>

<c:if test="${page.totalPages > 1}">
    <div class="pagination">
        <c:forEach var="i" begin="1" end="${page.totalPages}">
            <c:url var="pl" value="/admin/lostfound/list"><c:param name="keyword" value="${keyword}"/><c:param name="type" value="${type}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${i}"/></c:url>
            <c:choose>
                <c:when test="${i == page.pageNum}"><span class="current">${i}</span></c:when>
                <c:otherwise><a href="${pl}">${i}</a></c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
</c:if>

<%@ include file="/WEB-INF/views/common/admin_footer.jsp" %>
