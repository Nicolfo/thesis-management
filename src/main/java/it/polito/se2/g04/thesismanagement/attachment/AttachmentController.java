package it.polito.se2.g04.thesismanagement.attachment;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping("/API/uploadFile")
    @ResponseStatus(HttpStatus.CREATED)
    public AttachmentDTO addAttachment(@RequestPart("file")MultipartFile file) throws IOException {
        try{
            return attachmentService.addAttachment(file);
        }catch (IOException ioException){
            //handle errors in a better way
            throw ioException;
        }

    }

    @GetMapping("/getFile/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ByteArrayResource> getFile(@PathVariable Long id) {
        //add errors if user is not authorized to get file (he was not the one who uploaded it)

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
