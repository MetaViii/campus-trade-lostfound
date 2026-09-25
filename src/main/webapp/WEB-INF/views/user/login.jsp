<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value=""/>
<%-- 访客页面：不展示功能导航，页脚也只写中性版权，避免把系统功能直接列给访客 --%>
<c:set var="guestPage" value="true"/>
<c:set var="pageTitle" value="登录"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div style="display:flex;justify-content:center;padding:48px 0;">
    <div class="form-card" style="width:100%;max-width:400px;margin:0;">
        <div class="text-center" style="margin-bottom:24px;">
            <div style="font-size:40px;line-height:1;">🔐</div>
            <h2 style="margin:12px 0 6px;">登录</h2>
            <p class="text-muted" style="margin:0;font-size:13px;">请登录后继续使用</p>
        </div>

        <c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
        <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
        <c:if test="${param.tip == 'login'}"><div class="alert alert-info">请先登录后再进行该操作。</div></c:if>

        <form action="${ctx}/user/login" method="post">
            <input type="hidden" name="returnUrl" value="${param.returnUrl}">
            <div class="form-group">
                <label>账号</label>
                <input class="form-control" type="text" name="account" value="${account}"
                       placeholder="请输入账号" required autofocus>
            </div>
            <div class="form-group">
                <label>密码</label>
                <input class="form-control" type="password" name="password"
                       placeholder="请输入密码" required>
            </div>
            <button class="btn btn-primary btn-block" type="submit">登 录</button>
        </form>

        <p class="text-center text-muted mt-3">还没有账号？<a href="${ctx}/user/register">立即注册</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
