<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="active" value="index"/>
<c:set var="pageTitle" value="首页"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<section class="hero">
    <h1>校园二手交易 &amp; 失物招领</h1>
    <p>闲置好物在这里流转，丢失物品在这里重逢。让校园生活更便捷、更温暖。</p>
    <form class="hero-search" action="${ctx}/goods/list" method="get">
        <input class="form-control" type="text" name="keyword" placeholder="搜索你想要的二手好物，如：教材、键盘、台灯……">
        <button class="btn btn-accent" type="submit">🔍 搜索商品</button>
    </form>
    <div class="hero-actions">
        <a class="btn" href="${ctx}/goods/publish">＋ 发布闲置</a>
        <a class="btn" href="${ctx}/lostfound/publish">＋ 发布失物/招领</a>
        <a class="btn" href="${ctx}/lostfound/list">🔍 浏览失物招领</a>
    </div>
</section>

<div class="section-title">
    <span>📦 最新二手好物</span>
    <a class="more" href="${ctx}/goods/list">查看全部 →</a>
</div>
<c:choose>
    <c:when test="${empty latestGoods}">
        <div class="empty"><div class="icon">📦</div>暂时还没有商品，快来发布第一件吧！</div>
    </c:when>
    <c:otherwise>
        <div class="card-grid">
            <c:forEach var="g" items="${latestGoods}">
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
                            <span class="badge badge-primary">${g.categoryName}</span>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<div class="section-title mt-3" style="margin-top:36px;">
    <span>🔍 最新失物招领</span>
    <a class="more" href="${ctx}/lostfound/list">查看全部 →</a>
</div>
<c:choose>
    <c:when test="${empty latestLostFound}">
        <div class="empty"><div class="icon">🔍</div>暂时还没有失物招领信息。</div>
    </c:when>
    <c:otherwise>
        <div class="card-grid">
            <c:forEach var="lf" items="${latestLostFound}">
                <a class="card" href="${ctx}/lostfound/detail?id=${lf.id}">
                    <div class="card-img lf">
                        <c:choose>
                            <c:when test="${not empty lf.image}"><img src="${ctx}/static/uploads/${lf.image}" alt="${lf.name}"></c:when>
                            <c:otherwise><span class="placeholder">🔍</span></c:otherwise>
                        </c:choose>
                    </div>
                    <div class="card-body">
                        <h3 class="card-title">${lf.name}</h3>
                        <p class="card-desc">${lf.feature}</p>
                        <div class="card-meta">
                            <span class="badge ${lf.type==1?'badge-danger':'badge-success'}">${lf.typeText}</span>
                            <span class="badge badge-muted">${lf.statusText}</span>
                        </div>
                    </div>
                </a>
            </c:forEach>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
