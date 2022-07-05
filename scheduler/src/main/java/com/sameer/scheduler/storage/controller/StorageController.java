package com.sameer.scheduler.storage.controller;

import com.sameer.scheduler.storage.service.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/file")
public class StorageController {

    @Autowired
    StorageService storageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam(value = "file") MultipartFile multipartFile) {
        return ResponseEntity.ok(storageService.uploadFile(multipartFile));
    }

    @PostMapping("/upload-regular")
    public ResponseEntity<String> uploadRegularFile(@RequestParam(value = "file") File file) {
        return ResponseEntity.ok(storageService.uploadFile(file));
    }

    @GetMapping("/download/{fileName}")
    public ResponseEntity<ByteArrayResource> downloadFile(@PathVariable String fileName) {
        byte[] bytes = storageService.downloadFile(fileName);
        ByteArrayResource byteArrayResource = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .contentLength(bytes.length)
                .header("Content-type", "application/octat-stream")
                .header("Content-disposition", "attachment;fileName=\"" + fileName + "\"")
                .body(byteArrayResource);
    }

    @DeleteMapping("/delete/{fileName}")
    public ResponseEntity<String> deleteFile(@PathVariable String fileName) {
        return ResponseEntity.ok(storageService.deleteFile(fileName));
    }

}
