<%@ page pageEncoding="UTF-8" %>
</div><!-- /.container -->
<footer style="padding:26px 20px;text-align:center;color:#9ca3af;font-size:13px;border-top:1px solid #e5e7eb;background:#fff;margin-top:40px;">
    <%-- 访客页面（登录/注册）只写中性版权，不描述系统有哪些功能 --%>
    <c:choose>
        <c:when test="${guestPage}">© 2026 校园易物</c:when>
        <c:otherwise>© 2026 校园二手交易与失物招领管理系统 &nbsp;·&nbsp; 让闲置流转，让失物归家</c:otherwise>
    </c:choose>
</footer>
<script src="${ctx}/static/js/main.js"></script>
</body>
</html>
