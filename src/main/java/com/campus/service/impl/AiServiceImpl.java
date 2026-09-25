package com.campus.service.impl;

import com.campus.dao.AiConfigDao;
import com.campus.dao.AiEmbeddingDao;
import com.campus.dao.AiLogDao;
import com.campus.dao.AiProviderDao;
import com.campus.dao.GoodsDao;
import com.campus.dao.LostFoundDao;
import com.campus.dao.impl.AiConfigDaoImpl;
import com.campus.dao.impl.AiEmbeddingDaoImpl;
import com.campus.dao.impl.AiLogDaoImpl;
import com.campus.dao.impl.AiProviderDaoImpl;
import com.campus.dao.impl.GoodsDaoImpl;
import com.campus.dao.impl.LostFoundDaoImpl;
import com.campus.entity.AiConfig;
import com.campus.entity.AiEmbedding;
import com.campus.entity.AiLog;
import com.campus.entity.AiMatchResult;
import com.campus.entity.AiProvider;
import com.campus.entity.AiSearchResult;
import com.campus.entity.Goods;
import com.campus.entity.LostFound;
import com.campus.entity.User;
import com.campus.service.AiService;
import com.campus.util.AiClient;
import com.campus.util.PageBean;
import com.campus.util.ServiceException;
import com.campus.util.VectorUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 智能匹配服务实现类。
 *
 * <p>整体流程：取候选条目 → 向量化（命中缓存则跳过）→ 与用户描述的向量算余弦相似度
 * → 取相似度最高的若干条 → 交给对话模型生成一段答复 → 写对话日志。</p>
 *
 * <p>向量模型与对话模型各自从 ai_provider 表里取「当前使用中」的那条配置。</p>
 */
public class AiServiceImpl implements AiService {

    /** 参与匹配的候选条目上限，避免数据量变大后一次算太多 */
    private static final int MAX_CANDIDATES = 300;
    /** 一次批量向量化的条数，太多容易超出接口限制 */
    private static final int EMBED_BATCH_SIZE = 32;
    /** 相似度低于该值的候选直接丢掉，避免推荐明显无关的信息 */
    private static final double MIN_SIMILARITY = 0.2;
    /** 用户描述的最大长度 */
    private static final int MAX_QUERY_LENGTH = 500;
    /** 默认返回条数 */
    private static final int DEFAULT_TOP_N = 5;
    /** 详情摘要截断长度 */
    private static final int SNIPPET_LENGTH = 50;

    private static final String SYSTEM_PROMPT =
            "你是校园二手交易与失物招领系统的搜索助手。系统已用向量检索从数据库中"
            + "找出与用户描述最相似的若干条记录。请据此用简体中文简洁回复用户："
            + "有没有找到、最可能是哪几条、下一步建议怎么做。"
            + "只能依据给出的候选作答，不要编造候选之外的信息。"
            + "不要使用 Markdown 标记，直接输出纯文本，控制在 150 字以内。";

    private final AiConfigDao configDao = new AiConfigDaoImpl();
    private final AiProviderDao providerDao = new AiProviderDaoImpl();
    private final AiLogDao logDao = new AiLogDaoImpl();
    private final AiEmbeddingDao embeddingDao = new AiEmbeddingDaoImpl();
    private final LostFoundDao lostFoundDao = new LostFoundDaoImpl();
    private final GoodsDao goodsDao = new GoodsDaoImpl();

    // ------------------------------------------------------------------
    // 全局设置
    // ------------------------------------------------------------------

    @Override
    public AiConfig getConfig() {
        AiConfig config = configDao.find();
        if (config == null) {
            config = new AiConfig();
            config.setEnabled(0);
            config.setTopN(DEFAULT_TOP_N);
        }
        if (config.getTopN() == null || config.getTopN() < 1) {
            config.setTopN(DEFAULT_TOP_N);
        }
        return config;
    }

    @Override
    public void saveBasicConfig(AiConfig config) {
        if (config.getEnabled() == null) {
            config.setEnabled(0);
        }
        if (config.getTopN() == null || config.getTopN() < 1 || config.getTopN() > 20) {
            config.setTopN(DEFAULT_TOP_N);
        }

        // 要开启就先确认向量模型真的能用，避免「保存成功却跑不起来」
        if (config.getEnabled() == 1) {
            AiProvider embed = providerDao.findActive(AiProvider.KIND_EMBED);
            if (embed == null) {
                throw new ServiceException("要开启 AI 功能，请先添加并启用一条向量模型配置");
            }
            if (isBlank(embed.getBaseUrl())) {
                throw new ServiceException("启用中的向量模型配置「" + embed.getName() + "」缺少接口地址，请先补全");
            }
            if (isBlank(embed.getModel())) {
                throw new ServiceException("启用中的向量模型配置「" + embed.getName() + "」缺少模型名，请先补全");
            }
        }

        AiConfig old = configDao.find();
        if (old == null) {
            configDao.insert(config);
        } else {
            config.setId(old.getId());
            configDao.update(config);
        }
    }

