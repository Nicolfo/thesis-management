package it.polito.se2.g04.thesismanagement.attachment;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentDTO addAttachment(@RequestPart("file")MultipartFile file){
        return attachmentService.addAttachment(file);
    }

    @GetMapping("/getFile/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable Long id) {
        Attachment elem = attachmentService.getFileById(id);

        if (elem != null && elem.getAttachment() != null) {
            ByteArrayResource resource = new ByteArrayResource(elem.getAttachment());

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + elem.getFileName());
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(elem.getAttachment().length)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }



}
