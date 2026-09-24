<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="active" value=""/>
<c:set var="pageTitle" value="我的发布"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="flex-between mb-3">
    <h1 style="font-size:22px;margin:0;">我发布的商品</h1>
    <div>
        <a class="btn btn-outline" href="${ctx}/lostfound/my">我的失物招领</a>
        <a class="btn btn-primary" href="${ctx}/goods/publish">＋ 发布闲置</a>
    </div>
</div>

<c:choose>
    <c:when test="${empty page.list}">
        <div class="empty"><div class="icon">📦</div>你还没有发布过商品<br><a class="btn btn-primary mt-3" href="${ctx}/goods/publish">去发布</a></div>
    </c:when>
    <c:otherwise>
        <div class="panel">
            <table class="table">
                <thead>
                <tr><th>商品</th><th>价格</th><th>分类</th><th>状态</th><th>浏览</th><th>发布时间</th><th>操作</th></tr>
                </thead>
                <tbody>
                <c:forEach var="g" items="${page.list}">
                    <tr>
                        <td>
                            <div style="display:flex;align-items:center;gap:10px;">
                                <c:choose>
                                    <c:when test="${not empty g.image}"><img class="thumb" src="${ctx}/static/uploads/${g.image}"></c:when>
                                    <c:otherwise><span class="thumb" style="display:inline-flex;align-items:center;justify-content:center;background:#eef2ff;">📦</span></c:otherwise>
                                </c:choose>
                                <a href="${ctx}/goods/detail?id=${g.id}">${g.title}</a>
                            </div>
                        </td>
                        <td class="price" style="font-size:15px;">¥<fmt:formatNumber value="${g.price}" pattern="0.00"/></td>
                        <td>${g.categoryName}</td>
                        <td><span class="badge ${g.status==1?'badge-success':(g.status==2?'badge-warning':'badge-muted')}">${g.statusText}</span></td>
                        <td>${g.viewCount}</td>
                        <td class="text-muted" style="font-size:13px;"><fmt:formatDate value="${g.createTime}" pattern="yyyy-MM-dd"/></td>
                        <td>
                            <div class="actions">
                                <a class="btn btn-sm btn-outline" href="${ctx}/goods/edit?id=${g.id}">编辑</a>
                                <form action="${ctx}/goods/status" method="post" style="display:inline-flex;gap:4px;">
                                    <input type="hidden" name="id" value="${g.id}">
                                    <select name="status" class="form-control" style="padding:4px 6px;font-size:12px;width:auto;">
                                        <option value="1" ${g.status==1?'selected':''}>在售</option>
                                        <option value="2" ${g.status==2?'selected':''}>已售</option>
                                        <option value="0" ${g.status==0?'selected':''}>下架</option>
                                    </select>
                                    <button class="btn btn-sm btn-outline" type="submit">更新</button>
                                </form>
                                <form action="${ctx}/goods/delete" method="post" style="display:inline;">
                                    <input type="hidden" name="id" value="${g.id}">
                                    <button class="btn btn-sm btn-danger" type="submit" data-confirm="确定删除该商品吗？">删除</button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <c:if test="${page.totalPages > 1}">
            <div class="pagination">
                <c:forEach var="i" begin="1" end="${page.totalPages}">
                    <c:choose>
                        <c:when test="${i == page.pageNum}"><span class="current">${i}</span></c:when>
                        <c:otherwise><a href="${ctx}/goods/my?page=${i}">${i}</a></c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
