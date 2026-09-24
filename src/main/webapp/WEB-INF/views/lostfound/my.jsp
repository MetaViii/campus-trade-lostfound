<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="active" value=""/>
<c:set var="pageTitle" value="我的失物招领"/>
<%@ include file="/WEB-INF/views/common/header.jsp" %>

<div class="flex-between mb-3">
    <h1 style="font-size:22px;margin:0;">我发布的失物招领</h1>
    <div>
        <a class="btn btn-outline" href="${ctx}/goods/my">我的商品</a>
        <a class="btn btn-accent" href="${ctx}/lostfound/publish">＋ 发布失物/招领</a>
    </div>
</div>

<c:choose>
    <c:when test="${empty page.list}">
        <div class="empty"><div class="icon">🔍</div>你还没有发布过失物招领信息</div>
    </c:when>
    <c:otherwise>
        <div class="panel">
            <table class="table">
                <thead>
                <tr><th>类型</th><th>物品名称</th><th>分类</th><th>地点</th><th>状态</th><th>发布时间</th><th>操作</th></tr>
                </thead>
                <tbody>
                <c:forEach var="lf" items="${page.list}">
                    <tr>
                        <td><span class="badge ${lf.type==1?'badge-danger':'badge-success'}">${lf.typeText}</span></td>
                        <td><a href="${ctx}/lostfound/detail?id=${lf.id}">${lf.name}</a></td>
                        <td>${lf.categoryName}</td>
                        <td>${lf.place}</td>
                        <td><span class="badge badge-muted">${lf.statusText}</span></td>
                        <td class="text-muted" style="font-size:13px;"><fmt:formatDate value="${lf.createTime}" pattern="yyyy-MM-dd"/></td>
                        <td>
                            <div class="actions">
                                <a class="btn btn-sm btn-outline" href="${ctx}/lostfound/edit?id=${lf.id}">编辑</a>
                                <form action="${ctx}/lostfound/status" method="post" style="display:inline-flex;gap:4px;">
                                    <input type="hidden" name="id" value="${lf.id}">
                                    <select name="status" class="form-control" style="padding:4px 6px;font-size:12px;width:auto;">
                                        <option value="1" ${lf.status==1?'selected':''}>寻找中</option>
                                        <option value="2" ${lf.status==2?'selected':''}>已找回</option>
                                        <option value="3" ${lf.status==3?'selected':''}>待认领</option>
                                        <option value="4" ${lf.status==4?'selected':''}>已认领</option>
                                        <option value="0" ${lf.status==0?'selected':''}>已关闭</option>
                                    </select>
                                    <button class="btn btn-sm btn-outline" type="submit">更新</button>
                                </form>
                                <form action="${ctx}/lostfound/delete" method="post" style="display:inline;">
                                    <input type="hidden" name="id" value="${lf.id}">
                                    <button class="btn btn-sm btn-danger" type="submit" data-confirm="确定删除该信息吗？">删除</button>
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
                        <c:otherwise><a href="${ctx}/lostfound/my?page=${i}">${i}</a></c:otherwise>
                    </c:choose>
                </c:forEach>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<%@ include file="/WEB-INF/views/common/footer.jsp" %>
