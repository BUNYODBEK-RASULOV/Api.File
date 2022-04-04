package com.example.file_uplod_and_donloud.repository;

import com.example.file_uplod_and_donloud.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,Long> {
}
