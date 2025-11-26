package com.timeleafing.qqbot.common.file;

import org.jetbrains.annotations.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class InputStreamMultipartFile implements MultipartFile {

    private final String name;

    private final String originalFilename;

    private final String contentType;

    private final byte[] content;


    public InputStreamMultipartFile(
            String name,
            String originalFilename,
            String contentType,
            InputStream inputStream
    ) throws IOException {
        this.name = name;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.content = inputStream.readAllBytes();
    }

    @NotNull
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @NotNull
    @Override
    public byte[] getBytes() {
        return content;
    }

    @NotNull
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(@NotNull File dest) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            fos.write(content);
        }
    }
}
