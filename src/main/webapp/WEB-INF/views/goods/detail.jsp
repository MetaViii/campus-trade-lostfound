<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="active" value="goods"/>
<c:set var="pageTitle" value="${goods.title}"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>
<c:set var="canManage" value="${sessionScope.user != null && (sessionScope.user.id == goods.userId || sessionScope.user.admin)}"/>

<p class="text-muted" style="font-size:13px;"><a href="${ctx}/goods/list">二手市场</a> / 商品详情</p>

<div class="detail-wrap">
    <div>
        <div class="detail-img">
            <c:choose>
                <c:when test="${not empty goods.image}"><img src="${ctx}/static/uploads/${goods.image}" alt="${goods.title}"></c:when>
                <c:otherwise><span class="placeholder">📦</span></c:otherwise>
            </c:choose>
        </div>
    </div>
    <div class="detail-info">
        <h1>${goods.title}</h1>
        <div style="display:flex;gap:8px;flex-wrap:wrap;margin-bottom:10px;">
            <span class="badge badge-primary">${goods.categoryName}</span>
            <span class="badge ${goods.status==1?'badge-success':(goods.status==2?'badge-warning':'badge-muted')}">${goods.statusText}</span>
            <c:if test="${not empty goods.quality}"><span class="badge badge-info">成色：${goods.quality}</span></c:if>
        </div>
        <div class="price" style="font-size:28px;">¥<fmt:formatNumber value="${goods.price}" pattern="0.00"/></div>
        <table class="detail-table">
            <tr><th>交易地点</th><td>${not empty goods.tradePlace ? goods.tradePlace : '面议'}</td></tr>
            <tr><th>联系方式</th><td><b>${goods.contact}</b></td></tr>
            <tr><th>发布者</th><td>${goods.publisherName}</td></tr>
            <tr><th>发布时间</th><td><fmt:formatDate value="${goods.createTime}" pattern="yyyy-MM-dd HH:mm"/> · 浏览 ${goods.viewCount}</td></tr>
        </table>
        <div class="detail-desc">${goods.description}</div>
        <c:if test="${canManage}">
            <div class="actions mt-3">
                <a class="btn btn-outline" href="${ctx}/goods/edit?id=${goods.id}">编辑</a>
                <form action="${ctx}/goods/delete" method="post" style="display:inline;">
                    <input type="hidden" name="id" value="${goods.id}">
                    <button class="btn btn-danger" type="submit" data-confirm="确定删除该商品吗？">删除</button>
                </form>
            </div>
        </c:if>
    </div>
</div>

<!-- 留言区 -->
<div class="panel mt-3" style="margin-top:30px;">
    <div class="panel-head">💬 留言咨询（${fn:length(messages)} 条）</div>
    <div style="padding:18px 20px;">
        <c:if test="${not empty param.msgError}"><div class="alert alert-danger">${param.msgError}</div></c:if>
        <c:choose>
            <c:when test="${empty sessionScope.user}">
                <p class="text-muted">请先 <a href="${ctx}/user/login">登录</a> 后再留言。</p>
            </c:when>
            <c:otherwise>
                <form action="${ctx}/message/add" method="post" style="display:flex;gap:10px;align-items:flex-start;">
                    <input type="hidden" name="targetType" value="1">
                    <input type="hidden" name="targetId" value="${goods.id}">
                    <textarea class="form-control" name="content" rows="2" placeholder="向卖家提问，如：还在吗？能否优惠？" required></textarea>
                    <button class="btn btn-primary" type="submit" style="white-space:nowrap;">发表</button>
                </form>
            </c:otherwise>
        </c:choose>

        <div class="msg-list">
            <c:choose>
                <c:when test="${empty messages}"><p class="text-muted">还没有人留言，快来抢沙发~</p></c:when>
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
