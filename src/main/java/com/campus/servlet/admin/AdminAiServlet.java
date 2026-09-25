package com.campus.servlet.admin;

import com.campus.entity.AiConfig;
import com.campus.entity.AiProvider;
import com.campus.service.AiService;
import com.campus.service.impl.AiServiceImpl;
import com.campus.servlet.BaseServlet;
import com.campus.util.ServiceException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * 后台 AI 管理控制器。
 * 负责三件事：全局开关设置、向量/对话两组服务商配置的增删改与切换、查看所有用户的 AI 对话记录。
 */
@WebServlet("/admin/ai/*")
public class AdminAiServlet extends BaseServlet {

    /** 日志列表每页条数 */
    private static final int PAGE_SIZE = 10;

    private final AiService aiService = new AiServiceImpl();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String path = req.getPathInfo() == null ? "/config" : req.getPathInfo();
        switch (path) {
            case "/config":            config(req, resp); break;
            case "/basic/save":        saveBasic(req, resp); break;
            case "/provider/save":     saveProvider(req, resp); break;
            case "/provider/delete":   deleteProvider(req, resp); break;
            case "/provider/activate": activateProvider(req, resp); break;
            case "/provider/test":     testProvider(req, resp); break;
            case "/rebuild":           rebuild(req, resp); break;
            case "/logs":              logs(req, resp); break;
            default:                   resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /** 配置管理页：全局设置 + 两组服务商配置 + 索引状态 */
    private void config(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("config", aiService.getConfig());
        req.setAttribute("embedProviders", aiService.findProviders(AiProvider.KIND_EMBED));
        req.setAttribute("chatProviders", aiService.findProviders(AiProvider.KIND_CHAT));
        req.setAttribute("ready", aiService.isReady());
        req.setAttribute("unavailableReason", aiService.unavailableReason());
        req.setAttribute("lostFoundCached", aiService.cachedCount(AiService.MODULE_LOST_FOUND));
        req.setAttribute("lostFoundTotal", aiService.recordCount(AiService.MODULE_LOST_FOUND));
        req.setAttribute("goodsCached", aiService.cachedCount(AiService.MODULE_GOODS));
        req.setAttribute("goodsTotal", aiService.recordCount(AiService.MODULE_GOODS));

        // 点「编辑」时把那条配置带出来，让表单预填
        Integer editId = getInteger(req, "edit");
        if (editId != null) {
            req.setAttribute("editProvider", aiService.findProvider(editId));
        }
        forward(req, resp, "/WEB-INF/views/admin/ai_config.jsp");
    }

    /** 保存全局设置 */
    private void saveBasic(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AiConfig config = new AiConfig();
        config.setEnabled(getInt(req, "enabled", 0));
        config.setTopN(getInteger(req, "topN"));
        try {
            aiService.saveBasicConfig(config);
            redirect(req, resp, "/admin/ai/config?ok=" + enc("设置已保存"));
        } catch (ServiceException e) {
            redirect(req, resp, "/admin/ai/config?msg=" + enc(e.getMessage()));
        }
    }

    /** 新增或修改一条服务商配置 */
    private void saveProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AiProvider provider = new AiProvider();
        provider.setId(getInteger(req, "id"));
        provider.setKind(getInteger(req, "kind"));
        provider.setName(req.getParameter("name"));
        provider.setBaseUrl(req.getParameter("baseUrl"));
        provider.setApiKey(req.getParameter("apiKey"));
        provider.setModel(req.getParameter("model"));
        try {
            aiService.saveProvider(provider);
            redirect(req, resp, "/admin/ai/config?ok=" + enc("配置已保存"));
        } catch (ServiceException e) {
            redirect(req, resp, "/admin/ai/config?msg=" + enc(e.getMessage()));
        }
    }

    private void deleteProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        if (id != null) {
            aiService.deleteProvider(id);
        }
        redirect(req, resp, "/admin/ai/config?ok=" + enc("配置已删除"));
    }

    private void activateProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Integer id = getInteger(req, "id");
        String label = "1".equals(req.getParameter("kind")) ? "向量模型" : "对话模型";
        try {
            if (id != null) {
                aiService.activateProvider(id);
            }
            redirect(req, resp, "/admin/ai/config?ok=" + enc("已把该" + label + "配置设为使用中"));
        } catch (ServiceException e) {
            redirect(req, resp, "/admin/ai/config?msg=" + enc(e.getMessage()));
        }
    }

    /** 测试某条配置的连通性。Key 留空则用已保存的 */
    private void testProvider(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        AiProvider provider = new AiProvider();
        provider.setId(getInteger(req, "id"));
        provider.setKind(getInteger(req, "kind"));
        provider.setName(req.getParameter("name"));
        provider.setBaseUrl(req.getParameter("baseUrl"));
        provider.setApiKey(req.getParameter("apiKey"));
        provider.setModel(req.getParameter("model"));
        // 测完回到编辑该条的表单，方便直接改
        String back = provider.getId() == null ? "" : "&edit=" + provider.getId();
        redirect(req, resp, "/admin/ai/config?test=" + enc(aiService.testProvider(provider)) + back);
    }

    /** 重建某个模块的向量索引 */
    private void rebuild(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int module = getInt(req, "module", AiService.MODULE_LOST_FOUND);
        String label = module == AiService.MODULE_LOST_FOUND ? "失物招领" : "二手商品";
        try {
            int count = aiService.rebuildIndex(module);
            redirect(req, resp, "/admin/ai/config?ok="
                    + enc(label + "索引重建完成，共生成 " + count + " 条向量"));
        } catch (ServiceException e) {
            redirect(req, resp, "/admin/ai/config?msg=" + enc("索引重建失败：" + e.getMessage()));
        }
    }

    /** 所有用户的 AI 对话记录 */
    private void logs(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        Integer module = getInteger(req, "module");
        String keyword = req.getParameter("keyword");
        int pageNum = getInt(req, "page", 1);

        req.setAttribute("page", aiService.findLogs(module, keyword, pageNum, PAGE_SIZE));
        req.setAttribute("module", module);
        req.setAttribute("keyword", keyword);
        forward(req, resp, "/WEB-INF/views/admin/ai_log.jsp");
    }

    private static String enc(String s) throws IOException {
        return URLEncoder.encode(s == null ? "" : s, "UTF-8");
    }
}
