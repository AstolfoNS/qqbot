package com.timeleafing.qqbot.service.file.impl;

import com.timeleafing.qqbot.common.util.FileUtils;
import com.timeleafing.qqbot.common.util.MinioUtils;
import com.timeleafing.qqbot.exception.BusinessException;
import com.timeleafing.qqbot.service.file.MinioFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class MinioFileServiceImpl implements MinioFileService {

    private final MinioUtils minioUtils;


    @Override
    public String uploadPublicFileToMinio(MultipartFile file, String bucketName, Set<String> allowedContentTypes) {
        // file 参数校验
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("上传的文件不能为空");
        }
        // bucketName 参数校验
        if (!StringUtils.hasText(bucketName)) {
            throw new IllegalArgumentException("minio 存储桶不能为空");
        }
        String originalFilename = file.getOriginalFilename();
        // 判断 originalFilename 是否为空
        if (originalFilename == null) {
            throw new IllegalArgumentException("文件名不能为空");
        }
        // 文件类型检查
        String contentType = file.getContentType();
        // 判断 contentType 是否为空
        if (!StringUtils.hasText(contentType)) {
            throw new IllegalArgumentException("无法识别文件类型");
        }
        // 如果用户提供了 allowedContentTypes 集合，则执行类型校验
        if (allowedContentTypes != null && !allowedContentTypes.isEmpty()) {
            if (!allowedContentTypes.contains(contentType)) {
                throw new BusinessException("不支持的文件类型: %s".formatted(contentType));
            }
        }
        String objectName = FileUtils.generateFileName(originalFilename);

        try (InputStream inputStream = file.getInputStream()) {
            minioUtils.bucket(bucketName).uploadFile(objectName, inputStream, file.getSize(), file.getContentType());
        } catch (IOException e) {
            throw new BusinessException("文件上传失败，请稍后重试");
        }
        return minioUtils.bucket(bucketName).getPublicFileUrl(objectName);
    }

    @Override
    public void deletePublicFileFromMinio(String fileUrl, String bucketName) {
        // fileUrl 参数校验
        if (!StringUtils.hasText(bucketName)) {
            throw new IllegalArgumentException("fileUrl 不能为空");
        }
        // bucketName 参数校验
        if (!StringUtils.hasText(bucketName)) {
            throw new IllegalArgumentException("minio 存储桶不能为空");
        }
        try {
            minioUtils.bucket(bucketName).deleteFile(minioUtils.extractObjectName(fileUrl, bucketName));
        } catch (Exception e) {
            throw new BusinessException("文件删除失败，请稍后重试");
        }
    }

    @Override
    public MultipartFile downloadFileFromMinio(String fileUrl, String fileName) {
        // fileUrl 参数校验
        if (!StringUtils.hasText(fileUrl)) {
            throw new IllegalArgumentException("文件 url 无效");
        }
        // fileName 参数校验
        if (!StringUtils.hasText(fileName)) {
            throw new IllegalArgumentException("文件名称不能为空");
        }
        return minioUtils.downloadFileAsMultipart(fileUrl, fileName);
    }

}
