package com.timeleafing.qqbot.common.util;

import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public final class FileUtils {

    public static String generateFileObjectName(String originalFilename) {
        String ext = "";

        int dotIndex = originalFilename.lastIndexOf(".");

        if (dotIndex >= 0) {
            ext = originalFilename.substring(dotIndex);
        }
        return String.format("%tY%<tm%<td_%s%s", System.currentTimeMillis(), UUID.randomUUID(), ext);
    }

    public static String generateFileObjectName(String originalFilename, String path) {
        // 获取扩展名
        String ext = "";

        int dotIndex = originalFilename.lastIndexOf(".");
        
        if (dotIndex >= 0) {
            ext = originalFilename.substring(dotIndex);
        }
        LocalDate date = LocalDate.now();

        return "%s/%s/%s".formatted(path, "%04d/%02d/%02d".formatted(date.getYear(), date.getMonthValue(), date.getDayOfMonth()), UUID.randomUUID().toString().replace("-", "") + ext);
    }

    public static boolean checkContentType(String contentType, Set<String> allowedContentTypes) {
        // 判断 contentType 是否为空
        if (!StringUtils.hasText(contentType)) {
            throw new IllegalArgumentException("无法识别文件类型");
        }
        return allowedContentTypes.contains(contentType);
    }

}
