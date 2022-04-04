package com.example.file_uplod_and_donloud.repository;

import com.example.file_uplod_and_donloud.entity.AttachmentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AttachmentContentRepository extends JpaRepository<AttachmentContent,Long> {
Optional<AttachmentContent>findByAttachmentId(long id);
}
