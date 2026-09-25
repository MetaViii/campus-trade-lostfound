<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value="publish"/>
<c:set var="pageTitle" value="发布失物招领"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<p class="text-muted" style="font-size:13px;"><a href="${ctx}/publish">发布</a> / 发布招领</p>

<div class="form-card">
    <h2 style="margin-top:0;">发布失物 / 招领信息</h2>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <form action="${ctx}/lostfound/publish" method="post" enctype="multipart/form-data">
        <div class="form-row">
            <div class="form-group">
                <label>信息类型 <span class="req">*</span></label>
                <select class="form-control" name="type" required>
                    <option value="1" ${lf.type == 1 ? 'selected' : ''}>我丢失了物品（寻物）</option>
                    <option value="2" ${lf.type == 2 ? 'selected' : ''}>我捡到了物品（招领）</option>
                </select>
            </div>
            <div class="form-group">
                <label>物品分类 <span class="req">*</span></label>
                <select class="form-control" name="categoryId" required>
                    <option value="">请选择分类</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${lf.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label>物品名称 <span class="req">*</span></label>
            <input class="form-control" type="text" name="name" value="${lf.name}" placeholder="如：校园一卡通、黑色雨伞" required>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label>地点</label>
                <input class="form-control" type="text" name="place" value="${lf.place}" placeholder="丢失 / 拾到的地点">
            </div>
            <div class="form-group">
                <label>时间</label>
                <input class="form-control" type="datetime-local" name="happenTime">
            </div>
        </div>
        <div class="form-group">
            <label>物品特征 / 保管说明</label>
            <textarea class="form-control" name="feature" rows="3" placeholder="描述物品的颜色、外观特征，或目前的保管方式">${lf.feature}</textarea>
        </div>
        <div class="form-group">
            <label>联系方式 <span class="req">*</span></label>
            <input class="form-control" type="text" name="contact" value="${not empty lf.contact ? lf.contact : sessionScope.user.phone}" placeholder="手机号 / 微信" required>
        </div>
        <div class="form-group">
            <label>图片</label>
            <input class="form-control" type="file" name="image" accept="image/*" data-preview="imgPreview">
            <div id="imgPreview" class="mt-2"></div>
        </div>
        <button class="btn btn-accent" type="submit">立即发布</button>
        <a class="btn btn-outline" href="${ctx}/lostfound/list">取消</a>
    </form>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
