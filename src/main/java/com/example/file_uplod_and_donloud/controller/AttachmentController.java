package com.example.file_uplod_and_donloud.controller;

import com.example.file_uplod_and_donloud.entity.Attachment;
import com.example.file_uplod_and_donloud.entity.AttachmentContent;
import com.example.file_uplod_and_donloud.repository.AttachmentContentRepository;
import com.example.file_uplod_and_donloud.repository.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    @Autowired
    AttachmentRepository attachmentRepository;

    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    public static final   String uploadDirectory="yuklanganlar";

    @PostMapping("/uploadDb")
    public String uploadFileToDb(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());

        if (file!=null){
             String originalFilename = file.getOriginalFilename();
            long size = file.getSize();
            String contentType = file.getContentType();
            Attachment attachment=new Attachment();
            attachment.setContentType(contentType);
            attachment.setSize(size);
            attachment.setFileOriginalName(originalFilename);
            Attachment saveAttachment= attachmentRepository.save(attachment);

            AttachmentContent attachmentContent=new AttachmentContent();
            attachmentContent.setBytes(file.getBytes());
            attachmentContent.setAttachment(saveAttachment);
            attachmentContentRepository.save(attachmentContent);

            return "fayil saqlandi id:"+attachment.getId();

        }

        return "xatolik";
    }

    @PostMapping("/uploadSystem")
    public String uploadFileToSystem(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());

        if (file!=null){
            String originalFilename = file.getOriginalFilename();
            Attachment attachment=new Attachment();
            attachment.setContentType(file.getContentType());
            attachment.setSize(file.getSize());
            attachment.setFileOriginalName(file.getOriginalFilename());

            String[] split = originalFilename.split("\\.");
            String name=UUID.randomUUID().toString()+"."+split[split.length-1];
            attachment.setName(name);

            attachmentRepository.save(attachment);

            Path path= Paths.get(uploadDirectory+"/"+name);
            Files.copy(file.getInputStream(),path);
            return "saqlandi id"+attachment.getId();
        }

            return "xatolik saqlanmadi";
    }

    @GetMapping("/getFile/{id}")
    public void getFile(@PathVariable long id, HttpServletResponse response) throws IOException {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        if (optionalAttachment.isPresent()){
            Attachment attachment = optionalAttachment.get();

            Optional<AttachmentContent> byAttachmentId = attachmentContentRepository.findByAttachmentId(attachment.getId());
            if (byAttachmentId.isPresent()){
                AttachmentContent attachmentContent = byAttachmentId.get();

                response.setHeader("Content-Disposition","attachment; file=\"" + attachment.getFileOriginalName() + "\"");

                response.setContentType(attachment.getContentType());

                FileCopyUtils.copy(attachmentContent.getBytes(),response.getOutputStream());

            }
        }

    }

    @GetMapping("getFromFileSystem/{id}")
    public void  getFromFileSystem(@PathVariable long id,HttpServletResponse response) throws IOException {
        Optional<Attachment> byId = attachmentRepository.findById(id);
        if (byId.isPresent()){
            Attachment attachment = byId.get();
            response.setHeader("Content-Disposition","attachment; file=\"" + attachment.getFileOriginalName() + "\"");

            response.setContentType(attachment.getContentType());
            FileInputStream fileInputStream=new FileInputStream(uploadDirectory+"/"+attachment.getName());
            FileCopyUtils.copy(fileInputStream,response.getOutputStream());
        }
    }














}
