<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="adminActive" value="aiConfig"/>
<c:set var="pageTitle" value="AI 设置"/>
<c:set var="editEmbed" value="${not empty editProvider and editProvider.kind == 1}"/>
<c:set var="editChat" value="${not empty editProvider and editProvider.kind == 2}"/>
<%@ include file="/WEB-INF/views/common/admin_header.jsp" %>

<h1>AI 设置</h1>

<c:if test="${not empty param.ok}"><div class="alert alert-success">${param.ok}</div></c:if>
<c:if test="${not empty param.msg}"><div class="alert alert-danger">${param.msg}</div></c:if>
<c:if test="${not empty param.test}"><div class="alert alert-info">${param.test}</div></c:if>

<%-- 当前到底能不能用，直接说清楚，避免「保存成功却跑不起来」 --%>
<div class="alert ${ready ? 'alert-success' : 'alert-danger'}">
    <b>当前状态：</b>
    <c:choose>
        <c:when test="${ready}">AI 功能可用，前台「AI 匹配」页面可以正常使用。</c:when>
        <c:otherwise>AI 功能<b>不可用</b> —— ${unavailableReason}。</c:otherwise>
    </c:choose>
</div>

<p class="text-muted" style="font-size:13px;">
    向量模型负责把文字转成向量以查找相似信息，是<b>必填</b>；对话模型据此生成一段自然语言答复，<b>选填</b>。
    两类都可以配多条并随时切换启用。接口地址填到 <code>/v1</code> 这一层即可，系统会自动补上
    <code>/embeddings</code> 与 <code>/chat/completions</code>。
</p>

<%-- ==================== 全局设置 ==================== --%>
<form action="${ctx}/admin/ai/basic/save" method="post">
    <div class="panel">
        <div class="panel-head">基本设置</div>
        <div style="padding:18px 20px;">
            <div class="form-row">
                <div class="form-group">
                    <label>AI 功能</label>
                    <select class="form-control" name="enabled">
                        <option value="1" ${config.enabled==1?'selected':''}>开启</option>
                        <option value="0" ${config.enabled!=1?'selected':''}>关闭</option>
                    </select>
                    <div class="form-hint">开启前请先确保下面有一条「使用中」且填写完整的向量模型配置。</div>
                </div>
                <div class="form-group">
                    <label>每次返回条数</label>
                    <input class="form-control" type="number" name="topN" min="1" max="20" value="${config.topN}">
                    <div class="form-hint">相似度最高的前 N 条，建议 3~8。</div>
                </div>
            </div>
            <button class="btn btn-primary" type="submit">保存设置</button>
            <a class="btn btn-outline" href="${ctx}/admin/ai/logs">查看 AI 对话记录</a>
            <c:if test="${not empty config.updateTime}">
                <span class="text-muted" style="font-size:13px;margin-left:10px;">
                    最后修改 <fmt:formatDate value="${config.updateTime}" pattern="yyyy-MM-dd HH:mm"/>
                </span>
            </c:if>
        </div>
    </div>
</form>

