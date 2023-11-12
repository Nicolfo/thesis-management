package it.polito.se2.g04.thesismanagement.attachment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AttachmentServiceImpl implements AttachmentService{
    private final AttachmentRepository attachmentRepository;


    @Override
    public AttachmentDTO addAttachment(MultipartFile file) throws IOException {
        var toAdd = new Attachment(file.getBytes(), file.getContentType(), file.getOriginalFilename());
        Attachment savedAttachment = attachmentRepository.save(toAdd);
        return savedAttachment.toDto();
    }

    @Override
    public Attachment getFileById(Long id) {
        Optional<Attachment> optionalAttachment = attachmentRepository.findById(id);
        return optionalAttachment.orElse(null);
    }
}
