<%@ page pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}" default="后台管理"/> - 校园易物</title>
    <link rel="stylesheet" href="${ctx}/static/css/main.css?v=20260925">
</head>
<body>
<nav class="navbar">
    <div class="navbar-inner">
        <a class="brand" href="${ctx}/admin/index"><span class="logo">校</span>管理后台</a>
        <div class="nav-links"></div>
        <div class="nav-user">
            <span class="hi">${sessionScope.user.nickname}（管理员）</span>
            <a class="tag-link" href="${ctx}/index">返回前台</a>
            <a class="btn btn-outline btn-sm" href="${ctx}/user/logout">退出</a>
        </div>
    </div>
</nav>
<div class="admin-layout">
    <aside class="sidebar">
        <div class="title">管理菜单</div>
        <a href="${ctx}/admin/index" class="${adminActive=='dashboard'?'active':''}">📊 仪表盘</a>
        <a href="${ctx}/admin/user/list" class="${adminActive=='user'?'active':''}">👤 用户管理</a>
        <a href="${ctx}/admin/category/list" class="${adminActive=='category'?'active':''}">🏷️ 分类管理</a>
        <a href="${ctx}/admin/goods/list" class="${adminActive=='goods'?'active':''}">📦 商品管理</a>
        <a href="${ctx}/admin/lostfound/list" class="${adminActive=='lostfound'?'active':''}">🔍 失物招领</a>
        <a href="${ctx}/admin/ai/config" class="${adminActive=='aiConfig'?'active':''}">🤖 AI 设置</a>
        <a href="${ctx}/admin/ai/logs" class="${adminActive=='aiLogs'?'active':''}">💬 AI 对话记录</a>
    </aside>
    <main class="admin-main">