<%-- ==================== 向量模型配置 ==================== --%>
<div class="panel" style="margin-top:22px;">
    <div class="panel-head">🧮 向量模型配置</div>
    <table class="table">
        <thead>
        <tr>
            <th style="width:130px;">名称</th>
            <th>接口地址</th>
            <th style="width:150px;">模型名</th>
            <th style="width:110px;">API Key</th>
            <th style="width:110px;">状态</th>
            <th style="width:200px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${embedProviders}">
            <tr>
                <td><b>${p.name}</b></td>
                <td style="font-size:13px;">${p.baseUrl}</td>
                <td style="font-size:13px;">${p.model}</td>
                <td class="text-muted" style="font-size:13px;">${p.apiKeyMasked}</td>
                <td>
                    <c:choose>
                        <c:when test="${not p.complete}"><span class="badge badge-warning">配置不完整</span></c:when>
                        <c:when test="${p.activeOn}"><span class="badge badge-success">● 使用中</span></c:when>
                        <c:otherwise><span class="badge badge-muted">未启用</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <div class="actions">
                        <c:if test="${not p.activeOn}">
                            <form action="${ctx}/admin/ai/provider/activate" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="${p.id}">
                                <input type="hidden" name="kind" value="1">
                                <button class="btn btn-sm btn-success" type="submit">设为使用中</button>
                            </form>
                        </c:if>
                        <a class="btn btn-sm btn-outline" href="${ctx}/admin/ai/config?edit=${p.id}#embedForm">编辑</a>
                        <form action="${ctx}/admin/ai/provider/delete" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${p.id}">
                            <button class="btn btn-sm btn-danger" type="submit"
                                    data-confirm="确定删除配置「${p.name}」吗？">删除</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty embedProviders}">
            <tr><td colspan="6" class="text-muted text-center" style="padding:26px;">
                还没有向量模型配置，请在下面新增一条
            </td></tr>
        </c:if>
        </tbody>
    </table>

    <div style="padding:18px 20px;border-top:1px solid var(--border);" id="embedForm">
        <h3 style="margin-top:0;">${editEmbed ? '编辑向量模型配置' : '新增向量模型配置'}</h3>
        <form action="${ctx}/admin/ai/provider/save" method="post">
            <input type="hidden" name="kind" value="1">
            <input type="hidden" name="id" value="${editEmbed ? editProvider.id : ''}">
            <div class="form-row">
                <div class="form-group">
                    <label>配置名称 <span class="req">*</span></label>
                    <input class="form-control" type="text" name="name" required
                           value="${editEmbed ? editProvider.name : ''}" placeholder="如：硅基流动">
                </div>
                <div class="form-group">
                    <label>模型名 <span class="req">*</span></label>
                    <input class="form-control" type="text" name="model" required
                           value="${editEmbed ? editProvider.model : ''}" placeholder="BAAI/bge-m3">
                </div>
            </div>
            <div class="form-group">
                <label>接口地址 <span class="req">*</span></label>
                <input class="form-control" type="text" name="baseUrl" required
                       value="${editEmbed ? editProvider.baseUrl : ''}"
                       placeholder="https://api.siliconflow.cn/v1">
            </div>
            <div class="form-group">
                <label>API Key</label>
                <input class="form-control" type="text" name="apiKey" autocomplete="off"
                       placeholder="${editEmbed ? editProvider.apiKeyMasked : '本地 Ollama 之类的服务可以不填'}">
                <div class="form-hint">
                    <c:choose>
                        <c:when test="${editEmbed}">当前：${editProvider.apiKeyMasked}。留空表示不修改。</c:when>
                        <c:otherwise>留空表示该服务不需要 Key。</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <button class="btn btn-primary" type="submit">保存</button>
            <button class="btn btn-outline" type="submit"
                    formaction="${ctx}/admin/ai/provider/test">测试连通性</button>
            <c:if test="${editEmbed}">
                <a class="btn btn-outline" href="${ctx}/admin/ai/config#embedForm">取消编辑</a>
            </c:if>
        </form>
    </div>
</div>

