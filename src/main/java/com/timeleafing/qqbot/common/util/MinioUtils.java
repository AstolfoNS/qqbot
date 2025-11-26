package com.timeleafing.qqbot.common.util;

import com.timeleafing.qqbot.config.properties.MinioProperties;
import com.timeleafing.qqbot.common.file.InputStreamMultipartFile;
import com.timeleafing.qqbot.exception.InputStreamMultipartFileException;
import com.timeleafing.qqbot.exception.MinioFileUrlExtractException;
import io.minio.*;
import io.minio.http.Method;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@RequiredArgsConstructor
@Component
public class MinioUtils {

    private final MinioClient minioClient;

    private final OkHttpClient okHttpClient;

    private final MinioProperties minioProperties;

    private final Map<String, MinioBucket> bucketCache = new ConcurrentHashMap<>();


    public MinioBucket bucket(String bucketName) {
        validateBucketName(bucketName);

        return bucketCache.computeIfAbsent(bucketName, name -> new MinioBucket(name, minioProperties.getPresignedUrlExpiry(), minioProperties.getPartSize()));
    }

    /** 检查桶是否存在 */
    public boolean bucketExists(String bucketName) {
        validateBucketName(bucketName);

        return execute(() -> minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build()), "检查桶是否存在失败: %s".formatted(bucketName));
    }

    /** 解析出 minioFileUrl 中的 objectName */
    public String extractObjectName(String fileUrl, String bucketName) {
        // fileUrl 参数校验
        if (fileUrl == null || bucketName == null) {
            throw new IllegalArgumentException("fileUrl 或 bucketName 不能为空");
        }
        try {
            String prefixWithGateway = "%s/%s/".formatted(minioProperties.getPublicBaseUrl(), bucketName);

            if (!fileUrl.startsWith(prefixWithGateway)) {
                throw new IllegalArgumentException("fileUrl 不符合预期格式: %s".formatted(fileUrl));
            }
            return URLDecoder.decode(fileUrl.substring(prefixWithGateway.length()), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new MinioFileUrlExtractException("解析 MinIO 对象路径失败: %s".formatted(fileUrl), e);
        }
    }

    public MultipartFile downloadFileAsMultipart(String fileUrl, String fileName) {
        // fileURl 参数校验
        if (!StringUtils.hasText(fileUrl)) {
            throw new IllegalArgumentException("文件 URL 不能为空");
        }
        // 构建请求
        Request request = new Request
                .Builder()
                .url(fileUrl)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("下载文件失败，HTTP状态码: " + response.code());
            }
            ResponseBody body = response.body();

            if (body == null) {
                throw new RuntimeException("下载文件失败，响应体为空");
            }
            String contentType = body.contentType() != null ? Objects.requireNonNull(body.contentType()).toString() : "application/octet-stream";

            byte[] bytes = body.bytes();

            return new InputStreamMultipartFile(fileName, fileName, contentType, new ByteArrayInputStream(bytes));
        } catch (Exception e) {
            throw new InputStreamMultipartFileException("下载文件到 MultipartFile 失败: %s".formatted(fileUrl), e);
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


        /** 上传文件 */
        public void uploadFile(
                String objectName,
                InputStream inputStream,
                long size,
                String contentType
        ) {
            validateObjectName(objectName);

            if (inputStream == null) {
                throw new IllegalArgumentException("输入流不能为空");
            }
            execute(() -> {
                minioClient.putObject(
                        PutObjectArgs
                                .builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .stream(inputStream, size, partSize)
                                .contentType(StringUtils.hasText(contentType) ? contentType : "application/octet-stream")
                                .build()
                );
                log.info("文件上传成功: {}/{}", bucketName, objectName);

                return null;
            }, "上传文件失败: %s".formatted(objectName));
        }

        /** 下载文件 */
        public void downloadFile(String objectName, Consumer<InputStream> consumer) {
            validateObjectName(objectName);

            execute(() -> {
                try (InputStream stream = minioClient.getObject(
                        GetObjectArgs
                                .builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                )) {
                    consumer.accept(stream);

                    log.info("文件下载成功: {}/{}", bucketName, objectName);
                }
                return null;
            }, "下载文件失败: %s".formatted(objectName));
        }

        /** 删除文件 */
        public void deleteFile(String objectName) {
            validateObjectName(objectName);

            execute(() -> {
                minioClient.removeObject(
                        RemoveObjectArgs
                                .builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
                log.info("文件删除成功: {}/{}", bucketName, objectName);

                return null;
            }, "删除文件失败: %s".formatted(objectName));
        }

        /** 检查对象是否存在 */
        public boolean objectExists(String objectName) {
            validateObjectName(objectName);

            return execute(() -> {
                minioClient.statObject(
                        StatObjectArgs
                                .builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );

                return true;
            }, "对象不存在或检查失败: %s".formatted(objectName));
        }

        /** 生成 public 存储桶中的文件访问 URL */
        public String getPublicFileUrl(String objectName) {
            return "%s/%s/%s".formatted(minioProperties.getPublicBaseUrl(), bucketName, objectName);
        }

        /** 生成 GET 预签名 URL */
        public String getPresignedGetUrl(String objectName) {
            return getPresignedUrl(objectName, Method.GET, presignedUrlExpiry);
        }

        /** 生成 PUT 预签名 URL */
        public String getPresignedPutUrl(String objectName) {
            return getPresignedUrl(objectName, Method.PUT, presignedUrlExpiry);
        }

        private String getPresignedUrl(
                String objectName,
                Method method,
                long expiry
        ) {
            validateObjectName(objectName);

            return execute(() -> minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs
                            .builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .method(method)
                            .expiry((int) TimeUnit.SECONDS.toSeconds(expiry))
                            .build()
            ), "生成预签名 URL 失败: " + objectName);
        }

        private void validateObjectName(String objectName) {
            if (!StringUtils.hasText(objectName)) {
                throw new IllegalArgumentException("对象名不能为空");
            }
        }
    }
}
