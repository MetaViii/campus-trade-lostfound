<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="active" value=""/>
<c:set var="pageTitle" value="个人中心"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 style="font-size:22px;">个人中心</h1>

<c:if test="${not empty success}"><div class="alert alert-success">${success}</div></c:if>
<c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:24px;align-items:start;">
    <div class="form-card" style="margin:0;max-width:none;">
        <h3 style="margin-top:0;">基本资料</h3>
        <div class="form-group">
            <label>账号</label>
            <input class="form-control" type="text" value="${sessionScope.user.account}" disabled>
            <div class="form-hint">注册时间：<fmt:formatDate value="${sessionScope.user.createTime}" pattern="yyyy-MM-dd HH:mm"/></div>
        </div>
        <form action="${ctx}/user/profile" method="post">
            <div class="form-group">
                <label>昵称 <span class="req">*</span></label>
                <input class="form-control" type="text" name="nickname" value="${sessionScope.user.nickname}" required>
            </div>
            <div class="form-group">
                <label>联系方式</label>
                <input class="form-control" type="text" name="phone" value="${sessionScope.user.phone}" placeholder="手机号 / 微信">
            </div>
            <button class="btn btn-primary" type="submit">保存资料</button>
        </form>
    </div>

    <div class="form-card" style="margin:0;max-width:none;">
        <h3 style="margin-top:0;">修改密码</h3>
        <form action="${ctx}/user/password" method="post">
            <div class="form-group">
                <label>原密码 <span class="req">*</span></label>
                <input class="form-control" type="password" name="oldPwd" required>
            </div>
            <div class="form-group">
                <label>新密码 <span class="req">*</span></label>
                <input class="form-control" type="password" name="newPwd" placeholder="不少于 6 位" required>
            </div>
            <div class="form-group">
                <label>确认新密码 <span class="req">*</span></label>
                <input class="form-control" type="password" name="confirm" required>
            </div>
            <button class="btn btn-primary" type="submit">修改密码</button>
        </form>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
