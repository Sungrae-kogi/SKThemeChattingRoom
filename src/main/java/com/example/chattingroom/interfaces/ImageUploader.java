package com.example.chattingroom.interfaces;

import org.springframework.web.multipart.MultipartFile;

public interface ImageUploader {
    // 중괄호 {} 가 없는 메소드.
    // 저장 위치는 모르지만, 파일을 전달하면, 저장된 경로(String)을 반환하라는 규칙만
    String upload(MultipartFile file);
}
