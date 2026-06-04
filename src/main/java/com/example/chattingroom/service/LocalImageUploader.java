package com.example.chattingroom.service;

import com.example.chattingroom.enums.ErrorCode;
import com.example.chattingroom.exception.BusinessException;
import com.example.chattingroom.interfaces.ImageUploader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class LocalImageUploader implements ImageUploader {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    // interface 구현체로 호출시 이곳을 실행.
    @Override
    public String upload(MultipartFile file) {
        try {

            // MIME 타입 검증 과정 - 이미지 파일 확장자 검증, but 클라이언트가 보낸 정보는 조작가능하므로 단독으로 신뢰해서는 안된다.
            String contentType = file.getContentType();
            if (contentType == null || !contentType.contains("image")) {
                throw new BusinessException("이미지 파일만 업로드 가능", ErrorCode.INVALID_FILE_FORMAT);
            }

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
