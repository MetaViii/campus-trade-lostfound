<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="adminActive" value="aiLogs"/>
<c:set var="pageTitle" value="AI 对话记录"/>
<%@ include file="/WEB-INF/views/common/admin_header.jsp" %>

<h1>AI 对话记录</h1>
<p class="text-muted" style="font-size:13px;margin-top:0;">
    全站用户每次使用 AI 智能匹配都会记一条：谁问的、问了什么、AI 回了什么、命中了哪些信息。
</p>

<form class="toolbar" action="${ctx}/admin/ai/logs" method="get">
    <select class="form-control" name="module">
        <option value="">全部模块</option>
        <option value="1" ${module==1?'selected':''}>失物招领</option>
        <option value="2" ${module==2?'selected':''}>二手商品</option>
    </select>
    <input class="form-control grow" type="text" name="keyword" value="${keyword}"
           placeholder="按用户描述或 AI 答复内容搜索">
    <button class="btn btn-primary" type="submit">搜索</button>
    <a class="btn btn-outline" href="${ctx}/admin/ai/logs">重置</a>
</form>

<div class="panel">
    <table class="table">
        <thead>
        <tr>
            <th style="width:130px;">时间</th>
            <th style="width:90px;">用户</th>
            <th style="width:80px;">模块</th>
            <th>用户描述</th>
            <th style="width:34%;">AI 答复</th>
            <th style="width:60px;">命中</th>
            <th style="width:70px;">耗时</th>
            <th style="width:70px;">状态</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="log" items="${page.list}">
            <tr>
                <td class="text-muted" style="font-size:13px;">
                    <fmt:formatDate value="${log.createTime}" pattern="MM-dd HH:mm:ss"/>
                </td>
                <td>${log.userNickname}</td>
                <td>
                    <span class="badge ${log.module==1?'badge-primary':'badge-info'}">${log.moduleText}</span>
                </td>
                <td style="font-size:13px;">${log.queryText}</td>
                <td style="font-size:13px;">
                    <c:choose>
                        <c:when test="${not empty log.replyText}">
                            <c:choose>
                                <c:when test="${fn:length(log.replyText) > 60}">
                                    <details>
                                        <summary class="text-muted" style="cursor:pointer;">
                                            ${log.replyBrief}
                                        </summary>
                                        <div style="margin-top:6px;white-space:pre-wrap;">${log.replyText}</div>
                                    </details>
                                </c:when>
                                <c:otherwise>${log.replyText}</c:otherwise>
                            </c:choose>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted">${log.success ? '（未配置对话模型）' : '—'}</span>
                        </c:otherwise>
                    </c:choose>
                    <c:if test="${not log.success}">
                        <div class="text-muted" style="font-size:12px;color:var(--danger);">
                            失败：${log.errorMsg}
                        </div>
                    </c:if>
                </td>
                <td>${log.resultCount}</td>
                <td class="text-muted" style="font-size:13px;">${log.costMs} ms</td>
                <td>
                    <c:choose>
                        <c:when test="${log.success}"><span class="badge badge-success">成功</span></c:when>
                        <c:otherwise><span class="badge badge-danger">失败</span></c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty page.list}">
            <tr><td colspan="8" class="text-muted text-center" style="padding:30px;">
                还没有 AI 使用记录
            </td></tr>
        </c:if>
        </tbody>
    </table>
</div>

<c:if test="${page.totalPages > 1}">
    <div class="pagination">
        <c:forEach var="i" begin="1" end="${page.totalPages}">
            <c:url var="pl" value="/admin/ai/logs">
                <c:param name="module" value="${module}"/>
                <c:param name="keyword" value="${keyword}"/>
                <c:param name="page" value="${i}"/>
            </c:url>
            <c:choose>
                <c:when test="${i == page.pageNum}"><span class="current">${i}</span></c:when>
                <c:otherwise><a href="${pl}">${i}</a></c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
</c:if>

<%@ include file="/WEB-INF/views/common/admin_footer.jsp" %>