<%-- ==================== 对话模型配置 ==================== --%>
<div class="panel" style="margin-top:22px;">
    <div class="panel-head">💬 对话模型配置（选填）</div>
    <table class="table">
        <thead>
        <tr>
            <th style="width:130px;">名称</th>
            <th>接口地址</th>
            <th style="width:150px;">模型名</th>
            <th style="width:110px;">API Key</th>
            <th style="width:110px;">状态</th>
            <th style="width:200px;">操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="p" items="${chatProviders}">
            <tr>
                <td><b>${p.name}</b></td>
                <td style="font-size:13px;">${p.baseUrl}</td>
                <td style="font-size:13px;">${p.model}</td>
                <td class="text-muted" style="font-size:13px;">${p.apiKeyMasked}</td>
                <td>
                    <c:choose>
                        <c:when test="${not p.complete}"><span class="badge badge-warning">配置不完整</span></c:when>
                        <c:when test="${p.activeOn}"><span class="badge badge-success">● 使用中</span></c:when>
                        <c:otherwise><span class="badge badge-muted">未启用</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <div class="actions">
                        <c:if test="${not p.activeOn}">
                            <form action="${ctx}/admin/ai/provider/activate" method="post" style="display:inline;">
                                <input type="hidden" name="id" value="${p.id}">
                                <input type="hidden" name="kind" value="2">
                                <button class="btn btn-sm btn-success" type="submit">设为使用中</button>
                            </form>
                        </c:if>
                        <a class="btn btn-sm btn-outline" href="${ctx}/admin/ai/config?edit=${p.id}#chatForm">编辑</a>
                        <form action="${ctx}/admin/ai/provider/delete" method="post" style="display:inline;">
                            <input type="hidden" name="id" value="${p.id}">
                            <button class="btn btn-sm btn-danger" type="submit"
                                    data-confirm="确定删除配置「${p.name}」吗？">删除</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty chatProviders}">
            <tr><td colspan="6" class="text-muted text-center" style="padding:26px;">
                还没有对话模型配置。不配也能用，只是页面上不显示 AI 生成的那段文字答复。
            </td></tr>
        </c:if>
        </tbody>
    </table>

    <div style="padding:18px 20px;border-top:1px solid var(--border);" id="chatForm">
        <h3 style="margin-top:0;">${editChat ? '编辑对话模型配置' : '新增对话模型配置'}</h3>
        <form action="${ctx}/admin/ai/provider/save" method="post">
            <input type="hidden" name="kind" value="2">
            <input type="hidden" name="id" value="${editChat ? editProvider.id : ''}">
            <div class="form-row">
                <div class="form-group">
                    <label>配置名称 <span class="req">*</span></label>
                    <input class="form-control" type="text" name="name" required
                           value="${editChat ? editProvider.name : ''}" placeholder="如：DeepSeek">
                </div>
                <div class="form-group">
                    <label>模型名 <span class="req">*</span></label>
                    <input class="form-control" type="text" name="model" required
                           value="${editChat ? editProvider.model : ''}" placeholder="deepseek-chat">
                </div>
            </div>
            <div class="form-group">
                <label>接口地址 <span class="req">*</span></label>
                <input class="form-control" type="text" name="baseUrl" required
                       value="${editChat ? editProvider.baseUrl : ''}"
                       placeholder="https://api.deepseek.com/v1">
            </div>
            <div class="form-group">
                <label>API Key</label>
                <input class="form-control" type="text" name="apiKey" autocomplete="off"
                       placeholder="${editChat ? editProvider.apiKeyMasked : ''}">
                <div class="form-hint">
                    <c:choose>
                        <c:when test="${editChat}">当前：${editProvider.apiKeyMasked}。留空表示不修改。</c:when>
                        <c:otherwise>留空表示该服务不需要 Key。</c:otherwise>
                    </c:choose>
                </div>
            </div>
            <button class="btn btn-primary" type="submit">保存</button>
            <button class="btn btn-outline" type="submit"
                    formaction="${ctx}/admin/ai/provider/test">测试连通性</button>
            <c:if test="${editChat}">
                <a class="btn btn-outline" href="${ctx}/admin/ai/config#chatForm">取消编辑</a>
            </c:if>
        </form>
    </div>
</div>

<%-- ==================== 向量索引状态 ==================== --%>
<div class="panel" style="margin-top:22px;">
    <div class="panel-head">📇 向量索引状态</div>
    <table class="table">
        <thead>
        <tr><th>模块</th><th>参与匹配的条目</th><th>已建立索引</th><th>操作</th></tr>
        </thead>
        <tbody>
        <tr>
            <td>失物招领</td>
            <td>${lostFoundTotal}</td>
            <td>
                <c:choose>
                    <c:when test="${lostFoundCached >= lostFoundTotal && lostFoundTotal > 0}">
                        <span class="badge badge-success">${lostFoundCached} / ${lostFoundTotal}</span>
                    </c:when>
                    <c:otherwise><span class="badge badge-warning">${lostFoundCached} / ${lostFoundTotal}</span></c:otherwise>
                </c:choose>
            </td>
            <td>
                <form action="${ctx}/admin/ai/rebuild" method="post" style="display:inline;">
                    <input type="hidden" name="module" value="1">
                    <button class="btn btn-sm btn-outline" type="submit"
                            data-confirm="将清空并重建失物招领的全部向量，确定吗？">重建索引</button>
                </form>
            </td>
        </tr>
        <tr>
            <td>二手商品</td>
            <td>${goodsTotal}</td>
            <td>
                <c:choose>
                    <c:when test="${goodsCached >= goodsTotal && goodsTotal > 0}">
                        <span class="badge badge-success">${goodsCached} / ${goodsTotal}</span>
                    </c:when>
                    <c:otherwise><span class="badge badge-warning">${goodsCached} / ${goodsTotal}</span></c:otherwise>
                </c:choose>
            </td>
            <td>
                <form action="${ctx}/admin/ai/rebuild" method="post" style="display:inline;">
                    <input type="hidden" name="module" value="2">
                    <button class="btn btn-sm btn-outline" type="submit"
                            data-confirm="将清空并重建二手商品的全部向量，确定吗？">重建索引</button>
                </form>
            </td>
        </tr>
        </tbody>
    </table>
    <div style="padding:12px 20px;" class="text-muted">
        <span style="font-size:13px;">
            向量在首次检索时自动生成并缓存，不必手动重建；换了向量模型或修改了信息内容后再重建。
        </span>
    </div>
</div>

<%@ include file="/WEB-INF/views/common/admin_footer.jsp" %>
