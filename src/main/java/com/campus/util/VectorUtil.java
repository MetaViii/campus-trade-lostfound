package com.campus.util;

import com.google.gson.Gson;

/**
 * 向量工具类。
 *
 * <p>负责向量的 JSON 序列化/反序列化，以及余弦相似度计算。
 * 余弦相似度衡量两个向量方向上的接近程度，取值 -1~1，
 * 越接近 1 表示两段文本语义越相似，正好用来做「按描述找相似条目」。</p>
 */
public class VectorUtil {

    private static final Gson GSON = new Gson();

    private VectorUtil() {
    }

    /** 向量转 JSON 数组字符串，便于存进数据库的一个字段 */
    public static String toJson(double[] vec) {
        return GSON.toJson(vec);
    }

    /** JSON 数组字符串还原为向量 */
    public static double[] fromJson(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new double[0];
        }
        return GSON.fromJson(json, double[].class);
    }

    /**
     * 计算两个向量的余弦相似度。
     * 维度不一致或存在零向量时返回 0，避免除零。
     */
    public static double cosine(double[] a, double[] b) {
        if (a == null || b == null || a.length == 0 || a.length != b.length) {
            return 0.0;
        }
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }
        return dot / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
