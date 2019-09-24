package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.upload.config.FastClientImport;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private UploadProperties properties;
    private static final List<String> ALLOWS_TYPES = Arrays.asList("image/jpeg", "image/png", "image/jpg");

    public String uploadImage(MultipartFile file) {
        try {
            String contentType = file.getContentType();
            if (!properties.getAllowTypes().contains(contentType)) {
                throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
            }
            BufferedImage read = ImageIO.read(file.getInputStream());
            if (null == read) {
                throw new LyException(ExceptionEnum.UPLOAD_FILE_ERROR);
            }
//            File dest = new File("D:\\persional\\ldywork\\leyou\\ly-upload\\src\\main\\resources\\static",file.getOriginalFilename());
//            file.transferTo(dest);
            String extName = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extName, null);
            String fullPath = storePath.getFullPath();
            return properties.getBaseUrl() + storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
