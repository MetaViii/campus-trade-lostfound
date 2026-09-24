package com.campus.util;

import com.campus.entity.LostFound;

/**
 * 失物招领「智能匹配」算法工具类。
 *
 * <p>失物与拾物互为匹配对象。本类用简单直观的规则，从
 * 「分类、名称、地点、时间」四个方面给两条信息打一个相似度分（0~100），
 * 分数越高越可能是同一件物品。</p>
 *
 * <p>算法只用到 if 判断、字符串 contains / substring 和基本数值运算，
 * 不依赖任何第三方库，便于理解与课堂讲解。</p>
 */
public class MatchUtil {

    /** 一天的毫秒数，用于计算两个时间相差几天 */
    private static final long ONE_DAY = 24L * 60 * 60 * 1000;

    /**
     * 计算源信息 a 与候选信息 b 的相似度得分（0~100）。
     */
    public static int score(LostFound a, LostFound b) {
        int score = 0;

        // 1. 物品分类相同，加 40 分
        if (a.getCategoryId() != null && a.getCategoryId().equals(b.getCategoryId())) {
            score += 40;
        }

        // 2. 名称关键词匹配，最多加 30 分
        score += nameScore(a, b);

        // 3. 地点相近：一个地点包含另一个，加 15 分
        String pa = a.getPlace();
        String pb = b.getPlace();
        if (notEmpty(pa) && notEmpty(pb) && (pa.contains(pb) || pb.contains(pa))) {
            score += 15;
        }

        // 4. 时间相近：丢失与拾到时间相差不超过 7 天，加 15 分
        if (a.getHappenTime() != null && b.getHappenTime() != null) {
            long days = Math.abs(a.getHappenTime().getTime() - b.getHappenTime().getTime()) / ONE_DAY;
            if (days <= 7) {
                score += 15;
            }
        }
        return score;
    }

    /**
     * 名称关键词命中得分（最多 30 分）。
     * 思路：中文词语常由两个字组成，所以把 a 的名称里每相邻两个字当作一个关键词，
     * 看 b 的「名称 + 特征」中出现了几个，每命中一个加 10 分。
     */
    private static int nameScore(LostFound a, LostFound b) {
        String name = a.getName() == null ? "" : a.getName();
        String text = (b.getName() == null ? "" : b.getName())
                + (b.getFeature() == null ? "" : b.getFeature());
        int hit = 0;
        for (int i = 0; i + 1 < name.length(); i++) {
            String word = name.substring(i, i + 2);   // 相邻两个字组成一个关键词
            if (text.contains(word)) {
                hit++;
            }
        }
        int s = hit * 10;
        return s > 30 ? 30 : s;                        // 最多 30 分
    }

    /**
     * 生成匹配理由文字，如「同类目 名称相近 时间相近」，方便在页面上展示。
     */
    public static String reason(LostFound a, LostFound b) {
        String reason = "";
        if (a.getCategoryId() != null && a.getCategoryId().equals(b.getCategoryId())) {
            reason += "同类目 ";
        }
        if (nameScore(a, b) > 0) {
            reason += "名称相近 ";
        }
        String pa = a.getPlace();
        String pb = b.getPlace();
        if (notEmpty(pa) && notEmpty(pb) && (pa.contains(pb) || pb.contains(pa))) {
            reason += "地点相近 ";
        }
        if (a.getHappenTime() != null && b.getHappenTime() != null) {
            long days = Math.abs(a.getHappenTime().getTime() - b.getHappenTime().getTime()) / ONE_DAY;
            if (days <= 7) {
                reason += "时间相近 ";
            }
        }
        return reason.isEmpty() ? "可能相关" : reason.trim();
    }

    /** 判断字符串是否非空 */
    private static boolean notEmpty(String s) {
        return s != null && !s.trim().isEmpty();
    }
}
