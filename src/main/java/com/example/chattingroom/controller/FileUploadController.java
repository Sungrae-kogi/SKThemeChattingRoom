package com.example.chattingroom.controller;

import com.example.chattingroom.interfaces.ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    /*
     * 파일을 저장할 로컬 폴더 경로 설정 -> 실제 메신저의 파일전송은 FTP가 아닌 클라우드 서버에 이미지를 HTTP로 전송하고, URL에 그
     * 주소를 보내서 받는사람쪽에서 그 이미지를 불러오게끔.
     * 다형성을 위해 Interface 타입으로 선언 -> 생성자 호출은 지금은 LocalImageUploader 라고 Interface를 구현한
     * 타입의 생성자를 호출하지만, 코드나 시스템이 확장된다면 다형성에 의해 ImageUploader를 구현한 다양한 타입의 클래스가 올 수
     * 있을것.
     */
    private final ImageUploader uploader;

    // chat.js 에서 formData.append("file", file) 로 파일을 전송하면 백엔드 컨트롤러에서는 아래와 같이 JS의
    // key 값이 file 을 파라미터 이름 file에 매핑하여 처리
    @PostMapping("/image")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        // 굳이 이미지가 아니여도, MultipartFile 클래스를 사용함.
        // Java에서 다룰 수 있는 표준화된 규격.
        try {
            String imageUrl = uploader.upload(file);

            return ResponseEntity.ok(imageUrl);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("파일 업로드 실패");
        }
    }
}
