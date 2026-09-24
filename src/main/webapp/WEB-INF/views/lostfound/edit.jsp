<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="active" value=""/>
<c:set var="pageTitle" value="编辑失物招领"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="form-card">
    <h2 style="margin-top:0;">编辑失物 / 招领信息</h2>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <form action="${ctx}/lostfound/edit" method="post" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${lf.id}">
        <div class="form-row">
            <div class="form-group">
                <label>信息类型 <span class="req">*</span></label>
                <select class="form-control" name="type" required>
                    <option value="1" ${lf.type == 1 ? 'selected' : ''}>寻物（失物）</option>
                    <option value="2" ${lf.type == 2 ? 'selected' : ''}>招领（拾物）</option>
                </select>
            </div>
            <div class="form-group">
                <label>物品分类 <span class="req">*</span></label>
                <select class="form-control" name="categoryId" required>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${lf.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label>物品名称 <span class="req">*</span></label>
            <input class="form-control" type="text" name="name" value="${lf.name}" required>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label>地点</label>
                <input class="form-control" type="text" name="place" value="${lf.place}">
            </div>
            <div class="form-group">
                <label>时间</label>
                <input class="form-control" type="datetime-local" name="happenTime"
                       value="<fmt:formatDate value='${lf.happenTime}' pattern='yyyy-MM-dd\'T\'HH:mm'/>">
            </div>
        </div>
        <div class="form-group">
            <label>状态</label>
            <select class="form-control" name="status">
                <option value="1" ${lf.status==1?'selected':''}>寻找中</option>
                <option value="2" ${lf.status==2?'selected':''}>已找回</option>
                <option value="3" ${lf.status==3?'selected':''}>待认领</option>
                <option value="4" ${lf.status==4?'selected':''}>已认领</option>
                <option value="0" ${lf.status==0?'selected':''}>已关闭</option>
            </select>
        </div>
        <div class="form-group">
            <label>物品特征 / 保管说明</label>
            <textarea class="form-control" name="feature" rows="3">${lf.feature}</textarea>
        </div>
        <div class="form-group">
            <label>联系方式 <span class="req">*</span></label>
            <input class="form-control" type="text" name="contact" value="${lf.contact}" required>
        </div>
        <div class="form-group">
            <label>图片</label>
            <c:if test="${not empty lf.image}">
                <div style="margin-bottom:8px;"><img src="${ctx}/static/uploads/${lf.image}" style="max-width:160px;border-radius:8px;"></div>
            </c:if>
            <input class="form-control" type="file" name="image" accept="image/*" data-preview="imgPreview">
            <div id="imgPreview" class="mt-2"></div>
            <div class="form-hint">不选择则保留原图片。</div>
        </div>
        <button class="btn btn-primary" type="submit">保存修改</button>
        <a class="btn btn-outline" href="${ctx}/lostfound/detail?id=${lf.id}">取消</a>
    </form>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