    // ------------------------------------------------------------------
    // 服务商配置
    // ------------------------------------------------------------------

    @Override
    public List<AiProvider> findProviders(int kind) {
        return providerDao.findByKind(kind);
    }

    @Override
    public AiProvider findProvider(int id) {
        return providerDao.findById(id);
    }

    @Override
    public void saveProvider(AiProvider provider) {
        if (provider.getKind() == null
                || (provider.getKind() != AiProvider.KIND_EMBED
                    && provider.getKind() != AiProvider.KIND_CHAT)) {
            throw new ServiceException("请选择配置类型（向量模型 / 对话模型）");
        }
        if (isBlank(provider.getName())) {
            throw new ServiceException("请填写配置名称，方便区分不同服务商");
        }
        if (isBlank(provider.getBaseUrl())) {
            throw new ServiceException("请填写接口地址");
        }
        if (isBlank(provider.getModel())) {
            throw new ServiceException("请填写模型名");
        }
        provider.setName(provider.getName().trim());
        provider.setBaseUrl(provider.getBaseUrl().trim());
        provider.setModel(provider.getModel().trim());

        if (provider.getId() == null) {
            provider.setApiKey(trimOrEmpty(provider.getApiKey()));
            // 该类下还没有任何配置时，新加的直接设为使用中
            provider.setIsActive(providerDao.countByKind(provider.getKind()) == 0 ? 1 : 0);
            providerDao.insert(provider);
        } else {
            AiProvider old = providerDao.findById(provider.getId());
            if (old == null) {
                throw new ServiceException("要修改的配置不存在，可能已被删除");
            }
            // Key 留空表示沿用原来保存的，避免只改地址时把密钥清掉
            provider.setApiKey(isBlank(provider.getApiKey())
                    ? nullToEmpty(old.getApiKey())
                    : provider.getApiKey().trim());
            providerDao.update(provider);
        }
    }

    @Override
    public void deleteProvider(int id) {
        AiProvider provider = providerDao.findById(id);
        if (provider == null) {
            return;
        }
        providerDao.delete(id);
        // 删掉的正是使用中的那条，就把同类的下一条顶上，避免留下「没有启用项」的空档
        if (provider.isActiveOn()) {
            List<AiProvider> rest = providerDao.findByKind(provider.getKind());
            if (!rest.isEmpty()) {
                providerDao.clearActive(provider.getKind());
                providerDao.setActive(rest.get(0).getId());
            }
        }
    }

    @Override
    public void activateProvider(int id) {
        AiProvider provider = providerDao.findById(id);
        if (provider == null) {
            throw new ServiceException("配置不存在，可能已被删除");
        }
        providerDao.clearActive(provider.getKind());
        providerDao.setActive(id);
    }

