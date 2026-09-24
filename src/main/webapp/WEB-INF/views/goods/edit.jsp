<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value=""/>
<c:set var="pageTitle" value="编辑商品"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="form-card">
    <h2 style="margin-top:0;">编辑商品</h2>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <form action="${ctx}/goods/edit" method="post" enctype="multipart/form-data">
        <input type="hidden" name="id" value="${goods.id}">
        <div class="form-group">
            <label>商品标题 <span class="req">*</span></label>
            <input class="form-control" type="text" name="title" value="${goods.title}" required>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label>分类 <span class="req">*</span></label>
                <select class="form-control" name="categoryId" required>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${goods.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>价格（元）<span class="req">*</span></label>
                <input class="form-control" type="number" name="price" value="${goods.price}" min="0" step="0.01" required>
            </div>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label>成色</label>
                <select class="form-control" name="quality">
                    <c:forEach var="q" items="${['全新','99新','95新','9成新','8成新','7成新','7成新以下']}">
                        <option value="${q}" ${goods.quality == q ? 'selected' : ''}>${q}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>状态</label>
                <select class="form-control" name="status">
                    <option value="1" ${goods.status == 1 ? 'selected' : ''}>在售</option>
                    <option value="2" ${goods.status == 2 ? 'selected' : ''}>已售</option>
                    <option value="0" ${goods.status == 0 ? 'selected' : ''}>下架</option>
                </select>
            </div>
        </div>
        <div class="form-group">
            <label>交易地点</label>
            <input class="form-control" type="text" name="tradePlace" value="${goods.tradePlace}">
        </div>
        <div class="form-group">
            <label>联系方式 <span class="req">*</span></label>
            <input class="form-control" type="text" name="contact" value="${goods.contact}" required>
        </div>
        <div class="form-group">
            <label>商品描述</label>
            <textarea class="form-control" name="description" rows="4">${goods.description}</textarea>
        </div>
        <div class="form-group">
            <label>商品图片</label>
            <c:if test="${not empty goods.image}">
                <div class="mt-2" style="margin-bottom:8px;"><img src="${ctx}/static/uploads/${goods.image}" style="max-width:160px;border-radius:8px;"></div>
            </c:if>
            <input class="form-control" type="file" name="image" accept="image/*" data-preview="imgPreview">
            <div id="imgPreview" class="mt-2"></div>
            <div class="form-hint">不选择则保留原图片。</div>
        </div>
        <button class="btn btn-primary" type="submit">保存修改</button>
        <a class="btn btn-outline" href="${ctx}/goods/detail?id=${goods.id}">取消</a>
    </form>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
