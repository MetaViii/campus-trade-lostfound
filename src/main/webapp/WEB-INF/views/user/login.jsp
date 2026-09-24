<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value=""/>
<c:set var="pageTitle" value="登录"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="form-card" style="max-width:420px;">
    <h2 class="text-center" style="margin-top:0;">欢迎登录</h2>
    <p class="text-center text-muted" style="margin-top:-6px;">校园二手交易与失物招领管理系统</p>

    <c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>
    <c:if test="${param.tip == 'login'}"><div class="alert alert-info">请先登录后再进行该操作。</div></c:if>

    <form action="${ctx}/user/login" method="post">
        <input type="hidden" name="returnUrl" value="${param.returnUrl}">
        <div class="form-group">
            <label>账号</label>
            <input class="form-control" type="text" name="account" value="${account}" placeholder="请输入账号" required autofocus>
        </div>
        <div class="form-group">
            <label>密码</label>
            <input class="form-control" type="password" name="password" placeholder="请输入密码" required>
        </div>
        <button class="btn btn-primary btn-block" type="submit">登 录</button>
    </form>
    <p class="text-center text-muted mt-3">还没有账号？<a href="${ctx}/user/register">立即注册</a></p>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