    @Override
    public String testProvider(AiProvider provider) {
        if (provider.getKind() == null) {
            return "配置类型未知，无法测试";
        }
        if (isBlank(provider.getBaseUrl()) || isBlank(provider.getModel())) {
            return "接口地址与模型名都要填写才能测试";
        }
        // 测试时 Key 留空也沿用已保存的，这样只改地址不用重填 Key
        String key = provider.getApiKey();
        if (isBlank(key) && provider.getId() != null) {
            AiProvider old = providerDao.findById(provider.getId());
            if (old != null) {
                key = old.getApiKey();
            }
        }
        try {
            if (provider.getKind() == AiProvider.KIND_EMBED) {
                double[] vec = AiClient.embed(provider.getBaseUrl().trim(), key,
                        provider.getModel().trim(), "这是一段用于连通性测试的文本");
                return "向量接口连通正常，返回向量维度：" + vec.length;
            }
            String reply = AiClient.chat(provider.getBaseUrl().trim(), key,
                    provider.getModel().trim(),
                    "你是一个测试助手，只按用户要求回复，不要多说。",
                    "请只回复两个字：正常");
            return "对话接口连通正常，模型回复：" + reply;
        } catch (ServiceException e) {
            return "调用失败：" + e.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // 可用性
    // ------------------------------------------------------------------

    @Override
    public boolean isReady() {
        return unavailableReason().isEmpty();
    }

    @Override
    public String unavailableReason() {
        if (!getConfig().isOn()) {
            return "AI 功能当前处于关闭状态";
        }
        AiProvider embed = providerDao.findActive(AiProvider.KIND_EMBED);
        if (embed == null) {
            return "还没有把任何一条向量模型配置设为「使用中」";
        }
        if (isBlank(embed.getBaseUrl())) {
            return "使用中的向量模型配置「" + embed.getName() + "」缺少接口地址";
        }
        if (isBlank(embed.getModel())) {
            return "使用中的向量模型配置「" + embed.getName() + "」缺少模型名";
        }
        return "";
    }

    // ------------------------------------------------------------------
    // 检索
    // ------------------------------------------------------------------

    @Override
    public AiSearchResult search(int module, String query, User user) {
        long start = System.currentTimeMillis();
        AiLog log = new AiLog();
        log.setUserId(user == null ? 0 : user.getId());
        log.setModule(module);
        log.setQueryText(truncate(query, MAX_QUERY_LENGTH));
        // 先把各字段填上默认值：失败时不会走到成功分支，
        // 而 result_count、cost_ms 等列是 NOT NULL，传 null 会插入失败
        log.setReplyText("");
        log.setResultIds("");
        log.setResultCount(0);
        log.setCostMs(0);
        log.setStatus(1);

        try {
            if (query == null || query.trim().isEmpty()) {
                throw new ServiceException("请先描述一下你要找的东西");
            }
            String q = query.trim();
            if (q.length() > MAX_QUERY_LENGTH) {
                throw new ServiceException("描述太长了，请控制在 " + MAX_QUERY_LENGTH + " 字以内");
            }

            String reason = unavailableReason();
            if (!reason.isEmpty()) {
                throw new ServiceException("AI 功能暂时不可用：" + reason);
            }

            AiSearchResult result = doSearch(module, q);

            log.setStatus(1);
            log.setReplyText(truncate(result.getReply(), 2000));
            log.setResultCount(result.getMatches().size());
            log.setResultIds(joinIds(result.getMatches()));
            return result;
        } catch (ServiceException e) {
            log.setStatus(0);
            log.setErrorMsg(truncate(e.getMessage(), 500));
            throw e;
        } finally {
            log.setCostMs((int) (System.currentTimeMillis() - start));
            saveLogQuietly(log);
        }
    }

    /** 真正的检索流程 */
    private AiSearchResult doSearch(int module, String query) {
        AiProvider embedConfig = providerDao.findActive(AiProvider.KIND_EMBED);
        AiProvider chatConfig = providerDao.findActive(AiProvider.KIND_CHAT);

        List<Candidate> candidates = loadCandidates(module);
        if (candidates.isEmpty()) {
            return new AiSearchResult(Collections.<AiMatchResult>emptyList(),
                    "数据库里还没有可匹配的信息，暂时无法为你匹配。");
        }

        // 1. 拿到每条候选的向量（命中缓存直接用，没缓存的一次批量算好）
        Map<Integer, double[]> vectors = resolveVectors(embedConfig, candidates);

        // 2. 把用户描述也转成向量
        double[] queryVec = AiClient.embed(embedConfig.getBaseUrl(), embedConfig.getApiKey(),
                embedConfig.getModel(), query);

        // 3. 逐条算余弦相似度，过滤掉明显不相关的，再按相似度降序
        List<AiMatchResult> matches = new ArrayList<>();
        for (Candidate c : candidates) {
            double[] vec = vectors.get(c.id);
            if (vec == null) {
                continue;
            }
            double similarity = VectorUtil.cosine(queryVec, vec);
            if (similarity < MIN_SIMILARITY) {
                continue;
            }
            matches.add(toMatchResult(c, similarity));
        }
        matches.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));

        int topN = getConfig().getTopN() == null ? DEFAULT_TOP_N : getConfig().getTopN();
        if (matches.size() > topN) {
            matches = new ArrayList<>(matches.subList(0, topN));
        }

        // 4. 让对话模型基于命中结果生成一段答复（未配置对话模型时跳过）
        String reply = buildReply(chatConfig, query, matches);
        return new AiSearchResult(matches, reply);
    }

    /** 调用对话模型生成答复；失败不影响已检索到的结果 */
    private String buildReply(AiProvider chatConfig, String query, List<AiMatchResult> matches) {
        if (chatConfig == null || !chatConfig.isComplete()) {
            return "";
        }
        try {
            return AiClient.chat(chatConfig.getBaseUrl(), chatConfig.getApiKey(),
                    chatConfig.getModel(), SYSTEM_PROMPT, buildUserPrompt(query, matches));
        } catch (ServiceException e) {
            return "（对话模型调用失败，以下仅为向量检索结果：" + e.getMessage() + "）";
        }
    }

