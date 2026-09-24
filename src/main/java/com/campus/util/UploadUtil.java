package com.campus.util;

import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * 文件上传工具类。
 * 将表单中的图片保存到指定目录，并返回随机生成的新文件名。
 */
public class UploadUtil {

    /**
     * 保存上传的图片。
     *
     * @param part      表单文件部件
     * @param uploadDir 服务器端保存目录的绝对路径
     * @return 保存后的文件名；未选择文件时返回 null
     */
    public static String save(Part part, String uploadDir) throws IOException {
        if (part == null) {
            return null;
        }
        String original = part.getSubmittedFileName();
        if (original == null || original.trim().isEmpty() || part.getSize() <= 0) {
            return null;
        }
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot);
        }
        String fileName = System.currentTimeMillis() + "_" + new Random().nextInt(10000) + ext;
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        part.write(new File(dir, fileName).getAbsolutePath());
        return fileName;
    }
}
