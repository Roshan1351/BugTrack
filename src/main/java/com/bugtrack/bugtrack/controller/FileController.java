package com.bugtrack.bugtrack.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class FileController {

    @Value("${file.upload.dir}")
    private String uploadDir;

    @GetMapping("/uploads/**")
    public ResponseEntity<Resource> getFile(HttpServletRequest request) {
        try {
            String requestPath = request.getRequestURI();
            String relative = requestPath.substring("/uploads/".length());
            if (relative.contains("..")) {
                return ResponseEntity.badRequest().build();
            }

            Path uploadRoot = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path publicRoot = uploadRoot.getParent() != null ? uploadRoot.getParent() : uploadRoot;
            Path filePath = publicRoot.resolve(relative).normalize();

            if (!filePath.startsWith(publicRoot) || !Files.exists(filePath) || !Files.isRegularFile(filePath)) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new UrlResource(filePath.toUri());
            String filename = filePath.getFileName().toString().toLowerCase();
            MediaType contentType = MediaType.APPLICATION_OCTET_STREAM;

            if (filename.endsWith(".png")) contentType = MediaType.IMAGE_PNG;
            else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) contentType = MediaType.IMAGE_JPEG;
            else if (filename.endsWith(".gif")) contentType = MediaType.IMAGE_GIF;
            else if (filename.endsWith(".webp")) contentType = MediaType.parseMediaType("image/webp");
            else if (filename.endsWith(".pdf")) contentType = MediaType.APPLICATION_PDF;
            else if (filename.endsWith(".txt") || filename.endsWith(".log")) contentType = MediaType.TEXT_PLAIN;

            return ResponseEntity.ok()
                    .contentType(contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filePath.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
