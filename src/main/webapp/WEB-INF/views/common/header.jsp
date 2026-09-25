<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}" default="校园二手交易与失物招领管理系统"/></title>
    <link rel="stylesheet" href="${ctx}/static/css/main.css">
</head>
<body>
<nav class="navbar">
    <div class="navbar-inner">
        <a class="brand" href="${ctx}/index"><span class="logo">校</span>校园易物</a>
        <div class="nav-links">
            <%-- 登录 / 注册等访客页面设 guestPage，不把系统有哪些功能暴露出去 --%>
            <c:if test="${not guestPage}">
                <a href="${ctx}/index" class="${active=='index'?'active':''}">首页</a>
                <a href="${ctx}/goods/list" class="${active=='goods'?'active':''}">二手市场</a>
                <a href="${ctx}/lostfound/list" class="${active=='lostfound'?'active':''}">失物招领</a>
                <a href="${ctx}/ai/index" class="${active=='ai'?'active':''}">AI 匹配</a>
                <a href="${ctx}/publish" class="${active=='publish'?'active':''}">发布</a>
            </c:if>
        </div>
        <div class="nav-user">
            <c:choose>
                <c:when test="${empty sessionScope.user}">
                    <a class="btn btn-outline btn-sm" href="${ctx}/user/login">登录</a>
                    <a class="btn btn-primary btn-sm" href="${ctx}/user/register">注册</a>
                </c:when>
                <c:otherwise>
                    <span class="hi">你好，<b>${sessionScope.user.nickname}</b></span>
                    <c:if test="${sessionScope.user.admin}">
                        <a class="tag-link" href="${ctx}/admin/index">管理后台</a>
                    </c:if>
                    <a class="tag-link" href="${ctx}/goods/my">我的发布</a>
                    <a class="tag-link" href="${ctx}/user/profile">个人中心</a>
                    <a class="btn btn-outline btn-sm" href="${ctx}/user/logout">退出</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>
<div class="container">
