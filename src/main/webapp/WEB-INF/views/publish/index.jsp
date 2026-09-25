<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value="publish"/>
<c:set var="pageTitle" value="发布"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<h1 style="margin-bottom:6px;">发布信息</h1>
<p class="text-muted" style="margin-top:0;">先选一下要发布哪一类，再填写对应的信息。</p>

<div style="display:grid;grid-template-columns:1fr 1fr;gap:22px;margin-top:20px;">

    <div class="panel" style="padding:34px 28px;text-align:center;">
        <div style="font-size:46px;line-height:1;">📦</div>
        <h2 style="margin:16px 0 8px;">发布闲置</h2>
        <p class="text-muted" style="font-size:14px;min-height:44px;margin:0 0 18px;">
            把用不上的教材、数码、生活用品挂上来，<br>转给需要的同学。
        </p>
        <a class="btn btn-primary btn-block" href="${ctx}/goods/publish">去发布闲置</a>
    </div>

    <div class="panel" style="padding:34px 28px;text-align:center;">
        <div style="font-size:46px;line-height:1;">🔍</div>
        <h2 style="margin:16px 0 8px;">发布招领</h2>
        <p class="text-muted" style="font-size:14px;min-height:44px;margin:0 0 18px;">
            丢了东西，或捡到东西，<br>在这里登记一下方便对方找到。
        </p>
        <a class="btn btn-accent btn-block" href="${ctx}/lostfound/publish">去发布招领</a>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
