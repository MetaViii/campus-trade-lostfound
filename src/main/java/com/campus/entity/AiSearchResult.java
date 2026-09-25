package com.campus.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 检索的一次完整结果。
 * 除了命中的条目列表，还带上对话模型生成的文字答复，
 * 两者一起返回给页面，也一起写进 ai_log。
 */
public class AiSearchResult {

    /** 命中的条目，按相似度从高到低 */
    private List<AiMatchResult> matches = new ArrayList<>();
    /** 对话模型生成的答复；未配置对话模型时为空串 */
    private String reply = "";

    public AiSearchResult() {
    }

    public AiSearchResult(List<AiMatchResult> matches, String reply) {
        this.matches = matches == null ? new ArrayList<>() : matches;
        this.reply = reply == null ? "" : reply;
    }

    public boolean hasReply() {
        return reply != null && !reply.trim().isEmpty();
    }

    public List<AiMatchResult> getMatches() {
        return matches;
    }

    public void setMatches(List<AiMatchResult> matches) {
        this.matches = matches;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}