    private String buildUserPrompt(String query, List<AiMatchResult> matches) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户描述：").append(query).append("\n\n");
        if (matches.isEmpty()) {
            sb.append("向量检索没有找到相似度达标的信息。\n");
        } else {
            sb.append("向量检索命中的候选（按相似度从高到低）：\n");
            for (int i = 0; i < matches.size(); i++) {
                AiMatchResult m = matches.get(i);
                sb.append(i + 1).append(". [相似度 ").append(m.getSimilarityPercent()).append("%] ")
                        .append(m.getTitle()).append("（")
                        .append(m.getCategoryName()).append(" · ")
                        .append(m.getPlace()).append("）\n");
            }
        }
        sb.append("\n请据此回复用户。");
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // 候选加载
    // ------------------------------------------------------------------

    /** 参与匹配的候选条目 */
    private static class Candidate {
        int id;
        int entityType;
        String title;
        String categoryName;
        String place;
        String typeText;
        String badgeClass;
        String link;
        String snippet;
        /** 送去算向量的文本 */
        String embedText;
    }

    /**
     * 载入某个模块参与匹配的条目。
     * 失物招领只取仍有效的（寻找中 / 待认领），二手商品只取在售的，
     * 已找回、已售、下架的不再参与匹配。
     */
    private List<Candidate> loadCandidates(int module) {
        List<Candidate> list = new ArrayList<>();
        if (module == MODULE_LOST_FOUND) {
            // 复用现有方法：type=1 取失物、type=2 取拾物，内部已按有效状态过滤
            for (int type = 1; type <= 2; type++) {
                for (LostFound lf : lostFoundDao.findCandidates(type)) {
                    list.add(toCandidate(lf));
                }
            }
        } else {
            // status=1 只取在售商品
            for (Goods goods : goodsDao.findByPage(null, null, 1, null, 0, MAX_CANDIDATES)) {
                list.add(toCandidate(goods));
            }
        }
        if (list.size() > MAX_CANDIDATES) {
            list = new ArrayList<>(list.subList(0, MAX_CANDIDATES));
        }
        return list;
    }

    private Candidate toCandidate(LostFound lf) {
        Candidate c = new Candidate();
        c.id = lf.getId();
        c.entityType = MODULE_LOST_FOUND;
        c.title = lf.getName();
        c.categoryName = nullToEmpty(lf.getCategoryName());
        c.place = nullToEmpty(lf.getPlace());
        boolean isLost = lf.getType() != null && lf.getType() == 1;
        c.typeText = isLost ? "失物" : "拾物";
        c.badgeClass = isLost ? "badge-danger" : "badge-success";
        c.link = "/lostfound/detail?id=" + lf.getId();
        c.snippet = truncate(lf.getFeature(), SNIPPET_LENGTH);
        c.embedText = join(c.title, c.categoryName, c.place, lf.getFeature(),
                isLost ? "失物 丢失 寻回" : "拾物 招领 认领");
        return c;
    }

    private Candidate toCandidate(Goods goods) {
        Candidate c = new Candidate();
        c.id = goods.getId();
        c.entityType = MODULE_GOODS;
        c.title = goods.getTitle();
        c.categoryName = nullToEmpty(goods.getCategoryName());
        c.place = nullToEmpty(goods.getTradePlace());
        c.typeText = goods.getStatusText();
        c.badgeClass = "badge-success";
        c.link = "/goods/detail?id=" + goods.getId();
        String price = goods.getPrice() == null ? "" : ("￥" + goods.getPrice());
        c.snippet = truncate(price + " " + nullToEmpty(goods.getQuality()) + " "
                + nullToEmpty(goods.getDescription()), SNIPPET_LENGTH);
        c.embedText = join(c.title, c.categoryName, c.place, goods.getQuality(),
                goods.getDescription(), "二手 出售 闲置");
        return c;
    }

    // ------------------------------------------------------------------
    // 向量缓存
    // ------------------------------------------------------------------

