package com.timeleafing.qqbot.service.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface MinioFileService {

    String uploadPublicFileToMinio(MultipartFile file, String bucketName, Set<String> allowedContentTypes);

    void deletePublicFileFromMinio(String fileUrl, String bucketName);

    MultipartFile downloadFileFromMinio(String fileUrl, String fileName);

}
