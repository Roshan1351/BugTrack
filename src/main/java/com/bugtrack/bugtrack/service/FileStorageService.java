package com.bugtrack.bugtrack.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {
    @Value("${file.upload.dir}")
    private String uploadDir;
    //file save function.
    public String saveFile(MultipartFile file){
        try{
            Path uploadPath= Paths.get(uploadDir);
            if(!Files.exists(uploadPath)){
                Files.createDirectories(uploadPath);
            }

            String originalName= file.getOriginalFilename();
            String extension= "";
            if(originalName!= null && originalName.contains(".")){
                extension= originalName.substring(originalName.lastIndexOf(".")).toLowerCase();
            }

            String uniqueFileName= UUID.randomUUID().toString()+extension;

            Path filePath= uploadPath.resolve(uniqueFileName);
            Files.copy(file.getInputStream(),filePath, StandardCopyOption.REPLACE_EXISTING);
            return uploadDir+"/"+uniqueFileName;
        }catch(IOException e){
            throw new RuntimeException("File upload failed"+ e.getMessage());
        }
    }

    public void validateFile(MultipartFile file){

//        System.out.println("================================");
//        System.out.println("File Name    : " + file.getOriginalFilename());
//        System.out.println("Content Type : " + file.getContentType());
//        System.out.println("File Size    : " + file.getSize());
//        System.out.println("================================");

        if(file.isEmpty()){
            throw new RuntimeException("File is empty");
        }

//        String content_type= file.getContentType();
//        if(content_type==null || (!content_type.startsWith("image/") && !content_type.equals("application/pdf") && !content_type.equals("text/plain"))){
//            throw new RuntimeException("only image, pdfs, and text files is allowed");
//        }

        String originalName= file.getOriginalFilename();
        if(originalName==null || originalName.isEmpty()){
            throw new RuntimeException("File name is Invalid");
        }

        String extension= originalName.substring(originalName.lastIndexOf(".")+1).toLowerCase();
        List<String> allowedExtension= List.of("jpg", "jpeg","png","gif","webp","pdf","txt","log");

        if(!allowedExtension.contains(extension)){
            throw new RuntimeException("only images, pdfs, and text files are allowed. "+ "Got: "+ extension);
        }

        //file size checking file size not more than 10 mb
        if(file.getSize()>10*1024*1024){ //1024*1024= means 1 MB and after multiply by 10 then it is 10 MB which is in Bytes
            throw new RuntimeException("file size must be less than 10MB");
        }
    }

}
