package com.campus.entity;

/**
 * 智能匹配结果。
 * 封装一条候选失物/拾物信息、它与源信息的相似度得分以及匹配理由，
 * 供失物招领详情页的「智能匹配」区块展示。
 */
public class MatchResult {

    /** 候选信息（与源信息类型相反：源为失物则此为拾物，反之亦然） */
    private LostFound item;
    /** 相似度得分 0~100 */
    private int score;
    /** 人类可读的匹配理由，如「同类目 · 关键词高度重合 · 地点吻合」 */
    private String reason;

    public MatchResult() {
    }

    public MatchResult(LostFound item, int score, String reason) {
        this.item = item;
        this.score = score;
        this.reason = reason;
    }

    public LostFound getItem() {
        return item;
    }

    public void setItem(LostFound item) {
        this.item = item;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
