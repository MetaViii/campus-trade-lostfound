<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value=""/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="empty">
    <div class="icon">⚠️</div>
    <h2>出错了</h2>
    <p class="text-muted"><c:out value="${error}" default="抱歉，您访问的页面不存在或发生了错误。"/></p>
    <a class="btn btn-primary mt-3" href="${ctx}/index">返回首页</a>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
