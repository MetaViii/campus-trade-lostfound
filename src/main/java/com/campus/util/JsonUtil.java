package com.campus.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * JSON 工具类，对 Gson 做一层薄封装。
 *
 * <p>集中提供对象与 JSON 互转、按路径取字段等操作，供 AI 功能解析/构造请求体使用。
 * 解析失败时统一抛出 {@link ServiceException}，交给上层显示友好提示。</p>
 */
public class JsonUtil {

    private static final Gson GSON = new Gson();

    private JsonUtil() {
    }

    /** 对象转 JSON 字符串 */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /** JSON 字符串转指定类型的对象 */
    public static <T> T toObject(String json, Class<T> type) {
        try {
            return GSON.fromJson(json, type);
        } catch (RuntimeException e) {
            throw new ServiceException("解析 AI 返回内容失败：" + e.getMessage());
        }
    }

    /** 解析为 JsonObject，便于按键取字段 */
    public static JsonObject parse(String json) {
        try {
            return JsonParser.parseString(json).getAsJsonObject();
        } catch (RuntimeException e) {
            throw new ServiceException("AI 接口返回的不是合法 JSON：" + brief(json));
        }
    }

    /** 安全取字符串字段，缺失或为 null 时返回空串 */
    public static String getString(JsonObject obj, String key) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return "";
        }
        return obj.get(key).getAsString();
    }

    /** 安全取整型字段，缺失时返回默认值 */
    public static int getInt(JsonObject obj, String key, int defaultValue) {
        if (obj == null || !obj.has(key) || obj.get(key).isJsonNull()) {
            return defaultValue;
        }
        try {
            return obj.get(key).getAsInt();
        } catch (RuntimeException e) {
            return defaultValue;
        }
    }

    private static String brief(String s) {
        if (s == null) {
            return "（空）";
        }
        return s.length() <= 200 ? s : s.substring(0, 200) + "...";
    }
}
