<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="active" value="goods"/>
<c:set var="pageTitle" value="二手市场"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="flex-between mb-3">
    <h1 style="font-size:22px;margin:0;">二手市场</h1>
    <a class="btn btn-primary" href="${ctx}/goods/publish">＋ 发布闲置</a>
</div>

<form class="toolbar" action="${ctx}/goods/list" method="get">
    <input class="form-control grow" type="text" name="keyword" value="${keyword}" placeholder="搜索商品标题或描述">
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
    <a class="btn btn-outline" href="${ctx}/goods/list">重置</a>
</form>

<c:choose>
    <c:when test="${empty page.list}">
        <div class="empty"><div class="icon">📦</div>没有找到符合条件的商品</div>
    </c:when>
    <c:otherwise>
        <div class="card-grid">
            <c:forEach var="g" items="${page.list}">
                <a class="card" href="${ctx}/goods/detail?id=${g.id}">
                    <div class="card-img">
                        <c:choose>
                            <c:when test="${not empty g.image}"><img src="${ctx}/static/uploads/${g.image}" alt="${g.title}"></c:when>
                            <c:otherwise><span class="placeholder">📦</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-body">
                        <h3 class="card-title">${g.title}</h3>
                        <p class="card-desc">${g.description}</p>
                        <div class="card-meta">
                            <span class="price">¥<fmt:formatNumber value="${g.price}" pattern="0.00"/></span>
                            <span class="badge ${g.status==1?'badge-success':(g.status==2?'badge-warning':'badge-muted')}">${g.statusText}</span>
                        </div>
                        <div class="text-muted" style="font-size:12px;">${g.categoryName} · 👁 ${g.viewCount}</div>
                    </div>
                </a>
            </c:forEach>
        </div>

        <c:if test="${page.totalPages > 1}">
            <div class="pagination">
                <c:choose>
                    <c:when test="${page.hasPrev}">
                        <c:url var="prev" value="/goods/list"><c:param name="keyword" value="${keyword}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${page.prevPage}"/></c:url>
                        <a href="${prev}">上一页</a>
                    </c:when>
                    <c:otherwise><span class="disabled">上一页</span></c:otherwise>
                </c:choose>
                <c:forEach var="i" begin="1" end="${page.totalPages}">
                    <c:url var="pl" value="/goods/list"><c:param name="keyword" value="${keyword}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${i}"/></c:url>
                    <c:choose>
                        <c:when test="${i == page.pageNum}"><span class="current">${i}</span></c:when>
                        <c:otherwise><a href="${pl}">${i}</a></c:otherwise>
                    </c:choose>
                </c:forEach>
                <c:choose>
                    <c:when test="${page.hasNext}">
                        <c:url var="next" value="/goods/list"><c:param name="keyword" value="${keyword}"/><c:param name="categoryId" value="${categoryId}"/><c:param name="status" value="${status}"/><c:param name="page" value="${page.nextPage}"/></c:url>
                        <a href="${next}">下一页</a>
                    </c:when>
                    <c:otherwise><span class="disabled">下一页</span></c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
