package com.timeleafing.qqbot.common.util;

import com.timeleafing.qqbot.config.properties.MinioProperties;
import com.timeleafing.qqbot.exception.*;
import io.minio.*;
import io.minio.http.Method;
import io.minio.messages.Item;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class MinioUtils {

    private final MinioClient minioClient;

    private final MinioProperties props;

    private final Map<String, MinioBucket> bucketCache = new ConcurrentHashMap<>();

    /**
     * 异步线程池（可配置为 Spring Bean 注入替换）
     */
    private final ExecutorService fileExecutor = new ThreadPoolExecutor(
            2,
            Math.max(4, Runtime.getRuntime().availableProcessors()),
            60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(200),
            new ThreadFactory() {
                private final AtomicInteger idx = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "minio-file-" + idx.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            },
            new ThreadPoolExecutor.AbortPolicy()
    );

    @PreDestroy
    public void shutdownExecutor() {
        log.info("Shutting down MinioUtils fileExecutor...");
        fileExecutor.shutdown();
        try {
            if (!fileExecutor.awaitTermination(30, TimeUnit.SECONDS)) {
                fileExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            fileExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public MinioBucket bucket(String bucketName) {
        validateBucketName(bucketName);

        return bucketCache.computeIfAbsent(
                bucketName,
                name -> new MinioBucket(name, props.getPresignedUrlExpiry(), props.getPartSize())
        );
    }

    public boolean bucketExists(String bucketName) {
        validateBucketName(bucketName);

        return execute(
                () -> minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(bucketName)
                                .build()
                ),
                "检查桶是否存在失败: %s".formatted(bucketName)
        );
    }

    public String extractObjectName(String fileUrl, String bucketName) {
        if (fileUrl == null || bucketName == null) {
            throw new IllegalArgumentException("fileUrl 或 bucketName 不能为空");
        }
        try {
            String prefixWithGateway = "%s/%s/".formatted(props.getPublicBaseUrl(), bucketName);
            if (!fileUrl.startsWith(prefixWithGateway)) {
                throw new MinioFileUrlExtractException("fileUrl 不符合预期格式: %s".formatted(fileUrl));
            }
            return java.net.URLDecoder.decode(fileUrl.substring(prefixWithGateway.length()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MinioFileUrlExtractException("解析 MinIO 对象路径失败: %s".formatted(fileUrl), e);
        }
    }

    private <T> T execute(MinioAction<T> action, String errorMessage) {
        try {
            return action.run();
        } catch (Exception e) {
            throw new MinioOperationException(errorMessage, e);
        }
    }

    private void validateBucketName(String bucketName) {
        if (!StringUtils.hasText(bucketName)) {
            throw new IllegalArgumentException("桶名不能为空");
        }
    }

    @FunctionalInterface
    private interface MinioAction<T> {
        T run() throws Exception;
    }

    public static class MinioOperationException extends RuntimeException {
        public MinioOperationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    @AllArgsConstructor
    @Getter
    public class MinioBucket {

        private final String bucketName;

        private final int presignedUrlExpiry;

        private final int partSize;


        // ---------------------------
        // Upload
        // ---------------------------
        public void uploadFile(String objectName, InputStream inputStream, long size, String contentType) {
            validateObjectName(objectName);

            if (inputStream == null) {
                throw new IllegalArgumentException("输入流不能为空");
            }
            if (size < 0) {
                throw new IllegalArgumentException("size 必须 >= 0");
            }
            execute(() -> {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName).stream(inputStream, size, partSize)
                                .contentType(StringUtils.hasText(contentType) ? contentType : "application/octet-stream")
                                .build()
                );
                log.info("文件上传成功: {}/{}", bucketName, objectName);
                return null;
            }, "上传文件失败: %s".formatted(objectName));
        }

        public void uploadFile(String objectName, byte[] content, String contentType) {
            if (content == null) {
                throw new IllegalArgumentException("content 不能为空");
            }
            try (InputStream is = new ByteArrayInputStream(content)) {
                uploadFile(objectName, is, content.length, contentType);
            } catch (IOException e) {
                throw new MinioOperationException("byte[] 上传失败: " + objectName, e);
            }
        }

        public void uploadFile(String objectName, File file, String contentType) {
            try (InputStream is = new FileInputStream(file)) {
                uploadFile(objectName, is, Files.size(file.toPath()), contentType);
            } catch (IOException e) {
                throw new MinioOperationException("File 上传失败: " + objectName, e);
            }
        }

        // 异步上传
        public Future<Void> uploadFileAsync(String objectName, byte[] content, String contentType) {
            return fileExecutor.submit(() -> {
                uploadFile(objectName, content, contentType);
                return null;
            });
        }

        public CompletableFuture<Void> uploadFileAsyncCF(String objectName, byte[] content, String contentType) {
            return CompletableFuture.runAsync(() -> uploadFile(objectName, content, contentType), fileExecutor);
        }

        // ---------------------------
        // Download
        // ---------------------------
        public InputStream downloadAsStream(String objectName) {
            validateObjectName(objectName);

            return execute(() -> minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .build()), "下载文件失败: " + objectName);
        }

        public void downloadFile(String objectName, Consumer<InputStream> consumer) {
            validateObjectName(objectName);

            execute(() -> {
                try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build())) {
                    consumer.accept(stream);
                }
                return null;
            }, "下载文件失败: %s".formatted(objectName));
        }

        public byte[] downloadAsBytes(String objectName) {
            validateObjectName(objectName);

            try (InputStream stream = downloadAsStream(objectName)) {
                return stream.readAllBytes();
            } catch (IOException e) {
                throw new MinioOperationException("下载为 bytes 失败: " + objectName, e);
            }
        }

        public void downloadToFile(String objectName, File dest) {
            validateObjectName(objectName);

            try (InputStream stream = downloadAsStream(objectName);
                 OutputStream os = new FileOutputStream(dest)) {
                stream.transferTo(os);
            } catch (IOException e) {
                throw new MinioOperationException("下载到本地失败: " + objectName, e);
            }
        }

        public Future<Void> downloadFileAsync(String objectName, Consumer<InputStream> consumer) {
            return fileExecutor.submit(() -> {
                downloadFile(objectName, consumer);
                return null;
            });
        }

        public CompletableFuture<Void> downloadFileAsyncCF(String objectName, Consumer<InputStream> consumer) {
            return CompletableFuture.runAsync(() -> downloadFile(objectName, consumer), fileExecutor);
        }

        // ---------------------------
        // Delete / Exists
        // ---------------------------
        public void deleteFile(String objectName) {
            validateObjectName(objectName);

            execute(() -> {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );

                return null;
            }, "删除文件失败: %s".formatted(objectName));
        }

        public boolean objectExists(String objectName) {
            validateObjectName(objectName);

            return execute(() -> {
                minioClient.statObject(
                        StatObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );

                return true;
            }, "对象不存在或检查失败: %s".formatted(objectName));
        }

        // ---------------------------
        // Copy / Move / List
        // ---------------------------
        public void copy(String sourceObjectName, String targetObjectName) {
            validateObjectName(sourceObjectName);
            validateObjectName(targetObjectName);

            execute(() -> {
                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(bucketName)
                                .object(targetObjectName)
                                .source(
                                        CopySource.builder()
                                                .bucket(bucketName)
                                                .object(sourceObjectName)
                                                .build()
                                )
                                .build()
                );
                return null;
            }, "拷贝对象失败: %s -> %s".formatted(sourceObjectName, targetObjectName));
        }

        public void move(String sourceObjectName, String targetObjectName) {
            copy(sourceObjectName, targetObjectName);

            deleteFile(sourceObjectName);
        }

        public List<String> listObjects(String prefix) {
            return execute(() -> {
                Iterable<Result<Item>> results = minioClient.listObjects(
                        ListObjectsArgs.builder()
                                .bucket(bucketName)
                                .prefix(prefix)
                                .build()
                );
                List<String> list = new ArrayList<>();

                for (Result<Item> r : results) {
                    list.add(r.get().objectName());
                }
                return list;
            }, "列举对象失败: %s".formatted(prefix));
        }

        // ---------------------------
        // Public URL / Presigned
        // ---------------------------
        public String getPublicFileUrl(String objectName) {
            validateObjectName(objectName);

            return "%s/%s/%s".formatted(props.getPublicBaseUrl(), bucketName, FileUtils.encodeFilePath(objectName));
        }

        public String getPresignedGetUrl(String objectName) {
            return getPresignedUrl(objectName, Method.GET, presignedUrlExpiry);
        }

        public String getPresignedPutUrl(String objectName) {
            return getPresignedUrl(objectName, Method.PUT, presignedUrlExpiry);
        }

        private String getPresignedUrl(String objectName, Method method, long expirySeconds) {
            validateObjectName(objectName);

            return execute(() -> minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(method)
                            .expiry((int) expirySeconds)
                            .build()
            ), "生成预签名 URL 失败: " + objectName);
        }

        private void validateObjectName(String objectName) {
            if (!StringUtils.hasText(objectName)) throw new IllegalArgumentException("对象名不能为空");
        }
    }
}
