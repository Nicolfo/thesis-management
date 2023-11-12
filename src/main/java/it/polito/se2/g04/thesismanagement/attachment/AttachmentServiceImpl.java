package it.polito.se2.g04.thesismanagement.attachment;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AttachmentServiceImpl implements AttachmentService{
    private final AttachmentRepository attachmentRepository;

    public AttachmentServiceImpl(AttachmentRepository attachmentRepository) {
        this.attachmentRepository = attachmentRepository;
    }

    @Override
    public AttachmentDTO addAttachment(MultipartFile file) throws IOException {
        var toAdd=new Attachment(file.getBytes(),file.getContentType(),file.getOriginalFilename());
        return attachmentRepository.save(toAdd).toDto();
    }

    @Override
    public Attachment getFileById(Long id) {
        return attachmentRepository.getReferenceById(id);
    }
}
