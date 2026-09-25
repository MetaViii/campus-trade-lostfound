<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="active" value="ai"/>
<c:set var="pageTitle" value="AI 智能匹配"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 style="margin-bottom:6px;">AI 智能匹配</h1>
<p class="text-muted" style="margin-top:0;">
    用一句话把你要找的东西描述出来，系统会把它转成向量，在库里找出语义最相近的信息。
</p>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>
<c:if test="${not ready}">
    <div class="alert alert-info">
        AI 功能暂时不可用 —— ${unavailableReason}。
        <c:if test="${sessionScope.user.admin}">
            请到 <a href="${ctx}/admin/ai/config">后台 AI 设置</a> 处理。
        </c:if>
        <c:if test="${not sessionScope.user.admin}">
            请联系管理员在后台处理。
        </c:if>
    </div>
</c:if>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:22px;align-items:start;">

    <%-- 失物招领 --%>
    <div class="panel">
        <div class="panel-head">🔍 失物招领</div>
        <div style="padding:18px 20px;">
            <p class="text-muted" style="font-size:13px;margin-top:0;">
                丢了东西或捡到东西，描述一下物品特征、地点和时间，系统帮你找对应的信息。
            </p>
            <form action="${ctx}/ai/search" method="post">
                <input type="hidden" name="module" value="1">
                <div class="form-group">
                    <textarea class="form-control" name="query" rows="3" required
                              placeholder="例如：我昨天在三号教学楼丢了一个黑色的耳机充电盒"></textarea>
                </div>
                <button class="btn btn-primary btn-block" type="submit"
                        <c:if test="${not ready}">disabled</c:if>>开始匹配</button>
            </form>
        </div>
    </div>

    <%-- 二手商品 --%>
    <div class="panel">
        <div class="panel-head">📦 二手商品</div>
        <div style="padding:18px 20px;">
            <p class="text-muted" style="font-size:13px;margin-top:0;">
                想淘点什么，把用途、成色要求或预算说出来，系统帮你找在售的闲置。
            </p>
            <form action="${ctx}/ai/search" method="post">
                <input type="hidden" name="module" value="2">
                <div class="form-group">
                    <textarea class="form-control" name="query" rows="3" required
                              placeholder="例如：想买一本便宜的高等数学教材，最好没什么笔记"></textarea>
                </div>
                <button class="btn btn-primary btn-block" type="submit"
                        <c:if test="${not ready}">disabled</c:if>>开始匹配</button>
            </form>
        </div>
    </div>
</div>

<c:if test="${not empty aiResult}">
    <div class="panel" style="margin-top:26px;border:1px solid var(--primary);">
        <div class="panel-head" style="background:var(--primary-light);color:var(--primary);">
            ✨ AI 匹配结果 · ${activeModule==1?'失物招领':'二手商品'}
        </div>
        <div style="padding:16px 20px;">

            <p class="text-muted" style="font-size:13px;margin-top:0;">
                你的描述：<b>${fn:escapeXml(query)}</b>
            </p>

            <c:if test="${aiResult.reply ne ''}">
                <div class="alert alert-info" style="white-space:pre-wrap;">${aiResult.reply}</div>
            </c:if>

            <c:choose>
                <c:when test="${empty aiResult.matches}">
                    <p class="text-muted" style="padding:14px 0;">
                        没有找到相似度足够高的信息。可以换个说法再试，或者去
                        <a href="${ctx}${activeModule==1?'/lostfound/list':'/goods/list'}">
                            ${activeModule==1?'失物招领列表':'二手市场'}</a> 里翻翻。
                    </p>
                </c:when>
                <c:otherwise>
                    <p class="text-muted" style="font-size:13px;">
                        共找到 ${fn:length(aiResult.matches)} 条，按相似度从高到低排列：
                    </p>
                    <div class="card-grid">
                        <c:forEach var="mr" items="${aiResult.matches}">
                            <a class="card" href="${ctx}${mr.link}" style="text-decoration:none;color:inherit;">
                                <div class="card-body">
                                    <div class="flex-between">
                                        <span class="badge ${mr.badgeClass}">${mr.typeText}</span>
                                        <span class="badge badge-primary">相似度 ${mr.similarityPercent}%</span>
                                    </div>
                                    <p class="card-title" style="margin:6px 0 0;">${mr.title}</p>
                                    <p class="card-desc">${mr.categoryName} · ${mr.place}</p>
                                    <p class="text-muted" style="font-size:12px;margin:0;">${mr.snippet}</p>
                                </div>
                            </a>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</c:if>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
