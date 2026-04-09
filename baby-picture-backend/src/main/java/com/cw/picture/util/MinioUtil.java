package com.cw.picture.util;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Component
public class MinioUtil {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Value("${minio.endpoint}")
    private String endpoint;

    /**
     * 上传文件到MinIO
     * 
     * @param file 文件
     * @param objectName 对象名称
     * @return 上传结果
     * @throws IOException
     */
    public void upload(MultipartFile file, String objectName) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build();

            minioClient.putObject(putObjectArgs);
        } catch (Exception e) {
            throw new IOException("上传文件失败", e);
        }
    }

    /**
     * 从MinIO删除文件
     * 
     * @param objectName 对象名称
     * @throws Exception
     */
    public void delete(String objectName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(objectName)
                        .build()
        );
    }

    /**
     * 获取文件访问URL
     *
     * @param fileName 文件名称
     * @return 文件访问URL
     */
    public String getFileUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            log.error("获取文件访问URL失败：{}", e.getMessage());
            return null;
        }
    }
    
    /**
     * 从URL中提取对象名
     * 
     * @param url 完整URL或对象名
     * @return 对象名
     */
    private String extractObjectNameFromUrl(String url) {
        if (url == null) {
            return null;
        }
        
        // 如果是完整URL，提取对象名部分
        if (url.startsWith("http")) {
            int lastSlashIndex = url.lastIndexOf("/");
            if (lastSlashIndex != -1 && lastSlashIndex < url.length() - 1) {
                return url.substring(lastSlashIndex + 1);
            }
        }
        
        // 如果已经是对象名，直接返回
        return url;
    }
}