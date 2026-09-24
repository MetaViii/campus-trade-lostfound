<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="adminActive" value="user"/>
<c:set var="pageTitle" value="用户管理"/>
<%@ include file="/WEB-INF/views/common/admin_header.jsp" %>

<h1>用户管理</h1>
<c:if test="${not empty param.msg}"><div class="alert alert-danger">${param.msg}</div></c:if>

<form class="toolbar" action="${ctx}/admin/user/list" method="get">
    <input class="form-control grow" type="text" name="keyword" value="${keyword}" placeholder="按账号或昵称搜索">
    <button class="btn btn-primary" type="submit">搜索</button>
    <a class="btn btn-outline" href="${ctx}/admin/user/list">重置</a>
</form>

<div class="panel">
    <table class="table">
        <thead>
        <tr><th>ID</th><th>账号</th><th>昵称</th><th>联系方式</th><th>角色</th><th>状态</th><th>注册时间</th><th>操作</th></tr>
        </thead>
        <tbody>
        <c:forEach var="u" items="${page.list}">
            <tr>
                <td>${u.id}</td>
                <td>${u.account}</td>
                <td>${u.nickname}</td>
                <td>${u.phone}</td>
                <td>
                    <c:choose>
                        <c:when test="${u.role==1}"><span class="badge badge-primary">管理员</span></c:when>
                        <c:otherwise><span class="badge badge-muted">普通用户</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${u.status==1}"><span class="badge badge-success">正常</span></c:when>
                        <c:otherwise><span class="badge badge-danger">禁用</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="text-muted" style="font-size:13px;"><fmt:formatDate value="${u.createTime}" pattern="yyyy-MM-dd"/></td>
                <td>
                    <c:choose>
                        <c:when test="${u.role==1}"><span class="text-muted" style="font-size:13px;">—</span></c:when>
                        <c:otherwise>
                            <div class="actions">
                                <form action="${ctx}/admin/user/status" method="post" style="display:inline;">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <input type="hidden" name="status" value="${u.status==1?0:1}">
                                    <button class="btn btn-sm ${u.status==1?'btn-outline':'btn-success'}" type="submit">${u.status==1?'禁用':'恢复'}</button>
                                </form>
                                <form action="${ctx}/admin/user/delete" method="post" style="display:inline;">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button class="btn btn-sm btn-danger" type="submit" data-confirm="确定删除该用户吗？">删除</button>
                                </form>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty page.list}"><tr><td colspan="8" class="text-muted text-center" style="padding:30px;">没有找到用户</td></tr></c:if>
        </tbody>
    </table>
</div>

<c:if test="${page.totalPages > 1}">
    <div class="pagination">
        <c:forEach var="i" begin="1" end="${page.totalPages}">
            <c:url var="pl" value="/admin/user/list"><c:param name="keyword" value="${keyword}"/><c:param name="page" value="${i}"/></c:url>
            <c:choose>
                <c:when test="${i == page.pageNum}"><span class="current">${i}</span></c:when>
                <c:otherwise><a href="${pl}">${i}</a></c:otherwise>
            </c:choose>
        </c:forEach>
    </div>
</c:if>

<%@ include file="/WEB-INF/views/common/admin_footer.jsp" %>
