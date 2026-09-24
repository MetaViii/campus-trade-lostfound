<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="active" value="lostfound"/>
<c:set var="pageTitle" value="失物招领"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="flex-between mb-3">
    <h1 style="font-size:22px;margin:0;">失物招领</h1>
    <a class="btn btn-accent" href="${ctx}/lostfound/publish">＋ 发布失物/招领</a>
</div>

<form class="toolbar" action="${ctx}/lostfound/list" method="get">
    <input class="form-control grow" type="text" name="keyword" value="${keyword}" placeholder="搜索物品名称或特征">
    <select class="form-control" name="type">
        <option value="">全部类型</option>
        <option value="1" ${type == 1 ? 'selected' : ''}>失物（寻物）</option>
        <option value="2" ${type == 2 ? 'selected' : ''}>招领（拾物）</option>
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
    <a class="btn btn-outline" href="${ctx}/lostfound/list">重置</a>
</form>

<c:choose>
    <c:when test="${empty page.list}">
        <div class="empty"><div class="icon">🔍</div>没有找到符合条件的信息</div>
    </c:when>
    <c:otherwise>
        <div class="card-grid">
            <c:forEach var="lf" items="${page.list}">
                <a class="card" href="${ctx}/lostfound/detail?id=${lf.id}">
                    <div class="card-img lf">
                        <c:choose>
                            <c:when test="${not empty lf.image}"><img src="${ctx}/static/uploads/${lf.image}" alt="${lf.name}"></c:when>
                            <c:otherwise><span class="placeholder">🔍</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-body">
                        <div style="display:flex;gap:6px;">
                            <span class="badge ${lf.type==1?'badge-danger':'badge-success'}">${lf.typeText}</span>
                            <span class="badge badge-muted">${lf.statusText}</span>
                        </div>
                        <h3 class="card-title">${lf.name}</h3>
                        <p class="card-desc">${lf.feature}</p>
                        <div class="text-muted" style="font-size:12px;">📍 ${lf.place} · ${lf.categoryName}</div>
                    </div>
                </a>
            </c:forEach>
        </div>

        <c:if test="${page.totalPages > 1}">
            <div class="pagination">
                <c:choose>
                    <c:when test="${page.hasPrev}">
                        <c:url var="prev" value="/lostfound/list"><c:param name="keyword" value="${keyword}"/><c:param name="type" value="${type}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${page.prevPage}"/></c:url>
                        <a href="${prev}">上一页</a>
                    </c:when>
                    <c:otherwise><span class="disabled">上一页</span></c:otherwise>
                </c:choose>
                <c:forEach var="i" begin="1" end="${page.totalPages}">
                    <c:url var="pl" value="/lostfound/list"><c:param name="keyword" value="${keyword}"/><c:param name="type" value="${type}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${i}"/></c:url>
                    <c:choose>
                        <c:when test="${i == page.pageNum}"><span class="current">${i}</span></c:when>
                        <c:otherwise><a href="${pl}">${i}</a></c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:choose>
                    <c:when test="${page.hasNext}">
                        <c:url var="next" value="/lostfound/list"><c:param name="keyword" value="${keyword}"/><c:param name="type" value="${type}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${page.nextPage}"/></c:url>
                        <a href="${next}">下一页</a>
                    </c:when>
                    <c:otherwise><span class="disabled">下一页</span></c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
