package com.timeleafing.qqbot.common.util;

import java.util.UUID;

public final class FileUtils {

    public static String generateFileName(String originalFilename) {
        String ext = "";

        int dotIndex = originalFilename.lastIndexOf(".");

        if (dotIndex >= 0) {
            ext = originalFilename.substring(dotIndex);
        }
        return String.format("%tY%<tm%<td_%s%s", System.currentTimeMillis(), UUID.randomUUID(), ext);
    }

}
