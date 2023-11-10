package it.polito.se2.g04.thesismanagement.attachment;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AttachmentService {
    public AttachmentDTO addAttachment(MultipartFile file) throws IOException;
    public Attachment getFileById(Long id);

}
