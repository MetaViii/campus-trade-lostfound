<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="active" value="publish"/>
<c:set var="pageTitle" value="发布闲置"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<p class="text-muted" style="font-size:13px;"><a href="${ctx}/publish">发布</a> / 发布闲置</p>

<div class="form-card">
    <h2 style="margin-top:0;">发布二手商品</h2>
    <c:if test="${not empty error}"><div class="alert alert-danger">${error}</div></c:if>

    <form action="${ctx}/goods/publish" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label>商品标题 <span class="req">*</span></label>
            <input class="form-control" type="text" name="title" value="${goods.title}" placeholder="如：高等数学教材（同济第七版）" required>
        </div>
        <div class="form-row">
            <div class="form-group">
                <label>分类 <span class="req">*</span></label>
                <select class="form-control" name="categoryId" required>
                    <option value="">请选择分类</option>
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.id}" ${goods.categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-group">
                <label>价格（元）<span class="req">*</span></label>
                <input class="form-control" type="number" name="price" value="${goods.price}" min="0" step="0.01" placeholder="0.00" required>
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
                <label>交易地点</label>
                <input class="form-control" type="text" name="tradePlace" value="${goods.tradePlace}" placeholder="如：图书馆一楼、宿舍楼下">
            </div>
        </div>
        <div class="form-group">
            <label>联系方式 <span class="req">*</span></label>
            <input class="form-control" type="text" name="contact" value="${not empty goods.contact ? goods.contact : sessionScope.user.phone}" placeholder="手机号 / 微信" required>
        </div>
        <div class="form-group">
            <label>商品描述</label>
            <textarea class="form-control" name="description" rows="4" placeholder="描述商品的新旧程度、使用情况、是否包邮等">${goods.description}</textarea>
        </div>
        <div class="form-group">
            <label>商品图片</label>
            <input class="form-control" type="file" name="image" accept="image/*" data-preview="imgPreview">
            <div id="imgPreview" class="mt-2"></div>
            <div class="form-hint">支持 jpg/png，单张不超过 5MB；可不上传。</div>
        </div>
        <button class="btn btn-primary" type="submit">立即发布</button>
        <a class="btn btn-outline" href="${ctx}/goods/list">取消</a>
    </form>
</div>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
