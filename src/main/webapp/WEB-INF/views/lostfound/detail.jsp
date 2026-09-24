<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="active" value="lostfound"/>
<c:set var="pageTitle" value="${lf.name}"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<c:set var="canManage" value="${sessionScope.user != null && (sessionScope.user.id == lf.userId || sessionScope.user.admin)}"/>

<p class="text-muted" style="font-size:13px;"><a href="${ctx}/lostfound/list">失物招领</a> / 详情</p>

<div class="detail-wrap">
    <div>
        <div class="detail-img" style="background:var(--accent-light);">
            <c:choose>
                <c:when test="${not empty lf.image}"><img src="${ctx}/static/uploads/${lf.image}" alt="${lf.name}"></c:when>
                <c:otherwise><span class="placeholder" style="color:#fcd34d;">🔍</span></c:otherwise>
            </c:choose>
        </div>
    </div>
    <div class="detail-info">
        <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px;">
            <span class="badge ${lf.type==1?'badge-danger':'badge-success'}">${lf.typeText}</span>
            <span class="badge badge-muted">${lf.statusText}</span>
            <span class="badge badge-primary">${lf.categoryName}</span>
        </div>
        <h1>${lf.name}</h1>
        <table class="detail-table">
            <tr><th>${lf.type==1?'丢失地点':'拾到地点'}</th><td>${lf.place}</td></tr>
            <tr><th>${lf.type==1?'丢失时间':'拾到时间'}</th><td><fmt:formatDate value="${lf.happenTime}" pattern="yyyy-MM-dd HH:mm"/></td></tr>
            <tr><th>联系方式</th><td><b>${lf.contact}</b></td></tr>
            <tr><th>发布者</th><td>${lf.publisherName}</td></tr>
            <tr><th>发布时间</th><td><fmt:formatDate value="${lf.createTime}" pattern="yyyy-MM-dd HH:mm"/> · 浏览 ${lf.viewCount}</td></tr>
        </table>
        <div class="detail-desc"><b>${lf.type==1?'物品特征':'保管说明'}：</b><br>${lf.feature}</div>
        <c:if test="${canManage}">
            <div class="actions mt-3">
                <a class="btn btn-outline" href="${ctx}/lostfound/edit?id=${lf.id}">编辑</a>
                <form action="${ctx}/lostfound/delete" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="${lf.id}">
                    <button class="btn btn-danger" type="submit" data-confirm="确定删除该信息吗？">删除</button>
                </form>
            </div>
        </c:if>
    </div>
</div>

<c:if test="${not empty matches}">
<div class="panel" style="margin-top:30px;border:1px solid var(--primary);">
    <div class="panel-head" style="background:var(--primary-light);color:var(--primary);">
        🤝 智能匹配 · 系统为你找到 ${fn:length(matches)} 条疑似${lf.type==1?'招领':'失物'}信息
    </div>
    <div style="padding:14px 20px;">
        <p class="text-muted" style="font-size:13px;margin:0 0 12px;">
            系统根据<b>物品分类、名称特征、地点与时间</b>自动计算相似度，按可能性从高到低排序，仅供参考。
        </p>
        <div class="card-grid">
            <c:forEach var="mr" items="${matches}">
                <a class="card" href="${ctx}/lostfound/detail?id=${mr.item.id}" style="text-decoration:none;color:inherit;">
                    <div class="card-body">
                        <div class="flex-between">
                            <span class="badge ${mr.item.type==1?'badge-danger':'badge-success'}">${mr.item.typeText}</span>
                            <span class="badge badge-primary">相似度 ${mr.score}%</span>
                        </div>
                        <p class="card-title" style="margin:6px 0 0;">${mr.item.name}</p>
                        <p class="card-desc">${mr.item.categoryName} · ${mr.item.place}</p>
                        <p class="text-muted" style="font-size:12px;margin:0;">🔎 ${mr.reason}</p>
                    </div>
                </a>
            </c:forEach>
        </div>
    </div>
</div>
</c:if>

<div class="panel" style="margin-top:30px;">
    <div class="panel-head">💬 留言（${fn:length(messages)} 条）</div>
    <div style="padding:18px 20px;">
        <c:if test="${not empty param.msgError}"><div class="alert alert-danger">${param.msgError}</div></c:if>
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <p class="text-muted">请先 <a href="${ctx}/user/login">登录</a> 后再留言。</p>
            </c:when>
            <c:otherwise>
                <form action="${ctx}/message/add" method="post" style="display:flex;gap:10px;align-items:flex-start;">
                    <input type="hidden" name="targetType" value="2">
                    <input type="hidden" name="targetId" value="${lf.id}">
                    <textarea class="form-control" name="content" rows="2" placeholder="留言，如：这是我丢的，怎么联系您？" required></textarea>
                    <button class="btn btn-primary" type="submit" style="white-space:nowrap;">发表</button>
                </form>
            </c:otherwise>
        </c:choose>
        <div class="msg-list">
            <c:choose>
                <c:when test="${empty messages}"><p class="text-muted">还没有留言。</p></c:when>
                <c:otherwise>
                    <c:forEach var="m" items="${messages}">
                        <div class="msg-item">
                            <div class="msg-head">
                                <span class="name">${m.userNickname}</span>
                                <span><fmt:formatDate value="${m.createTime}" pattern="yyyy-MM-dd HH:mm"/></span>
                            </div>
                            <div>${m.content}</div>
                            <c:if test="${sessionScope.user != null && (sessionScope.user.id == m.userId || sessionScope.user.admin)}">
                                <form action="${ctx}/message/delete" method="post" style="display:inline;">
                                    <input type="hidden" name="id" value="${m.id}">
                                    <button class="btn btn-sm btn-outline" type="submit" data-confirm="删除这条留言？" style="margin-top:6px;">删除</button>
                                </form>
                            </c:if>
                        </div>
                    </c:forEach>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
