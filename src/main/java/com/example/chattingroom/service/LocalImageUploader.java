package com.example.chattingroom.service;

import com.example.chattingroom.interfaces.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class LocalImageUploader implements ImageUploader {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    @Override
    public String upload(MultipartFile file) {
        try{
            File dir = new File(uploadDir);
            if(!dir.exists()){
                if(!dir.mkdirs()){
                    throw new IOException("디렉토리 생성 실패");
                }
            }

            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            File destination = new File(uploadDir + uniqueName);
            file.transferTo(destination);

            return "/uploads/" + uniqueName;
        } catch (Exception e){
            throw new RuntimeException("파일 업로드 실패");
        }
    }
}
