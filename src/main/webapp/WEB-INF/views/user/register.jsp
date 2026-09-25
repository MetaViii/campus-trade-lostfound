<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value=""/>
<%-- 访客页面：不展示功能导航，页脚也只写中性版权，避免把系统功能直接列给访客 --%>
<c:set var="guestPage" value="true"/>
<c:set var="pageTitle" value="注册"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div style="display:flex;justify-content:center;padding:40px 0;">
    <div class="form-card" style="width:100%;max-width:460px;margin:0;">
        <div class="text-center" style="margin-bottom:24px;">
            <div style="font-size:40px;line-height:1;">📝</div>
            <h2 style="margin:12px 0 6px;">注册</h2>
            <p class="text-muted" style="margin:0;font-size:13px;">创建一个账号即可开始使用</p>
        </div>

        <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

        <form action="${ctx}/user/register" method="post">
            <div class="form-group">
                <label>账号 <span class="req">*</span></label>
                <input class="form-control" type="text" name="account" value="${account}"
                       placeholder="3~20 个字符，登录时使用" required>
            </div>
            <div class="form-group">
                <label>昵称 <span class="req">*</span></label>
                <input class="form-control" type="text" name="nickname" value="${nickname}"
                       placeholder="展示给其他同学看的名字" required>
            </div>
            <div class="form-row">
                <div class="form-group">
                    <label>密码 <span class="req">*</span></label>
                    <input class="form-control" type="password" name="password"
                           placeholder="不少于 6 位" required>
                </div>
                <div class="form-group">
                    <label>确认密码 <span class="req">*</span></label>
                    <input class="form-control" type="password" name="confirm"
                           placeholder="再次输入密码" required>
                </div>
            </div>
            <div class="form-group">
                <label>联系方式</label>
                <input class="form-control" type="text" name="phone" value="${phone}"
                       placeholder="手机号 / 微信，方便别人联系你">
            </div>
            <button class="btn btn-primary btn-block" type="submit">注 册</button>
        </form>

        <p class="text-center text-muted mt-3">已有账号？<a href="${ctx}/user/login">返回登录</a></p>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
