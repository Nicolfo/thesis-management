package it.polito.se2.g04.thesismanagement.attachment;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class AttachmentServiceImpl implements AttachmentService{
    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public AttachmentDTO addAttachment(MultipartFile file) {
        return null;
    }

    @Override
    public Attachment getFileById(Long id) {
        return null;
    }
}
