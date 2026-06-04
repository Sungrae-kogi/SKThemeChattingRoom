package com.example.chattingroom.service;

import com.example.chattingroom.enums.ErrorCode;
import com.example.chattingroom.exception.BusinessException;
import com.example.chattingroom.interfaces.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
public class LocalImageUploader implements ImageUploader {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    // interface 구현체로 호출시 이곳을 실행.
    @Override
    public String upload(MultipartFile file) {
        try {
            File dir = new File(uploadDir); // 디렉토리 참조객체
            if (!dir.exists()) { // 디렉토리가 없다?
                if (!dir.mkdirs()) { // 그럼만들어 -> 그런데 만들지 못했다?
                    throw new IOException("디렉토리 생성 실패");
                }
            }
            // 디렉토리가 있다

            String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            File destination = new File(uploadDir + uniqueName);
            file.transferTo(destination);

            return "/uploads/" + uniqueName;
        } catch (Exception e) {
            throw new BusinessException("파일 업로드 실패", ErrorCode.FILE_UPLOAD_ERROR);
        }
    }
}
