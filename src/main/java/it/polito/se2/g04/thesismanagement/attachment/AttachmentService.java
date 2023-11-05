package it.polito.se2.g04.thesismanagement.attachment;

import org.springframework.web.multipart.MultipartFile;

public interface AttachmentService {
    public AttachmentDTO addAttachment(MultipartFile file);
    public Attachment getFileById(Long id);

}
