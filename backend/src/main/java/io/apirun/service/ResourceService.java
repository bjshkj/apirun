package io.apirun.service;

import io.apirun.commons.exception.MSException;
import io.apirun.commons.utils.FileUtils;
import io.apirun.controller.request.MdUploadRequest;
import io.apirun.i18n.Translator;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;

@Service
@Transactional(rollbackFor = Exception.class)
public class ResourceService {

//    private static final String RESOURCE_DIR = "/opt/apirun/data/resource/";
    private static final String MD_IMAGE_DIR = "/opt/apirun/data/image/markdown";

    public void mdUpload(MdUploadRequest request, MultipartFile file) {
        FileUtils.uploadFile(file, MD_IMAGE_DIR, request.getId() + "_" + file.getOriginalFilename());
    }

    public ResponseEntity<FileSystemResource> getMdImage(String name) {
        if (name.contains("/")) {
            MSException.throwException(Translator.get("invalid_parameter"));
        }
        File file = new File(MD_IMAGE_DIR + "/" + name);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + file.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(file));
    }

    public void mdDelete(String fileName) {
        if (fileName.contains("/")) {
            MSException.throwException(Translator.get("invalid_parameter"));
        }
        FileUtils.deleteFile(MD_IMAGE_DIR + "/" + fileName);
    }
}
