<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="adminActive" value="dashboard"/>
<c:set var="pageTitle" value="仪表盘"/>
<%@ include file="/WEB-INF/views/common/admin_header.jsp" %>

<h1>仪表盘</h1>

<div class="stat-grid">
    <div class="stat-card">
        <div class="stat-icon" style="background:var(--primary-light);color:var(--primary);">👤</div>
        <div><div class="num">${userCount}</div><div class="label">注册用户</div></div>
    </div>
    <div class="stat-card">
        <div class="stat-icon" style="background:#dcfce7;color:var(--success);">📦</div>
        <div><div class="num">${goodsCount}</div><div class="label">二手商品</div></div>
    </div>
    <div class="stat-card">
        <div class="stat-icon" style="background:var(--accent-light);color:var(--accent);">🔍</div>
        <div><div class="num">${lostFoundCount}</div><div class="label">失物招领</div></div>
    </div>
    <div class="stat-card">
        <div class="stat-icon" style="background:#e0f2fe;color:var(--info);">🏷️</div>
        <div><div class="num">${categoryCount}</div><div class="label">分类数量</div></div>
    </div>
</div>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:22px;align-items:start;">
    <div class="panel">
        <div class="panel-head">最新二手商品</div>
        <table class="table">
            <thead><tr><th>标题</th><th>价格</th><th>状态</th><th>发布者</th></tr></thead>
            <tbody>
            <c:forEach var="g" items="${recentGoods}">
                <tr>
                    <td><a href="${ctx}/goods/detail?id=${g.id}">${g.title}</a></td>
                    <td class="price">¥<fmt:formatNumber value="${g.price}" pattern="0.00"/></td>
                    <td><span class="badge ${g.status==1?'badge-success':(g.status==2?'badge-warning':'badge-muted')}">${g.statusText}</span></td>
                    <td>${g.publisherName}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentGoods}"><tr><td colspan="4" class="text-muted text-center">暂无数据</td></tr></c:if>
            </tbody>
        </table>
    </div>
    <div class="panel">
        <div class="panel-head">最新失物招领</div>
        <table class="table">
            <thead><tr><th>类型</th><th>名称</th><th>状态</th><th>发布者</th></tr></thead>
            <tbody>
            <c:forEach var="lf" items="${recentLostFound}">
                <tr>
                    <td><span class="badge ${lf.type==1?'badge-danger':'badge-success'}">${lf.typeText}</span></td>
                    <td><a href="${ctx}/lostfound/detail?id=${lf.id}">${lf.name}</a></td>
                    <td><span class="badge badge-muted">${lf.statusText}</span></td>
                    <td>${lf.publisherName}</td>
                </tr>
            </c:forEach>
            <c:if test="${empty recentLostFound}"><tr><td colspan="4" class="text-muted text-center">暂无数据</td></tr></c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/admin_footer.jsp" %>
