package com.campus.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 大模型接口客户端。
 *
 * <p>按 OpenAI 兼容格式调用两类接口，两类可以指向不同服务商：</p>
 * <ul>
 *   <li>向量接口 {@code POST {baseUrl}/embeddings} —— 把一段文本转成向量</li>
 *   <li>对话接口 {@code POST {baseUrl}/chat/completions} —— 生成一段文字回复</li>
 * </ul>
 *
 * <p>只用 JDK 自带的 HttpURLConnection，不引入 HTTP 客户端框架。</p>
 */
public class AiClient {

    /** 建立连接的超时时间 */
    private static final int CONNECT_TIMEOUT_MS = 10_000;
    /** 读取响应的超时时间，大模型生成较慢，给足时间 */
    private static final int READ_TIMEOUT_MS = 90_000;
    /** 出错时最多把响应体的前多少字符带进异常信息 */
    private static final int ERROR_BODY_LIMIT = 300;

    private AiClient() {
    }

    /**
     * 把单段文本转成向量。
     *
     * @return 向量数组；调用失败抛出 {@link ServiceException}
     */
    public static double[] embed(String baseUrl, String apiKey, String model, String text) {
        List<double[]> list = embedBatch(baseUrl, apiKey, model, Collections.singletonList(text));
        if (list.isEmpty()) {
            throw new ServiceException("向量接口没有返回任何向量");
        }
        return list.get(0);
    }

    /**
     * 批量把多段文本转成向量，一次请求算完，比逐条调用快得多。
     *
     * @return 与入参顺序一致的向量列表
     */
    public static List<double[]> embedBatch(String baseUrl, String apiKey, String model,
                                            List<String> texts) {
        if (texts == null || texts.isEmpty()) {
            return Collections.emptyList();
        }
        requireConfigured(baseUrl, model, "向量模型");

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("input", texts);

        String resp = postJson(joinUrl(baseUrl, "/embeddings"), apiKey, JsonUtil.toJson(body));

        JsonObject root = JsonUtil.parse(resp);
        JsonArray data = root.has("data") ? root.getAsJsonArray("data") : null;
        if (data == null || data.size() == 0) {
            throw new ServiceException("向量接口返回内容为空，请确认模型名是否支持 embeddings");
        }
        if (data.size() != texts.size()) {
            throw new ServiceException("向量接口返回条数(" + data.size()
                    + ")与请求条数(" + texts.size() + ")不一致");
        }

        // 响应里带 index，按 index 放回对应位置，不依赖返回顺序
        double[][] ordered = new double[texts.size()][];
        for (int i = 0; i < data.size(); i++) {
            JsonObject item = data.get(i).getAsJsonObject();
            JsonElement emb = item.get("embedding");
            if (emb == null || !emb.isJsonArray()) {
                throw new ServiceException("向量接口返回格式不符合预期（缺少 embedding 字段）");
            }
            JsonArray arr = emb.getAsJsonArray();
            double[] vec = new double[arr.size()];
            for (int j = 0; j < arr.size(); j++) {
                vec[j] = arr.get(j).getAsDouble();
            }
            int idx = item.has("index") ? item.get("index").getAsInt() : i;
            if (idx < 0 || idx >= texts.size()) {
                idx = i;
            }
            ordered[idx] = vec;
        }

        List<double[]> result = new ArrayList<>(texts.size());
        for (double[] vec : ordered) {
            if (vec == null || vec.length == 0) {
                throw new ServiceException("向量接口返回了空向量");
            }
            result.add(vec);
        }
        return result;
    }

    /**
     * 调用对话模型，返回模型生成的文本。
     *
     * @return 模型回复的正文；调用失败抛出 {@link ServiceException}
     */
    public static String chat(String baseUrl, String apiKey, String model,
                              String systemPrompt, String userPrompt) {
        requireConfigured(baseUrl, model, "对话模型");

        Map<String, Object> sysMsg = new HashMap<>();
        sysMsg.put("role", "system");
        sysMsg.put("content", systemPrompt);
        Map<String, Object> userMsg = new HashMap<>();
        userMsg.put("role", "user");
        userMsg.put("content", userPrompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", new Object[]{sysMsg, userMsg});
        body.put("temperature", 0.3);
        body.put("stream", false);

        String resp = postJson(joinUrl(baseUrl, "/chat/completions"), apiKey, JsonUtil.toJson(body));

        JsonObject root = JsonUtil.parse(resp);
        JsonArray choices = root.has("choices") ? root.getAsJsonArray("choices") : null;
        if (choices == null || choices.size() == 0) {
            throw new ServiceException("对话接口返回内容为空");
        }
        JsonElement content = choices.get(0).getAsJsonObject()
                .getAsJsonObject("message").get("content");
        if (content == null || content.isJsonNull()) {
            throw new ServiceException("对话接口未返回正文");
        }
        return content.getAsString().trim();
    }

    /**
     * 拼接接口地址。
     * 去掉 baseUrl 末尾多余的斜杠；若用户直接把完整端点填进了 baseUrl 就原样使用，避免重复拼接。
     */
    private static String joinUrl(String baseUrl, String path) {
        String base = baseUrl == null ? "" : baseUrl.trim();
        while (base.endsWith("/")) {
            base = base.substring(0, base.length() - 1);
        }
        if (base.endsWith(path)) {
            return base;
        }
        return base + path;
    }

    /** 发送 JSON 请求，返回响应体字符串 */
    private static String postJson(String url, String apiKey, String jsonBody) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
            conn.setReadTimeout(READ_TIMEOUT_MS);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            if (apiKey != null && !apiKey.trim().isEmpty()) {
                conn.setRequestProperty("Authorization", "Bearer " + apiKey.trim());
            }

            try (OutputStream out = conn.getOutputStream()) {
                out.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            int code = conn.getResponseCode();
            if (code < 200 || code >= 300) {
                String detail = readAll(conn.getErrorStream());
                throw new ServiceException("AI 接口返回 " + code + "：" + brief(detail));
            }
            return readAll(conn.getInputStream());
        } catch (ServiceException e) {
            throw e;
        } catch (IOException e) {
            throw new ServiceException("调用 AI 接口失败（网络或地址不通）：" + e.getMessage());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static String readAll(InputStream in) throws IOException {
        if (in == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /** 截断过长的错误信息，避免把整个响应体塞进页面 */
    private static String brief(String s) {
        if (s == null || s.isEmpty()) {
            return "（无响应内容）";
        }
        return s.length() <= ERROR_BODY_LIMIT ? s : s.substring(0, ERROR_BODY_LIMIT) + "...";
    }

    private static void requireConfigured(String baseUrl, String model, String what) {
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new ServiceException(what + "的接口地址未配置，请联系管理员在后台填写");
        }
        if (model == null || model.trim().isEmpty()) {
            throw new ServiceException(what + "的模型名未配置，请联系管理员在后台填写");
        }
    }
}