    /**
     * 取到每条候选的向量。
     * 缓存命中（模型名和文本指纹都没变）就直接用，否则批量调用向量接口并把结果写回缓存。
     */
    private Map<Integer, double[]> resolveVectors(AiProvider embedConfig, List<Candidate> candidates) {
        Map<Integer, double[]> vectors = new HashMap<>();
        List<Candidate> missing = new ArrayList<>();
        String model = embedConfig.getModel();

        for (Candidate c : candidates) {
            if (isBlank(c.embedText)) {
                continue;
            }
            c.embedText = c.embedText.trim();
            String hash = md5(c.embedText);
            AiEmbedding cached = embeddingDao.findByEntity(c.entityType, c.id);
            if (cached != null && model.equals(cached.getModel()) && hash.equals(cached.getTextHash())) {
                double[] vec = VectorUtil.fromJson(cached.getVec());
                if (vec.length > 0) {
                    vectors.put(c.id, vec);
                    continue;
                }
            }
            missing.add(c);
        }

        for (int from = 0; from < missing.size(); from += EMBED_BATCH_SIZE) {
            int to = Math.min(from + EMBED_BATCH_SIZE, missing.size());
            List<Candidate> batch = missing.subList(from, to);
            List<String> texts = new ArrayList<>();
            for (Candidate c : batch) {
                texts.add(c.embedText);
            }
            List<double[]> vecs = AiClient.embedBatch(embedConfig.getBaseUrl(),
                    embedConfig.getApiKey(), model, texts);
            for (int i = 0; i < batch.size(); i++) {
                Candidate c = batch.get(i);
                double[] vec = vecs.get(i);
                vectors.put(c.id, vec);
                embeddingDao.save(new AiEmbedding(c.entityType, c.id, model,
                        vec.length, VectorUtil.toJson(vec), md5(c.embedText)));
            }
        }
        return vectors;
    }

    @Override
    public int rebuildIndex(int module) {
        AiProvider embedConfig = providerDao.findActive(AiProvider.KIND_EMBED);
        if (embedConfig == null || !embedConfig.isComplete()) {
            throw new ServiceException("请先添加并启用一条填写完整的向量模型配置，再重建索引");
        }
        embeddingDao.deleteByType(module);
        List<Candidate> candidates = loadCandidates(module);
        if (candidates.isEmpty()) {
            return 0;
        }
        return resolveVectors(embedConfig, candidates).size();
    }

    @Override
    public int cachedCount(int module) {
        return embeddingDao.countByType(module);
    }

    @Override
    public int recordCount(int module) {
        return loadCandidates(module).size();
    }

    // ------------------------------------------------------------------
    // 日志
    // ------------------------------------------------------------------

    @Override
    public PageBean<AiLog> findLogs(Integer module, String keyword, int pageNum, int pageSize) {
        int total = logDao.count(module, keyword);
        PageBean<AiLog> page = new PageBean<>(pageNum, pageSize, total, Collections.<AiLog>emptyList());
        List<AiLog> list = logDao.findByPage(module, keyword,
                (page.getPageNum() - 1) * pageSize, pageSize);
        page.setList(list);
        return page;
    }

    /**
     * 写日志失败不应该影响主流程，因此这里不向外抛；
     * 但要打印出来，否则日志写不进去时完全无声，排查会很困难。
     */
    private void saveLogQuietly(AiLog log) {
        try {
            logDao.insert(log);
        } catch (RuntimeException e) {
            System.err.println("[AI] 写入对话日志失败：" + e.getMessage());
        }
    }

    private String joinIds(List<AiMatchResult> matches) {
        StringBuilder sb = new StringBuilder();
        for (AiMatchResult m : matches) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(m.getEntityType()).append(":").append(m.getEntityId());
        }
        return truncate(sb.toString(), 500);
    }

    private AiMatchResult toMatchResult(Candidate c, double similarity) {
        AiMatchResult m = new AiMatchResult();
        m.setEntityType(c.entityType);
        m.setEntityId(c.id);
        m.setTitle(c.title);
        m.setCategoryName(c.categoryName);
        m.setPlace(c.place);
        m.setTypeText(c.typeText);
        m.setBadgeClass(c.badgeClass);
        m.setLink(c.link);
        m.setSnippet(c.snippet);
        m.setSimilarity(similarity);
        return m;
    }

    // ------------------------------------------------------------------
    // 小工具
    // ------------------------------------------------------------------

    private static String md5(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("计算 MD5 失败", e);
        }
    }

    /** 用空格把非空片段拼起来，作为送去向量化的文本 */
    private static String join(String... parts) {
        StringBuilder sb = new StringBuilder();
        for (String p : parts) {
            if (p != null && !p.trim().isEmpty()) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append(p.trim());
            }
        }
        return sb.toString();
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return "";
        }
        String t = s.trim();
        return t.length() <= max ? t : t.substring(0, max) + "...";
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private static String nullToEmpty(String s) {
        return s == null ? "" : s;
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }
}
